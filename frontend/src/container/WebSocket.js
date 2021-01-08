import {Client} from "@stomp/stompjs";
import * as SockJS from 'sockjs-client';

import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

import * as Store from "../store/ReduxActions";

const inputState = {
    mousePosX: 0,
    mousePosY: 0,
    isPressedW: false,
    isPressedA: false,
    isPressedS: false,
    isPressedD: false
}

function onKeyPress(e) {
    switch (e.keyCode) {
        case 119:
            inputState.isPressedW = true;
            break;
        case 97:
            inputState.isPressedA = true;
            break;
        case 100:
            inputState.isPressedD = true;
            break;
        case 115:
            inputState.isPressedS = true;
            break;
    }
};

function onKeyUp(e) {

    switch (e.keyCode) {
        case 87:
            inputState.isPressedW = false;
            break;
        case 65:
            inputState.isPressedA = false;
            break;
        case 68:
            inputState.isPressedD = false;
            break;
        case 83:
            inputState.isPressedS = false;
            break;
    }
};

function onMouseUpdate(e) {
    inputState.mousePosX = e.pageX;
    inputState.mousePosY = e.pageY;
}

const ENDPOINT = {
    APP_NAME: "/game",
    TOPIC_PLAYER_LIST: '/backend/player/list',
    TOPIC_PLAYER_UPDATE: '/frontend/update'
};

const webSocket = new Client();

function WebSocket({host, actions}) {

    useEffect(() => {
        webSocket.configure({
            webSocketFactory: () => new SockJS(host + ENDPOINT.APP_NAME),
            onConnect: (frame) => {

                var url = webSocket.webSocket._transport.url;
                url = url.split("/");
                var sessionId = url[url.length - 2];
                webSocket.sessionId = sessionId;

                actions.updateSessionId(sessionId)
                actions.changePlayerName({sessionId: sessionId})

                webSocket.subscribe(ENDPOINT.TOPIC_PLAYER_LIST, message => {
                    const state = JSON.parse(message.body);
                    actions.addSnapshot(state);
                });

                let timerId = setTimeout(function tick() {

                    webSocket.publish({
                        destination: ENDPOINT.TOPIC_PLAYER_UPDATE,
                        body: JSON.stringify(inputState)
                    });
                    timerId = setTimeout(tick, 40); // (*)
                }, 40);
            },
            onWebSocketError: e => console.error("Chat service is unavailable", e),
            // debug: str => console.log(new Date(), str)
        });
        webSocket.activate();

        document.addEventListener('mousemove', onMouseUpdate, false);
        document.addEventListener('mouseenter', onMouseUpdate, false);
        document.addEventListener('keypress', onKeyPress, false);
        document.addEventListener('keyup', onKeyUp, false);
    })

    return (
        <div/>
    );
}

const mapDispatchToProps = dispatch => ({
    actions: bindActionCreators(Store, dispatch)
});
export default connect(null, mapDispatchToProps)(WebSocket);
