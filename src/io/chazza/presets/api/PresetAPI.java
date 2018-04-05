package io.chazza.presets.api;

import io.chazza.presets.Presets;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Created by Chazmondo
 */
public class PresetAPI {

    private static Presets core;
    public PresetAPI(){
        core = (Presets) JavaPlugin.getProvidingPlugin(Presets.class);
    }

    public static Presets getCore() {
        return core;
    }

    public static List<Preset> getPresets(){
        return core.getPresets();
    }

    public static String getDefaultPrimary(){
        return core.getConfig().getString("default.primary");
    }

    public static String getDefaultSecondary(){
        return core.getConfig().getString("default.secondary");
    }

    public static Preset getDefault(){
        return new Preset("Default", getColor(getDefaultPrimary()), getColor(getDefaultSecondary()));
    }

    public static Preset getUserPreset(UUID id){
        if(core.getUserPresets().containsKey(id)){
            return core.getUserPresets().get(id);
        } else {
            UserManager um = new UserManager(id);
            Preset p;
            try {
                p = getPreset(um.getConfig().getString("preset"));
                core.getUserPresets().put(id, p);
            } catch (NoSuchElementException e){
                p = getDefault();
            }
            return p;
        }
    }

    private static Preset getPreset(String id){
        return getPresets().stream().filter(name -> name.getId().equalsIgnoreCase(id)).findFirst().get();
    }

    public static ChatColor getColor(String s){
        if(s == null || s.isEmpty()) return ChatColor.BLACK;

        if(s.contains("&") || s.contains("§")){
            return ChatColor.getByChar(s.replace("&", "").replace("§", ""));
        } else {
            return ChatColor.valueOf(s.toUpperCase());
        }
    }
}
