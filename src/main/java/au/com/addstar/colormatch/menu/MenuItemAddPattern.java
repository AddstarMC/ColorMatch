package au.com.addstar.colormatch.menu;

import au.com.mineauz.minigames.menu.MenuItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

class MenuItemAddPattern extends MenuItem {

    private final PatternListMenu menu;

    public MenuItemAddPattern(String name, Material displayItem, PatternListMenu menu) {
        super(name, displayItem);
        this.menu = menu;
    }

    @Override
    public ItemStack onClick() {

        PatternSelectMenu selectMenu = new PatternSelectMenu(menu);
        selectMenu.show();

        return getItem();
    }
}
