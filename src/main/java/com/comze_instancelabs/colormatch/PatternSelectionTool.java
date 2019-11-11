package com.comze_instancelabs.colormatch;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.comze_instancelabs.colormatch.patterns.CustomPattern;
import com.comze_instancelabs.colormatch.patterns.PatternRegistry;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;
import org.jetbrains.annotations.NotNull;

public class PatternSelectionTool implements ToolMode {

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
		return Material.WHITE_TERRACOTTA;
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
			player.sendMessage("Both corners must be on the same Y level. 3D platforms are not supported",MinigameMessageType.ERROR);
			return;
		}
		
		int width = Math.abs(corner1.getBlockX() - corner2.getBlockX()) + 1;
		int height = Math.abs(corner1.getBlockZ() - corner2.getBlockZ()) + 1;
		
		player.sendMessage( "Size: " + width + "x" + height + ". " + (width*height) + " blocks total", MinigameMessageType.INFO);
	}

	@Override
	public void onLeftClick(final MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
		if (player.getPlayer().isSneaking()) {
			if (corner1 != null && corner2 != null && corner1.getBlockY() == corner2.getBlockY()) {
				final CustomPattern pattern = CustomPattern.createFrom(corner1, corner2);
				
				// Use a conversation to get the pattern name
				Conversation conversation = new ConversationFactory(Main.plugin)
					.withFirstPrompt(new StringPrompt() {
						@NotNull
            @Override
						public String getPromptText(@NotNull ConversationContext context) {
							return "> Please enter the name for this new pattern";
						}
						
						@Override
						public Prompt acceptInput(@NotNull ConversationContext context, String input) {
							if (input.contains(" ") || input.contains(".") || input.contains("|")) {
								player.sendMessage( "Name contains invalid values", MinigameMessageType.ERROR);
							} else {
								try {
									PatternRegistry.addPattern(input, pattern);
									PatternRegistry.save(input, pattern);
									context.getForWhom().sendRawMessage("Pattern Saved");
								} catch(IllegalArgumentException e) {
									player.sendInfoMessage( "A pattern with that name already exists");
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
			player.sendInfoMessage( String.format("[ColorMatch] Corner 1 set %d,%d,%d", corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ()));
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
