package me.mistergone.AWizardDidIt.baseClasses;

import me.mistergone.AWizardDidIt.baseClasses.MagicFunction;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignFunction extends MagicFunction {
    public Block clickedBlock;
    public PlayerInteractEvent event;

    // TODO: Pass the sign State to the function because the Listener already has it
    public void setClickedBlock( Block block) {
        this.clickedBlock = block;
    }
    public void setEvent( PlayerInteractEvent event ) {
        this.event = event;
    }
}
