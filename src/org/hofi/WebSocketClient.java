package org.hofi;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;


class WebSocketClient {
  private WebSocketSession webSocketSession;

  public static void main(String[] args) throws InterruptedException, ExecutionException, URISyntaxException, IOException {
    new WebSocketClient().connect();
  }

  private WebSocketClient() {

  }

  private void connect() throws URISyntaxException, ExecutionException, InterruptedException, IOException {
    URI endpointUrl = new URI("ws://10.2.2.14:5120/endpoint");
    WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    AbstractWebSocketHandler result = new AbstractWebSocketHandler() {
      protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println("Received Payload: " + message.getPayload());
        webSocketSession.close();
      }
    };

    StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
    webSocketSession = webSocketClient
      .doHandshake(result, headers, endpointUrl).get();

    TextMessage textMessage = new TextMessage("Hello World");

    webSocketSession.sendMessage(textMessage);

    while(webSocketSession.isOpen())
    {
      // wait
    }
  }

}