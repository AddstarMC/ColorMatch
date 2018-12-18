package com.comze_instancelabs.colormatch;

import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/12/2018.
 */
public class Colors  {
    private static List<ColourList> colours = new ArrayList<>();

    static {
        List<Material> red = new ArrayList<>();
        List<Material> pink = new ArrayList<>();
        List<Material> yellow = new ArrayList<>();
        List<Material> cyan = new ArrayList<>();
        List<Material> lime = new ArrayList<>();
        List<Material> green = new ArrayList<>();
        List<Material> blue = new ArrayList<>();
        List<Material> black = new ArrayList<>();
        List<Material> brown = new ArrayList<>();
        List<Material> white = new ArrayList<>();
        List<Material> gray = new ArrayList<>();
        List<Material> light_gray = new ArrayList<>();
        List<Material> magenta = new ArrayList<>();
        List<Material> light_blue = new ArrayList<>();
        List<Material> purple = new ArrayList<>();

        red.add(Material.RED_CONCRETE);
        red.add(Material.RED_CONCRETE_POWDER);
        red.add(Material.RED_GLAZED_TERRACOTTA);
        red.add(Material.RED_STAINED_GLASS);
        red.add(Material.RED_STAINED_GLASS_PANE);
        red.add(Material.RED_TERRACOTTA);
        red.add(Material.RED_WOOL);
        colours.add(new ColourList(DyeColor.RED,red));
        pink.add(Material.PINK_CONCRETE);
        pink.add(Material.PINK_CONCRETE_POWDER);
        pink.add(Material.PINK_GLAZED_TERRACOTTA);
        pink.add(Material.PINK_STAINED_GLASS);
        pink.add(Material.PINK_STAINED_GLASS_PANE);
        pink.add(Material.PINK_TERRACOTTA);
        pink.add(Material.PINK_WOOL);
        colours.add(new ColourList(DyeColor.PINK,pink));

        yellow.add(Material.YELLOW_CONCRETE);
        yellow.add(Material.YELLOW_CONCRETE_POWDER);
        yellow.add(Material.YELLOW_GLAZED_TERRACOTTA);
        yellow.add(Material.YELLOW_STAINED_GLASS);
        yellow.add(Material.YELLOW_STAINED_GLASS_PANE);
        yellow.add(Material.YELLOW_TERRACOTTA);
        yellow.add(Material.YELLOW_WOOL);
        colours.add(new ColourList(DyeColor.YELLOW,yellow));

        List<Material> orange = new ArrayList<>();
        orange.add(Material.ORANGE_CONCRETE);
        orange.add(Material.ORANGE_CONCRETE_POWDER);
        orange.add(Material.ORANGE_GLAZED_TERRACOTTA);
        orange.add(Material.ORANGE_STAINED_GLASS);
        orange.add(Material.ORANGE_STAINED_GLASS_PANE);
        orange.add(Material.ORANGE_TERRACOTTA);
        orange.add(Material.ORANGE_WOOL);
        colours.add(new ColourList(DyeColor.ORANGE,orange));

        lime.add(Material.LIME_CONCRETE);
        lime.add(Material.LIME_CONCRETE_POWDER);
        lime.add(Material.LIME_GLAZED_TERRACOTTA);
        lime.add(Material.LIME_STAINED_GLASS);
        lime.add(Material.LIME_STAINED_GLASS_PANE);
        lime.add(Material.LIME_TERRACOTTA);
        lime.add(Material.LIME_WOOL);
        colours.add(new ColourList(DyeColor.LIME,lime));

        cyan.add(Material.CYAN_CONCRETE);
        cyan.add(Material.CYAN_CONCRETE_POWDER);
        cyan.add(Material.CYAN_GLAZED_TERRACOTTA);
        cyan.add(Material.CYAN_STAINED_GLASS);
        cyan.add(Material.CYAN_STAINED_GLASS_PANE);
        cyan.add(Material.CYAN_TERRACOTTA);
        cyan.add(Material.CYAN_WOOL);
        colours.add(new ColourList(DyeColor.CYAN,cyan));

        blue.add(Material.BLUE_CONCRETE);
        blue.add(Material.BLUE_CONCRETE_POWDER);
        blue.add(Material.BLUE_GLAZED_TERRACOTTA);
        blue.add(Material.BLUE_STAINED_GLASS);
        blue.add(Material.BLUE_STAINED_GLASS_PANE);
        blue.add(Material.BLUE_TERRACOTTA);
        blue.add(Material.BLUE_WOOL);
        colours.add(new ColourList(DyeColor.BLUE,blue));

        light_blue.add(Material.LIGHT_BLUE_CONCRETE);
        light_blue.add(Material.LIGHT_BLUE_CONCRETE_POWDER);
        light_blue.add(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
        light_blue.add(Material.LIGHT_BLUE_STAINED_GLASS);
        light_blue.add(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        light_blue.add(Material.LIGHT_BLUE_TERRACOTTA);
        light_blue.add(Material.LIGHT_BLUE_WOOL);
        colours.add(new ColourList(DyeColor.LIGHT_BLUE,light_blue));

        green.add(Material.GREEN_CONCRETE);
        green.add(Material.GREEN_CONCRETE_POWDER);
        green.add(Material.GREEN_GLAZED_TERRACOTTA);
        green.add(Material.GREEN_STAINED_GLASS);
        green.add(Material.GREEN_STAINED_GLASS_PANE);
        green.add(Material.GREEN_TERRACOTTA);
        green.add(Material.GREEN_WOOL);
        colours.add(new ColourList(DyeColor.GREEN,green));

        purple.add(Material.PURPLE_CONCRETE);
        purple.add(Material.PURPLE_CONCRETE_POWDER);
        purple.add(Material.PURPLE_GLAZED_TERRACOTTA);
        purple.add(Material.PURPLE_STAINED_GLASS);
        purple.add(Material.PURPLE_STAINED_GLASS_PANE);
        purple.add(Material.PURPLE_TERRACOTTA);
        purple.add(Material.PURPLE_WOOL);
        colours.add(new ColourList(DyeColor.PURPLE,purple));

        magenta.add(Material.MAGENTA_CONCRETE);
        magenta.add(Material.MAGENTA_CONCRETE_POWDER);
        magenta.add(Material.MAGENTA_GLAZED_TERRACOTTA);
        magenta.add(Material.MAGENTA_STAINED_GLASS);
        magenta.add(Material.MAGENTA_STAINED_GLASS_PANE);
        magenta.add(Material.MAGENTA_TERRACOTTA);
        magenta.add(Material.MAGENTA_WOOL);
        colours.add(new ColourList(DyeColor.MAGENTA,magenta));

        light_gray.add(Material.LIGHT_GRAY_CONCRETE);
        light_gray.add(Material.LIGHT_GRAY_CONCRETE_POWDER);
        light_gray.add(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
        light_gray.add(Material.LIGHT_GRAY_STAINED_GLASS);
        light_gray.add(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        light_gray.add(Material.LIGHT_GRAY_TERRACOTTA);
        light_gray.add(Material.LIGHT_GRAY_WOOL);
        colours.add(new ColourList(DyeColor.LIGHT_GRAY,light_gray));

        gray.add(Material.GRAY_CONCRETE);
        gray.add(Material.GRAY_CONCRETE_POWDER);
        gray.add(Material.GRAY_GLAZED_TERRACOTTA);
        gray.add(Material.GRAY_STAINED_GLASS);
        gray.add(Material.GRAY_STAINED_GLASS_PANE);
        gray.add(Material.GRAY_TERRACOTTA);
        gray.add(Material.GRAY_WOOL);
        colours.add(new ColourList(DyeColor.GRAY,gray));

        black.add(Material.BLACK_CONCRETE);
        black.add(Material.BLACK_CONCRETE_POWDER);
        black.add(Material.BLACK_GLAZED_TERRACOTTA);
        black.add(Material.BLACK_STAINED_GLASS);
        black.add(Material.BLACK_STAINED_GLASS_PANE);
        black.add(Material.BLACK_TERRACOTTA);
        black.add(Material.BLACK_WOOL);
        colours.add(new ColourList(DyeColor.BLACK,black));

        white.add(Material.WHITE_CONCRETE);
        white.add(Material.WHITE_CONCRETE_POWDER);
        white.add(Material.WHITE_GLAZED_TERRACOTTA);
        white.add(Material.WHITE_STAINED_GLASS);
        white.add(Material.WHITE_STAINED_GLASS_PANE);
        white.add(Material.WHITE_TERRACOTTA);
        white.add(Material.WHITE_WOOL);
        colours.add(new ColourList(DyeColor.WHITE,white));

        brown.add(Material.BROWN_CONCRETE);
        brown.add(Material.BROWN_CONCRETE_POWDER);
        brown.add(Material.BROWN_GLAZED_TERRACOTTA);
        brown.add(Material.BROWN_STAINED_GLASS);
        brown.add(Material.BROWN_STAINED_GLASS_PANE);
        brown.add(Material.BROWN_TERRACOTTA);
        brown.add(Material.BROWN_WOOL);
        colours.add(new ColourList(DyeColor.BROWN,brown));

    }

    public static DyeColor getColour(Material material){
        for(ColourList list:colours){
            if(list.materials.contains(material))
                return list.colour;
        }
        return null;
    }

    public static Material getColour(Material base, DyeColor color){
        String baseName = base.name().toUpperCase();
        String raw = baseName.replace("WHITE","");
        for(ColourList list:colours){
            if(list.colour.equals(color)){
                for(Material mat: list.materials){
                    if(mat.name().toUpperCase().contains(raw)){
                        return mat;
                    }
                }
            }
        }
        return null;
    }

    private static class ColourList {
         DyeColor colour;
         List<Material> materials;
         ColourList(DyeColor color, List<Material> materials ){
             this.colour = color;
             this.materials = materials;
         }
    }
}
