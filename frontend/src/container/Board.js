import React from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

import * as Store from "../store/ReduxActions";
import Player from "../component/Player";
import Polygon from "../component/Polygon";
import Wall from "../component/Wall";
import Point from "../component/Point";

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

function random() {
    const min = Math.ceil(0);
    const max = Math.floor(800);
    return Math.floor(Math.random() * (max - min + 1)) + min;
}
const p1 = [random(), random()]
const p2 = [random(), random()]
const p3 = [random(), random()]
const p4 = [random(), random()]

function Line({p1, p2, color}) {
    return <g>
        <line stroke={color}
              x1={p1[0]} x2={p2[0]}
              y1={p1[1]} y2={p2[1]}/>
        <circle cx={p1[0]} cy={p1[1]} r={5} fill={color}/>
        <circle cx={p2[0]} cy={p2[1]} r={5} fill={color}/>
    </g>;
}

/**
 * determinant for matrix [
 *   [a, c],
 *   [b, d]
 * ]
 * @param matrix [2][2]
 * @returns {number}
 */
function det(matrix) {
    const a = matrix[0][0], b = matrix[1][0], c = matrix[0][1], d = matrix[1][1];
    return a * d - b * c;
}

function intersect(p1, p2, p3, p4) {

    const x1 = p1[0], x2 = p2[0], x3 = p3[0], x4 = p4[0];
    const y1 = p1[1], y2 = p2[1], y3 = p3[1], y4 = p4[1];

    const uNumerator = det([
        [x4 - x2, x4 - x3],
        [y4 - y2, y4 - y3]
    ])
    const vNumerator = det([
        [x1 - x2, x4 - x2],
        [y1 - y2, y4 - y2]
    ]);
    const denominator = det([
        [x1 - x2, x4 - x3],
        [y1 - y2, y4 - y3]
    ]);
    const u = uNumerator / denominator;
    const v = vNumerator / denominator;
    return {
        intersect: u > 0 && u < 1 && v > 0 && v < 1,
        point: [u * (x1 - x2) + x2, u * (y1 - y2) + y2]
    };
}
const intersected = intersect(p1, p2, p3, p4);
console.log(p1, p2, p3, p4, intersected)

function Board({character, enemies, projectiles, explosions, walls}) {

    const now = new Date().getTime();
    return (
        <svg version="1.1"
             baseProfile="full"
             xmlns="http://www.w3.org/2000/svg">
{/*
            <Grid width={2000} height={2000} n={40}/>

            <Line p1={p1} p2={p2} color={"red"}/>
            <Line p1={p3} p2={p4} color={"red"}/>

            {intersected.intersect && <circle cx={intersected.point[0]} cy={intersected.point[1]} r={5} fill={"blue"}/>}
*/}
            <Player character={character} isEnemy={false} color={"blue"}/>

            {enemies.map((item, i) => <Player key={i} character={item} isEnemy={true} color={"red"}/>)}
            {walls.map((wall, i) => <Wall key={i} gameObject={wall}/>)}
            {projectiles.map((projectile, i) => projectile && <Polygon key={i} polygon={projectile.shape} color={"red"}/>)}
            {explosions.map((explosion, i) => <Point key={i} data={explosion.point} radius={(now - explosion.timestamp) / 1000 * 60} color={"yellow"}/>)}

        </svg>
    );
}

const mapStateToProps = state => ({
    character: state.state.character,
    enemies: state.state.enemies,
    projectiles: state.state.projectiles,
    explosions: state.state.explosions,
    walls: state.state.walls
});
const mapDispatchToProps = dispatch => ({
    actions: bindActionCreators(Store, dispatch)
});
export default connect(mapStateToProps, mapDispatchToProps)(Board);
