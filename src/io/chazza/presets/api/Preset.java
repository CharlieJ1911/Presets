package io.chazza.presets.api;

import org.bukkit.ChatColor;

/**
 * Created by Chazmondo
 */
public class Preset {

    private String id;
    private ChatColor primaryColor;
    private ChatColor secondaryColor;

    public Preset(String id, ChatColor primary, ChatColor secondary){
        this.id = id;
        this.primaryColor = primary;
        this.secondaryColor = secondary;
    }

    public String getId() {
        return id;
    }

    public String getPrimaryColor() {
        return primaryColor.toString();
    }

    public String getSecondaryColor() {
        return secondaryColor.toString();
    }
}
