package com.shazam.hackday;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.pubsub.RedisPubSubConnection;
import com.lambdaworks.redis.pubsub.RedisPubSubListener;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.platform.Verticle;

/**
 * Created by colin on 10/03/2014.
 */
public class WebSocketVerticle extends Verticle {
	public void start() {
		HttpServer server = vertx.createHttpServer();

		server.websocketHandler(new Handler<ServerWebSocket>() {
			public void handle(final ServerWebSocket ws) {
				final Handler<Message<String>> tagHandler = new Handler<Message<String>>() {
					@Override
					public void handle(Message<String> event) {
						ws.writeTextFrame(event.body());
					}
				};
				ws.closeHandler(new Handler<Void>() {
					@Override
					public void handle(Void event) {
						System.out.println("Socket closed: removing handler " + tagHandler);
						vertx.eventBus().unregisterHandler("tags", tagHandler);
					}
				});
				vertx.eventBus().registerHandler("tags", tagHandler);
				System.out.println("Socket connected registered handler: " + tagHandler);
			}
		}).listen(7380);

		System.out.println("Subscribing to Redis");
		RedisClient client = new RedisClient("colin.dev.shazamteam.net", 6370);
		RedisPubSubConnection<String, String> connection = client.connectPubSub();
		connection.addListener(new RedisPubSubListener<String, String>() {
			@Override
			public void message(String channel, String message) {
				vertx.eventBus().publish("tags", message);
			}

			@Override
			public void message(String pattern, String channel, String message) { }
			@Override
			public void subscribed(String channel, long count) { }
			@Override
			public void psubscribed(String pattern, long count) { }
			@Override
			public void unsubscribed(String channel, long count) { }
			@Override
			public void punsubscribed(String pattern, long count) { }
		});

		connection.subscribe("tags");
	}
}
