import React from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

import * as Store from "../store/ReduxActions";

import PlayerMouse from "../component/PlayerMouse";
import Player from "../component/Player";

function Board({players}) {

    players = players || [];

    const verticalLines = [];
    const horizontalLines = [];

    const width = 2000;
    const height = 2000

    {
        const n = 40;
        for (var i = 1; i < n; i++) {
            const y = (height / n) * i;
            const x = (width / n) * i;

            const color = i % 2 != 0 ? "#eee" : "#888";

            verticalLines.push(<line key={i} x1="0" y1={y} x2="2000" y2={y} stroke={color}/>);
            horizontalLines.push(<line key={i} x1={x} y1="0" x2={x} y2="2000" stroke={color}/>);
        }
    }
    return (
        <svg version="1.1"
             baseProfile="full"
             xmlns="http://www.w3.org/2000/svg">

            {verticalLines}
            {horizontalLines}

            {players.map((item, i) =>
                <g>
                    <Player x={item.characterPosX} y={item.characterPosY}/>
                    <PlayerMouse key={i} x={item.x} y={item.y} name={item.username} color={"red"}/>
                </g>)}
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
