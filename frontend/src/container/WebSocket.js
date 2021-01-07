import {Client} from "@stomp/stompjs";
import * as SockJS from 'sockjs-client';

import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

import * as Store from "../store/ReduxActions";

var x = null;
var y = null;

function onMouseUpdate(e) {
    x = e.pageX;
    y = e.pageY;
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
                actions.changePlayerName({sessionId:sessionId})

                webSocket.subscribe(ENDPOINT.TOPIC_PLAYER_LIST, message => {
                    const state = JSON.parse(message.body);
                    actions.addSnapshot(state);
                });

                let timerId = setTimeout(function tick() {

                    webSocket.publish({
                        destination: ENDPOINT.TOPIC_PLAYER_UPDATE,
                        body: JSON.stringify({x: x, y: y})
                    });
                    timerId = setTimeout(tick, 100); // (*)
                }, 100);
            },
            onWebSocketError: e => console.error("Chat service is unavailable", e),
            // debug: str => console.log(new Date(), str)
        });
        webSocket.activate();

        document.addEventListener('mousemove', onMouseUpdate, false);
        document.addEventListener('mouseenter', onMouseUpdate, false);
    })

    return (
        <div/>
    );
}

const mapDispatchToProps = dispatch => ({
    actions: bindActionCreators(Store, dispatch)
});
export default connect(null, mapDispatchToProps)(WebSocket);
