import * as types from '../ActionTypes';

const initialState = {
    character: {
        sessionId: false,
        username: "",
        mousePos: {d0: 0.0, d1: 0.0},
        gameObject: {
            transform: {
                position: {d0: 0.0, d1: 0.0},
                rotation: {d0: 0.0, d1: 0.0}
            },
            rigidBody: {
                shape: {
                    points: []
                }
            }
        }
    },
    enemies: [],
    walls: [],
    snapshots: []
};

export default function players(state = initialState, action) {
    switch (action.type) {
        case types.UPDATE_SESSION_ID: {
            return {
                ...state,
                character: {
                    ...state.character,
                    sessionId: action.sessionId
                }
            }
        }
        case types.UPDATE_PLAYER_DATA: {
            return {
                ...state,
                character: {
                    ...state.character,
                    username: action.username
                }
            }
        }
        case types.UPDATE_STATE: {
            return {
                ...state,
                character: {
                    ...state.character,
                    mousePos: action.character.mousePos,
                    gameObject: action.character.gameObject
                }
            }
        }
        case types.UPDATE_SNAPSHOTS: {
            return {
                ...state,
                snapshots: [{
                    ...action.snapshot.snapshot,
                    timestamp: new Date().getTime()
                }].concat(state.snapshots.slice(0, 10))
            };
        }
        default:
            return state;
    }
}