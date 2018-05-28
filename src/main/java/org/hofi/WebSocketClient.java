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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class WebSocketClient {
  private final Lock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();

  public static void main(String[] args) throws InterruptedException, ExecutionException, URISyntaxException, IOException {
    new WebSocketClient().connect();
  }

  private void connect() throws URISyntaxException, ExecutionException, InterruptedException, IOException {
    lock.lock();
    try {
      URI endpointUrl = new URI("ws://localhost:8080/endpoint");
      WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
      AbstractWebSocketHandler result = new AbstractWebSocketHandler() {
        protected void handleTextMessage(WebSocketSession session, TextMessage message) {
          System.out.println("Received Payload: " + message.getPayload());
          lock.lock();
          try {
            condition.signal();
          }
          finally {
            lock.unlock();
          }
        }
      };

      StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
      WebSocketSession webSocketSession = webSocketClient
        .doHandshake(result, headers, endpointUrl).get();

      TextMessage textMessage = new TextMessage("Hello World");

      webSocketSession.sendMessage(textMessage);

      condition.await();

      webSocketSession.close();
    }
    finally {
      lock.unlock();
    }
  }
}