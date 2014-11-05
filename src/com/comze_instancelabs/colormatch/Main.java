package com.comze_instancelabs.colormatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import au.com.addstar.signmaker.*;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.comze_instancelabs.colormatch.Util.Metrics;
import com.comze_instancelabs.colormatch.Util.Updater;
import com.comze_instancelabs.colormatch.modes.ColorMatchClayMode;
import com.comze_instancelabs.colormatch.modes.ColorMatchGlassMode;
import com.comze_instancelabs.colormatch.modes.ColorMatchx32;
import com.comze_instancelabs.colormatch.modes.ColorMatchx32Clay;
import com.comze_instancelabs.colormatch.modes.ColorMatchx32Glass;
import com.google.common.collect.Maps;

public class Main extends JavaPlugin implements Listener {

	/*
	 * 
	 * SETUP
	 * 
	 * cm setmainlobby
	 * 
	 * for each new arena:
	 * 
	 * cm createarena arena cm setlobby arena cm setup arena
	 */

	public static Economy econ = null;
	public static SignMakerPlugin signmaker = null;
	
	private HashMap<String, GameBoard> games = new HashMap<String, GameBoard>();

	public static HashMap<String, Boolean> ingame = new HashMap<String, Boolean>(); // arena -> whether arena is ingame or not
	public static HashMap<String, BukkitTask> tasks = new HashMap<String, BukkitTask>(); // arena -> task/ task
	public static HashMap<Player, String> arenap = new HashMap<Player, String>(); // player -> arena
	public static HashMap<String, String> arenap_ = new HashMap<String, String>(); // player -> arena
	public static HashMap<Player, ItemStack[]> pinv = new HashMap<Player, ItemStack[]>(); // player -> inventory
	public static HashMap<Player, GameMode> pgamemode = new HashMap<Player, GameMode>(); // player -> gamemode
	public static HashMap<Player, String> lost = new HashMap<Player, String>(); // player -> whether lost or not
	public static HashMap<Player, Integer> xpsecp = new HashMap<Player, Integer>();
	public static HashMap<String, Integer> a_round = new HashMap<String, Integer>();
	public static HashMap<String, Integer> a_n = new HashMap<String, Integer>();
	public static HashMap<String, Integer> a_currentw = new HashMap<String, Integer>();
	public static HashMap<String, AClass> pclass = new HashMap<String, AClass>(); // player -> class
	public static HashMap<String, AClass> aclasses = new HashMap<String, AClass>(); // classname -> class
	//public static HashMap<String, Boolean> pseenfall = new HashMap<String, Boolean>();  // REMOVED: Why does this exist?

	int rounds_per_game = 10;
	// int minplayers = 4;
	int default_max_players = 4;
	int default_min_players = 3;

	boolean economy = true;
	int reward = 30;
	int itemid = 264;
	int itemamount = 1;
	boolean command_reward = false;
	String cmd = "";
	boolean start_announcement = false;
	boolean winner_announcement = false;
	boolean bling_sounds = false;

	int start_countdown = 5;

	public String saved_arena = "";
	public String saved_lobby = "";
	public String saved_setup = "";
	public String saved_mainlobby = "";
	public String not_in_arena = "";
	public String reloaded = "";
	public String arena_ingame = "";
	public String arena_invalid = "";
	public String arena_invalid_sign = "";
	public String you_fell = "";
	public String arena_invalid_component = "";
	public String you_won = "";
	public String starting_in = "";
	public String starting_in2 = "";
	public String arena_full = "";
	public String removed_arena = "";
	public String winner_an = "";

	// anouncements
	public String starting = "";
	public String started = "";

	public ColorMatchx32Clay cmx32clay;
	public ColorMatchx32 cmx32;
	public ColorMatchGlassMode cmglass;
	public ColorMatchClayMode cmclay;
	public ColorMatchx32Glass cmx32glass;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		Minigames.plugin.mdata.addModule(ColorMatchModule.class);
		GameMechanics.addGameMechanic(new ColorMatchMechanic());
		
		loadDefaults();

		if (getConfig().isSet("config.min_players")) {
			getConfig().set("config.min_players", null);
		}
		this.saveConfig();

		getConfigVars();

