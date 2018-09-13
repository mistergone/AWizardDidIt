package me.mistergone.AWizardDidIt;

import me.mistergone.AWizardDidIt.Listeners.MagicListener;
import me.mistergone.AWizardDidIt.Listeners.ToolListener;
import me.mistergone.AWizardDidIt.Listeners.WandListener;
import me.mistergone.AWizardDidIt.helpers.MagicCommands;
import org.bukkit.plugin.java.JavaPlugin;

public class AWizardDidIt extends JavaPlugin{

    @Override
    public void onEnable(){

        getServer().getPluginManager().registerEvents(new WandListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new ToolListener( Wizardry.getWizardry() ), this);
        getServer().getPluginManager().registerEvents(new MagicListener( Wizardry.getWizardry() ), this);

//        MagicCommands commandHandler = new MagicCommands(this );
//        getCommand( "wizardry" ).setExecutor( commandHandler );
    }

    @Override
    public void onDisable(){
        //Fired when the server stops and disables all plugins
    }
}
