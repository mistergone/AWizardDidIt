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

import java.util.*;

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
        World world = event.getEntity().getWorld();
        Location loc = event.getEntity().getLocation();

        WizardPlayer wizardPlayer = wizardry.getWizardPlayer( id );
        wizardPlayer.setLastDeathLocation( loc );

        // Save Wizard Items on death
        if ( list != null ) {
            int saved = 0;
            // We reverse iterate so worn items are saved first
            ListIterator listIterator = list.listIterator(list.size());
            while ( listIterator.hasPrevious() ) {
                ItemStack i = (ItemStack)listIterator.previous();
                if ( saved >= 23 ) {
                    world.dropItemNaturally( loc, i );
                } else if ( WandHelper.isActuallyAWand( i ) ) {
                    wizardry.getWizardPlayer(id).addDeathItem(i);
                    saved++;
                } else if ( i.getType() == Material.FEATHER ) {
                    if ( i.getAmount() > 1 ) {
                        i.setAmount( i.getAmount() - 1 );
                        world.dropItemNaturally( loc, i );
                    }
                    ItemStack f = new ItemStack( Material.FEATHER );
                    f.setAmount( 1 );
                    wizardry.getWizardPlayer(id).addDeathItem(f);
                    saved++;
                } else if ( ItemHelper.hasWizardLore( i ) ) {
                    ItemMeta meta = i.getItemMeta();
                    if ( meta instanceof Damageable) {
                        int max = i.getType().getMaxDurability();
                        int damage = ((Damageable) meta).getDamage();
                        double percent = (double)damage / (double)max;
                        double newPercent = percent + .01;
                        if ( newPercent > 1 ) {
                            newPercent = percent + ( 1 - percent ) / 2;
                        }
                        int newDamage = (int)Math.floor( newPercent * max );
                        if ( newDamage <= 0 ) newDamage = 1;
                        ((Damageable) meta).setDamage( newDamage );
                        i.setItemMeta( meta );
                    }
                    wizardry.getWizardPlayer(id).addDeathItem(i);
                    saved++;
                } else {
                    world.dropItemNaturally( loc, i );
                }
            }
            if ( saved > 0 ) {
                String msg = ChatColor.YELLOW + "Good news! Magic has preserved some of your Wizard items!";
                msg += " They will be returned to you when you respawn.";
                if ( saved >= 23 ) {
                    msg += " Unfortunately, you had too many Wizard items to save, so some have been dropped.";
                }
                p.sendMessage( msg );
            }

            event.getDrops().clear();
        }

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
