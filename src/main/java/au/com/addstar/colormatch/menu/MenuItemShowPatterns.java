package au.com.addstar.colormatch.menu;

import au.com.addstar.colormatch.GameBoard;
import au.com.mineauz.minigames.menu.MenuItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemShowPatterns extends MenuItem {
    private final GameBoard game;

    public MenuItemShowPatterns(String name, List<String> description, Material displayItem, GameBoard game) {
        super(name, description, displayItem);
        this.game = game;
    }

    public MenuItemShowPatterns(String name, Material displayItem, GameBoard game) {
        super(name, displayItem);
        this.game = game;
    }

    @Override
    public ItemStack onClick() {
        PatternListMenu menu = new PatternListMenu(game, getContainer());
        menu.show(getContainer().getViewer());

        return getItem();
    }
}
