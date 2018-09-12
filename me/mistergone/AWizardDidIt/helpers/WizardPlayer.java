package me.mistergone.AWizardDidIt.helpers;

import me.mistergone.AWizardDidIt.AWizardDidIt;
import me.mistergone.AWizardDidIt.timedTasks.TimedTask;
import net.minecraft.server.v1_13_R2.BossBattle;
import net.minecraft.server.v1_13_R2.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WizardPlayer {
    Player player;
    ArrayList<String> activeSpells;
    Map< String, Long > messageCooldowns;
    BossBar wizardBar;
    AWizardDidIt plugin;
    BukkitTask scheduler;

    public WizardPlayer( Player p ) {
        this.player = p;
        this.activeSpells = new ArrayList<>();
        this.messageCooldowns = new HashMap<>();
        this.wizardBar = Bukkit.createBossBar( "Wizard Power", BarColor.BLUE, BarStyle.SEGMENTED_20 );
        this.wizardBar.addPlayer( this.player );
        this.wizardBar.setVisible( false );
        this.plugin = (AWizardDidIt)Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt");
    }

    /**
     * Get the Player class attached to this WizardPlayer instance
     * @return The Player
     */
    public Player getPlayer() {
        return player;
    }

    /*****##### activeSpells methods #####*****/

    /**
     * Adds a spell (String) to the array of activeSpells
     * @param spell A String that is keyed to the spell
     */
    public void addSpell( String spell) {
        this.activeSpells.add( spell );
    }

    /**
     * Get the list of active spells
     * @return An array of Strings of active spells
     */
    public ArrayList<String> getSpells() {
        return this.activeSpells;
    }

    /**
     * Check to see if a spell is in the activeSpells list
     * @param spell The spell (String) to check
     * @return True if the spell is there, false if not
     */
    public Boolean checkSpell( String spell ) {
        return activeSpells.contains( spell );
    }

    /**
     * Remove a spell from the activeSpells
     * @param spell The spell (String) to remove
     * @return True if the spell was found and removed, false if it was not found
     */
    public Boolean removeSpell( String spell ) {
        if ( checkSpell( spell ) == true ) {
            this.activeSpells.remove( spell );
            return true;
        } else {
            return false;
        }
    }

    /*****##### messageCooldowns methods #####*****/
    // Message cooldowns prevent a player from being spammed by spell messages

    /**
     * Set a message cooldown for a spell
     * @param name The name (String) associated with the spell message
     * @param l The time (in seconds) to wait before sending the spell message again
     */
    public void addMsgCooldown( String name, long l ) {
        this.messageCooldowns.put(
                name,
                System.currentTimeMillis() + ( l * 1000 )
        );
    }

    /**
     * Check if a message is on cooldown.
     * NOTE: This method will also remove the entry from the array if the cooldown has passed.
     * @param name The name (String) associated with the spell message
     * @return True if the message is on cooldown, false if it is not
     */
    public Boolean checkMsgCooldown( String name ) {
        if ( this.messageCooldowns.containsKey( name ) ) {
            long expiration = this.messageCooldowns.get(name);
            if ( expiration < System.currentTimeMillis() ) {
                this.messageCooldowns.remove( name );
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Remove a message cooldown
     * @param name The name (String) associated with the spell message
     * @return
     */
    public Boolean removeMsgCooldown( String name ) {
        if ( this.messageCooldowns.containsKey( name ) ) {
            this.messageCooldowns.remove( name );
            return true;
        } else {
            return false;
        }
    }

    /*****##### WIZARD POWER! methods #####*****/

    /**
     * Shows the Wizard Power BossBar for 5 seconds
     */
    public void showWizardBar() {
        this.wizardBar.setVisible( true );
        if ( this.scheduler != null  ) {
            this.scheduler.cancel();
        }
        this.scheduler = Bukkit.getServer().getScheduler().runTaskLater(
                plugin,
                new Runnable() {
                    @Override
                    public void run() {
                        wizardBar.setVisible( false );
                    }
                },
                100
        );
    }

    /**
     * Get the Wizard Power BossBar
     * @return the Wizard Power BossBar
     */
    public BossBar getWizardBar() {
        return this.wizardBar;
    }

    /**
     * Get the player's current Wizard Power
     * @return the player's current Wizard Power
     */
     public double getWizardPower() {
        return this.wizardBar.getProgress();
     }

    /**
     * Try to spend the specified wizard power, return whether it was successful
     * @param amount The amount of wizard power to try to spend
     * @return
     */
     public Boolean spendWizardPower( double amount) {
         double wizardPower = this.wizardBar.getProgress();
        if ( wizardPower > amount ) {
            this.wizardBar.setProgress( wizardPower - amount );
            return true;
        } else {
            return false;
        }
     }

    /**
     * Add the amount to current Wizard Power
     * @param amount The amount of Wizard Power to add
     */
     public void gainWizardPower( double amount ) {
         double wizardPower = this.wizardBar.getProgress();
         this.wizardBar.setProgress( wizardPower + amount );
     }






}
