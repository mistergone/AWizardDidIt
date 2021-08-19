package me.mistergone.AWizardDidIt;

import me.mistergone.AWizardDidIt.listeners.*;
import me.mistergone.AWizardDidIt.helpers.MagicCommands;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public class AWizardDidIt extends JavaPlugin{

    @Override
    public void onEnable(){

        getServer().getPluginManager().registerEvents(new BlockListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new ChestListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new DamageListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new DeathListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new MagicListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new SignListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new ToolListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new WandListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new WeaponListener( Wizardry.getWizardry() ), this);

        MagicCommands commandHandler = new MagicCommands(this );
        getCommand( "wizardry" ).setExecutor( commandHandler );

        Bukkit.getScheduler().scheduleSyncRepeatingTask(
                (AWizardDidIt)Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt"),
                new Runnable() {
                    @Override
                    public void run() {
                        for ( WizardPlayer wiz: Wizardry.getWizardry().getWizardList().values() ) {
                            double d = (double)wiz.getPlayer().getFoodLevel() / 20 * 100;
                            wiz.gainWizardPower( (int)Math.round( d ) );
                        }
                    }
                }, 600, 600 );

    }

    @Override
    public void onDisable(){
        //Fired when the server stops and disables all plugins
        Map<UUID, WizardPlayer> wizardList = Wizardry.getWizardry().getWizardList();
        for ( WizardPlayer wizardPlayer: wizardList.values() ) {
            wizardPlayer.savePlayerData();
        }

    }
}
