package com.comze_instancelabs.colormatch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import au.com.addstar.signmaker.TextSign;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;

import com.comze_instancelabs.colormatch.Util.WeightedPatternMap;
import com.comze_instancelabs.colormatch.patterns.PatternBase;
import com.comze_instancelabs.colormatch.patterns.PatternRegistry;
import com.comze_instancelabs.colormatch.patterns.logic.PreRoundState;
import com.comze_instancelabs.colormatch.patterns.logic.StateEngine;

public class GameBoard {
	private Random random;
	
	private Location spawn;
	
	private WeightedPatternMap availablePatterns;
	private PatternBase currentPattern;
	private DyeColor currentColour;
	private int round;
	
	private List<Player> spectators;
	
	private StateEngine<GameBoard> engine;
	private List<Block> activeBlocks;
	private ColorMatchModule module;
	
	public GameBoard(Plugin plugin) {
		random = new Random();
		activeBlocks = new ArrayList<Block>();
		engine = new StateEngine<GameBoard>(plugin);
		availablePatterns = new WeightedPatternMap();
		spectators = new LinkedList<Player>();
		
		currentPattern = PatternRegistry.getPattern("squares");
	}
	
	public void initialize(ColorMatchModule module) {
		this.module = module;
	}
	
	public void clear() {
		for (Block block : activeBlocks) {
			block.setType(Material.AIR);
		}
		
		activeBlocks.clear();
	}
	
	public void clearExcept(DyeColor colour) {
		for (Block block : activeBlocks) {
			if (Utilities.getBlockColour(block) != colour)
				block.setType(Material.AIR);
		}
	}
	
	public Location getBoardOrigin() {
		return new Location(spawn.getWorld(), spawn.getBlockX() - currentPattern.getWidth() / 2, spawn.getBlockY(), spawn.getBlockZ() - currentPattern.getHeight() / 2);
	}
	
	public Location getPlayerSpawn() {
		return new Location(spawn.getWorld(), spawn.getX(), spawn.getY() + 2, spawn.getZ());
	}
	
	public Location getSpectatorSpawn() {
		return new Location(spawn.getWorld(), spawn.getX(), spawn.getY() + module.getSpectateHeight(), spawn.getZ());
	}
	
	public void generate() {
		clear();
		currentPattern.placeAt(getBoardOrigin(), module.getBoardMaterial(), activeBlocks, random);
	}
	
	public void broadcast(String message) {
		for (MinigamePlayer player : module.getMinigame().getPlayers()) {
			player.sendMessage(message);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void updateSigns(String message, DyeColor colour) {
		if (Main.signmaker != null) {
			for (int i = 1; i <= module.getSuperSignCount(); ++i) {
				String signName = "colormatch_" + getMinigame().getName(false) + i;
				TextSign sign = Main.signmaker.getSign(signName);
				if (sign != null) {
					sign.setMaterial(new MaterialData(getMaterial(), colour.getWoolData()));
					sign.setText(message);
					sign.redraw();
				}
			}
		}
	}
	
	private String scoreboardLastColourText = "";
	private String scoreboardLastPlayerCountText = "";
	
	public void updateScoreboard() {
		int remain = getRemainingCount();
		int lost = spectators.size();
		
		Scoreboard board = getMinigame().getScoreboardManager();
		
		// Remove player names
		for (MinigamePlayer player : getMinigame().getPlayers()) {
			board.resetScores(player.getName());
		}
		
		board.resetScores(scoreboardLastColourText);
		board.resetScores(scoreboardLastPlayerCountText);
		
		Objective objective = board.getObjective(getMinigame().getName(false));
		objective.setDisplayName(ChatColor.YELLOW + getMinigame().getName(true));
		
		objective.getScore(Utilities.translate("&7Colour")).setScore(5);
		if (engine.getCurrentState() instanceof PreRoundState) {
			scoreboardLastColourText = Utilities.dyeToChat(currentColour).toString() + ChatColor.BOLD + " " + currentColour.toString();
		} else {
			scoreboardLastColourText = Utilities.translate("&8&l -");
		}
		objective.getScore(scoreboardLastColourText).setScore(4);
		objective.getScore(Utilities.translate("&8")).setScore(3);
		objective.getScore(Utilities.translate("&7Players Left")).setScore(2);
		scoreboardLastPlayerCountText = ChatColor.YELLOW.toString() + remain + "/" + (remain + lost); 
		objective.getScore(scoreboardLastPlayerCountText).setScore(1);
	}
	
	public void joinArena(Player player) {
		if (module.getMinigame().getPlayers().size() == 1)
			updateSigns("Waiting", DyeColor.LIME);
		
		updateScoreboard();
	}
	
	public void leaveArena(Player player) {
		spectators.remove(player);
		
		if (engine.isRunning())
			engine.sendEvent("leave", player);
		else if (module.getMinigame().getPlayers().size() == 1)
			updateSigns(module.getIdleMessage(), DyeColor.RED);
		
		updateScoreboard();
	}
	
	public void setSpectator(Player player) {
		spectators.add(player);
		
		MinigamePlayer mplayer = Minigames.plugin.pdata.getMinigamePlayer(player);
		mplayer.setCanFly(true);
		player.setFlying(true);
		mplayer.teleport(getSpectatorSpawn());
		
		updateScoreboard();
	}
	
	public boolean isSpectator(Player player) {
		return spectators.contains(player);
	}
	
	public void killPlayer(Player player) {
		engine.sendEvent("kill", player);
	}
	
	public void start() {
		round = 0;
		engine.start(new PreRoundState(), this);
	}
	
	public void stop() {
		engine.end();
	}
	
	public Random getRandom() {
		return random;
	}
	
	public void setColour(DyeColor colour) {
		currentColour = colour;
	}
	
	public DyeColor getColour() {
		return currentColour;
	}
	
	public DyeColor getRandomColour() {
		return Main.colors[random.nextInt(Main.colors.length)];
	}
	
	public WeightedPatternMap getPatternMap() {
		return availablePatterns;
	}
	
	public void setCurrentPattern(PatternBase pattern) {
		currentPattern = pattern;
	}
	
	public PatternBase getCurrentPattern() {
		return currentPattern;
	}
	
	public void setSpawn(Location location) {
		spawn = location;
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public Material getMaterial() {
		return module.getBoardMaterial();
	}
	
	public Minigame getMinigame() {
		return module.getMinigame();
	}
	
	public ColorMatchModule getModule() {
		return module;
	}
	
	public int getRemainingCount() {
		return module.getMinigame().getPlayers().size() - spectators.size();
	}
	
	public void setRound(int round) {
		this.round = round;
	}
	
	public int getRound() {
		return round;
	}
}
