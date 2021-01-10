import React from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

import * as Store from "../store/ReduxActions";

import PlayerMouse from "../component/PlayerMouse";
import Player from "../component/Player";

function Grid({width, height, n}) {
    const grid = [];
    for (var i = 1; i < n; i++) {

        const y = (height / n) * i;
        const x = (width / n) * i;
        const color = i % 2 != 0 ? "#eee" : "#888";

        grid.push(<line key={"v" + i} x1="0" y1={y} x2={width}  y2={y}      stroke={color}/>);
        grid.push(<line key={"h" + i} x1={x} y1="0" x2={x}      y2={height} stroke={color}/>);
    }
    return grid;
}

function Board({character, enemies}) {

    enemies = enemies || []
    return (
        <svg version="1.1"
             baseProfile="full"
             xmlns="http://www.w3.org/2000/svg">

            <Grid width={2000} height={2000} n={40}/>

            <Player character={character} isEnemy={false} color={"blue"}/>
            {enemies.map((item, i) => <Player key={i} character={item} isEnemy={true} color={"red"}/>)}
        </svg>
    );
}

const mapStateToProps = state => ({
    character: state.state.character,
    enemies: state.state.enemies
});
const mapDispatchToProps = dispatch => ({
    actions: bindActionCreators(Store, dispatch)
});
export default connect(mapStateToProps, mapDispatchToProps)(Board);
