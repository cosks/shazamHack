package com.shazam.tagstream;

import java.io.IOException;
import java.util.Locale;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class TagStreamer {

	public static final class JedisPubSubAdapter extends JedisPubSub {
		private final ObjectMapper objectMapper = new ObjectMapper();

		@Override public void onUnsubscribe(String channel, int subscribedChannels) {}
		@Override public void onSubscribe(String channel, int subscribedChannels) {}
		@Override public void onPUnsubscribe(String pattern, int subscribedChannels) {}
		@Override public void onPSubscribe(String pattern, int subscribedChannels) {}
		@Override public void onPMessage(String pattern, String channel, String message) {}

		@Override public void onMessage(String channel, String message) {
			try {
				JsonNode jsonNode = objectMapper.readTree(message);

				String trackTitle = jsonNode.get("match").get("track").get("metadata").get("trackTitle").getTextValue();
				String artist = jsonNode.get("match").get("track").get("metadata").get("artistName").getTextValue();

				System.out.println(String.format("Tagged '%s' by %s", trackTitle, artist));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		Jedis jedis = new Jedis("colin.dev.shazamteam.net", 6370);
		
		JedisPubSub jedisPubSub = new JedisPubSubAdapter();
		jedis.subscribe(jedisPubSub, "tags");
	}
}
