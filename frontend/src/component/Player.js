import React from "react";

export default function Player({x, y, name, color}) {
    return (
        <g transform={"translate("+ (x-9) + ","+  (y-4) + ")"}>
            <text x={25}
                  y={-10}
                  fontSize="16"
                  fill={color}>
                {name}
            </text>
            <path fill={color}
                  d="M7,2l12,11.2l-5.8,0.5l3.3,7.3l-2.2,1l-3.2-7.4L7,18.5V2"/>
        </g>
    );
}
