package me.mistergone.AWizardDidIt.helpers;

import me.mistergone.AWizardDidIt.AWizardDidIt;
import org.bukkit.Bukkit;
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
    BukkitTask mightyLeapTimer;
    File playerDataFile;
    FileConfiguration playerDataConfig;

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
    public Boolean addSpell( String spell) {
        System.out.println( spell + String.valueOf( this.activeSpells.contains( spell ) ) );
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
            this.showWizardBar();
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
         double newPower = Math.min( wizardPower + amount, 1 );
         this.showWizardBar();
         this.wizardBar.setProgress( newPower );

     }


     /*****##### Player File IO #####*****/

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
                 this.getWizardBar().setProgress( Double.parseDouble( power ) );
                 System.out.println( "Success!(?)" );
             } catch( Exception ex ) {
                 System.out.println( "Exception! " + ex.toString() );
             }
         } else {
             System.out.println( "Player file not found!" );
         }
     }

    /**
     * Remove Mighty Leap's protection after 10 seconds
     */
    public void setMightyLeapTimer() {
        if ( this.mightyLeapTimer != null  ) {
            this.mightyLeapTimer.cancel();
        }
        this.mightyLeapTimer = Bukkit.getServer().getScheduler().runTaskLater(
                plugin,
                new Runnable() {
                    @Override
                    public void run() {
                        activeSpells.remove( "Mighty Leap" );
                    }
                },
                200
        );
    }
}
