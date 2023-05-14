/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby;

import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Websocket. Usage:
 *
 * <pre>{@code
 * ws("/pattern", (ctx, configurer) -> {
 *   configurer.onConnect(ws -> {
 *     // Connect callback
 *   }):
 *
 *   configurer.onMessage((ws, message) -> {
 *     ws.send("Got: " + message.value());
 *   });
 *
 *   configurer.onClose((ws, closeStatus) -> {
 *     // Closing websocket
 *   });
 *
 *   configurer.onError((ws, cause) -> {
 *
 *   });
 * });
 *
 * }</pre>
 *
 * @author edgar
 * @since 2.2.0
 */
public interface WebSocket {
  /**
   * Websocket initializer. Give you access to a read-only {@link Context} you are free to access to
   * request attributes, while attempt to modify a response results in exception.
   */
  interface Initializer {
    /**
     * Callback with a readonly context and websocket configurer.
     *
     * @param ctx Readonly context.
     * @param configurer WebSocket configurer.
     */
    void init(@NonNull Context ctx, @NonNull WebSocketConfigurer configurer);
  }

  /** On connect callback. */
  interface OnConnect {
    /**
     * On connect callback with recently created web socket.
     *
     * @param ws WebSocket.
     */
    void onConnect(@NonNull WebSocket ws);
  }

  /**
   * On message callback. When a Message is send by a client, this callback allow you to
   * handle/react to it.
   */
  interface OnMessage {
    /**
     * Generated when a client send a message.
     *
     * @param ws WebSocket.
     * @param message Client message.
     */
    void onMessage(@NonNull WebSocket ws, @NonNull WebSocketMessage message);
  }

  /**
   * On close callback. Generated when client close the connection or when explicit calls to {@link
   * #close(WebSocketCloseStatus)}.
   */
  interface OnClose {
    /**
     * Generated when client close the connection or when explicit calls to {@link
     * #close(WebSocketCloseStatus)}.
     *
     * @param ws WebSocket.
     * @param closeStatus Close status.
     */
    void onClose(@NonNull WebSocket ws, @NonNull WebSocketCloseStatus closeStatus);
  }

  /** On error callback. Generated when unexpected error occurs. */
  interface OnError {
    /**
     * Error callback, let you listen for exception. Websocket might or might not be open.
     *
     * @param ws Websocket.
     * @param cause Cause.
     */
    void onError(@NonNull WebSocket ws, @NonNull Throwable cause);
  }

  /** Max message size for websocket (128K). */
  int MAX_BUFFER_SIZE = 131072;

  /**
   * Originating HTTP context. Please note this is a read-only context, so you are not allowed to
   * modify or produces a response from it.
   *
   * <p>The context let give you access to originating request (then one that was upgrade it).
   *
   * @return Read-only originating HTTP request.
   */
  @NonNull Context getContext();

  /**
   * Context attributes (a.k.a request attributes).
   *
   * @return Context attributes.
   */
  default @NonNull Map<String, Object> getAttributes() {
    return getContext().getAttributes();
  }

  /**
   * Get an attribute by his key. This is just an utility method around {@link #getAttributes()}.
   * This method look first in current context and fallback to application attributes.
   *
   * @param key Attribute key.
   * @param <T> Attribute type.
   * @return Attribute value.
   */
  default @NonNull <T> T attribute(@NonNull String key) {
    return getContext().getAttribute(key);
  }

  /**
   * Set an application attribute.
   *
   * @param key Attribute key.
   * @param value Attribute value.
   * @return This router.
   */
  default @NonNull WebSocket attribute(@NonNull String key, Object value) {
    getContext().setAttribute(key, value);
    return this;
  }

  /**
   * Web sockets connected to the same path. This method doesn't include the current websocket.
   *
   * @return Web sockets or empty list.
   */
  @NonNull List<WebSocket> getSessions();

  /**
   * True if websocket is open.
   *
   * @return True when open.
   */
  boolean isOpen();