		cmx32clay = new ColorMatchx32Clay(this);
		cmx32 = new ColorMatchx32(this);
		cmglass = new ColorMatchGlassMode(this);
		cmclay = new ColorMatchClayMode(this);
		cmx32glass = new ColorMatchx32Glass(this);

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
		}

		if (getConfig().getBoolean("config.auto_updating")) {
			Updater updater = new Updater(this, 71774, this.getFile(), Updater.UpdateType.DEFAULT, false);
		}

		if (economy) {
			if (!setupEconomy()) {
				getLogger().severe(String.format("[%s] - No iConomy dependency found! Disabling Economy.", getDescription().getName()));
				economy = false;
			}
		}
		
		if (setupSignMaker()) {
			getLogger().info(String.format("[%s] SuperSigns plugin found - integration is enabled", getDescription().getName()));
		} else {
			getLogger().warning(String.format("[%s] SuperSigns plugin was not found - integration is disabled!", getDescription().getName()));
		}

		loadClasses();

		// reset arenas that were interrupted by a server stop
		if (getConfig().isSet("ingamearenas")) {
			ArrayList<String> arenas = new ArrayList<String>(getConfig().getConfigurationSection("ingamearenas.").getKeys(false));
			for (String arena : arenas) {
				stop(null, arena);
				getConfig().set("ingamearenas." + arena, null);
				this.saveConfig();
			}
		}
		if (getConfig().isSet("leftplayers")) {
			ArrayList<String> players = new ArrayList<String>(getConfig().getConfigurationSection("leftplayers.").getKeys(false));
			for (String p : players) {
				this.left_players.add(p);
				getConfig().set("leftplayers." + p, null);
				this.saveConfig();
			}
		}
	}
	
	private void loadDefaults() {
		getConfig().options().header(ct("I recommend you to set auto_updating to true for possible future bugfixes. If use_economy is set to false, the winner will get the item reward."));
		getConfig().addDefault("config.auto_updating", true);
		getConfig().addDefault("config.rounds_per_game", 10);
		getConfig().addDefault("config.start_countdown", 5);
		getConfig().addDefault("config.default_max_players", 4);
		getConfig().addDefault("config.default_min_players", 3);
		getConfig().addDefault("config.use_economy_reward", true);
		getConfig().addDefault("config.money_reward_per_game", 30);
		getConfig().addDefault("config.itemid", 264); // diamond
		getConfig().addDefault("config.itemamount", 1);
		getConfig().addDefault("config.use_command_reward", false);
		getConfig().addDefault("config.command_reward", "pex user <player> group set ColorPro");
		getConfig().addDefault("config.start_announcement", false);
		getConfig().addDefault("config.winner_announcement", false);
		getConfig().addDefault("config.game_on_join", false);
		getConfig().addDefault("config.bling_sounds", false);

		getConfig().addDefault("config.kits.default.name", "default");
		getConfig().addDefault("config.kits.default.potioneffect", "SPEED");
		getConfig().addDefault("config.kits.default.amplifier", 1);
		getConfig().addDefault("config.kits.default.gui_item_id", 341);
		getConfig().addDefault("config.kits.default.lore", ct("&2The default class."));

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
		
		// check for any running arenas because some sloppy owner just stops the server without checking stuff
		for (String arena : ingame.keySet()) {
			if (ingame.get(arena)) {
				getConfig().set("ingamearenas." + arena, true);
			}
		}
		for (Player p : arenap.keySet()) {
			getConfig().set("leftplayers." + p.getName(), true);
		}
		this.saveConfig();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private boolean setupSignMaker() {
		signmaker = (SignMakerPlugin) getServer().getPluginManager().getPlugin("SuperSigns");
		return signmaker != null;
	}

	public void getConfigVars() {
		rounds_per_game = getConfig().getInt("config.rounds_per_game");
		default_max_players = getConfig().getInt("config.default_max_players");
		default_min_players = getConfig().getInt("config.default_min_players");
		reward = getConfig().getInt("config.money_reward");
		itemid = getConfig().getInt("config.itemid");
		itemamount = getConfig().getInt("config.itemamount");
		economy = getConfig().getBoolean("config.use_economy_reward");
		command_reward = getConfig().getBoolean("config.use_command_reward");
		cmd = getConfig().getString("config.command_reward");
		start_countdown = getConfig().getInt("config.start_countdown");
		start_announcement = getConfig().getBoolean("config.start_announcement");
		winner_announcement = getConfig().getBoolean("config.winner_announcement");
		bling_sounds = getConfig().getBoolean("config.bling_sounds");

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
					sender.sendMessage(ct("&2To &6setup the main lobby &2, type in &c/cm setmainlobby"));
					sender.sendMessage(ct("&2To &6setup &2a new arena, type in the following commands:"));
					sender.sendMessage(ct("&2/cm createarena [name]"));
					sender.sendMessage(ct("&2/cm setlobby [name] &6 - for the waiting lobby"));
					sender.sendMessage(ct("&2/cm setup [name]"));
					sender.sendMessage("");
					sender.sendMessage(ct("&2You can join with &c/cm join [name] &2and leave with &c/cm leave&2."));
					sender.sendMessage(ct("&2You can force an arena to start with &c/cm start [name]&2."));
				}
			} else {
				sender.sendMessage(ct("&6-= ColorMatch &2help: &6=-"));
				sender.sendMessage(ct("&2To &6setup the main lobby &2, type in &c/cm setmainlobby"));
				sender.sendMessage(ct("&2To &6setup &2a new arena, type in the following commands:"));
				sender.sendMessage(ct("&2/cm createarena [name]"));
				sender.sendMessage(ct("&2/cm setlobby [name] &6 - for the waiting lobby"));
				sender.sendMessage(ct("&2/cm setup [name]"));
				sender.sendMessage("");
				sender.sendMessage(ct("&2You can join with &c/cm join [name] &2and leave with &c/cm leave&2."));
				sender.sendMessage(ct("&2You can force an arena to start with &c/cm start [name]&2."));
			}
			return true;
		}
		return false;
	}

	public ArrayList<String> left_players = new ArrayList<String>();

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		if (arenap.containsKey(event.getPlayer())) {
			String arena = arenap.get(event.getPlayer());
			getLogger().info(arena);
			int count = 0;
			for (Player p_ : arenap.keySet()) {
				if (arenap.get(p_).equalsIgnoreCase(arena)) {
					count++;
				}
			}

			try {
				Sign s = this.getSignFromArena(arena);
				if (s != null) {
					s.setLine(1, ct("&2[Join]"));
					s.setLine(3, Integer.toString(count - 1) + "/" + Integer.toString(getArenaMaxPlayers(arena)));
					s.update();
				}
			} catch (Exception e) {
				getLogger().warning("You forgot to set a sign for arena " + arena + "! This might lead to errors.");
			}

			leaveArena(event.getPlayer(), true, true);
			left_players.add(event.getPlayer().getName());
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		if (left_players.contains(event.getPlayer().getName())) {
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				public void run() {
					p.teleport(getMainLobby());
					p.setFlying(false);
				}
			}, 5);
			left_players.remove(event.getPlayer().getName());
		}

		if (getConfig().getBoolean("config.game_on_join")) {
			int c = 0;
			final List<String> arenas = new ArrayList<String>();
			for (String arena : getConfig().getKeys(false)) {
				if (!arena.equalsIgnoreCase("mainlobby") && !arena.equalsIgnoreCase("strings") && !arena.equalsIgnoreCase("config") && !arena.equalsIgnoreCase("leftplayers") && !arena.equalsIgnoreCase("ingamearenas")) {
					c++;
					arenas.add(arena);
				}
			}
			if (c < 1) {
				getLogger().severe("Couldn't find any arena even though game_on_join was turned on. Please setup an arena to fix this!");
				return;
			}

			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				public void run() {
					joinLobby(p, arenas.get(0));
				}
			}, 30L);
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (arenap_.containsKey(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onHunger(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (arenap_.containsKey(p.getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent event) {
		// if (arenap_.containsKey(event.getPlayer().getName())) {
		if (arenap.containsKey(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onBlockPlace(BlockPlaceEvent event) {
		// if (arenap_.containsKey(event.getPlayer().getName())) {
		if (arenap.containsKey(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (arenap_.containsKey(event.getPlayer().getName())) {
			final String arena = arenap.get(event.getPlayer());
			if (lost.containsKey(event.getPlayer())) {
				// Player did not win.. keep them in the spectator area
				Location l = getSpawn(arena);
				final Location spectatorlobby = new Location(l.getWorld(), l.getBlockX(), l.getBlockY() + getArenaSpectateHeight(arena), l.getBlockZ());
				if (event.getPlayer().getLocation().getBlockY() < spectatorlobby.getBlockY() || event.getPlayer().getLocation().getBlockY() > spectatorlobby.getBlockY()) {
					final Player p = event.getPlayer();
					final float b = p.getLocation().getYaw();
					final float c = p.getLocation().getPitch();
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						@Override
						public void run() {
							try {
								p.setAllowFlight(true);
								p.setFlying(true);
								p.teleport(new Location(p.getWorld(), p.getLocation().getBlockX(), spectatorlobby.getBlockY(), p.getLocation().getBlockZ(), b, c));
								updateScoreboard(arena);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, 5);
					// REMOVED: Why does this even exist?
					/*if (!pseenfall.containsKey(p.getName())) {
						pseenfall.put(p.getName(), false);
					}
					if (!pseenfall.get(p.getName())) {
						p.sendMessage(you_fell);
						pseenfall.put(p.getName(), true);
					}*/
				}
			} else {
				Location l = getSpawn(arena);
				if (event.getPlayer().getLocation().getBlockY() < l.getBlockY() - getArenaFallDepth(arena)) {
					// Player has fallen, make them a spectator
					final Location spectatorlobby = new Location(l.getWorld(), l.getBlockX(), l.getBlockY() + getArenaSpectateHeight(arena), l.getBlockZ());
					lost.put(event.getPlayer(), arenap.get(event.getPlayer()));
					final Player p__ = event.getPlayer();
					Bukkit.getScheduler().runTaskLater(this, new Runnable() {
						public void run() {
							try {
								Location l = getSpawn(arena);
								p__.teleport(spectatorlobby);
								p__.setAllowFlight(true);
								p__.setFlying(true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, 5);
	
					int count = 0;
	
					for (Player p : arenap.keySet()) {
						if (arenap.get(p).equalsIgnoreCase(arena)) {
							if (!lost.containsKey(p)) {
								count++;
							}
						}
					}
	
					if (count < 2) {
						// last man standing!
						stop(h.get(arena), arena);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onSignUse(PlayerInteractEvent event) {
		if (event.hasBlock()) {
			if (event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN) {
				final Sign s = (Sign) event.getClickedBlock().getState();
				if (s.getLine(0).toLowerCase().contains("colormatch")) {
					if (s.getLine(1).equalsIgnoreCase(ct("&2[join]"))) {
						if (isValidArena(s.getLine(2))) {
							joinLobby(event.getPlayer(), s.getLine(2));
						} else {
							event.getPlayer().sendMessage(arena_invalid);
						}
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onSignChange(SignChangeEvent event) {
		Player p = event.getPlayer();
		if (event.getLine(0).toLowerCase().equalsIgnoreCase("colormatch")) {
			if (event.getPlayer().hasPermission("cm.sign") || event.getPlayer().hasPermission("colormatch.sign") || event.getPlayer().isOp()) {
				event.setLine(0, ct("&6&lColorMatch"));
				if (!event.getLine(2).equalsIgnoreCase("")) {
					String arena = event.getLine(2);
					if (isValidArena(arena)) {
						getConfig().set(arena + ".sign.world", p.getWorld().getName());
						getConfig().set(arena + ".sign.loc.x", event.getBlock().getLocation().getBlockX());
						getConfig().set(arena + ".sign.loc.y", event.getBlock().getLocation().getBlockY());
						getConfig().set(arena + ".sign.loc.z", event.getBlock().getLocation().getBlockZ());
						this.saveConfig();
						p.sendMessage(ct("&2Successfully created arena sign."));
					} else {
						p.sendMessage(arena_invalid_component);
						event.getBlock().breakNaturally();
					}
					event.setLine(1, ct("&2[Join]"));
					event.setLine(2, arena);
					event.setLine(3, "0/" + Integer.toString(getArenaMaxPlayers(arena)));
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		if (arenap.containsKey(event.getPlayer()) && !event.getPlayer().isOp()) {
			if (!event.getMessage().startsWith("/cm") && !event.getMessage().startsWith("/colormatch")) {
				event.getPlayer().sendMessage(ct("&cPlease use &6/cm leave &cto leave this minigame."));
				event.setCancelled(true);
				return;
			}
		}
	}

	public Sign getSignFromArena(String arena) {
		Location b_ = new Location(getServer().getWorld(getConfig().getString(arena + ".sign.world")), getConfig().getInt(arena + ".sign.loc.x"), getConfig().getInt(arena + ".sign.loc.y"), getConfig().getInt(arena + ".sign.loc.z"));
		BlockState bs = b_.getBlock().getState();
		Sign s_ = null;
		if (bs instanceof Sign) {
			s_ = (Sign) bs;
		} else {
		}
		return s_;
	}

	public Location getLobby(String arena) {
		Location ret = null;
		if (isValidArena(arena)) {
			ret = new Location(Bukkit.getWorld(getConfig().getString(arena + ".lobby.world")), getConfig().getInt(arena + ".lobby.loc.x"), getConfig().getInt(arena + ".lobby.loc.y"), getConfig().getInt(arena + ".lobby.loc.z"));
		}
		return ret;
	}

	public Location getMainLobby() {
		Location ret;
		if (getConfig().isSet("mainlobby")) {
			ret = new Location(Bukkit.getWorld(getConfig().getString("mainlobby.world")), getConfig().getInt("mainlobby.loc.x"), getConfig().getInt("mainlobby.loc.y"), getConfig().getInt("mainlobby.loc.z"));
		} else {
			ret = null;
			getLogger().warning("A Mainlobby could not be found. This will lead to errors, please fix this with /cm setmainlobby.");
		}
		return ret;
	}

	public Location getSpawn(String arena) {
		Location ret = null;
		if (isValidArena(arena)) {
			ret = new Location(Bukkit.getWorld(getConfig().getString(arena + ".spawn.world")), getConfig().getInt(arena + ".spawn.loc.x"), getConfig().getInt(arena + ".spawn.loc.y"), getConfig().getInt(arena + ".spawn.loc.z"));
		}
		return ret;
	}

	public Location getSpawnForPlayer(String arena) {
		Location ret = null;
		if (isValidArena(arena)) {
			ret = new Location(Bukkit.getWorld(getConfig().getString(arena + ".spawn.world")), getConfig().getInt(arena + ".spawn.loc.x"), getConfig().getInt(arena + ".spawn.loc.y") + 2, getConfig().getInt(arena + ".spawn.loc.z"));
		}
		return ret;
	}

	public boolean isValidArena(String arena) {
		if (getConfig().isSet(arena + ".spawn") && getConfig().isSet(arena + ".lobby")) {
			return true;
		}
		return false;
	}

	public HashMap<Player, Boolean> winner = new HashMap<Player, Boolean>();

	public void leaveArena(final Player p, boolean flag, boolean hmmthisbug) {
		try {
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				public void run() {
					if (p.isOnline()) {
						p.teleport(getMainLobby());
						// p.setFlying(false);
						for (PotionEffect pe : p.getActivePotionEffects()) {
							try {
								if (p.hasPotionEffect(pe.getType())) {
									p.removePotionEffect(pe.getType());
								}
							} catch (Exception e) {
							}
						}
					}
				}
			}, 5);

			if (lost.containsKey(p)) {
				lost.remove(p);
			}

			if (pclass.containsKey(p.getName())) {
				pclass.remove(p.getName());
			}

			// REMOVED: Why does this even exist?
			/*if (pseenfall.containsKey(p.getName())) {
				pseenfall.remove(p.getName());
			}*/

			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				public void run() {
					if (p.isOnline()) {
						p.setAllowFlight(false);
						p.setFlying(false);
						if (pgamemode.get(p) == GameMode.CREATIVE) {
							p.setAllowFlight(true);
						}
					}
				}
			}, 10);

			/*
			 * if (p.isOnline()) { p.setAllowFlight(false); p.setFlying(false); }
			 */

			final String arena = arenap.get(p);

			removeScoreboard(arena, p);

			if (flag) {
				if (arenap.containsKey(p)) {
					arenap.remove(p);
				}
				if (xpsecp.containsKey(p)) {
					xpsecp.remove(p);
				}
			}
			if (arenap_.containsKey(p.getName())) {
				arenap_.remove(p.getName());
			}

			updateScoreboard(arena);

			removeScoreboard(arena, p);

			if (p.isOnline()) {
				p.getInventory().setContents(pinv.get(p));
				p.updateInventory();
				p.setGameMode(pgamemode.get(p));
			}

			if (winner.containsKey(p)) {
				if (economy) {
					EconomyResponse r = econ.depositPlayer(p.getName(), getConfig().getDouble("config.money_reward_per_game"));
					if (!r.transactionSuccess()) {
						getServer().getPlayer(p.getName()).sendMessage(String.format("An error occured: %s", r.errorMessage));
					}
				} else {
					p.getInventory().addItem(new ItemStack(Material.getMaterial(itemid), itemamount));
					p.updateInventory();
				}

				// command reward
				if (command_reward) {
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("<player>", p.getName()));
				}
			}

			int count = 0;
			for (Player p_ : arenap.keySet()) {
				if (arenap.get(p_).equalsIgnoreCase(arena)) {
					count++;
				}
			}

			/*
			 * if (hmmthisbug && count > 0) { getLogger().info("Sorry, I could not fix the game. Stopping now."); stop(h.get(arena), arena); }
			 */

			if (count < 2) {
				if (flag) {
					stop(h.get(arena), arena);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void joinLobby(final Player p, final String arena) {
		// check first if max players are reached.
		int count_ = 0;
		for (Player p_ : arenap.keySet()) {
			if (arenap.get(p_).equalsIgnoreCase(arena)) {
				count_++;
			}
		}
		if (count_ > getArenaMaxPlayers(arena) - 1) {
			p.sendMessage(arena_full);
			return;
		}
		
		if (count_ == 0) {
			updateSuperSigns(arena, "Waiting", new ItemStack(Material.STAINED_CLAY, 1, DyeColor.LIME.getData()));
		}

		// continue
		arenap.put(p, arena);
		pgamemode.put(p, p.getGameMode());
		p.setGameMode(GameMode.SURVIVAL);
		pinv.put(p, p.getInventory().getContents());
		p.getInventory().clear();
		p.updateInventory();
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				p.teleport(getLobby(arena));
				p.setFoodLevel(20);
			}
		}, 4);

		int count = 0;
		for (Player p_ : arenap.keySet()) {
			if (arenap.get(p_).equalsIgnoreCase(arena)) {
				count++;
			}
		}
		if (count > getArenaMinPlayers(arena) - 1) {
			for (Player p_ : arenap.keySet()) {
				final Player p__ = p_;
				if (arenap.get(p_).equalsIgnoreCase(arena)) {
					Bukkit.getScheduler().runTaskLater(this, new Runnable() {
						public void run() {
							p__.teleport(getSpawnForPlayer(arena));
						}
					}, 7);
				}
			}
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				public void run() {
					if (!ingame.containsKey(arena)) {
						ingame.put(arena, false);
					}
					if (!ingame.get(arena)) {
						//updateSuperSigns(arena, "", null);
						start(arena);
					}
				}
			}, 10);
		}

		if (!ingame.containsKey(arena)) {
			ingame.put(arena, false);
		}
		if (ingame.get(arena)) {
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				public void run() {
					p.teleport(getSpawnForPlayer(arena));
				}
			}, 7);
		}

		updateScoreboard(arena);

		try {
			Sign s = this.getSignFromArena(arena);
			if (s != null) {
				s.setLine(3, Integer.toString(count) + "/" + Integer.toString(getArenaMaxPlayers(arena)));
				s.update();
			}
		} catch (Exception e) {
			getLogger().warning("You forgot to set a sign for arena " + arena + "! This may lead to errors.");
		}

	}

	// Arena removal

	public static void removeArena(Location start, Main main, String name_) {
		int x = start.getBlockX() - 32;
		int y = start.getBlockY();
		int y_ = start.getBlockY() - 4;
		int z = start.getBlockZ() - 32;

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int x_ = x + i * 4;
				int z_ = z + j * 4;

				for (int i_ = 0; i_ < 4; i_++) {
					for (int j_ = 0; j_ < 4; j_++) {
						Block b = start.getWorld().getBlockAt(new Location(start.getWorld(), x_ + i_, y, z_ + j_));
						b.setType(Material.AIR);
						Block b_ = start.getWorld().getBlockAt(new Location(start.getWorld(), x_ + i_, y_, z_ + j_));
						b_.setType(Material.AIR);
					}
				}
			}
		}
	}

	public static void removeArenax32(Location start, Main main, String name_) {
		int x = start.getBlockX() - 16;
		int y = start.getBlockY();
		int y_ = start.getBlockY() - 4;
		int z = start.getBlockZ() - 16;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				int x_ = x + i * 4;
				int z_ = z + j * 4;

				for (int i_ = 0; i_ < 4; i_++) {
					for (int j_ = 0; j_ < 4; j_++) {
						Block b = start.getWorld().getBlockAt(new Location(start.getWorld(), x_ + i_, y, z_ + j_));
						b.setType(Material.AIR);
						Block b_ = start.getWorld().getBlockAt(new Location(start.getWorld(), x_ + i_, y_, z_ + j_));
						b_.setType(Material.AIR);
					}
				}
			}
		}
	}

	// COPIED FROM MINIGAMES PARTY
	public void setup(Location start, Main main, String name_) {
		int x = start.getBlockX() - 32;
		int y = start.getBlockY();
		int y_ = start.getBlockY() - 7;
		int z = start.getBlockZ() - 32;

		int current = 0;

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int x_ = x + i * 4;
				int z_ = z + j * 4;

				int newcurrent = r.nextInt(colors.size());
				if (current == newcurrent) {
					// newcurrent = r.nextInt(colors.size());
					if (newcurrent > 0) {
						newcurrent -= 1;
					} else {
						newcurrent += 2;
					}
				}

				if (ints.size() > 15) {
					// Block b = start.getWorld().getBlockAt(new Location(start.getWorld(), x_, y, z_));
					// Bukkit.getLogger().info(Integer.toString(b.getLocation().getBlockX()) + " " + Integer.toString(b.getLocation().getBlockZ()) +
					// " . " + Integer.toString(colors.get(newcurrent).getData()) + " " + Integer.toString(ints.get(ints.size() - 16)));

					if (ints.get(ints.size() - 16) == colors.get(newcurrent).getData()) {
						if (newcurrent > 0) {
							newcurrent -= 1;
						} else {
							newcurrent += 2;
						}
					}
				}

				current = newcurrent;
				// ints.add(current);
				ints.add((int) colors.get(current).getData());

				for (int i_ = 0; i_ < 4; i_++) {
					for (int j_ = 0; j_ < 4; j_++) {
						Block b = start.getWorld().getBlockAt(new Location(start.getWorld(), x_ + i_, y, z_ + j_));
						Block b_ = start.getWorld().getBlockAt(new Location(start.getWorld(), x_ + i_, y_, z_ + j_));
						b_.setType(Material.GLOWSTONE);
						b.setType(Material.WOOL);
						b.setData(colors.get(current).getData());
					}
				}
			}
		}
	}

	final Main m = this;

	public static ArrayList<Integer> ints = new ArrayList<Integer>();
	public static ArrayList<DyeColor> colors = new ArrayList<DyeColor>(Arrays.asList(DyeColor.BLUE, DyeColor.RED, DyeColor.CYAN, DyeColor.BLACK, DyeColor.GREEN, DyeColor.YELLOW, DyeColor.ORANGE, DyeColor.PURPLE));
	public static Random r = new Random();

	final public HashMap<String, BukkitTask> h = new HashMap<String, BukkitTask>();
	final public HashMap<String, Integer> countdown_count = new HashMap<String, Integer>();
	final public HashMap<String, Integer> countdown_id = new HashMap<String, Integer>();

	public BukkitTask start(final String arena) {
		ingame.put(arena, true);

		// setup arena
		a_round.put(arena, 0);
		a_n.put(arena, 0);
		a_currentw.put(arena, 0);

		// setup ints arraylist
		if (isArenax32(arena)) {
			cmx32.getAll(getSpawn(arena));
		} else if (isArenax32ClayMode(arena)) {
			cmx32clay.getAll(getSpawn(arena));
		} else if (isArenax32GlassMode(arena)) {
			cmx32glass.getAll(getSpawn(arena));
		} else if (isArenaGlassMode(arena)) {
			cmglass.getAll(getSpawn(arena));
		} else if (isArenaClayMode(arena)) {
			cmclay.getAll(getSpawn(arena));
		} else {
			getAll(getSpawn(arena));
		}

		// start countdown timer
		if (start_announcement) {
			Bukkit.getServer().broadcastMessage(starting + " " + Integer.toString(start_countdown));
		}

		Bukkit.getServer().getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				// clear hostile mobs on start:
				for (Player p : arenap.keySet()) {
					p.playSound(p.getLocation(), Sound.CAT_MEOW, 1, 0);
					if (arenap.get(p).equalsIgnoreCase(arena)) {
						for (Entity t : p.getNearbyEntities(64, 64, 64)) {
							if (t.getType() == EntityType.ZOMBIE || t.getType() == EntityType.SKELETON || t.getType() == EntityType.CREEPER || t.getType() == EntityType.CAVE_SPIDER || t.getType() == EntityType.SPIDER || t.getType() == EntityType.WITCH || t.getType() == EntityType.GIANT) {
								t.remove();
							}
						}
						break;
					}
				}
			}
		}, 20L);

		int t = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(m, new Runnable() {
			public void run() {
				if (!countdown_count.containsKey(arena)) {
					countdown_count.put(arena, start_countdown);
				}
				int count = countdown_count.get(arena);
				for (Player p : arenap.keySet()) {
					if (arenap.get(p).equalsIgnoreCase(arena)) {
						p.sendMessage(starting_in + count + starting_in2);
					}
				}
				count--;
				countdown_count.put(arena, count);
				if (count < 0) {
					countdown_count.put(arena, start_countdown);

					if (start_announcement) {
						Bukkit.getServer().broadcastMessage(started);
					}

					// update sign
					Bukkit.getServer().getScheduler().runTask(m, new Runnable() {
						public void run() {
							Sign s = getSignFromArena(arena);
							if (s != null) {
								s.setLine(1, ct("&4[Ingame]"));
								s.update();
							}
						}
					});

					for (Player p : arenap.keySet()) {
						if (arenap.get(p).equalsIgnoreCase(arena)) {
							if (pclass.containsKey(p.getName())) {
								m.getClass(p.getName());
							} else {
								// setClass("default", p.getName());
							}
						}
					}

					Bukkit.getServer().getScheduler().cancelTask(countdown_id.get(arena));
				}
			}
		}, 0, 20).getTaskId();
		countdown_id.put(arena, t);

		int difficulty = this.getArenaDifficulty(arena);
		if (difficulty > 3 || difficulty < 0) {
			this.setArenaDifficulty(arena, 1);
			difficulty = 1;
		}

		final int d = difficulty;

		BukkitTask id__ = null;
		id__ = Bukkit.getServer().getScheduler().runTaskTimer(m, new Runnable() {
			@Override
			public void run() {
				try {
					a_round.put(arena, a_round.get(arena) + 1);
					final int n = a_n.get(arena);
					if (a_round.get(arena) > rounds_per_game) {
						a_round.put(arena, 0);
						stop(h.get(arena), arena);
					}

					final ArrayList<BukkitTask> tasks = new ArrayList<BukkitTask>();

					int temp = r.nextInt(colors.size());
					if (a_currentw.get(arena) == temp) {
						a_currentw.put(arena, r.nextInt(colors.size()));
					} else {
						a_currentw.put(arena, temp);
					}
					int currentw = a_currentw.get(arena);

					// REMOVED: Why is this even here???
					//Wool w = new Wool();
					//w.setColor(colors.get(currentw));

					DyeColor dc = colors.get(currentw);
					ItemStack item;
					if (isArenaClayMode(arena) || isArenax32ClayMode(arena)) {
						item = new ItemStack(Material.STAINED_CLAY, 1, dc.getData());
					} else if (isArenaGlassMode(arena) || isArenax32GlassMode(arena)) {
						item = new ItemStack(Material.STAINED_GLASS, 1, dc.getData());
					} else {
						item = new ItemStack(Material.WOOL, 1, dc.getData());
					}
					ItemMeta im = item.getItemMeta();
					im.setDisplayName(Utilities.dyeToChat(dc) + "" + ChatColor.BOLD + dc.name());
					item.setItemMeta(im);

					// Update SuperSigns
					updateSuperSigns(arena, dc.name(), item);

					for (final Player p : arenap.keySet()) {
						if (p.isOnline()) {
							if (arenap.get(p).equalsIgnoreCase(arena)) {
								arenap_.put(p.getName(), arena);
								// set inventory and exp bar
								p.getInventory().clear();
								p.updateInventory();

								p.setExp(0.97F);
								if (!xpsecp.containsKey(p)) {
									xpsecp.put(p, 1);
								}
								tasks.add(Bukkit.getServer().getScheduler().runTaskTimer(m, new Runnable() {
									public void run() {
										if (!xpsecp.containsKey(p)) {
											xpsecp.put(p, 1);
										}
										int xpsec = xpsecp.get(p);
										p.setExp(1 - (0.083F * xpsec));
										xpsecp.put(p, xpsec + 1);
										if (bling_sounds) {
											if (xpsec == 8) {
												p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 0);
											} else if (xpsec == 10) {
												p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 0);
											} else if (xpsec == 12) {
												p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 0);
											}
										}
									}
								}, (80L - (d * 20) - n) / 12, (80L - (d * 20) - n) / 12));

								for (int i = 0; i < 9; i++) {
									p.getInventory().setItem(i, item);
								}
								p.updateInventory();
							}
						}
					}
					// remove all wools except current one
					Bukkit.getServer().getScheduler().runTaskLater(m, new Runnable() {
						// Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(m,
						// new Runnable(){
						public void run() {
							// Don't do anything if game has finished
							if (!ingame.get(arena))
								return;

							if (isArenax32(arena)) {
								cmx32.removeAllExceptOne(getSpawn(arena), arena);
							} else if (isArenax32ClayMode(arena)) {
								cmx32clay.removeAllExceptOne(getSpawn(arena), arena);
							} else if (isArenax32GlassMode(arena)) {
								cmx32glass.removeAllExceptOne(getSpawn(arena), arena);
							} else if (isArenaGlassMode(arena)) {
								cmglass.removeAllExceptOne(getSpawn(arena), arena);
							} else if (isArenaClayMode(arena)) {
								cmclay.removeAllExceptOne(getSpawn(arena), arena);
							} else {
								removeAllExceptOne(getSpawn(arena), arena);
							}
							for (BukkitTask t : tasks) {
								t.cancel();
							}
							for (Player p : xpsecp.keySet()) {
								if (arenap.containsKey(p)) {
									if (arenap.get(p).equalsIgnoreCase(arena)) {
										xpsecp.put(p, 1);
									}
								}
							}
						}
					}, 80L - (d * 20) - n);

					// BukkitTask id =
					// Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(m,
					// new Runnable() {
					BukkitTask id = Bukkit.getServer().getScheduler().runTaskLater(m, new Runnable() {
						@Override
						public void run() {
							// Don't do anything if game has finished
							if (!ingame.get(arena))
								return;

							updateSuperSigns(arena, "", null);
							if (isArenax32(arena)) {
								cmx32.reset(getSpawn(arena));
							} else if (isArenax32ClayMode(arena)) {
								cmx32clay.reset(getSpawn(arena));
							} else if (isArenax32GlassMode(arena)) {
								cmx32glass.reset(getSpawn(arena));
							} else if (isArenaGlassMode(arena)) {
								cmglass.reset(getSpawn(arena));
							} else if (isArenaClayMode(arena)) {
								cmclay.reset(getSpawn(arena));
							} else {
								reset(getSpawn(arena));
							}
						}
					}, 110 - (n / 2));
					// update count
					if (a_n.get(arena) < (80L - (d * 20) - 10)) {
						a_n.put(arena, a_n.get(arena) + 4);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, 20 + 20 * start_countdown, 120); // 6 seconds

		h.put(arena, id__);
		tasks.put(arena, id__);
		return id__;
	}

	public static void getAll(Location start) {
		ints.clear();

		int x = start.getBlockX() - 32;
		int y = start.getBlockY();
		int z = start.getBlockZ() - 32;

		int current = 0;
		int count = 0;

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int x_ = x + i * 4;
				int z_ = z + j * 4;

				Block b = start.getWorld().getBlockAt(new Location(start.getWorld(), x_, y, z_));

				ints.add((int) b.getData());
			}
		}
	}

	public void reset(final Location start) {
		try {
			// final MassBlockUpdate mbu =
			// CraftMassBlockUpdate.createMassBlockUpdater(this,
			// start.getWorld());

			// mbu.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.NEVER);

			if (ints.size() < 1) {
				getAll(start);
			}

			int x = start.getBlockX() - 32;
			int y = start.getBlockY();
			int y_ = start.getBlockY() - 4;
			int z = start.getBlockZ() - 32;

			World w = start.getWorld();

			int current = 0;
			int count = 0;

			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					int x_ = x + i * 4;
					int z_ = z + j * 4;

					// current = r.nextInt(colors.size());
					current = ints.get(count);
					if (current < 1) {
						current = (int) colors.get(r.nextInt(colors.size())).getData();
					}
					count += 1;

					for (int i_ = 0; i_ < 4; i_++) {
						for (int j_ = 0; j_ < 4; j_++) {
							// Block b = start.getWorld().getBlockAt(new
							// Location(start.getWorld(), x_ + i_, y, z_ + j_));
							Block b = start.getWorld().getBlockAt(new Location(start.getWorld(), x_ + i_, y, z_ + j_));
							// mbu.setBlock(x_ + i_, y, z_ + j_, 35, current);
							// mbu.setBlock(x_ + i_, y_, z_ + j_, 89);
							b.setType(Material.WOOL);
							b.setData((byte) current);
						}
					}
				}
			}

			// mbu.notifyClients();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeAllExceptOne(Location start, String arena) {
		// final MassBlockUpdate mbu =
		// CraftMassBlockUpdate.createMassBlockUpdater(m, start.getWorld());

		// mbu.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.NEVER);

		int x = start.getBlockX() - 32;
		int y = start.getBlockY();
		int z = start.getBlockZ() - 32;
		Byte data = colors.get(a_currentw.get(arena)).getData();

		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 64; j++) {
				Block b = start.getWorld().getBlockAt(new Location(start.getWorld(), x + i, y, z + j));
				if (b.getData() != data) {
					b.setType(Material.AIR);
					// mbu.setBlock(x + i, y, z + j, 0);
				}
			}
		}

		// mbu.notifyClients();
	}

	// [END] COPIED FROM MINIGAMESPARTY

	public void stop(BukkitTask t, final String arena) {
		ingame.put(arena, false);
		try {
			t.cancel();
		} catch (Exception e) {

		}

		// Reset super signs to the "idle" message
		updateSuperSigns(arena, getArenaIdleMessage(arena), new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData()));

		// runs all that stuff later, that fixes the
		// "players are stuck in arena" bug!
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {

			public void run() {
				countdown_count.put(arena, start_countdown);
				try {
					Bukkit.getServer().getScheduler().cancelTask(countdown_id.get(arena));
				} catch (Exception e) {
				}

				// removeScoreboard(arena);
				
				ArrayList<Player> torem = new ArrayList<Player>();
				determineWinners(arena);
				for (Player p : arenap.keySet()) {
					if (arenap.get(p).equalsIgnoreCase(arena)) {
						leaveArena(p, false, false);
						torem.add(p);
					}
				}

				for (Player p : arenap.keySet()) {
					if (arenap.get(p).equalsIgnoreCase(arena)) {
						removeScoreboard(arena, p);
					}
				}

				for (Player p : torem) {
					arenap.remove(p);
				}
				torem.clear();

				winner.clear();

				Sign s = getSignFromArena(arena);
				if (s != null) {
					s.setLine(1, ct("&2[Join]"));
					s.setLine(3, "0/" + Integer.toString(getArenaMaxPlayers(arena)));
					s.update();
				}

				h.remove(arena);

				// reset arena
				for (Player p : xpsecp.keySet()) {
					xpsecp.put(p, 1);
				}
				a_round.put(arena, 0);
				a_n.put(arena, 0);
				a_currentw.put(arena, 0);

				if (isArenax32(arena)) {
					cmx32.reset(getSpawn(arena));
				} else if (isArenax32ClayMode(arena)) {
					cmx32clay.reset(getSpawn(arena));
				} else if (isArenax32GlassMode(arena)) {
					cmx32glass.reset(getSpawn(arena));
				} else if (isArenaGlassMode(arena)) {
					cmglass.reset(getSpawn(arena));
				} else if (isArenaClayMode(arena)) {
					cmclay.reset(getSpawn(arena));
				} else {
					reset(getSpawn(arena));
				}

				// clean out offline players
				clean();
			}

		}, 20); // 1 second

	}

	public void clean() {
		for (Player p : arenap.keySet()) {
			if (!p.isOnline()) {
				leaveArena(p, false, false);
			}
		}
	}

	public void determineWinners(String arena) {
		for (Player p : arenap.keySet()) {
			if (arenap.get(p).equalsIgnoreCase(arena)) {
				if (!lost.containsKey(p)) {
					// this player is a winner
					p.sendMessage(you_won);

					if (winner_announcement) {
						getServer().broadcastMessage(winner_an.replaceAll("<player>", p.getName()).replaceAll("<arena>", arena));
					}

					winner.put(p, true);
				} else {
					lost.remove(p);
				}
			}
		}
	}

	public void updateScoreboard(String arena) {
		try {
			ScoreboardManager manager = Bukkit.getScoreboardManager();

			int count = 0;
			for (Player p_ : arenap.keySet()) {
				if (arenap.get(p_).equalsIgnoreCase(arena)) {
					count++;
				}
			}

			int lostcount = 0;
			for (Player p : arenap.keySet()) {
				if (arenap.get(p).equalsIgnoreCase(arena)) {
					if (lost.containsKey(p)) {
						lostcount++;
					}
				}
			}

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (arenap.containsKey(p)) {
					if (arenap.get(p).equalsIgnoreCase(arena)) {
						Scoreboard board = manager.getNewScoreboard();

						Objective objective = board.registerNewObjective("test", "dummy");
						objective.setDisplaySlot(DisplaySlot.SIDEBAR);

						objective.setDisplayName(ct("&cC&3o&dl&5o&6r&1M&aa&2t&4c&eh!")); // <- ColorMatch

						try {
							objective.getScore(Bukkit.getOfflinePlayer(ct(" &8-  "))).setScore(5);
							objective.getScore(Bukkit.getOfflinePlayer(ct("&aArena"))).setScore(4);
							objective.getScore(Bukkit.getOfflinePlayer(ct("&d" + arena))).setScore(3);
							objective.getScore(Bukkit.getOfflinePlayer(ct(" &8- "))).setScore(2);
							objective.getScore(Bukkit.getOfflinePlayer(ct("&aPlayers Left"))).setScore(1);
							objective.getScore(Bukkit.getOfflinePlayer(Integer.toString(count - lostcount) + "/" + Integer.toString(count))).setScore(0);
						} catch (Exception e) {
							//
						}

						p.setScoreboard(board);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeScoreboard(String arena) {
		try {
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			Scoreboard sc = manager.getNewScoreboard();

			sc.clearSlot(DisplaySlot.SIDEBAR);

			getLogger().info("Removing scoreboard.");

			for (Player p : Bukkit.getOnlinePlayers()) {
				p.setScoreboard(sc);
				if (arenap.containsKey(p)) {
					if (arenap.get(p).equalsIgnoreCase(arena)) {
						getLogger().info(p.getName());
						p.setScoreboard(sc);
						p.setScoreboard(null);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeScoreboard(String arena, Player p) {
		try {
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			Scoreboard sc = manager.getNewScoreboard();

			sc.clearSlot(DisplaySlot.SIDEBAR);
			p.setScoreboard(sc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getArenaDifficulty(String arena) {
		if (!getConfig().isSet(arena + ".difficulty")) {
			setArenaDifficulty(arena, 1);
		}
		return getConfig().getInt(arena + ".difficulty");
	}

	public void setArenaDifficulty(String arena, int difficulty) {
		getConfig().set(arena + ".difficulty", difficulty);
		this.saveConfig();
	}

	public int getArenaMaxPlayers(String arena) {
		if (!getConfig().isSet(arena + ".max_players")) {
			setArenaMaxPlayers(arena, default_max_players);
		}
		return getConfig().getInt(arena + ".max_players");
	}

	public void setArenaMaxPlayers(String arena, int players) {
		getConfig().set(arena + ".max_players", players);
		this.saveConfig();
	}

	public int getArenaMinPlayers(String arena) {
		if (!getConfig().isSet(arena + ".min_players")) {
			setArenaMinPlayers(arena, default_min_players);
		}
		return getConfig().getInt(arena + ".min_players");
	}

	public void setArenaMinPlayers(String arena, int players) {
		getConfig().set(arena + ".min_players", players);
		this.saveConfig();
	}

	public int getArenaSpectateHeight(String arena) {
		if (!getConfig().isSet(arena + ".spectate_height")) {
			setArenaSpectateHeight(arena, 20);
		}
		return getConfig().getInt(arena + ".spectate_height");
	}

	public void setArenaSpectateHeight(String arena, int height) {
		getConfig().set(arena + ".spectate_height", height);
		this.saveConfig();
	}

	public int getArenaFloorDepth(String arena) {
		if (!getConfig().isSet(arena + ".floor_depth")) {
			setArenaFloorDepth(arena, 20);
		}
		return getConfig().getInt(arena + ".floor_depth");
	}

	public void setArenaFloorDepth(String arena, int depth) {
		getConfig().set(arena + ".floor_depth", depth);
		this.saveConfig();
	}

	public int getArenaFallDepth(String arena) {
		if (!getConfig().isSet(arena + ".fall_depth")) {
			setArenaFallDepth(arena, 5);
		}
		return getConfig().getInt(arena + ".fall_depth");
	}

	public void setArenaFallDepth(String arena, int depth) {
		getConfig().set(arena + ".fall_depth", depth);
		this.saveConfig();
	}

	public int getArenaSuperSigns(String arena) {
		if (!getConfig().isSet(arena + ".supersigns")) {
			setArenaSuperSigns(arena, 0);
		}
		return getConfig().getInt(arena + ".supersigns");
	}

	public void setArenaSuperSigns(String arena, int signs) {
		getConfig().set(arena + ".supersigns", signs);
		this.saveConfig();
	}

	public String getArenaIdleMessage(String arena) {
		if (!getConfig().isSet(arena + ".idle_message")) {
			setArenaIdleMessage(arena, "Game Over");
		}
		return getConfig().getString(arena + ".idle_message");
	}

	public void setArenaIdleMessage(String arena, String msg) {
		getConfig().set(arena + ".idle_message", msg);
		this.saveConfig();
	}

	public boolean isNumeric(String s) {
		return s.matches("[-+]?\\d*\\.?\\d+");
	}

	public boolean isArenax32(String arena) {
		if (getConfig().isSet(arena + ".x32")) {
			return getConfig().getBoolean(arena + ".x32");
		}
		return false;
	}

	public void setArenax32(String arena) {
		getConfig().set(arena + ".x32", true);
		this.saveConfig();
	}

	public boolean isArenaGlassMode(String arena) {
		if (getConfig().isSet(arena + ".glassmode")) {
			return getConfig().getBoolean(arena + ".glassmode");
		}
		return false;
	}

	public void setArenaGlassMode(String arena, boolean f) {
		if (f) {
			getConfig().set(arena + ".glassmode", true);
		} else {
			getConfig().set(arena + ".glassmode", null);
		}
		this.saveConfig();
	}

	public boolean isArenaClayMode(String arena) {
		if (getConfig().isSet(arena + ".claymode")) {
			return getConfig().getBoolean(arena + ".claymode");
		}
		return false;
	}

	public void setArenaClayMode(String arena, boolean f) {
		if (f) {
			getConfig().set(arena + ".claymode", true);
		} else {
			getConfig().set(arena + ".claymode", null);
		}
		this.saveConfig();
	}

	public boolean isArenax32ClayMode(String arena) {
		if (getConfig().isSet(arena + ".x32claymode")) {
			return getConfig().getBoolean(arena + ".x32claymode");
		}
		return false;
	}

	public void setArenax32ClayMode(String arena, boolean f) {
		if (f) {
			getConfig().set(arena + ".x32claymode", true);
		} else {
			getConfig().set(arena + ".x32claymode", null);
		}
		this.saveConfig();
	}

	public boolean isArenax32GlassMode(String arena) {
		if (getConfig().isSet(arena + ".x32glassmode")) {
			return getConfig().getBoolean(arena + ".x32glassmode");
		}
		return false;
	}

	public void setArenax32GlassMode(String arena, boolean f) {
		if (f) {
			getConfig().set(arena + ".x32glassmode", true);
		} else {
			getConfig().set(arena + ".x32glassmode", null);
		}
		this.saveConfig();
	}

	public void getClass(String player) {
		AClass c = pclass.get(player);
		getServer().getPlayer(player).getInventory().clear();
		getServer().getPlayer(player).getInventory().setArmorContents(null);
		getServer().getPlayer(player).updateInventory();
		for (PotionEffect pot : c.potioneffect) {
			getServer().getPlayer(player).addPotionEffect(pot);
		}
		getServer().getPlayer(player).updateInventory();
	}

	public void setClass(String classname, String player) {
		pclass.put(player, aclasses.get(classname));
	}

	public void loadClasses() {
		if (getConfig().isSet("config.kits")) {
			for (String aclass : getConfig().getConfigurationSection("config.kits.").getKeys(false)) {
				AClass n = new AClass(this, aclass, this.parsePotionEffects(getConfig().getString("config.kits." + aclass + ".potioneffect"), aclass));
				aclasses.put(aclass, n);
				if (!getConfig().isSet("config.kits." + aclass + ".potioneffect") || !getConfig().isSet("config.kits." + aclass + ".lore")) {
					getLogger().warning("One of the classes found in the config file is invalid: " + aclass + ". Missing itemid or lore!");
				}
			}
		}
	}

	public PotionEffect[] parsePotionEffects(String str, String aclass) {
		PotionEffect[] ret = new PotionEffect[StringUtils.countMatches(str, "#") + 1];
		int count = 0;
		for (String pot : str.split("#")) {
			if (pot.length() > 1) {
				ret[count] = new PotionEffect(PotionEffectType.getByName(pot), 20 * 64, getConfig().getInt("config.kits." + aclass + ".amplifier"));
				count++;
			}
		}
		return ret;
	}

	public void openGUI(final Main m, String p) {
		IconMenu iconm = new IconMenu("Kits", 18, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(IconMenu.OptionClickEvent event) {
				String d = event.getName();
				Player p = event.getPlayer();
				if (aclasses.containsKey(d)) {
					m.setClass(d, p.getName());
				}
				event.setWillClose(true);
			}
		}, m);

		int c = 0;
		for (String ac : aclasses.keySet()) {
			if (getConfig().isSet("config.kits." + ac + ".gui_item_id")) {
				iconm.setOption(c, new ItemStack(Material.getMaterial(getConfig().getInt("config.kits." + ac + ".gui_item_id"))), ac, ct(m.getConfig().getString("config.kits." + ac + ".lore")));
			} else {
				iconm.setOption(c, new ItemStack(Material.SLIME_BALL), ac, ct(m.getConfig().getString("config.kits." + ac + ".lore")));
			}

			c++;
		}

		iconm.open(Bukkit.getPlayerExact(p));
	}

	public void updateSuperSigns(String arena, String text, ItemStack item) {
		if ((signmaker != null) && (getArenaSuperSigns(arena) > 0)) {
			final String arenaname = arena;
			final String signtext = ChatColor.stripColor(text);
			final ItemStack stack = item;
			Bukkit.getServer().getScheduler().runTask(m, new Runnable() {
				public void run() {
					for (int x = 1; x < (getArenaSuperSigns(arenaname) + 1); x++) {
						String signname = "colormatch_" + arenaname + x;
						TextSign sign = signmaker.getSign(signname);
						if (sign != null) {
							if (stack != null)
								sign.setMaterial(stack.getData());
							sign.setText(signtext);
							sign.redraw();
						}
					}
				}
			});
		}
	}
	
	public String ct(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}

}
