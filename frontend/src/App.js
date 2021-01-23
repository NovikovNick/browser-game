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
import * as ShapeService from "./service/ShapeService";
import * as GeometryService from "./service/GeometryService";


const store = createStore(combineReducers(reducers), applyMiddleware(thunk));
// window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__());

function interpolateNumbers(p1, p2, mod) {
    return p1 + (p2 - p1) * mod;
}

function interpolatePoints(p1, p2, mod) {
    return [
        interpolateNumbers(p1[0], p2[0], mod),
        interpolateNumbers(p1[1], p2[1], mod)
    ];
}

function interpolateGameObject(p1, p2, mod) {

    return {
        id: p1.id,
        pos: interpolatePoints(p1.pos, p2.pos, mod),
        rot: interpolateNumbers(p1.rot, p2.rot, mod),
        shape: p1.shape
    };
}

function interpolatePlayer(p1, p2, mod) {

    const character = {
        ...p1,
        gameObject: interpolateGameObject(p2.obj, p1.obj, mod)
    };
    return character;
}

let timerId = setTimeout(function tick() {

    const center = [window.innerWidth / 2, window.innerHeight / 2]
    const state = store.getState().state;
    const snapshots = state.snapshots;
    const walls = state.walls;

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
            const offset = [
                character.gameObject.pos[0] - center[0],
                character.gameObject.pos[1] - center[1]
            ];
            if (character) {
                character.gameObject.pos = center;
                character.gameObject.shape = ShapeService.getPlayerShape().map(p => {
                    return GeometryService.rotate(
                        [p[0] + center[0], p[1] + center[1]],
                        character.gameObject.rot,
                        center
                    );
                });
            }

            // enemies
            const enemies = [];
            {
                const fstGroupedById = fst.enemies.reduce((r, a) => {
                    r[a.obj.id] = a;
                    return r;
                }, {});

                const sndGroupedById = snd.enemies.reduce((r, a) => {
                    r[a.obj.id] = a;
                    return r;
                }, {});

                for (const [id, value] of Object.entries(fstGroupedById)) {
                    if (sndGroupedById[id]) {
                        const p1 = value
                        const p2 = sndGroupedById[id]
                        const item = interpolatePlayer(p1, p2, mod);

                        const pos = [item.gameObject.pos[0] - offset[0], item.gameObject.pos[1] - offset[1]];
                        item.gameObject.pos = pos;
                        item.gameObject.shape = ShapeService.getPlayerShape().map(p => {
                            return GeometryService.rotate(
                                [p[0] + pos[0], p[1] + pos[1]],
                                item.gameObject.rot,
                                pos
                            );
                        });

                        enemies.push(item);
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

                        const item = interpolateGameObject(p2, p1, mod);

                        const pos = [item.pos[0] - offset[0], item.pos[1] - offset[1]];
                        item.pos = pos;
                        item.shape = ShapeService.getBulletShape().map(p => {
                            return GeometryService.rotate(
                                [p[0] + pos[0], p[1] + pos[1]],
                                item.rot,
                                pos
                            );
                        });

                        projectiles.push(item);
                    }
                }
            }

            // explosions
            const explosions = []; /*fst.explosions.map(i => {
                return {timestamp: now, point: i}
            })*/

            // walls
            const updatedWalls = [];
            {
                const wallObjIds = new Set();
                walls.forEach(w => wallObjIds.add(w.id));
                fst.walls.length > 0 && fst.walls.forEach(w => wallObjIds.add(w.id))
                snd.walls.length > 0 && snd.walls.forEach(w => wallObjIds.add(w.id))

                const wallGroupedById = walls.reduce((r, a) => {r[a.id] = a;return r;}, {});
                const fstGroupedById = fst.walls.reduce((r, a) => {r[a.id] = a;return r;}, {});
                const sndGroupedById = snd.walls.reduce((r, a) => {r[a.id] = a;return r;}, {});

                wallObjIds.forEach(id => {

                    const isExist = wallGroupedById[id] != undefined;
                    const isUpdated = fstGroupedById[id] != undefined && sndGroupedById[id] != undefined;
                    const isCreated = fstGroupedById[id] != undefined && wallGroupedById[id] == undefined;
                    const isRemoved = fst.removed && fst.removed.includes(id);

                    if(!isRemoved && (isExist || isUpdated || isCreated)) {

                        const wall = isUpdated
                            ? interpolateGameObject(sndGroupedById[id], fstGroupedById[id], mod)
                            : isCreated ? fstGroupedById[id] : wallGroupedById[id];

                        const wallOffset = [wall.pos[0] - offset[0], wall.pos[1] - offset[1]];

                        updatedWalls.push({
                            id: id,
                            pos: wall.pos,
                            rot: wall.rot,
                            shape: ShapeService.getWallShape().map(p => {
                                return GeometryService.rotate([p[0] + wallOffset[0], p[1] + wallOffset[1]], wall.rot, wallOffset);
                            })
                        })
                    }
                })
            }

            store.dispatch(actions.updateState(character, enemies, projectiles, explosions, updatedWalls));
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
