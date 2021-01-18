import React from "react";

export default function Polygon({offset = [0, 0], polygon, color, empty = false}) {

    return <polygon points={polygon.points.map(p => (p.d0 - offset[0]) + "," + (p.d1 - offset[1])).join(" ")}
                    fill={empty ?  "none" : color}
                    stroke={color}/>
}
