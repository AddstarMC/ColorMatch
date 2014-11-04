package com.comze_instancelabs.colormatch;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenaSettings {
	private ConfigurationSection config;
	private JavaPlugin plugin;
	
	public ArenaSettings(JavaPlugin plugin, ConfigurationSection config) {
		this.plugin = plugin;
		this.config = config;
	}
	
	public int getArenaDifficulty() {
		if (!config.isSet("difficulty")) {
			setArenaDifficulty(1);
		}
		return config.getInt("difficulty");
	}

	public void setArenaDifficulty(int difficulty) {
		config.set("difficulty", difficulty);
		plugin.saveConfig();
	}

	public int getArenaMaxPlayers() {
		if (!config.isSet("max_players")) {
			setArenaMaxPlayers(4);
		}
		return config.getInt("max_players");
	}

	public void setArenaMaxPlayers(int players) {
		config.set("max_players", players);
		plugin.saveConfig();
	}

	public int getArenaMinPlayers() {
		if (!config.isSet("min_players")) {
			setArenaMinPlayers(3);
		}
		return config.getInt("min_players");
	}

	public void setArenaMinPlayers(int players) {
		config.set("min_players", players);
		plugin.saveConfig();
	}

	public int getArenaSpectateHeight() {
		if (!config.isSet("spectate_height")) {
			setArenaSpectateHeight(20);
		}
		return config.getInt("spectate_height");
	}

	public void setArenaSpectateHeight(int height) {
		config.set("spectate_height", height);
		plugin.saveConfig();
	}

	public int getArenaFloorDepth() {
		if (!config.isSet("floor_depth")) {
			setArenaFloorDepth(20);
		}
		return config.getInt("floor_depth");
	}

	public void setArenaFloorDepth(int depth) {
		config.set("floor_depth", depth);
		plugin.saveConfig();
	}

	public int getArenaFallDepth() {
		if (!config.isSet("fall_depth")) {
			setArenaFallDepth(5);
		}
		return config.getInt("fall_depth");
	}

	public void setArenaFallDepth(int depth) {
		config.set("fall_depth", depth);
		plugin.saveConfig();
	}

	public int getArenaSuperSigns() {
		if (!config.isSet("supersigns")) {
			setArenaSuperSigns(0);
		}
		return config.getInt("supersigns");
	}

	public void setArenaSuperSigns(int signs) {
		config.set("supersigns", signs);
		plugin.saveConfig();
	}

	public String getArenaIdleMessage() {
		if (!config.isSet("idle_message")) {
			setArenaIdleMessage("Game Over");
		}
		return config.getString("idle_message");
	}

	public void setArenaIdleMessage(String msg) {
		config.set("idle_message", msg);
		plugin.saveConfig();
	}

	public boolean isNumeric(String s) {
		return s.matches("[-+]?\\d*\\.?\\d+");
	}

	public boolean isArenax32() {
		if (config.isSet("x32")) {
			return config.getBoolean("x32");
		}
		return false;
	}

	public void setArenax32() {
		config.set("x32", true);
		plugin.saveConfig();
	}

	public boolean isArenaGlassMode() {
		if (config.isSet("glassmode")) {
			return config.getBoolean("glassmode");
		}
		return false;
	}

	public void setArenaGlassMode(boolean f) {
		if (f) {
			config.set("glassmode", true);
		} else {
			config.set("glassmode", null);
		}
		plugin.saveConfig();
	}

	public boolean isArenaClayMode() {
		if (config.isSet("claymode")) {
			return config.getBoolean("claymode");
		}
		return false;
	}

	public void setArenaClayMode(boolean f) {
		if (f) {
			config.set("claymode", true);
		} else {
			config.set("claymode", null);
		}
		plugin.saveConfig();
	}

	public boolean isArenax32ClayMode() {
		if (config.isSet("x32claymode")) {
			return config.getBoolean("x32claymode");
		}
		return false;
	}

	public void setArenax32ClayMode(boolean f) {
		if (f) {
			config.set("x32claymode", true);
		} else {
			config.set("x32claymode", null);
		}
		plugin.saveConfig();
	}

	public boolean isArenax32GlassMode(String arena) {
		if (config.isSet("x32glassmode")) {
			return config.getBoolean("x32glassmode");
		}
		return false;
	}

	public void setArenax32GlassMode(String arena, boolean f) {
		if (f) {
			config.set("x32glassmode", true);
		} else {
			config.set("x32glassmode", null);
		}
		plugin.saveConfig();
	}
}
