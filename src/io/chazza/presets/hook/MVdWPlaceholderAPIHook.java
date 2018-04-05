package io.chazza.presets.hook;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import io.chazza.presets.Presets;
import io.chazza.presets.api.Preset;
import io.chazza.presets.api.PresetAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MVdWPlaceholderAPIHook {

    public void hook(Presets core) {
        PlaceholderAPI.registerPlaceholder(core, "presets_primary", e -> {
            Player player = e.getPlayer();
            OfflinePlayer offlinePlayer = e.getPlayer();

            if (offlinePlayer == null) {
                return PresetAPI.getDefaultPrimary();
            }

            Preset preset = PresetAPI.getUserPreset(player.getUniqueId());
            return preset.getPrimaryColor();
        });

        PlaceholderAPI.registerPlaceholder(core, "presets_secondary", e -> {
            Player player = e.getPlayer();
            OfflinePlayer offlinePlayer = e.getPlayer();

            if (offlinePlayer == null) {
                return PresetAPI.getDefaultSecondary();
            }

            Preset preset = PresetAPI.getUserPreset(player.getUniqueId());
            return preset.getSecondaryColor();
        });

        PlaceholderAPI.registerPlaceholder(core, "presets_name", e -> {
            Player player = e.getPlayer();
            OfflinePlayer offlinePlayer = e.getPlayer();

            if (offlinePlayer == null) {
                return "Default";
            }
            return PresetAPI.getUserPreset(player.getUniqueId()).getId();
        });

    }
}
