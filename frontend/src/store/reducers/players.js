import * as types from '../ActionTypes';

const initialState = {
    players: [],
};

export default function players(state = initialState, action) {
    switch (action.type) {

        case types.UPDATE_STATE: {
            return {
                ...state,
                players: action.players
            }
        }
        default:
            return state;
    }
}