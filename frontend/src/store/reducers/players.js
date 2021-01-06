import * as types from '../ActionTypes';

const initialState = {
    players: [],
    snapshots:[]
};

export default function players(state = initialState, action) {
    switch (action.type) {

        case types.UPDATE_STATE: {
            return {
                ...state,
                players: action.players
            }
        }
        case types.UPDATE_SNAPSHOTS: {
            return {
                ...state,
                snapshots: [{
                    ...action.snapshot,
                    timestamp: new Date().getTime()
                }].concat(state.snapshots.slice(0, 10))
            };
        }
        default:
            return state;
    }
}