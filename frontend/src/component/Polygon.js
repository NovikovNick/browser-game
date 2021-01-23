import React from "react";

export default function Polygon({polygon, color, empty = false}) {
    const points = Array.isArray(polygon) ? polygon.map(p => p[0] + "," + p[1]).join(" ") : "";
    return <polygon points={points} fill={empty ? "none" : color} stroke={color}/>
}
