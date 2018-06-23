package au.com.addstar.colormatch;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.StartMinigameEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanicBase;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.EnumSet;
import java.util.List;

class ColorMatchMechanic extends GameMechanicBase {

    private GameBoard getGame(Player player) {
        MinigamePlayer mplayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);
        if (mplayer.isInMinigame()) {
            return getGame(mplayer.getMinigame());
        }

        return null;
    }

    private GameBoard getGame(Minigame minigame) {
        ColorMatchModule module = ColorMatchModule.getModule(minigame);
        if (module != null)
            return module.getGame();
        return null;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GameBoard game = getGame(player);
        if (game == null)
            return;

        if (game.getMinigame().getState() != MinigameState.STARTED)
            return;

        if (game.isSpectator(player)) {
            Location spectateSpawn = game.getSpectatorSpawn();

            if (player.getLocation().getY() < spectateSpawn.getY() - 0.5 || player.getLocation().getY() > spectateSpawn.getY() + 0.5) {
                Location newLoc = player.getLocation();
                newLoc.setY(spectateSpawn.getY());
                MinigamePlayer mplayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);
                mplayer.teleport(newLoc);
                mplayer.getPlayer().setFlying(true);
            }
        } else {
            Location spawn = game.getPlayerSpawn();
            if (player.getLocation().getY() < spawn.getY() - game.getModule().getFallDepth()) {
                Location loc = player.getLocation();
                player.getWorld().spawnParticle(Particle.BLOCK_DUST, loc.getX(), loc.getY(), loc.getZ(), 1, 1, 1, 60, 20);
                game.killPlayer(player);
            }
        }
    }

    @EventHandler
    private void onMinigameStart(StartMinigameEvent event) {
        GameBoard board = getGame(event.getMinigame());
        if (board == null)
            return;

        board.start();
    }

    @Override
    public String getMechanic() {
        return "colormatch";
    }

    @Override
    public EnumSet<MinigameType> validTypes() {
        return EnumSet.of(MinigameType.MULTIPLAYER);
    }

    private void sendInfo(MinigamePlayer player, String message) {
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage(message);
        } else {
            player.sendInfoMessage(message);
        }
    }

    private void sendWarn(MinigamePlayer player, String message) {
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage(message);
        } else {
            player.sendMessage(message, MinigameMessageType.ERROR);
        }
    }

    @Override
    public boolean checkCanStart(Minigame minigame, MinigamePlayer player) {
        GameBoard game = getGame(minigame);
        if (game == null) {
            sendWarn(player, "ColorMatch arena not setup. Use /cm setup " + minigame.getName(false) + " to set it up.");
            return false;
        }

        if (game.getSpawn() == null) {
            sendWarn(player, "Board position is not set");
            return false;
        }

        if (game.getPatternMap().getPatterns().isEmpty()) {
            sendWarn(player, "There are no defined patterns");
            return false;
        }

        // TODO: Check state
        return true;
    }

    @Override
    public MinigameModule displaySettings(Minigame minigame) {
        return ColorMatchModule.getModule(minigame);
    }

    @Override
    public void startMinigame(Minigame minigame, MinigamePlayer player) {
    } // Only for global minigames

    @Override
    public void stopMinigame(Minigame minigame, MinigamePlayer player) {
    } // Only for global minigames

    @Override
    public void onJoinMinigame(Minigame minigame, MinigamePlayer player) {
        GameBoard game = getGame(minigame);
        if (game == null)
            return;

        game.joinArena(player.getPlayer());
    }

    @Override
    public void quitMinigame(Minigame minigame, MinigamePlayer player, boolean forced) {
        GameBoard game = getGame(minigame);
        if (game == null)
            return;

        game.leaveArena(player.getPlayer());
    }

    @Override
    public void endMinigame(Minigame minigame, List<MinigamePlayer> winners, List<MinigamePlayer> losers) {
        GameBoard board = getGame(minigame);
        if (board == null)
            return;

        board.stop();
    }
}
