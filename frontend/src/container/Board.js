import React from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

import * as Store from "../store/ReduxActions";

import Player from "../component/Player";

function Board({players}) {

    players = players || [];

    return (
        <svg version="1.1"
             baseProfile="full"
             xmlns="http://www.w3.org/2000/svg">

            {players.map((item, i) => <Player key={i}
                                              x={item.x}
                                              y={item.y}
                                              name={item.username}
                                              color={"red"}/>)}
        </svg>
    );
}

const mapStateToProps = state => ({
    players: state.state.players
});
const mapDispatchToProps = dispatch => ({
    actions: bindActionCreators(Store, dispatch)
});
export default connect(mapStateToProps, mapDispatchToProps)(Board);
