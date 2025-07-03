package dev.hoodieboi.creativeworld;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeleportCommand implements CommandExecutor {

    private final CreativeWorld plugin;

    public TeleportCommand(CreativeWorld plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command!");
            return true;
        }
        if (label.equalsIgnoreCase("creative")) {
            if (plugin.getCreativeWorlds().contains(player.getWorld())) {
                player.sendMessage(plugin.getMessage("already_in_creative"));
                return true;
            }
            // Teleport to creative world
            Location location = plugin.getLocationManager().getLocation(player, "creative");
            List<World> creativeWorlds = plugin.getCreativeWorlds();
            if (location == null) {
                teleportToCreativeSpawn(player);
            } else if (!creativeWorlds.contains(location.getWorld())) {
                    plugin.getLogger().warning(String.format(
                            "%s's creative world location was not in a registered creative world. Sending to creative world spawn...", player.getName()));
                teleportToCreativeSpawn(player);
            } else {
                player.teleport(location);
            }
            player.sendMessage(plugin.getMessage("teleport_creative"));
        } else if (label.equalsIgnoreCase("survival")) {
            if (plugin.getSurvivalWorlds().contains(player.getWorld())) {
                player.sendMessage(plugin.getMessage("already_in_survival"));
                return true;
            }
            // Remove player from group
            // Teleport to survival world
            Location location = plugin.getLocationManager().getLocation(player, "survival");
            List<World> survivalWorlds = plugin.getSurvivalWorlds();
            if (location == null) {
                teleportToSurvivalSpawn(player);
            } else if (!survivalWorlds.contains(location.getWorld())) {
                plugin.getLogger().warning(String.format(
                        "%s's survival world location was not in a registered survival world. Sending to survival world spawn...", player.getName()));
                teleportToSurvivalSpawn(player);
            } else {
                player.teleport(location);
            }
            player.sendMessage(plugin.getMessage("teleport_survival"));
        } else {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(command.getUsage()));
        }
        return true;
    }

    private void teleportToCreativeSpawn(Player player) {
        player.teleport(plugin.getCreativeSpawnLocation());
    }

    private void teleportToSurvivalSpawn(Player player) {
        player.teleport(plugin.getSurvivalSpawnLocation());
    }
}
