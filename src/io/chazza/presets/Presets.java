package io.chazza.presets;

import co.aikar.commands.BukkitCommandManager;
import io.chazza.presets.api.Preset;
import io.chazza.presets.api.PresetAPI;
import io.chazza.presets.api.UserManager;
import io.chazza.presets.command.MainCommand;
import io.chazza.presets.command.SelectCommand;
import io.chazza.presets.hook.ClipPlaceholderAPIHook;
import io.chazza.presets.hook.MVdWPlaceholderAPIHook;
import io.chazza.presets.util.ColorUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.*;

/**
 * Created by Chazmondo
 */
public class Presets extends JavaPlugin {
    private List<Preset> presets;
    public List<Preset> getPresets() {
        return presets;
    }

    public void debug(String msg){
        getLogger().info("[DEBUG] " + msg);
    }


    private Map<UUID, Preset> userPresets;
    public Map<UUID, Preset> getUserPresets() {
        return userPresets;
    }

    public void handleReload(){
        reloadConfig();
        presets = new ArrayList<>();
        Preset preset;
        for(String presetStr : getConfig().getConfigurationSection("preset").getKeys(false)){
            preset = new Preset(presetStr.toLowerCase(), PresetAPI.getColor(getConfig().getString("preset."+presetStr+".primary")),
                PresetAPI.getColor(getConfig().getString("preset."+presetStr+".secondary")));
            presets.add(preset);
        }
        registerBook();
    }

    private ItemStack book;
    public ItemStack getBook(){
        return book;
    }

    private void registerBook(){
        BookUtil.BookBuilder bookBuilder = BookUtil.writtenBook()
            .author("Server")
            .title("Preset Selector");

        BookUtil.PageBuilder pageBuilder = new BookUtil.PageBuilder();
        pageBuilder.add(ColorUtil.translate(getConfig().getString("message.title")));
        pageBuilder.newLine();
        pageBuilder.newLine();

        pageBuilder.add("➤ ")
            .add(BookUtil.TextBuilder.of(WordUtils.capitalize("Default"))
                .color(PresetAPI.getColor(PresetAPI.getDefaultPrimary()))
                .style(ChatColor.BOLD)
                .onClick(BookUtil.ClickAction.runCommand("/presets select default"))
                .onHover(BookUtil.HoverAction.showText("§fClick to select preset!"))
                .build());
        pageBuilder.newLine();

        for(Preset preset : getPresets()){
            pageBuilder.add("➤ ")
                .add(BookUtil.TextBuilder.of(WordUtils.capitalize(ColorUtil.translate(preset.getId())))
                    .color(PresetAPI.getColor(preset.getPrimaryColor()))
                    .style(ChatColor.BOLD)
                    .onClick(BookUtil.ClickAction.runCommand("/presets select " + preset.getId()))
                    .onHover(BookUtil.HoverAction.showText("§fClick to select preset!"))
                    .build());
            pageBuilder.newLine();
        }

        bookBuilder.pages(pageBuilder.build());
        this.book = bookBuilder.build();
    }

    @Override
    public void onEnable(){
        saveDefaultConfig();
        userPresets = new HashMap<>();

        BukkitCommandManager bcm = new BukkitCommandManager(this);
        new MainCommand(this, bcm);
        new SelectCommand(this, bcm);
        new PresetAPI();

        presets = new ArrayList<>();
        Preset preset;
        String primary, secondary;
        for(String presetStr : getConfig().getConfigurationSection("preset").getKeys(false)){
            primary = getConfig().getString("preset."+presetStr+".primary");
            secondary = getConfig().getString("preset."+presetStr+".secondary");

            preset = new Preset(presetStr.toLowerCase(), PresetAPI.getColor(primary), PresetAPI.getColor(secondary));
            presets.add(preset);
        }

        if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
            new MVdWPlaceholderAPIHook().hook(this);
            getLogger().info("MVdWPlaceholderAPI Hooked.");
        }

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new ClipPlaceholderAPIHook(this, "presets").hook();
            getLogger().info("PlaceholderAPI Hooked.");
        }

        registerBook();
    }

    @Override
    public void onDisable(){
        for(Map.Entry<UUID, Preset> user : getUserPresets().entrySet()){
            UserManager um = new UserManager(user.getKey());
            um.getConfig().set("preset", user.getValue().getId());
            um.save();
        }
        presets.clear();
        userPresets.clear();
    }
}
