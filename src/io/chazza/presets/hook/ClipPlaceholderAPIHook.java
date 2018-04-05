package io.chazza.presets.hook;

import io.chazza.presets.api.Preset;
import io.chazza.presets.api.PresetAPI;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ClipPlaceholderAPIHook extends EZPlaceholderHook {

    public ClipPlaceholderAPIHook(Plugin plugin, String identifier) {
        super(plugin, identifier);
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {
        if(player == null){
            return PresetAPI.getDefaultPrimary();
        }

        if(placeholder.equalsIgnoreCase("primary")){
            Preset preset = PresetAPI.getUserPreset(player.getUniqueId());
            return preset.getPrimaryColor();
        }

        if(placeholder.equalsIgnoreCase("secondary")){
            Preset preset = PresetAPI.getUserPreset(player.getUniqueId());
            return preset.getSecondaryColor();
        }

        if(placeholder.equalsIgnoreCase("name")){
            return WordUtils.capitalize(PresetAPI.getUserPreset(player.getUniqueId()).getId());
        }
        return null;
    }
}
