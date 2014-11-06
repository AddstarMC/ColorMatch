package com.comze_instancelabs.colormatch;

import java.io.IOException;

import net.milkbowl.vault.economy.Economy;
import au.com.addstar.signmaker.*;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.tool.ToolModes;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.colormatch.Util.Metrics;
import com.comze_instancelabs.colormatch.patterns.PatternRegistry;

public class Main extends JavaPlugin implements Listener {

	public static final DyeColor[] colors = new DyeColor[] {DyeColor.BLUE, DyeColor.RED, DyeColor.CYAN, DyeColor.BLACK, DyeColor.GREEN, DyeColor.YELLOW, DyeColor.ORANGE, DyeColor.PURPLE};
	public static Economy econ = null;
	public static SignMakerPlugin signmaker = null;
	public static Main plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		getServer().getPluginManager().registerEvents(this, this);

		Minigames.plugin.mdata.addModule(ColorMatchModule.class);
		GameMechanics.addGameMechanic(new ColorMatchMechanic());
		ToolModes.addToolMode(new PatternSelectionTool());
		
		PatternRegistry.loadSaved();
		
		loadDefaults();
		saveConfig();

		getConfigVars();

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
		}

		if (setupSignMaker()) {
			getLogger().info(String.format("[%s] SuperSigns plugin found - integration is enabled", getDescription().getName()));
		} else {
			getLogger().warning(String.format("[%s] SuperSigns plugin was not found - integration is disabled!", getDescription().getName()));
		}
	}
	
	private void loadDefaults() {
		getConfig().addDefault("strings.saved.arena", ct("&aSuccessfully saved arena."));
		getConfig().addDefault("strings.saved.lobby", ct("&aSuccessfully saved lobby."));
		getConfig().addDefault("strings.saved.setup", ct("&6Successfully saved spawn. Now setting up, might &2lag&6 a little bit."));
		getConfig().addDefault("strings.removed_arena", ct("&cSuccessfully removed arena."));
		getConfig().addDefault("strings.not_in_arena", ct("&cYou don't seem to be in an arena right now."));
		getConfig().addDefault("strings.config_reloaded", ct("&6Successfully reloaded config."));
		getConfig().addDefault("strings.arena_is_ingame", ct("&cThe arena appears to be ingame."));
		getConfig().addDefault("strings.arena_invalid", ct("&cThe arena appears to be invalid."));
		getConfig().addDefault("strings.arena_invalid_sign", ct("&cThe arena appears to be invalid, because a join sign is missing."));
		getConfig().addDefault("strings.arena_invalid_component", ct("&2The arena appears to be invalid (missing components or misstyped arena)!"));
		getConfig().addDefault("strings.you_fell", ct("&3You fell! Type &6/cm leave &3to leave."));
		getConfig().addDefault("strings.you_won", ct("&aYou won this round, awesome man! Here, enjoy your reward."));
		getConfig().addDefault("strings.starting_in", ct("&aStarting in &6"));
		getConfig().addDefault("strings.starting_in2", ct("&a seconds."));
		getConfig().addDefault("strings.arena_full", ct("&cThis arena is full!"));
		getConfig().addDefault("strings.starting_announcement", ct("&aStarting a new ColorMatch Game in &6"));
		getConfig().addDefault("strings.started_announcement", ct("&aA new ColorMatch Round has started!"));
		getConfig().addDefault("strings.winner_announcement", ct("&6<player> &awon the game on arena &6<arena>!"));
		
		getConfig().options().copyDefaults(true);
	}

	@Override
	public void onDisable() {
		Minigames.plugin.mdata.removeModule("ColorMatch", ColorMatchModule.class);
		GameMechanics.removeGameMechanic("colormatch");
		ToolModes.removeToolMode("CMPATTERN");
	}

	private boolean setupSignMaker() {
		signmaker = (SignMakerPlugin) getServer().getPluginManager().getPlugin("SuperSigns");
		return signmaker != null;
	}

	public void getConfigVars() {
		Messages.loadValues(getConfig());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("cm") || cmd.getName().equalsIgnoreCase("colormatch")) {
			if (args.length > 0) {
				String action = args[0];
				if (action.equalsIgnoreCase("setup")) {
					if (args.length > 1) {
						if (sender.hasPermission("colormatch.setup")) {
							Minigame minigame = Minigames.plugin.mdata.getMinigame(args[1]);
							if (minigame == null) {
								sender.sendMessage(ChatColor.RED + "Unknown minigame");
								return true;
							}
							
							if (!minigame.getMechanicName().equals("colormatch")) {
								sender.sendMessage(ChatColor.RED + "That minigame is not a ColorMatch minigame, please change its mechanic");
								return true;
							}
							
							GameBoard board = new GameBoard(this);
							ColorMatchModule.getModule(minigame).setGame(board);
							
							board.setSpawn(((Player)sender).getLocation());
							board.generate();
							
							sender.sendMessage(ChatColor.GREEN + "The board has been setup.");
						}
					}
				} else {
					sender.sendMessage(ct("&6-= ColorMatch &2help: &6=-"));
					sender.sendMessage(ct("&2/cm setup <minigame>"));
				}
			} else {
				sender.sendMessage(ct("&6-= ColorMatch &2help: &6=-"));
				sender.sendMessage(ct("&2/cm setup <minigame>"));
			}
			return true;
		}
		return false;
	}

	public String ct(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
}
