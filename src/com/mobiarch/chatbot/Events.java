package com.mobiarch.chatbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class Events implements Listener {

	private Plugin plugin;

	public Events(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		plugin.getServer()
				.getScheduler()
				.scheduleAsyncDelayedTask(plugin,
						new MessagePoster(player, plugin, event.getMessage()),
						10);
	}
}

class MessagePoster implements Runnable {
	Player player;
	Plugin plugin;
	String inputMsg, outputMsg;

	public MessagePoster(Player p, Plugin pl, String in) {
		player = p;
		plugin = pl;
		inputMsg = in;
	}

	String getStringDelimited(String source, String start, String end) {
		int startIdx = source.indexOf(start);
		int endIdx = source.indexOf(end, startIdx + start.length());
		if (startIdx < 0 || endIdx < 0) {
			return null;
		}
		return source.substring(startIdx + start.length(), endIdx);
	}

	public void run() {
		try {
			String botid = plugin.getConfig().getString("botid");// "b0dafd24ee35a477";
															// //"ae8206713e34cb2e";
			String metadataKey = "Chatbot.custid";
			String urlStr = "http://www.pandorabots.com/pandora/talk-xml";
			String data = "botid=" + URLEncoder.encode(botid, "UTF-8")
					+ "&input=" + URLEncoder.encode(inputMsg, "UTF-8");

			if (player.hasMetadata(metadataKey)) {
				String custId = player.getMetadata("Chatbot.custid").get(0).asString();
				data += "&custid="
						+ URLEncoder.encode(custId, "UTF-8");
				plugin.log("Using custid: " + custId);
			}

			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				plugin.log(line);
				outputMsg = getStringDelimited(line, "<that>", "</that>");
				if (outputMsg == null) {
					break;
				}
				String custId = getStringDelimited(line, "custid=\"",
						"\"");
				player.setMetadata(metadataKey, new FixedMetadataValue(plugin, custId));
				outputMsg = plugin.formatMessage(outputMsg);
				player.sendMessage(outputMsg);
				break;
			}
			wr.close();
			rd.close();
		} catch (Exception e) {
			plugin.log("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
