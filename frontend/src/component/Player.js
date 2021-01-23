import React from "react";
import Polygon from "./Polygon";

function Ship({isEnemy, color, center, angleDegree}) {
    return (
        <g transform={"rotate(" + (angleDegree + 90) + ", " + (center[0]) + "," + (center[1]) + ")"}>
            <g transform={"translate(" + (center[0] - 51) + "," + (center[1] - 51) + ")"}>

                <g transform={"scale(0.2, 0.2) "}>
                    <rect x="168.229" y="387.657" fill={isEnemy ? color : "#3C5D76"} width="43.886" height="73.143"/>
                    <rect x="299.886" y="387.657" fill={isEnemy ? color : "#1E2E3B"} width="43.886" height="73.143"/>
                    <g>
                        <rect y="124.343" fill={isEnemy ? color : "#3C5D76"} width="43.886" height="336.457"/>
                        <rect x="87.771" y="168.229" fill={isEnemy ? color : "#3C5D76"} width="43.886" height="234.057"/>
                    </g>
                    <polygon fill={isEnemy ? color : "#5A8BB0"}
                             points="0,324.56 0,416.914 263.314,416.914 263.314,289.451 "/>
                    <g>
                        <rect x="468.114" y="124.343" fill={isEnemy ? color : "#1E2E3B"} width="43.886" height="336.457"/>
                        <rect x="380.343" y="168.229" fill={isEnemy ? color : "#1E2E3B"} width="43.886" height="234.057"/>
                    </g>
                    <polygon fill={isEnemy ? color : "#3C5D76"}
                             points="248.686,289.451 248.686,416.914 512,416.914 512,324.56 "/>
                    <circle fill={isEnemy ? color : "#FFDA44"} cx="256" cy="431.543" r="29.257"/>
                    <path fill={isEnemy ? color : "#FF9811"} d="M191.463,373.029v37.786c0,58.668,64.537,93.872,64.537,93.872s64.537-35.204,64.537-93.872v-37.786
H191.463z M256.029,449.916c-8.973-8.631-17.905-20.114-20.145-33.002L256,402.286l20.123,14.629
C273.9,429.837,265.005,441.297,256.029,449.916z"/>
                    <path fill={isEnemy ? color : "#FF5023"} d="M256,373.029v43.886h20.123c-2.224,12.923-11.118,24.383-20.094,33.002
c-0.01-0.009-0.019-0.019-0.029-0.028v54.797c0,0,64.537-35.204,64.537-93.872v-37.786H256z"/>
                    <path fill={isEnemy ? color : "#CCE9F9"} d="M343.771,416.914l-29.257-87.771V92.426C314.514,39.231,256,7.314,256,7.314
s-58.514,31.917-58.514,85.112v236.717l-29.257,87.771H343.771z"/>
                    <path fill={isEnemy ? color : "#93C7EF"}
                          d="M343.771,416.914l-29.257-87.771V92.426C314.514,39.231,256,7.314,256,7.314v409.6H343.771z"/>
                    <rect x="234.057" y="270.629" fill={isEnemy ? color : "#1E2E3B"} width="43.886" height="102.4"/>
                </g>
            </g>
        </g>
    );
}

export default function Player({character, isEnemy, color}) {

    const angleRadian = character.gameObject.rot;
    const angleDegree = angleRadian * 180 / Math.PI + 180;

    const debug = true;

    return (
        <g>
            <Ship color={color} isEnemy={isEnemy} center={character.gameObject.pos} angleDegree={angleDegree}/>

            {debug && <Polygon polygon={character.gameObject.shape} color={color} empty={true}/>}
        </g>
    );
}
