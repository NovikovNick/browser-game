import React from "react";

export default function Point({data, offset = [0, 0], radius = 5, color = "black"}) {
    return (<circle cx={(data[0] - offset[0])} cy={(data[1] - offset[1])} r={radius} fill={color}/>)
}
