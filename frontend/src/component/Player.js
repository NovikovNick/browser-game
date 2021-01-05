import React from "react";

export default function Player({x, y, name}) {
    return (
        <g>
            <circle cx={x} cy={y} r="5" fill="red"/>
            <text x={x + 25}
                  y={y - 10}
                  fontSize="16"
                  textAnchor="middle"
                  fill="black">
                {name}
            </text>
        </g>
    );
}
