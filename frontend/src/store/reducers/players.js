import * as types from '../ActionTypes';

const initialState = {
    character: {
        sessionId: false,
        username: "",
        gameObject: {
            transform: {
                position: {d0: 0.0, d1: 0.0},
                rotationAngleRadian: 0
            },
            rigidBody: {
                shape: {
                    points: []
                },
                transformed: {
                    points: []
                }
            }
        }
    },
    projectiles: [
        {
            points: [{d0: 0.0, d1: 0.0}]
        }
    ],
    explosions: [],
    enemies: [],
    walls: [],
    snapshots: []
};

export default function players(state = initialState, action) {

    const now = new Date().getTime();

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
                    gameObject: action.character.gameObject
                },
                enemies: action.enemies,
                projectiles: action.projectiles,
                walls: action.walls,
                explosions: [...state.explosions]
                    .filter(i => i && (now - i.timestamp) < 1000)
                    .concat(action.explosions)
            }
        }
        case types.UPDATE_SNAPSHOTS: {
            return {
                ...state,
                snapshots: [{
                    ...action.snapshot.snapshot,
                    timestamp: now
                }].concat(state.snapshots.slice(0, 10))
            };
        }
        default:
            return state;
    }
}