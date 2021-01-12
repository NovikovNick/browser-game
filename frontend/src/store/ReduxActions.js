import * as types from './ActionTypes';
import * as Service from "../service/service";

export const updateState = (character, enemies, projectiles) => ({
    type: types.UPDATE_STATE,
    character: character,
    enemies: enemies,
    projectiles: projectiles
});
export const addSnapshot = (snapshot) => ({type: types.UPDATE_SNAPSHOTS, snapshot : snapshot});
export const updatePlayerData = (username) => ({type: types.UPDATE_PLAYER_DATA, username : username});
export const updateSessionId = (sessionId) => ({type: types.UPDATE_SESSION_ID, sessionId : sessionId});



export function changePlayerName(req = {}) {
    return (dispatch) => {
        return Service.changePlayerName(req)
            .then(res => dispatch(updatePlayerData(res.username)));
    };
}