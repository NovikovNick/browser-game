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

function interpolatePoints(p1, p2, mod) {
    return {
        d0: window.innerWidth / 2  + p1.d0 + (p2.d0 - p1.d0) * mod,
        d1: window.innerHeight / 2  + p1.d1 + (p2.d1 - p1.d1) * mod
    };
}

function interpolatePolygon(p1, p2, mod) {

    const points = [];
    for (let i = 0; i < p1.points.length; i++) {
        const p1Point = p1.points[i]
        const p2Point = p2.points[i]
        points.push(interpolatePoints(p2Point, p1Point, mod))
    }
    return {points: points}
}

function interpolateGameObject(p1, p2, mod) {

    let transformed = {};
    if(p1.rigidBody.transformed && p2.rigidBody.transformed) {
        transformed = interpolatePolygon(p2.rigidBody.transformed, p1.rigidBody.transformed, mod);
    }


    return {
        transform: {
            rotationAngleRadian: p1.transform.rotationAngleRadian + (p2.transform.rotationAngleRadian - p1.transform.rotationAngleRadian) * mod,
            position: interpolatePoints(p1.transform.position, p2.transform.position, mod)
        },
        rigidBody: {
            ...p1.rigidBody,
            transformed: transformed
        }
    };
}

function interpolatePlayer(p1, p2, mod) {

    const character = {
        ...p1,
        gameObject: interpolateGameObject(p2.gameObject, p1.gameObject, mod)
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
            const now = new Date().getTime();
            const delay = now - fst.timestamp;
            const mod = frame < delay ? 1 : delay / frame;

            // player
            const character = interpolatePlayer(fst.character, snd.character, mod);

            // enemies
            const enemies = [];
            {
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
                        enemies.push(interpolatePlayer(p1, p2, mod));
                    }
                }
            }

            // projectiles
            const projectiles = [];
            {
                const fstGroupedById = fst.projectiles.reduce((r, a) => {
                    r[a.id] = a;
                    return r;
                }, {});
                const sndGroupedById = snd.projectiles.reduce((r, a) => {
                    r[a.id] = a;
                    return r;
                }, {});

                for (const [id, value] of Object.entries(fstGroupedById)) {
                    if (sndGroupedById[id]) {
                        const p1 = value
                        const p2 = sndGroupedById[id]

                        projectiles.push({
                            ...p1,
                            gameObject: interpolateGameObject(p2.gameObject, p1.gameObject, mod)
                        });
                    }
                }
            }

            // explosions
            const explosions = fst.explosions.map(i => {
                return {timestamp: now, point: i}
            })

            // walls
            const walls = fst.walls.map((p1, i) => interpolatePolygon(p1, snd.walls[i], mod));

            store.dispatch(actions.updateState(character, enemies, projectiles, explosions, walls));
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
