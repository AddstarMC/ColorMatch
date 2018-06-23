package au.com.addstar.colormatch.patterns.logic;

import au.com.addstar.colormatch.GameBoard;
import au.com.addstar.colormatch.patterns.PatternBase;
import au.com.mineauz.minigames.MinigamePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PreRoundState extends State<GameBoard> {

    @Override
    public void onStart(StateEngine<GameBoard> engine, GameBoard game) {
        Material material = game.getMaterialGroup().getRandomMaterial();
        game.setCurrentMaterial(material);

        PatternBase pattern = game.getPatternMap().getRandom(game.getRandom());
        game.setCurrentPattern(pattern);

        game.generate();

        // Let players know what colour it is now
        ItemStack hintItem = new ItemStack(game.getCurrentMaterial(), 1);
        ItemMeta meta = hintItem.getItemMeta();
        meta.setDisplayName(game.getMaterialGroup().getMaterialName(game.getCurrentMaterial()));

        for (MinigamePlayer player : game.getMinigame().getPlayers()) {
            player.getPlayer().getInventory().clear();
            for (int i = 0; i < 9; ++i) {
                player.getPlayer().getInventory().setItem(i, hintItem);
            }

            player.updateInventory();
        }

        game.updateSigns(game.getMaterialGroup().getMaterialName(material), material);
        game.updateScoreboard();

        engine.setState(new RoundState());
    }
}
