package au.com.addstar.colormatch;

import au.com.addstar.colormatch.Util.WeightedPatternMap;
import au.com.addstar.colormatch.materials.MaterialGroup;
import au.com.addstar.colormatch.patterns.PatternBase;
import au.com.addstar.colormatch.patterns.PatternRegistry;
import au.com.addstar.colormatch.patterns.logic.PreRoundState;
import au.com.addstar.colormatch.patterns.logic.StateEngine;
import au.com.addstar.signmaker.TextSign;
import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GameBoard {
    private final Random random;

    private Location spawn;

    private final WeightedPatternMap availablePatterns;
    private PatternBase currentPattern;
    private Material currentMaterial;
    private int round;

    private final List<Player> spectators;

    private final StateEngine<GameBoard> engine;
    private final List<Block> activeBlocks;
    private ColorMatchModule module;
    private String scoreboardLastMaterialText = "";
    private String scoreboardLastPlayerCountText = "";

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

    private void clear() {
        for (Block block : activeBlocks) {
            block.setType(Material.AIR);
        }

        activeBlocks.clear();
    }

    public void clearExcept(Material material) {
        for (Block block : activeBlocks) {
            if (block.getType() != material)
                block.setType(Material.AIR);
        }
    }

    private Location getBoardOrigin() {
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
        currentPattern.placeAt(getBoardOrigin(), module.getBoardMaterial(), activeBlocks,
                random, currentMaterial);
    }

    public void broadcast(String message) {
        for (MinigamePlayer player : module.getMinigame().getPlayers()) {
            player.sendMessage(message, MinigameMessageType.INFO);
        }
    }

    public void updateSigns(String message, Material material) {
        if (ColorMatch.signmaker != null) {
            for (int i = 1; i <= module.getSuperSignCount(); ++i) {
                String signName = "colormatch_" + getMinigame().getName(false) + i;
                TextSign sign = ColorMatch.signmaker.getSign(signName);
                if (sign != null) {
                    sign.setMaterial(material);
                    sign.setText(message);
                    sign.redraw();
                }
            }
        }
    }

    public void updateScoreboard() {
        int remain = getRemainingCount();
        int lost = spectators.size();

        Scoreboard board = getMinigame().getScoreboardManager();

        // Remove player names
        for (MinigamePlayer player : getMinigame().getPlayers()) {
            board.resetScores(player.getName());
        }

        board.resetScores(scoreboardLastMaterialText);
        board.resetScores(scoreboardLastPlayerCountText);

        Objective objective = board.getObjective(getMinigame().getName(false));
        objective.setDisplayName(ChatColor.YELLOW + getMinigame().getName(true));

        objective.getScore(Utilities.translate("&7Colour")).setScore(5);
        if (engine.getCurrentState() instanceof PreRoundState) {
            scoreboardLastMaterialText = getMaterialGroup().getMaterialName(currentMaterial);
        } else {
            scoreboardLastMaterialText = Utilities.translate("&8&l -");
        }
        objective.getScore(scoreboardLastMaterialText).setScore(4);
        objective.getScore(Utilities.translate("&8")).setScore(3);
        objective.getScore(Utilities.translate("&7Players Left")).setScore(2);
        scoreboardLastPlayerCountText = ChatColor.YELLOW.toString() + remain + "/" + (remain + lost);
        objective.getScore(scoreboardLastPlayerCountText).setScore(1);
    }

    public void joinArena(Player player) {
        if (module.getMinigame().getPlayers().size() == 1)
            updateSigns("Waiting", Material.LIME_CONCRETE);

        updateScoreboard();
    }

    public void leaveArena(Player player) {
        spectators.remove(player);

        if (engine.isRunning())
            engine.sendEvent("leave", player);
        else if (module.getMinigame().getPlayers().size() == 1)
            updateSigns(module.getIdleMessage(), Material.RED_CONCRETE);

        updateScoreboard();
    }

    public void setSpectator(Player player) {
        spectators.add(player);

        MinigamePlayer mplayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);
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

    public Material getCurrentMaterial() {
        return currentMaterial;
    }

    public void setCurrentMaterial(Material material) {
        currentMaterial = material;
    }

    public WeightedPatternMap getPatternMap() {
        return availablePatterns;
    }

    public PatternBase getCurrentPattern() {
        return currentPattern;
    }

    public void setCurrentPattern(PatternBase pattern) {
        currentPattern = pattern;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location location) {
        spawn = location;
    }

    public MaterialGroup getMaterialGroup() {
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

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
