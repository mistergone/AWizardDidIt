package me.mistergone.AWizardDidIt.listeners;

import com.mysql.jdbc.Buffer;
import me.mistergone.AWizardDidIt.Wizardry;
import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ChestListener implements Listener {
    private Wizardry wizardry;

    public ChestListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler
    public void onInventoryOpenEvent( InventoryOpenEvent event ) {

    }
}
