import React from "react";
import Polygon from "./Polygon";
import Point from "./Point";

export default function Wall({offset = [0, 0], gameObject}) {
    const fillColor = "#eee";
    const lineColor = "#ccc";
    const cornerColor = "#ddd";
    return (<g>

        <Polygon offset={offset} polygon={gameObject.rigidBody.transformed} color={lineColor} empty={true}/>
        <Polygon offset={offset} polygon={gameObject.rigidBody.transformed} color={fillColor} empty={false}/>
        {gameObject.rigidBody.transformed.points.map((p, i) =>
            <Point offset={offset} key={i}  data={[p.d0, p.d1]} radius={3} color={cornerColor}/>)}
    </g>)
}
