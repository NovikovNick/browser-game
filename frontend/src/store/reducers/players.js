import * as types from '../ActionTypes';

const initialState = {
    session: {
        sessionId: false,
        username: ""
    },
    players: [],
    snapshots:[]
};

export default function players(state = initialState, action) {
    switch (action.type) {
        case types.UPDATE_SESSION_ID: {
            return {
                ...state,
                session: {
                    ...state.session,
                    sessionId: action.sessionId
                }
            }
        }
        case types.UPDATE_PLAYER_DATA: {
            return {
                ...state,
                session: {
                    ...state.session,
                    username: action.username
                }
            }
        }
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