package me.mistergone.AWizardDidIt;

import me.mistergone.AWizardDidIt.Listeners.*;
import me.mistergone.AWizardDidIt.helpers.MagicCommands;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public class AWizardDidIt extends JavaPlugin{

    @Override
    public void onEnable(){

        getServer().getPluginManager().registerEvents(new WandListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new ToolListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new MagicListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new SignListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new DamageListener( Wizardry.getWizardry() ), this);

        MagicCommands commandHandler = new MagicCommands(this );
        getCommand( "wizardry" ).setExecutor( commandHandler );
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
