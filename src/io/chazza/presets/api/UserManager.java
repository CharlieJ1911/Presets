package io.chazza.presets.api;

import io.chazza.presets.Presets;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Chazmondo
 */
public class UserManager {

    private final UUID uuid;
    private final File userData;
    private final Presets core;
    private YamlConfiguration yamlConfig;

    public UserManager(UUID uuid) {
        this.core = (Presets) JavaPlugin.getProvidingPlugin(Presets.class);
        this.uuid = uuid;
        this.userData = new File(core.getDataFolder() + File.separator + "user-data" + File.separator + uuid + ".yml");
        userData.getParentFile().mkdirs();

        if(!userData.exists()){
            try {
                userData.createNewFile();
            } catch (IOException e) {
                core.debug(e.getLocalizedMessage());
            }
        }

        this.yamlConfig = YamlConfiguration.loadConfiguration(userData);
    }

    public File getUserData() {
        return userData;
    }

    public YamlConfiguration getConfig(){
        return yamlConfig;
    }

    public void save(){
        try {
            getConfig().save(userData);
        } catch (IOException e) {
            core.debug(e.getLocalizedMessage());
        }
    }

    public void reload(){
        this.yamlConfig = YamlConfiguration.loadConfiguration(userData);
    }

    public void delete(){
        userData.delete();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Presets getCore() {
        return core;
    }
}
