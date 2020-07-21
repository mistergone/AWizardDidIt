package me.mistergone.AWizardDidIt.listeners;

import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.helpers.ItemHelper;
import me.mistergone.AWizardDidIt.helpers.WandHelper;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DeathListener implements Listener {
    private Wizardry wizardry;

    public DeathListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler
    public void onPlayerDeath( PlayerDeathEvent event) {
        Player p = event.getEntity().getPlayer();
        UUID id = p.getUniqueId();
        List<ItemStack> list = event.getDrops();
        if ( list == null ) return;
        World world = event.getEntity().getWorld();
        Location loc = event.getEntity().getLocation();
        int saved = 0;

        for ( ItemStack i : list ) {
            if ( saved >= 13 ) {
                world.dropItemNaturally( loc, i );
            } else if ( WandHelper.isActuallyAWand( i ) ) {
                wizardry.getWizardPlayer(id).addDeathItem(i);
                saved++;
            } else if ( i.getType() == Material.FEATHER ) {
                i.setAmount( i.getAmount() - 1 );
                world.dropItemNaturally( loc, i );
                ItemStack f = new ItemStack( Material.FEATHER );
                f.setAmount( 1 );
                wizardry.getWizardPlayer(id).addDeathItem(f);
                saved++;
            } else if ( ItemHelper.hasWizardLore( i ) ) {
                ItemMeta meta = i.getItemMeta();
                if ( meta instanceof Damageable) {
                    int max = i.getType().getMaxDurability();
                    int damage = ((Damageable) meta).getDamage();
                    double percent = damage / max;
                    double newPercent = percent + .1;
                    if ( newPercent > 1 ) {
                        newPercent = percent + ( 1 - percent ) / 2;
                    }
                    int newDamage = (int)Math.ceil( newPercent * max );
                    ((Damageable) meta).setDamage( newDamage );
                    i.setItemMeta( meta );
                }
                wizardry.getWizardPlayer(id).addDeathItem(i);
                saved++;
            } else {
                world.dropItemNaturally( loc, i );
            }
        }
        event.getDrops().clear();
    }

    @EventHandler()
    public void onRespawn(PlayerRespawnEvent event){
        Player p = event.getPlayer();
        if ( p == null ) return;
        WizardPlayer wiz = wizardry.getWizardPlayer( p.getUniqueId() );

        if( wiz.getDeathItems().size() > 0 ) {
            for( ItemStack stack : wiz.getDeathItems() ){
                p.getInventory().addItem( stack );
            }
            p.sendMessage(ChatColor.AQUA + "Thanks to your wizardry, some of your items have been returned to you." );

            wiz.clearDeathItems();
        }
    }

}
