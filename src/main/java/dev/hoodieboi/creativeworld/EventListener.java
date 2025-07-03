package dev.hoodieboi.creativeworld;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EventListener implements Listener {

    private final CreativeWorld plugin;

    public EventListener(CreativeWorld plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerAdvancementCriterionGrant(PlayerAdvancementCriterionGrantEvent event) {
        if (event.getAdvancement().getKey().getNamespace().equalsIgnoreCase("minecraft")
                && plugin.getCreativeWorlds().contains(event.getPlayer().getWorld()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        World fromWorld = event.getFrom().getWorld();
        World toWorld = event.getTo().getWorld();
        // From creative to survival
        if (plugin.getCreativeWorlds().contains(fromWorld)
                && plugin.getSurvivalWorlds().contains(toWorld)) {
            // Notify location manager
            plugin.getLocationManager().changedWorld(event.getPlayer(), "creative");
        // From survival to creative
        } else if (plugin.getSurvivalWorlds().contains(fromWorld)
                && plugin.getCreativeWorlds().contains(toWorld)) {
            // Notify location manager
            plugin.getLocationManager().changedWorld(event.getPlayer(), "survival");
        }
    }
}
