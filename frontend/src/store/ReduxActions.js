import * as types from './ActionTypes';

export const updateState = (players) => ({type: types.UPDATE_STATE, players : players});

export const addSnapshot = (snapshot) => ({type: types.UPDATE_SNAPSHOTS, snapshot : snapshot});
