package me.mistergone.AWizardDidIt.listeners;

import me.mistergone.AWizardDidIt.Wizardry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

public class BlockListener implements Listener {
    private Wizardry wizardry;

    public BlockListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }
    
}
