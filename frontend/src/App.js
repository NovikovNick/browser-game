import React from 'react';
import {combineReducers, createStore} from 'redux';
import {Provider} from 'react-redux';

import * as reducers from './store/reducers';

import './App.css';
import Board from "./container/Board";
import WebSocket from "./container/WebSocket";
import * as actions from "./store/ReduxActions";


const store = createStore(combineReducers(reducers),
    window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__());

let timerId = setTimeout(function tick() {

    const snapshots = store.getState().state.snapshots;

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
            <div className="App">
                <WebSocket/>
                <Board/>
            </div>
        </Provider>
    );
}

export default App;
