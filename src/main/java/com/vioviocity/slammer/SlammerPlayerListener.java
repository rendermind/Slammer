package com.vioviocity.slammer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

public class SlammerPlayerListener implements Listener {
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Slammer.checkSlammed(event.getPlayer().getName()))
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
	//Slammer.log.severe(event.getPlayer().getName());
        if (Slammer.checkSlammed(event.getPlayer().getName())) {
	    Slammer.log.severe(event.getPlayer().getName());
            event.setCancelled(true);
	}
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (Slammer.checkSlammed(event.getPlayer().getName()))
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (Slammer.checkSlammed(event.getPlayer().getName()))
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (Slammer.checkSlammed(event.getPlayer().getName()))
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        
        // entity damages jailed player
        if (event.getEntity() instanceof Player) {
            Player target = (Player)event.getEntity();
            if (Slammer.checkSlammed(target.getName()))
                event.setCancelled(true);
        }
        
        // jailed player damages entity
        if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent)event.getEntity().getLastDamageCause();
            if (event2.getDamager() instanceof Player) {
                Player source = (Player)event2.getDamager();
                if (Slammer.checkSlammed(source.getName()))
                    event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        
        // check if player is jailed
        if (!Slammer.checkSlammed(event.getPlayer().getName()))
            return;
        
        // respawn player to jail
        
    }
    
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
	if (Slammer.checkSlammed(event.getPlayer().getName()))
            event.setCancelled(true);
    }
}