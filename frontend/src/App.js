import {combineReducers, createStore} from 'redux';
import {Provider} from 'react-redux';

import * as reducers from './store/reducers';

import './App.css';
import Board from "./container/Board";
import WebSocket from "./container/WebSocket";


const store = createStore(combineReducers(reducers),
    window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__());

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
