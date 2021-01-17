import React from "react";
import Polygon from "./Polygon";

export default function Wall({gameObject}) {
    const fillColor = "#eee";
    const lineColor = "#ccc";
    const cornerColor = "#ddd";
    return (<g>
        <Polygon polygon={gameObject.rigidBody.transformed} color={fillColor} empty={false}/>
        <Polygon polygon={gameObject.rigidBody.transformed} color={lineColor} empty={true}/>
        {gameObject.rigidBody.transformed.points.map((p, i) => <circle key={i}  cx={p.d0} cy={p.d1} r="3" fill={cornerColor}/>)}
    </g>)
}
