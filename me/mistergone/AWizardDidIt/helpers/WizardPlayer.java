package me.mistergone.AWizardDidIt.helpers;

import me.mistergone.AWizardDidIt.AWizardDidIt;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import javax.sound.midi.SysexMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WizardPlayer {
    Player player;
    ArrayList<String> activeSpells;
    Map< String, Long > messageCooldowns;
    BossBar wizardBar;
    AWizardDidIt plugin;
    BukkitTask wizardBarTimer;
    HashMap< String, BukkitTask > spellTimers;
    File playerDataFile;
    FileConfiguration playerDataConfig;
    BlockFace lastFaceToolClicked;
    int wizardPower;

    public WizardPlayer( Player p ) {
        this.player = p;
        this.activeSpells = new ArrayList<>();
        this.messageCooldowns = new HashMap<>();
        this.wizardBar = Bukkit.createBossBar( "Wizard Power", BarColor.BLUE, BarStyle.SEGMENTED_20 );
        this.wizardBar.addPlayer( this.player );
        this.wizardBar.setVisible( false );
        this.spellTimers = new HashMap<>();
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
    public Boolean addSpell( String spell) {
        if ( !this.activeSpells.contains( spell ) ) {
            this.activeSpells.add( spell );
            return true;
        } else {
            return false;
        }
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
        resetWizardBar();
        this.wizardBar.setVisible( true );
        if ( this.wizardBarTimer != null  ) {
            this.wizardBarTimer.cancel();
        }
        this.wizardBarTimer = Bukkit.getServer().getScheduler().runTaskLater(
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
    public void resetWizardBar() {
        this.wizardBar.setProgress( (double)this.wizardPower/1000 );
    }

    /**
     * Get the player's current Wizard Power
     * @return the player's current Wizard Power
     */
     public int getWizardPower() {
        return this.wizardPower;
     }

    /**
     * Try to spend the specified wizard power, return whether it was successful
     * @param amount The amount of wizard power to try to spend
     * @return
     */
     public Boolean spendWizardPower( int amount) {
         if ( wizardPower > amount ) {
             wizardPower -= amount;
             showWizardBar();
             return true;
         } else {
             return false;
         }
     }

    /**
     * Add the amount to current Wizard Power
     * @param amount The amount of Wizard Power to add
     */
     public void gainWizardPower( int amount ) {
         wizardPower = Math.min( 1000, wizardPower + amount );
         showWizardBar();
     }

    /**
     * Set current Wizard Power to specified amount
     * @param amount The amount at which to set Wizard Power
     */
    public void setWizardPower( double amount ) {
        wizardPower = (int)Math.max( 0, Math.min( 1000, amount ) );
        this.showWizardBar();
    }

     /*****##### Player File IO #####*****/

    /**
     * Saves WizardPlayer data to file
     */
    public void savePlayerData() {
         String path = Bukkit.getPluginManager().getPlugin( "AWizardDidIt" ).getDataFolder().toString() + "/players";
         File filePath = new File( path );
         String playerName = this.player.getDisplayName();
         playerDataConfig = new YamlConfiguration();

         if( !filePath.exists() ){
             System.out.println("File path does not exist, creating it...");
             filePath.mkdirs();
             try {
                 filePath.createNewFile();
             } catch( Exception ex ) {
                 // Oops?
             }
         }
         System.out.println("Got file path, attempting to get file...");
         playerDataFile = new File(  filePath + "/" + playerName + ".yml");

         if( !playerDataFile.exists() ){
             System.out.println("File doesnt exist, creating it...");
             try {
                 playerDataFile.createNewFile();
                 System.out.println("created it!");
             } catch( IOException ex ) {

             }
         }
         if ( playerDataFile.exists() ) {
             try {
                 playerDataConfig.createSection("Wizard Power");
                 playerDataConfig.set("Wizard Power", getWizardPower() );
                 playerDataConfig.save( playerDataFile );
             } catch (IOException e) {
             }

         }
     }

    /**
     * Load saved WizardPlayer data from file
     */
    public void loadSavedPlayerData() {
         String path = Bukkit.getPluginManager().getPlugin( "AWizardDidIt" ).getDataFolder().toString()
                 + "/players/" + player.getDisplayName() + ".yml";
         System.out.println( "Saving player data..." );
         File playerDataFile = new File( path );
         FileConfiguration fileConfiguration = new YamlConfiguration();
         if ( playerDataFile.exists() ) {
             try {
                 System.out.println( "Trying to open player file..." );
                 fileConfiguration.load( playerDataFile );
                 String power = fileConfiguration.getString( "Wizard Power");
                 this.wizardPower = 1000;
                 try {
                     this.wizardPower = Integer.parseInt( power );
                 } catch( Exception ex ) {
                 }
                 System.out.println( "Success!(?)" );
             } catch( Exception ex ) {
                 System.out.println( "Exception! " + ex.toString() );
             }
         } else {
             System.out.println( "Player file not found!" );
         }
     }

    /**
     * Remove a spellTimer after a set time
     * @param spellName Name of spell to be removed from activeSpells
     * @param timer Ticks to wait to remove it
     */
    public void setSpellTimer( String spellName, int timer ) {
        if ( this.spellTimers.get( spellName ) != null  ) {
            this.spellTimers.get( spellName ).cancel();
        }

        BukkitTask task = Bukkit.getServer().getScheduler().runTaskLater(
                plugin,
                new Runnable() {
                    @Override
                    public void run() {
                        activeSpells.remove( spellName );
                    }
                },
                timer
        );
        this.spellTimers.put( spellName, task );
    }

    /**
     * Set the last block face clicked by a tool
     */
    public void setLastFaceClicked( BlockFace face ) {
        this.lastFaceToolClicked = face;
    }

    /**
     * Get the last block face clicked by a tool
     */
    public BlockFace getLastFaceClicked() {
        return this.lastFaceToolClicked;
    }
}
