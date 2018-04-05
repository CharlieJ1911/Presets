package io.chazza.presets.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import io.chazza.presets.Presets;
import io.chazza.presets.util.ColorUtil;
import org.bukkit.entity.Player;

/**
 * Created by Chazmondo
 */
@CommandAlias("presets")
public class ReloadCommand extends BaseCommand {

    private Presets core;
    public ReloadCommand(Presets core, BukkitCommandManager cmdManager){
        this.core = core;
        cmdManager.registerCommand(this, true);
    }

    @Subcommand("reload")
    public void onCommand(Player p) {
        if(p.hasPermission("presets.reload")){
            core.handleReload();
            p.sendMessage(ColorUtil.translate(core.getConfig().getString("message.reloaded")));
        } else {
            p.sendMessage(ColorUtil.translate(core.getConfig().getString("message.permission")));
        }
    }
}
