package me.mistergone.AWizardDidIt.Listeners;

import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.UUID;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;


public class MagicListener implements Listener {

    private Wizardry wizardry;

    public MagicListener(Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {
        wizardry.addWizardPlayer( new WizardPlayer( event.getPlayer() ) );
        wizardry.getWizardPlayer( event.getPlayer().getUniqueId() ).loadSavedPlayerData();
    }

    @EventHandler
    public void onPlayerQuit( PlayerQuitEvent event ) {
        UUID uuid = event.getPlayer().getUniqueId();
        wizardry.getWizardPlayer( uuid ).savePlayerData();
        wizardry.removeWizardPlayer( uuid );
    }

    @EventHandler
    public void onEntityDamage( EntityDamageEvent event) {
        if ( event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL ) {
            WizardPlayer wizardPlayer = wizardry.getWizardPlayer( event.getEntity().getUniqueId() );
            if ( wizardPlayer.checkSpell("Mighty Leap") ) {
                double damageCost = event.getDamage() / 2000;
                Bukkit.broadcastMessage( String.valueOf( event.getDamage() ) + ", " + String.valueOf( damageCost ) + ", "
                 + String.valueOf( wizardPlayer.getWizardPower() ) );
                if ( wizardPlayer.spendWizardPower( damageCost ) ) {
                    event.setDamage( 0 );

                    event.setCancelled( true );
                    if ( wizardPlayer.checkMsgCooldown( "Mighty Leap (Damage)" ) == false ) {
                        wizardPlayer.getPlayer().sendMessage(ChatColor.YELLOW + "The magic of Mighty Leap has protected you from harm... this time...");
                        wizardPlayer.addMsgCooldown( "Mighty Leap (Damage)", 60 );
                    }
                } else {
                    wizardPlayer.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "You lack the Wizard Power to be protected from the fall!");
                }
                wizardPlayer.removeSpell("Mighty Leap");
            }
        }
    }

    /**
     * If the server tries to toggle off glide, a player with Magic Leap gets to glide
     * @param event A toggleGlide event
     */
    @EventHandler
    public void onGlideToggle( EntityToggleGlideEvent event ) {
        if ( event.getEntity() instanceof Player ) {
            WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( event.getEntity().getUniqueId() );
            if ( wizardPlayer.getSpells().contains( "Cloud Rider (Gliding)" ) && !wizardPlayer.getPlayer().isOnGround() ) {
                event.setCancelled( true );
            }
        }
    }

    @EventHandler
    public void onPlayerMove( PlayerMoveEvent event ) {
        WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( event.getPlayer().getUniqueId() );
        if ( event.getPlayer().isOnGround() == true && wizardPlayer.checkSpell( "Cloud Rider (Gliding)" ) ) {
            wizardPlayer.removeSpell( "Cloud Rider (Gliding)" );
        }
    }

    @EventHandler
    public void onPlayerXP( PlayerExpChangeEvent event ) {
        int amount = event.getAmount();
        WizardPlayer wizardPlayer = wizardry.getWizardPlayer( event.getPlayer().getUniqueId() );
        wizardPlayer.gainWizardPower( (double)amount / 1000 );
        wizardPlayer.showWizardBar();
    }

}
