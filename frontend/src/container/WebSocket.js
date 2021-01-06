import {Client} from "@stomp/stompjs";
import * as SockJS from 'sockjs-client';

import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

import * as Store from "../store/ReduxActions";

export function restart() {

    webSocket.deactivate();
    webSocket.activate();
}

var x = null;
var y = null;

function onMouseUpdate(e) {

    x = e.pageX;
    y = e.pageY;
}

export function sendMessage() {

    webSocket.publish({
        destination: ENDPOINT.TOPIC_PLAYER_UPDATE,
        body: JSON.stringify({x: x, y: y})
    });
}


const ENDPOINT = {
    HOST: "http://192.168.0.103:8080",
    APP_NAME: "/game",
    TOPIC_PLAYER_LIST: '/backend/player/list',
    TOPIC_PLAYER_UPDATE: '/frontend/update'
};

const webSocket = new Client();

function WebSocket({snapshots, actions}) {

    useEffect(() => {
        webSocket.configure({
            webSocketFactory: function () {
                return new SockJS(ENDPOINT.HOST + ENDPOINT.APP_NAME);
            },
            onConnect: (frame) => {

                var url = webSocket.webSocket._transport.url;
                url = url.split("/");
                var sessionId = url[url.length - 2];
                webSocket.sessionId = sessionId;

                webSocket.subscribe(ENDPOINT.TOPIC_PLAYER_LIST, message => {
                    const state = JSON.parse(message.body);
                    actions.addSnapshot(state);
                });
            },
            onWebSocketError: (e) => {
                console.error("Chat service is unavailable");
            },
            // debug: str => console.log(new Date(), str)
        });

        document.addEventListener('mousemove', onMouseUpdate, false);
        document.addEventListener('mouseenter', onMouseUpdate, false);
    })

    return (
        <div className={"controls"}>
            <button onClick={() => {
                restart()
            }}>
                Connect
            </button>

            <button onClick={() => {
                let timerId = setTimeout(function tick() {
                    sendMessage()
                    timerId = setTimeout(tick, 100); // (*)
                }, 100);
            }}>
                Send position
            </button>
        </div>
    );
}

const mapStateToProps = state => ({
    snapshots: state.state.snapshots
});
const mapDispatchToProps = dispatch => ({
    actions: bindActionCreators(Store, dispatch)
});
export default connect(mapStateToProps, mapDispatchToProps)(WebSocket);
