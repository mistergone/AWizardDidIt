package me.mistergone.AWizardDidIt.baseClasses;

import me.mistergone.AWizardDidIt.baseClasses.MagicFunction;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignFunction extends MagicFunction {
    public Block clickedBlock;
    public PlayerInteractEvent event;
    public Block signBlock;
    public BlockState state;
    public String[] lines;

    public void setClickedBlock( Block block) {
        this.clickedBlock = block;
    }
    public void setEvent( PlayerInteractEvent event ) { this.event = event; }
    public void setSignBlock( Block block) {
        this.signBlock = block;
    }
    public void setState( BlockState state ) {
        this.state = state;
    }
    public void setLines( String[] lines ) {
        this.lines = lines;
    }
}
