package au.com.addstar.colormatch;

import au.com.addstar.colormatch.patterns.CustomPattern;
import au.com.addstar.colormatch.patterns.PatternRegistry;
import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

class PatternSelectionTool implements ToolMode {

    private Location corner1;
    private Location corner2;

    @Override
    public String getName() {
        return "CMPATTERN";
    }

    @Override
    public String getDisplayName() {
        return "Pattern Selection";
    }

    @Override
    public String getDescription() {
        return "Selects areas of custom patterns";
    }

    @Override
    public Material getIcon() {
        return Material.GREEN_GLAZED_TERRACOTTA;
    }

    @Override
    public void onSetMode(MinigamePlayer player, MinigameTool tool) {
    }

    @Override
    public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
    }

    private void printState(MinigamePlayer player) {
        if (corner1 == null || corner2 == null)
            return;

        if (corner1.getBlockY() != corner2.getBlockY()) {
            player.sendMessage("Both corners must be on the same Y level. 3D platforms are not supported", MinigameMessageType.ERROR);
            return;
        }

        int width = Math.abs(corner1.getBlockX() - corner2.getBlockX()) + 1;
        int height = Math.abs(corner1.getBlockZ() - corner2.getBlockZ()) + 1;

        player.sendInfoMessage(ChatColor.GOLD + "Size: " + width + "x" + height + ". " + (width * height) + " blocks total");
    }

    @Override
    public void onLeftClick(final MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
        if (player.getPlayer().isSneaking()) {
            if (corner1 != null && corner2 != null && corner1.getBlockY() == corner2.getBlockY()) {
                final CustomPattern pattern = CustomPattern.createFrom(corner1, corner2);

                // Use a conversation to get the pattern name
                Conversation conversation = new ConversationFactory(ColorMatch.plugin)
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return "> Please enter the name for this new pattern";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                if (input.contains(" ") || input.contains(".") || input.contains("|")) {
                                    player.sendMessage("Name contains invalid values", MinigameMessageType.ERROR);
                                } else {
                                    try {
                                        PatternRegistry.addPattern(input, pattern);
                                        PatternRegistry.save(input, pattern);
                                        player.sendInfoMessage("Pattern saved");
                                    } catch (IllegalArgumentException e) {
                                        player.sendMessage("A pattern with that name already exists", MinigameMessageType.ERROR);
                                    }
                                }
                                return Prompt.END_OF_CONVERSATION;
                            }
                        })
                        .withLocalEcho(false)
                        .buildConversation(player.getPlayer());

                conversation.begin();
            } else {
                printState(player);
            }
            return;
        }
        if (event.hasBlock()) {
            corner1 = event.getClickedBlock().getLocation();
            player.sendInfoMessage(String.format("[ColorMatch] Corner 1 set %d,%d,%d", corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ()));
            printState(player);
        }
    }

    @Override
    public void onRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
        if (event.hasBlock()) {
            corner2 = event.getClickedBlock().getLocation();
            player.sendInfoMessage(String.format("[ColorMatch] Corner 2 set %d,%d,%d", corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ()));
            printState(player);
        }
    }

    @Override
    public void onEntityLeftClick(MinigamePlayer player, Minigame minigame, Team team, EntityDamageByEntityEvent event) {

    }

    @Override
    public void onEntityRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEntityEvent event) {

    }

    @Override
    public void select(MinigamePlayer player, Minigame minigame, Team team) {
    }

    @Override
    public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
    }

}
