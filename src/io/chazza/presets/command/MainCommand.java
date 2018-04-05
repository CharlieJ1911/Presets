package io.chazza.presets.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import io.chazza.presets.Presets;
import io.chazza.presets.util.ColorUtil;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.book.BookUtil;
import xyz.upperlevel.spigot.book.NmsBookHelper;

/**
 * Created by Chazmondo
 */
public class MainCommand extends BaseCommand {

    private Presets core;
    public MainCommand(Presets core, BukkitCommandManager cmdManager){
        this.core = core;
        cmdManager.registerCommand(this, true);
    }

    @CommandAlias("presets")
    public void onCommand(Player p){
        if(p.hasPermission("presets.view")){
            try {
                BookUtil.openPlayer(p, core.getBook());
            } catch (NmsBookHelper.UnsupportedVersionException e){
                io.chazza.presets.util.V_1_8.BookUtil.openBook(core.getBook(), p);
            }
        } else {
            p.sendMessage(ColorUtil.translate(core.getConfig().getString("message.permission")));
        }
    }
}
