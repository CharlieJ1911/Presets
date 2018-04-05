package io.chazza.presets.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.chazza.presets.Presets;
import io.chazza.presets.api.Preset;
import io.chazza.presets.api.PresetAPI;
import io.chazza.presets.api.UserManager;
import io.chazza.presets.util.ColorUtil;
import org.bukkit.entity.Player;

import java.util.NoSuchElementException;

/**
 * Created by Chazmondo
 */
@CommandAlias("presets")
public class SelectCommand extends BaseCommand {

    private Presets core;
    public SelectCommand(Presets core, BukkitCommandManager cmdManager){
        this.core = core;
        cmdManager.registerCommand(this, true);
    }

    @Subcommand("select")
    public void onCommand(Player p, String presetStr) {
        UserManager um = new UserManager(p.getUniqueId());

        if(p.hasPermission("presets.select")){
            if(presetStr.equalsIgnoreCase("default")){
                p.sendMessage(ColorUtil.translate(core.getConfig().getString("message.default")));
                core.getUserPresets().put(p.getUniqueId(), PresetAPI.getDefault());
            } else {
                try {
                    Preset preset = core.getPresets().stream().filter(name -> name.getId()
                        .equalsIgnoreCase(presetStr)).findFirst().get();

                    p.sendMessage(ColorUtil.translate(core.getConfig().getString("message.selected")
                    .replace("%name%", preset.getId())));
                    core.getUserPresets().put(p.getUniqueId(), preset);
                } catch (NoSuchElementException e) {
                    p.sendMessage(ColorUtil.translate(core.getConfig().getString("message.invalid")));
                }
            }
            um.save();
        } else {
            p.sendMessage(ColorUtil.translate(core.getConfig().getString("message.permission")));
        }
    }
}
