package com.comze_instancelabs.colormatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;

public class ColorMatchModule extends MinigameModule {
	
	private IntegerFlag difficulty;
	private IntegerFlag spectatorHeight;
	private IntegerFlag floorDepth;
	private IntegerFlag fallDepth;
	private IntegerFlag superSigns;
	private StringFlag idleMessage;
	private IntegerFlag roundsPerGame;
	private IntegerFlag roundWaitTime;
	private IntegerFlag postGameTime;
	private MaterialFlag material;
	
	private HashMap<String, Flag<?>> flags;
	
	private GameBoard game;
	private boolean hasInitted;
	
	public ColorMatchModule(Minigame mgm) {
		super(mgm);
		
		flags = new HashMap<String, Flag<?>>();
		addFlag(difficulty = new IntegerFlag(1, "difficulty"));
		addFlag(spectatorHeight = new IntegerFlag(5, "spectator-height"));
		addFlag(floorDepth = new IntegerFlag(10, "floor-depth"));
		addFlag(fallDepth = new IntegerFlag(5, "fall-depth"));
		addFlag(superSigns = new IntegerFlag(0, "supersigns"));
		addFlag(idleMessage = new StringFlag("Game Over", "idle-message"));
		addFlag(roundsPerGame = new IntegerFlag(10, "rounds-per-game"));
		addFlag(material = new MaterialFlag(Material.STAINED_CLAY, "material"));
		addFlag(roundWaitTime = new IntegerFlag(2, "round-wait-time"));
		addFlag(postGameTime = new IntegerFlag(2, "post-game-time"));
	}
	
	private void addFlag(Flag<?> flag) {
		flags.put(flag.getName(), flag);
	}

	@Override
	public String getName() {
		return "ColorMatch";
	}

	@Override
	public Map<String, Flag<?>> getFlags() {
		return flags;
	}

	@Override
	public boolean useSeparateConfig() {
		return true;
	}

	@Override
	public void save(FileConfiguration config) {
		if (game != null) {
			config.set("board.location.world", game.getSpawn().getWorld().getName());
			config.set("board.location.x", game.getSpawn().getBlockX());
			config.set("board.location.y", game.getSpawn().getBlockY());
			config.set("board.location.z", game.getSpawn().getBlockZ());
		}
	}

	@Override
	public void load(FileConfiguration config) {
		if (config.isConfigurationSection("board")) {
			game = new GameBoard(Minigames.plugin);
			World world = Bukkit.getWorld(config.getString("board.location.world"));
			game.setSpawn(new Location(world, config.getInt("board.location.x"), config.getInt("board.location.y"), config.getInt("board.location.z")));
			game.initialize(this);
			hasInitted = true;
		}
	}

	@Override
	public void addEditMenuOptions(Menu menu) {
	}

	@Override
	public boolean displayMechanicSettings(Menu previous) {
		Menu menu = new Menu(6, getMinigame().getName(false), previous.getViewer());
		menu.addItem(difficulty.getMenuItem("Difficulty", Material.DIAMOND_SWORD, Arrays.asList("The difficulty of the board. ", "0 is the easiest and 3, the hardest.", "This only effects the time available to move."), 0, 3));
		menu.addItem(fallDepth.getMenuItem("Fall Depth", Material.FEATHER, Arrays.asList("The distance below the spawn ", "that a player must pass below to ", "be considered off the board"), 1, 255));
		menu.addItem(floorDepth.getMenuItem("Floor Depth", Material.BRICK, 1, 255));
		menu.addItem(material.getMenuItem("Board Material", Material.STAINED_CLAY, Arrays.asList("The base material the board", "is made of. The colour ", "of that material will be randomized")));
		menu.addItem(spectatorHeight.getMenuItem("Spectate Height", Material.GLASS, Arrays.asList("The height above the board", "that spectators will hover at")));
		menu.addItem(superSigns.getMenuItem("Super Sign Count", Material.SIGN, Arrays.asList("The number of super signs", "this game will attempt to use.", "They must be named as such ", "'colormatch_<minigame><#>'", "where <minigame> is the name ", "of the minigame and ", "<#> is the number of the sign ", "starting from 1"), 0, 10));
		menu.addItem(idleMessage.getMenuItem("Idle Message", Material.BOOK, Arrays.asList("The message super signs", "will display after the game")));
		menu.addItem(roundsPerGame.getMenuItem("Rounds per Game", Material.DIODE, 0, Integer.MAX_VALUE));
		menu.addItem(roundWaitTime.getMenuItem("Round Wait Time", Material.WATCH, Arrays.asList("The time between the colour", "being removed and a new round", "begining in seconds"), 0, 200));
		menu.addItem(postGameTime.getMenuItem("Post Game Time", Material.WATCH, Arrays.asList("The time in seconds to", "wait after the game has", "ended"), 0, 200));
		
		menu.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), menu.getSize() - 9);
		menu.displayMenu(previous.getViewer());
		return true;
	}
	
	public int getDifficulty() {
		return difficulty.getFlag();
	}
	
	public int getFallDepth() {
		return fallDepth.getFlag();
	}
	
	public int getSpectateHeight() {
		return spectatorHeight.getFlag();
	}
	
	public int getSuperSignCount() {
		return superSigns.getFlag();
	}
	
	public int getRoundsPerGame() {
		return roundsPerGame.getFlag();
	}
	
	public Material getBoardMaterial() {
		if (material.getFlag() == null) {
			return material.getDefaultFlag();
		}
		return material.getFlag();
	}
	
	public String getIdleMessage() {
		return idleMessage.getFlag();
	}
	
	public long getRoundWaitTime() {
		return roundWaitTime.getFlag() * 1000;
	}
	
	public long getPostGameTime() {
		return postGameTime.getFlag() * 1000;
	}
	
	public GameBoard getGame() {
		if (!getMinigame().getMechanicName().equals("colormatch"))
			return null;
		
		if (!hasInitted && game != null) {
			game.initialize(this);
		}
		
		return game;
	}
	
	public void setGame(GameBoard game) {
		this.game = game;
		game.initialize(this);
		hasInitted = true;
	}
	
	public static ColorMatchModule getModule(Minigame minigame) {
		return (ColorMatchModule)minigame.getModule("ColorMatch");
	}
}
