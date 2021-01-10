import React from 'react';
import {applyMiddleware, combineReducers, createStore} from 'redux';
import {Provider} from 'react-redux';
import thunk from 'redux-thunk';

import * as reducers from './store/reducers';
// Importing Sass with Bootstrap CSS
import './App.scss';

import Board from "./container/Board";
import WebSocket from "./container/WebSocket";
import * as actions from "./store/ReduxActions";
import {Container} from "react-bootstrap";
import Controls from "./container/Controls";


const store = createStore(combineReducers(reducers), applyMiddleware(thunk));
// window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__());

function interpolatePosition(p1, p2, mod) {

    const character = {
        ...p1,
        mousePos: {
            d0: p2.mousePos.d0 + (p1.mousePos.d0 - p2.mousePos.d0) * mod,
            d1: p2.mousePos.d1 + (p1.mousePos.d1 - p2.mousePos.d1) * mod
        },
        gameObject: {
            transform: {
                position: {
                    d0: p2.gameObject.transform.position.d0 + (p1.gameObject.transform.position.d0 - p2.gameObject.transform.position.d0) * mod,
                    d1: p2.gameObject.transform.position.d1 + (p1.gameObject.transform.position.d1 - p2.gameObject.transform.position.d1) * mod
                },
                rotation: {
                    ...p1.gameObject.transform.rotation
                }
            },
            rigidBody: p1.gameObject.rigidBody
        }
    };
    return character;
}

let timerId = setTimeout(function tick() {

    const snapshots = store.getState().state.snapshots;

    if (snapshots) {
        const fst = snapshots[0]
        const snd = snapshots[1]

        if (fst && snd && fst.character && snd.character) {

            const frame = fst.timestamp - snd.timestamp;
            const timestamp = new Date().getTime();
            const delay = timestamp - fst.timestamp;
            const mod = frame < delay ? 1 : delay / frame;

            // player
            const character = interpolatePosition(fst.character, snd.character, mod);

            // enemies
            const enemies = [];
            const fstGroupedById = fst.enemies.reduce((r, a) => {
                r[a.sessionId] = a;
                return r;
            }, {});

            const sndGroupedById = snd.enemies.reduce((r, a) => {
                r[a.sessionId] = a;
                return r;
            }, {});

            for (const [sessionId, value] of Object.entries(fstGroupedById)) {
                if (sndGroupedById[sessionId]) {
                    const p1 = value
                    const p2 = sndGroupedById[sessionId]
                    enemies.push(interpolatePosition(p1, p2, mod));
                }
            }

            store.dispatch(actions.updateState(character, enemies));
        }
    }

    timerId = setTimeout(tick, 15);
}, 15);

function App() {
    return (
        <Provider store={store}>

            <WebSocket host={"http://192.168.0.103:8080"}/>
            <Board/>

            <Container className="p-3">
                <div className="row">
                    <nav className="offset-md-10 col-md-2 d-none d-md-block bg-light sidebar">
                        <Controls/>
                    </nav>

                    <main role="main" className="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4"></main>

                </div>
            </Container>
        </Provider>
    );
}

export default App;
