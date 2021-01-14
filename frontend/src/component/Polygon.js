import React from "react";

export default function Polygon({polygon, color, empty = false}) {
    return <polygon points={polygon.points.map(p => p.d0 + "," + p.d1).join(" ")}
                    fill={empty ?  "none" : color}
                    stroke={color}/>
}
