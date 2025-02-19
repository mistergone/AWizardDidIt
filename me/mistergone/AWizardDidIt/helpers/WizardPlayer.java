package me.mistergone.AWizardDidIt.helpers;

import me.mistergone.AWizardDidIt.AWizardDidIt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.*;

public class WizardPlayer {
    Player player;
    ArrayList<String> activeSpells;
    ArrayList<ItemStack> deathItems;
    ArrayList<Inventory> wizardVaults;
    Map< String, Long > messageCooldowns;
    Map< String, Long > spellCooldowns;
    Map< String, Long > transmuteCooldowns;
    BossBar wizardBar;
    AWizardDidIt plugin;
    BukkitTask wizardBarTimer;
    UnseenAssistant unseenAssistant;
    HashMap< String, BukkitTask > spellTimers;
    BlockFace lastFaceToolClicked;
    Location lastKnownLocation;
    Location intendedDestination;
    Location lastDeathLocation;
    int wizardToolUses;
    int unseenEnergy;
    long lastSaved;

    int wizardPower;
    int wizardLevel;
    int wizardExp;
    int recentWizardExp;
    long wizardExpCooldown;

    public WizardPlayer( Player p ) {
        this.player = p;
        this.activeSpells = new ArrayList<>();
        this.spellCooldowns = new HashMap<>();
        this.transmuteCooldowns = new HashMap<>();
        this.messageCooldowns = new HashMap<>();
        this.deathItems = new ArrayList<>();
        this.wizardVaults = new ArrayList<>();
        this.unseenAssistant = new UnseenAssistant( this );
        this.wizardBar = Bukkit.createBossBar( "Wizard Power", BarColor.BLUE, BarStyle.SEGMENTED_20 );
        this.wizardBar.addPlayer( this.player );
        this.wizardBar.setVisible( false );
        this.spellTimers = new HashMap<>();
        this.plugin = (AWizardDidIt)Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt");
        this.lastSaved = System.currentTimeMillis();
        this.wizardLevel = 0;
        this.wizardExp = 0;
        this.recentWizardExp = 0;
        this.wizardExpCooldown = 0;
        this.wizardToolUses = 0;
        this.lastKnownLocation = player.getLocation();
        this.lastDeathLocation = null;

        //set up wizardVaults
        for( int i = 0; i < 10; i++ ) {
            wizardVaults.add( Bukkit.createInventory( this.player, 54 ) );
        }
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

    /**
     * Remove an active spell after a set time
     * @param spellName Name of spell to be removed from activeSpells
     * @param timer Ticks to wait to remove it
     */
    public void setSpellTimer( String spellName, int timer ) {
        if ( this.spellTimers.get( spellName ) != null  ) {
            this.spellTimers.get( spellName ).cancel();
        }

        BukkitTask task = Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(
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
        this.addSpell( spellName );
    }

    /*****##### messageCooldowns methods #####*****/
    // Message cooldowns prevent a player from being spammed by spell messages

    /**
     * Send message and add cooldown
     * @param key (String) A short string for the message "key"
     * @param message (String) The message
     * @param cooldown (long) The cooldown in seconds
     * @return Boolean True if the message was sent, false if it's on cooldow
     */
    public Boolean sendMsgWithCooldown( String key, String message, long cooldown ) {
        if ( !checkMsgCooldown( key ) ) {
            player.sendMessage( message );
            addMsgCooldown( key, cooldown );
            return true;
        } else {
            return false;
        }
    }

    /**
     * Send title and add cooldown
     * @param key (String) A short string for the message "key"
     * @param title (String) The title
     * @param subtitle (String) The subtitle
     * @param cooldown (long) The cooldown in seconds
     * @return Boolean True if the message was sent, false if it's on cooldow
     */
    public Boolean sendTitleWithCooldown( String key, String title, String subtitle, long cooldown ) {
        if ( !checkMsgCooldown( key ) ) {
            player.sendTitle( title, subtitle, 5, 20 , 5 );
            addMsgCooldown( key, cooldown );
            return true;
        } else {
            return false;
        }
    }

    /**
     * Alert player when a spell is cast
     * @param spellname (String) spell name, displayed as Title
     * @param message (String) message, for subtitle
     */
    public void spellAlert( String spellname, String message ) {
        player.sendTitle( ChatColor.AQUA + spellname, ChatColor.DARK_AQUA + message, 5, 20, 5 );
    }

    /**
     * Alert player when a spell is cast
     * @param spellname (String) spell name, displayed as Title, used for key
     * @param message (String) message, for subtitle
     * @param cooldown (long) The cooldown in seconds
     */
    public Boolean spellAlertWithCooldown( String spellname, String message, long cooldown ) {
        if ( !checkMsgCooldown( spellname ) ) {
            player.sendTitle( ChatColor.AQUA + spellname + "!", ChatColor.DARK_AQUA + message, 5, 30, 5 );
            addMsgCooldown( spellname, cooldown );
            return true;
        } else {
            return false;
        }


    }

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

    /******** SPELL COOLDOWNS ***********/

    /**
     * Set a spell cooldown
     * @param name The name (String) of the spell
     * @param l The time (in seconds) to wait before the spell can be cast again
     */
    public void addSpellCooldown( String name, long l ) {
        this.spellCooldowns.put(
                name,
                System.currentTimeMillis() + ( l * 1000 )
        );
    }

    /**
     * Check if a spell is on cooldown.
     * NOTE: This method will also remove the entry from the array if the cooldown has passed.
     * @param name The name (String) of the spell
     * @return True if the spell is on cooldown, false if it is not
     */
    public Boolean checkSpellCooldown( String name ) {
        if ( this.spellCooldowns.containsKey( name ) ) {
            long expiration = this.spellCooldowns.get(name);
            if ( expiration < System.currentTimeMillis() ) {
                this.spellCooldowns.remove( name );
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Remove a spell cooldown
     * @param name The name (String) of the spell
     * @return
     */
    public Boolean removeSpellCooldown( String name ) {
        if ( this.spellCooldowns.containsKey( name ) ) {
            this.spellCooldowns.remove( name );
            return true;
        } else {
            return false;
        }
    }

    /********* TRANSMUTE COOLDOWNS **************/
    /**
     * Set a transmute cooldown
     * @param name The name (String) of the transmute
     * @param l The time (in seconds) to wait before the spell can be cast again
     */
    public void addTransmuteCooldown( String name, long l ) {
        this.transmuteCooldowns.put(
                name,
                System.currentTimeMillis() + ( l * 1000 )
        );
    }

    /**
     * Check if a transmute is on cooldown.
     * NOTE: This method will also remove the entry from the array if the cooldown has passed.
     * @param name The name (String) of the transmute
     * @return True if the transmute is on cooldown, false if it is not
     */
    public Boolean checkTransmuteCooldown( String name ) {
        if ( this.transmuteCooldowns.containsKey( name ) ) {
            long expiration = this.transmuteCooldowns.get(name);
            if ( expiration < System.currentTimeMillis() ) {
                this.transmuteCooldowns.remove( name );
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Remove a transmute cooldown
     * @param name The name (String) of the transmute
     * @return
     */
    public Boolean removeTransmuteCooldown( String name ) {
        if ( this.transmuteCooldowns.containsKey( name ) ) {
            this.transmuteCooldowns.remove( name );
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return all transmute cooldowns
     * @return HashMap of the cooldowns
     */
    public Map<String, Long> getTransmuteCooldowns() {
        return this.transmuteCooldowns;
    }

    public void sendTitle( String title, String subtitle ) {
        ChatColor color = ChatColor.WHITE;
        switch(title) {
            case "Error!":
                color = ChatColor.RED;
                break;
        }

        this.player.sendTitle( color + title, subtitle );
    }


    /**********##### Death items #####***********/
    /**
     * Add item to deathList
     */
    public void addDeathItem( ItemStack i ) {
        this.deathItems.add( i );
    }

    /**
     * Get deathItems
     */
    public ArrayList<ItemStack> getDeathItems() {
        return this.deathItems;
    }

    /**
     * Get WizardVaults
     */
    public ArrayList<Inventory> getWizardVaults() {
        return this.wizardVaults;
    }

    /**
     * Get WizardVaultsAs
     */
    public ArrayList<ItemStack> getWizardVaultsAsItemStacks() {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for ( Inventory v: this.wizardVaults ) {
            ItemStack[] items = v.getContents();
            for ( ItemStack s: items ) {
                stacks.add( s );
            }
        }
        return stacks;
    }

    /**
     * Get a specific Wizard Vault
     */
    public Inventory getWizardVaultByNumber( int i ) {
        return this.wizardVaults.get( i );
    }

    /**
     * Clear deathItems
     */
    public void clearDeathItems() {
        this.deathItems.clear();
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
        this.wizardBarTimer = Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(
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
     public Boolean spendWizardPower( int amount, String spellName ) {
         amount = (int) Math.floor( amount * ( .25 + this.wizardLevel / 100 * .75 ) );
         if ( this.wizardLevel < 1 ) {
             player.sendTitle("Error!" , ChatColor.DARK_RED + "You are not a wizard! ");
             return false;
         } else if ( wizardPower >= amount ) {
             wizardPower -= amount;
             showWizardBar();
             // Wizards gain 1/10th of WP expenditures as Wizard Exp
             addWizardExp( amount / 10 );
             checkLastSave();
             return true;
         } else {
             checkLastSave();
             if ( spellName != null && !this.checkMsgCooldown( spellName + "OOM" ) ) {
                 player.sendMessage( ChatColor.DARK_RED + "You do not have enough Wizard Power to invoke " + spellName + "!");
                 this.addMsgCooldown(spellName + "OOM", 5 );
             }
             return false;
         }
     }

    /**
     * Try to spend specified Wizard Tool Uses, return whether it was ultimately successful.
     * @param amount
     * @return
     */
     public Boolean spendToolUse( int amount, String spellName ) {
         while ( this.wizardToolUses <= 0 ) {
             if ( spendWizardPower( 1, spellName ) ) {
                 this.wizardToolUses += 100;
             } else {
                 return false;
             }
         }
         this.wizardToolUses -= amount;
         return true;
     }

    /**
     * Add the amount to current Wizard Power
     * @param amount The amount of Wizard Power to add
     */
     public void gainWizardPower( int amount ) {
         if ( wizardPower < 1000 ) {
             wizardPower = Math.min( 1000, wizardPower + amount );
             showWizardBar();
         }
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
     * Check when the player was last saved. If it was a while, save their data.
     */
    public void checkLastSave() {
        long now = System.currentTimeMillis();
        // If it was more than 5 minutes ago, save the data.
        if ( now > ( this.lastSaved + 1000 * 60 * 5 ) ) {
            this.savePlayerData();
        }
    }

    /**
     * Saves WizardPlayer data to file
     */
    public void savePlayerData() {
        File path = WizardryData.openWizardryFolder( "/players" );
        String fileName = player.getName() + ".yml";
        File playerDataFile = WizardryData.openWizardryFile( path, fileName );
        FileConfiguration playerDataConfig;
        if ( !playerDataFile.exists() ) {
            playerDataFile = WizardryData.createWizardryFile( path, fileName );
            playerDataConfig = new YamlConfiguration();
            try {
                playerDataConfig.createSection("Wizard Power");
                playerDataConfig.createSection("Wizard Exp");
                playerDataConfig.createSection("Recent Wizard Exp");
                playerDataConfig.createSection("Wizard Exp Cooldown");
                playerDataConfig.createSection("Transmute Cooldowns");
                playerDataConfig.createSection("Active Spells");
                playerDataConfig.createSection("Death Items");
                playerDataConfig.createSection("Wizard Vaults");
                playerDataConfig.createSection("Last Death Location");
                playerDataConfig.createSection("Last Saved");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            playerDataConfig = YamlConfiguration.loadConfiguration( playerDataFile );
        }

         // Write data to file
        try {
            playerDataConfig.set( "Wizard Power", getWizardPower() );
            playerDataConfig.set( "Wizard Exp", getWizardExp() );
            playerDataConfig.set( "Recent Wizard Exp", getRecentWizardExp() );
            playerDataConfig.set( "Wizard Exp Cooldown", getWizardExpCooldown() );
            playerDataConfig.set( "Transmute Cooldowns", getTransmuteCooldowns() );
            playerDataConfig.set( "Active Spells", getSpells() );
            playerDataConfig.set( "Death Items", getDeathItems() );
            playerDataConfig.set( "Wizard Vaults", getWizardVaultsAsItemStacks() );
            playerDataConfig.set( "Last Death Location", getLastDeathLocation() );
            playerDataConfig.save( playerDataFile );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Load saved WizardPlayer data from file
     */
    public void loadSavedPlayerData() {
        File path = WizardryData.openWizardryFolder( "/players" );
        String fileName = player.getName() + ".yml";
        File playerDataFile = WizardryData.openWizardryFile( path, fileName );
        FileConfiguration fileConfiguration = new YamlConfiguration();
        this.wizardPower = 1000;

        System.out.println( "Saving player data..." );

        if ( !playerDataFile.exists() ) {
            playerDataFile = WizardryData.createWizardryFile( path, fileName );
            this.savePlayerData();
        } else {
            try {
                fileConfiguration.load( playerDataFile );
                String power = fileConfiguration.getString( "Wizard Power");
                String wizard_exp = fileConfiguration.getString( "Wizard Exp");
                if ( wizard_exp == null ) wizard_exp = "0";
                String recent_wizard_exp = fileConfiguration.getString( "Recent Wizard Exp");
                if ( recent_wizard_exp == null ) recent_wizard_exp = "0";
                String wizard_exp_cooldown = fileConfiguration.getString( "Wizard Exp Cooldown");
                if ( wizard_exp_cooldown == null ) wizard_exp_cooldown = "0";
                ConfigurationSection tCooldownsSect = fileConfiguration.getConfigurationSection("Transmute Cooldowns");
                if ( tCooldownsSect != null ) {
                    Set<String> transmuteCooldowns = tCooldownsSect.getKeys(false);
                    for ( String t: transmuteCooldowns) {
                        if ( t == null ) continue;
                        Long cooldown = fileConfiguration.getLong( "Transmute Cooldowns." + t );
                        addTransmuteCooldown( t, cooldown );
                    }
                }
                List<String> activeSpells = fileConfiguration.getStringList( "Active Spells" );
                List<ItemStack> list = (List<ItemStack>) fileConfiguration.getList("Death Items");
                List<ItemStack> vaults = (List<ItemStack>) fileConfiguration.getList( "Wizard Vaults");
                Location deathLoc = (Location) fileConfiguration.getLocation( "Last Death Location" );
                try {
                    this.wizardPower = Integer.parseInt( power );
                    this.wizardExp = Integer.parseInt( wizard_exp );
                    this.wizardLevel = getWizardLevelByWizardExp( this.wizardExp );
                    this.recentWizardExp = Integer.parseInt( recent_wizard_exp );
                    this.wizardExpCooldown = Long.parseLong( wizard_exp_cooldown );
                    if ( this.wizardExpCooldown == 0 ) {
                        this.wizardExpCooldown = System.currentTimeMillis();
                    }
                    this.setLastDeathLocation( deathLoc );
                    for ( String str: activeSpells ) {
                        this.addSpell( str );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    for ( ItemStack i: list ) {
                        this.addDeathItem( i );
                    }

                    int vaultNumber = 0;
                    int index = 0;
                    for ( ItemStack stack: vaults ) {
                        wizardVaults.get( vaultNumber ).setItem( index, stack );
                        index++;
                        if ( index == 54 ) {
                            index = 0;
                            vaultNumber++;
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e ) {
                e.printStackTrace();
            }
        }
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


    /*****##### UNSEEN ASSISTANT STUFF #####*****/
    public UnseenAssistant getUnseenAssistant() {
        return this.unseenAssistant;
    }

    /**
     * Try to spend "Unseen Energy", return whether it was ultimately successful.
     * @param amount
     * @return
     */
    public Boolean spendUnseenEnergy( int amount, String spellName ) {
        if ( this.unseenEnergy <= 0 ) {
            if ( spendWizardPower( 1, spellName ) ) {
                this.unseenEnergy += 100;
            } else {
                return false;
            }
        }
        this.unseenEnergy -= amount;
        return true;
    }


    /*****##### Last Known Location #####*****/
    // This exists to help unstick stuck players on WizardElevator, etc

    public Location getLastKnownLocation() {
        return this.lastKnownLocation;
    }

    public void setLastKnownLocation( Location loc ) {
        this.lastKnownLocation = loc;
    }

    /*****##### Intended destination #####*****/
    // This helps us get players to their destinations when trouble occurs.

    public Location getIntendedDestination() { return this.intendedDestination; }

    public void setIntendedDestination( Location loc ) { this.intendedDestination = loc; }

    /*****##### Intended destination #####*****/
    // This lets us tell the player where they died last

    public Location getLastDeathLocation() { return this.lastDeathLocation; }

    public void setLastDeathLocation( Location loc ) { this.lastDeathLocation = loc; }


    /****** Wizard Exp Functions ********/

    /**
     * Get the player's current Wizard Power
     * @return the player's current Wizard Power
     */
    public int getWizardLevel() {
        return this.wizardLevel;
    }

    /**
     * Get the player's current Wizard Exp
     * @return the player's current Wizard Exp
     */
    public int getWizardExp() {
        return this.wizardExp;
    }

    /**
     * Get the player's recent Wizard Exp
     * @return the player's recent Wizard Exp
     */
    public int getRecentWizardExp() {
        return this.recentWizardExp;
    }

    /**
     * Get the players Wizard Exp cooldown
     * @return long representing the last cooldown timestamp
     */
    public long getWizardExpCooldown() {
        return this.wizardExpCooldown;
    }

    /**
     * Set the players Wizard Exp cooldown
     */
    public void setWizardExpCooldown( long timestamp) {
        this.wizardExpCooldown = timestamp;
    }


    /**
     * Set the player's current Wizard Power
     */
    public void setWizardLevel( int level ) {
        if ( this.wizardLevel != level ) {
            this.wizardLevel = level;
        }
    }

    public static int getWizardLevelByWizardExp( int exp ) {
        // Wizard Levels cost 10 times the Exp as regular Minecraft levels
        int level = ExpHelper.getLevelBelow( exp / 10 );
        // Level 1 represents 0 Wizard EXP, so we always add 1 level
        return level + 1;
    }

    public void addWizardExp( int newExp ) {
        if ( this.wizardLevel == 0 ) return;
        checkWizardExpCooldownReset();
        if ( this.recentWizardExp < 200 && this.recentWizardExp + newExp > 200 ) {
            this.recentWizardExp = 200;
        }
        if ( this.recentWizardExp >= 200 && this.recentWizardExp < 300 ) {
            newExp = 1;
        }
        this.wizardExp += newExp;
        this.recentWizardExp += newExp;
        if ( this.wizardExp > 449999 ) this.wizardExp = 449999;
        int checkLevel = getWizardLevelByWizardExp( this.wizardExp );
        if ( checkLevel != this.wizardLevel ) {
            this.wizardLevel = checkLevel;
            this.player.sendTitle("", "You have reached Wizard Level " + String.valueOf( checkLevel ) );
        }
        checkLastSave();
    }

    private void checkWizardExpCooldownReset() {
        long cooldown = 24 * 60 * 60 * 1000; // 24 hours
        if ( this.wizardExpCooldown + cooldown < System.currentTimeMillis() ) {
            this.wizardExpCooldown = System.currentTimeMillis();
            this.recentWizardExp = 0;
        }
    }

}
