import React, {useEffect, useRef} from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

import * as Store from "../store/ReduxActions";

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
// console.log(p1, p2, p3, p4, intersected)

let sequence = 0;
let frame = 0;

const environment = {
    ground: new Image(),
    isLoading: true
}
environment.ground.src = "/images/environment.png";
environment.ground.onload = () => {
    environment.isLoading = false;
}

const animation = {
    characterWalkLeft : {
        sprites: new Image(),
        frameWidth: 108,
        frameHeight: 140,
        coords: [
            [0, 0],
            [108, 0],
            [108*2, 0],
            [108*3, 0],
            [108*4, 0],
            [108*5, 0],
            [108*6, 0],
            [108*7, 0]
        ],
        isLoading: true
    }
};
animation.characterWalkLeft.sprites.src = "/images/sprite.png";
animation.characterWalkLeft.sprites.onload = () => {
    animation.characterWalkLeft.isLoading = false;
}

function isLoading () {
    return environment.isLoading && animation.characterWalkLeft.isLoading;
};

function Board({character, enemies, projectiles, explosions, walls}) {

    const canvas = useRef(null);

    useEffect(() => {

        const ctx = canvas.current.getContext('2d');
        ctx.font = "24px serif";

        sequence++;
        frame = sequence % 60;

        ctx.fillStyle = 'black';
        ctx.fillRect(0, 0, 2000, 1000)

        // console.log(frame + " - " + Math.ceil(frame / 14))

        ctx.fillStyle = 'white';
        ctx.fillText("Frame number: " + frame, 10, 30);
        ctx.fillText("Ping: " + 0, 10, 60);

        if(isLoading()) {
            ctx.fillText("connecting...", 10, 60);
        } else {

            const coord = animation.characterWalkLeft.coords[Math.ceil(frame / 9)]

            { // player
                const pos = character.gameObject.pos;
                ctx.fillStyle = '#e6f7ff';
                ctx.fillRect(pos[0]-400, pos[1]-400, 800, 800);

                for (let i = 0; i < character.gameObject.shape.length; i++) {
                    const p = character.gameObject.shape[i];

                    ctx.drawImage(
                        animation.characterWalkLeft.sprites,
                        coord[0], coord[1],
                        108, 140,
                        pos[0] - 108/2, pos[1] - 140/2, 108, 140
                    );
                    ctx.fillStyle = 'blue';
                    ctx.fillRect(p[0], p[1], 5, 5);
                }
            }

            {// enemies
                for (let i = 0; i < enemies.length; i++) {
                    const enemy = enemies[i];
                    const pos = enemy.pos;

                    for (let j = 0; j < enemy.shape.length; j++) {
                        const p = enemy.shape[j];

                        ctx.drawImage(
                            animation.characterWalkLeft.sprites,
                            coord[0], coord[1],
                            108, 140,
                            pos[0] - 108/2, pos[1] - 140/2, 108, 140
                        );
                        ctx.fillStyle = 'blue';
                        ctx.fillRect(p[0], p[1], 5, 5);
                    }
                }
            }

            {// wall
                for (let i = 0; i < walls.length; i++) {
                    const pos = walls[i].pos;
                    const wall = walls[i].shape;

                    ctx.drawImage(
                        environment.ground,
                        184, 102,
                        76, 76,
                        pos[0] - 51, pos[1] - 49, 100, 100
                    );

                    for (let j = 0; j < wall.length; j++) {
                        const p = wall[j];
                        ctx.fillStyle = 'white';
                        ctx.fillRect(p[0], p[1], 5, 5);
                    }
                }
            }
        }
    });

    return ( <canvas ref={canvas} width={2000} height={1000}/>);
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
