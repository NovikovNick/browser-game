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

let timerId = setTimeout(function tick() {

    const snapshots = store.getState().state.snapshots;
    const session = store.getState().state.session;

    if (snapshots) {
        const fst = snapshots[0]
        const snd = snapshots[1]
        if (fst && snd && fst.players && snd.players) {

            const fstGroupedById = fst.players.reduce((r, a) => {
                r[a.id] = a;
                return r;
            }, {});

            const sndGroupedById = snd.players.reduce((r, a) => {
                r[a.id] = a;
                return r;
            }, {});

            const frame = fst.timestamp - snd.timestamp;
            const timestamp = new Date().getTime();
            const delay = timestamp - fst.timestamp;
            const mod = frame < delay ? 1 : delay / frame;

            const players = [];
            for (const [id, value] of Object.entries(fstGroupedById)) {
                if (sndGroupedById[id]) {
                    const p1 = value
                    const p2 = sndGroupedById[id]
                    const player = {
                        id: id,
                        username: p1.username,
                        x: p2.x + (p1.x - p2.x) * mod,
                        y: p2.y + (p1.y - p2.y) * mod
                    };
                    players.push(player);
                }
            }
            store.dispatch(actions.updateState(players));
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
                    <nav className="col-md-2 d-none d-md-block bg-light sidebar">
                        <Controls/>
                    </nav>

                    <main role="main" className="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4"></main>

                </div>
            </Container>
        </Provider>
    );
}

export default App;
