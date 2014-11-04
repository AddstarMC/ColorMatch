package com.comze_instancelabs.colormatch;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

public class Messages {
	public static String saved_arena = "";
	public static String saved_lobby = "";
	public static String saved_setup = "";
	public static String saved_mainlobby = "";
	public static String not_in_arena = "";
	public static String reloaded = "";
	public static String arena_ingame = "";
	public static String arena_invalid = "";
	public static String arena_invalid_sign = "";
	public static String you_fell = "";
	public static String arena_invalid_component = "";
	public static String you_won = "";
	public static String starting_in = "";
	public static String starting_in2 = "";
	public static String arena_full = "";
	public static String removed_arena = "";
	public static String winner_an = "";

	// anouncements
	public static String starting = "";
	public static String started = "";
	
	public static void loadValues(Configuration config) {
		saved_arena = ct(config.getString("strings.saved.arena"));
		saved_lobby = ct(config.getString("strings.saved.lobby"));
		saved_setup = ct(config.getString("strings.saved.setup"));
		saved_mainlobby = ct("&aSuccessfully saved main lobby");
		not_in_arena = ct(config.getString("strings.not_in_arena"));
		reloaded = ct(config.getString("strings.config_reloaded"));
		arena_ingame = ct(config.getString("strings.arena_is_ingame"));
		arena_invalid = ct(config.getString("strings.arena_invalid"));
		arena_invalid_sign = ct(config.getString("strings.arena_invalid_sign"));
		you_fell = ct(config.getString("strings.you_fell"));
		arena_invalid_component = ct(config.getString("strings.arena_invalid_component"));
		you_won = ct(config.getString("strings.you_won"));
		starting_in = ct(config.getString("strings.starting_in"));
		starting_in2 = ct(config.getString("strings.starting_in2"));
		arena_full = ct(config.getString("strings.arena_full"));
		starting = ct(config.getString("strings.starting_announcement"));
		started = ct(config.getString("strings.started_announcement"));
		removed_arena = ct(config.getString("strings.removed_arena"));
		winner_an = ct(config.getString("strings.winner_announcement"));
	}
	
	private static String ct(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
}
