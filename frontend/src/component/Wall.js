import React from "react";
import Polygon from "./Polygon";
import Point from "./Point";

export default function Wall({gameObject}) {
    const fillColor = "#eee";
    const lineColor = "#ccc";
    const cornerColor = "#ddd";
    return (<g>

        <Polygon polygon={gameObject.shape} color={lineColor} empty={true}/>
        <Polygon polygon={gameObject.shape} color={fillColor} empty={false}/>
        {gameObject.shape.map((p, i) => <Point key={i} data={p} radius={3} color={cornerColor}/>)}
    </g>)
}
