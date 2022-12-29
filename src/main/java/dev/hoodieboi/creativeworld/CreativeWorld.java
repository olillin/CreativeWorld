package dev.hoodieboi.creativeworld;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static net.kyori.adventure.text.Component.text;

public final class CreativeWorld extends JavaPlugin {

    private LocationManager locationManager;
    private List<World> survivalWorlds;
    private List<World> creativeWorlds;

    private ConfigurationSection messages;

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerCommands();
        locationManager = new LocationManager(this);
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        saveDefaultConfig();
        List<World> previousSurvivalWorlds = survivalWorlds;
        List<World> previousCreativeWorlds = creativeWorlds;
        boolean firstLoad = survivalWorlds == null || creativeWorlds == null;
        FileConfiguration config = getConfig();
        List<?> survival = config.getList("worlds.survival");
        List<?> creative = config.getList("worlds.creative");
        if (survival == null || !(survival.get(0) instanceof String)
            || creative == null || !(creative.get(0) instanceof String)) {
            if (firstLoad) {
                throw new RuntimeException("Config is invalid.");
            } else {
                getLogger().warning("Config is invalid. Cancelling reload.");
                return;
            }
        }
        survivalWorlds = getWorlds(survival);
        creativeWorlds = getWorlds(creative);
        if (survivalWorlds.isEmpty() || creativeWorlds.isEmpty()) {
            if (firstLoad) {
                throw new RuntimeException("No valid worlds could be found.");
            } else {
                getLogger().warning("No valid worlds could be found. Cancelling reload.");
                survivalWorlds = previousSurvivalWorlds;
                creativeWorlds = previousCreativeWorlds;
            }
        }

        messages = config.getConfigurationSection("messages");
    }

    private List<World> getWorlds(List<?> names) {
        return names.stream().map(name -> {
            World world = getServer().getWorld((String) name);
            if (world == null)
                getLogger().warning("World '" + name + "' does not exist.");
            return world;
        }).filter(Objects::nonNull).toList();
    }

    private void registerCommands() {
        TeleportCommand teleportCommand = new TeleportCommand(this);
        getCommand("creative").setExecutor(teleportCommand);
        getCommand("survival").setExecutor(teleportCommand);
        getCommand("creativeworld").setExecutor((sender, command, label, args) -> {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                sender.sendMessage(text("Config reloaded.").color(NamedTextColor.GREEN));
                getLogger().info("Config reloaded.");
            } else {
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(command.getUsage()));
            }
            return true;
        });

        if (CommodoreProvider.isSupported())
            registerCommodoreCompletions();
    }

    private void registerCommodoreCompletions() {
        Commodore commodore = CommodoreProvider.getCommodore(this);
        commodore.register(getCommand("creative"), literal("creative").build());
        commodore.register(getCommand("survival"), literal("survival").build());
        commodore.register(getCommand("creativeworld"), literal("creativeworld").then(literal("reload")).build());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public MVWorldManager getMVWorldManager() {
        MultiverseCore core = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
        assert core != null;
        return core.getMVWorldManager();
    }

    public List<World> getSurvivalWorlds() {
        return survivalWorlds;
    }

    public List<World> getCreativeWorlds() {
        return creativeWorlds;
    }

    public Location getCreativeSpawnLocation() {
        return getMVWorldManager().getMVWorld(creativeWorlds.get(0)).getSpawnLocation();
    }

    public Location getSurvivalSpawnLocation() {
        return getMVWorldManager().getMVWorld(survivalWorlds.get(0)).getSpawnLocation();
    }

    public Component getMessage(String key) {
        if (messages == null || !(messages.get(key) instanceof String)) {
            getLogger().warning(String.format("Could not show message: \"%s\"", key));
            return text("Could not show message ").color(NamedTextColor.RED).append(text(key).decorate(TextDecoration.ITALIC));
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(messages.getString(key));
    }
}