  /**
   * Send a text message to client.
   *
   * @param message Text Message.
   * @return This websocket.
   */
  default @NonNull WebSocket send(@NonNull String message) {
    return send(message, false);
  }

  /**
   * Send a text message to current client (broadcast = false) or to ALL connected clients under the
   * websocket path (broadcast = true).
   *
   * @param message Text Message.
   * @param broadcast True to send to all connected clients.
   * @return This websocket.
   */
  @NonNull WebSocket send(@NonNull String message, boolean broadcast);

  /**
   * Send a text message to client.
   *
   * @param message Text Message.
   * @return This websocket.
   */
  default @NonNull WebSocket send(@NonNull byte[] message) {
    return send(message, false);
  }

  /**
   * Send a text message to current client (broadcast = false) or to ALL connected clients under the
   * websocket path (broadcast = true).
   *
   * @param message Text Message.
   * @param broadcast True to send to all connected clients.
   * @return This websocket.
   */
  @NonNull WebSocket send(@NonNull byte[] message, boolean broadcast);

  /**
   * Send a binary message to client.
   *
   * @param message Binary Message.
   * @return This websocket.
   */
  default @NonNull WebSocket sendBinary(@NonNull String message) {
    return sendBinary(message, false);
  }

  /**
   * Send a binary message to current client (broadcast = false) or to ALL connected clients under
   * the websocket path (broadcast = true).
   *
   * @param message Binary Message.
   * @param broadcast True to send to all connected clients.
   * @return This websocket.
   */
  @NonNull WebSocket sendBinary(@NonNull String message, boolean broadcast);

  /**
   * Send a binary message to client.
   *
   * @param message Binary Message.
   * @return This websocket.
   */
  default @NonNull WebSocket sendBinary(@NonNull byte[] message) {
    return sendBinary(message, false);
  }

  /**
   * Send a binary message to current client (broadcast = false) or to ALL connected clients under
   * the websocket path (broadcast = true).
   *
   * @param message Binary Message.
   * @param broadcast True to send to all connected clients.
   * @return This websocket.
   */
  @NonNull WebSocket sendBinary(@NonNull byte[] message, boolean broadcast);

  /**
   * Encode a value and send a text message to client.
   *
   * @param value Value to send.
   * @return This websocket.
   */
  default @NonNull WebSocket render(@NonNull Object value) {
    return render(value, false);
  }

  /**
   * Encode a value and send a text message to current client (broadcast = false) or to ALL
   * connected clients under the websocket path (broadcast = true).
   *
   * @param value Value to send.
   * @param broadcast True to send to all connected clients.
   * @return This websocket.
   */
  @NonNull WebSocket render(@NonNull Object value, boolean broadcast);

  /**
   * Encode a value and send a binary message to client.
   *
   * @param value Value to send.
   * @return This websocket.
   */
  default @NonNull WebSocket renderBinary(@NonNull Object value) {
    return renderBinary(value, false);
  }

  /**
   * Encode a value and send a binary message to current client (broadcast = false) or to ALL
   * connected clients under the websocket path (broadcast = true).
   *
   * @param value Value to send.
   * @param broadcast True to send to all connected clients.
   * @return This websocket.
   */
  @NonNull WebSocket renderBinary(@NonNull Object value, boolean broadcast);

  /**
   * Close the web socket and send a {@link WebSocketCloseStatus#NORMAL} code to client.
   *
   * <p>This method fires a {@link OnClose#onClose(WebSocket, WebSocketCloseStatus)} callback.
   *
   * @return This websocket.
   */
  default @NonNull WebSocket close() {
    return close(WebSocketCloseStatus.NORMAL);
  }

  /**
   * Close the web socket and send a close status code to client.
   *
   * <p>This method fires a {@link OnClose#onClose(WebSocket, WebSocketCloseStatus)} callback.
   *
   * @param closeStatus Close status.
   * @return This websocket.
   */
  @NonNull WebSocket close(@NonNull WebSocketCloseStatus closeStatus);
}
