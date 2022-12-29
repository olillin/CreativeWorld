package dev.hoodieboi.creativeworld;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

public class LocationManager {

    private final Plugin plugin;
    private final String locationsFileName;
    public LocationManager(Plugin plugin) {
        this.plugin = plugin;
        locationsFileName = "locations.yml";
    }
    public void saveLocation(@NotNull Player player, @NotNull String key) {
        File file = new File(plugin.getDataFolder(), locationsFileName);
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        data.set(player.getUniqueId() + "." + key, player.getLocation());
        try {
            data.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public @Nullable Location getLocation(@NotNull Player player, @NotNull String key) {
        File file = new File(plugin.getDataFolder(), locationsFileName);
        if (!file.exists())
            return null;
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        return data.getLocation(player.getUniqueId() + "." + key);
    }
}
