package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ToolFunction extends PatternFunction {
    public ItemStack tool;
    public ToolFunction toolFunction;
    public BlockBreakEvent blockBreakEvent;

    public void setTool(ItemStack tool) {
        this.tool = tool;

    }

    public void setBlockBreakEvent( BlockBreakEvent event ) {
        this.blockBreakEvent= event;
    }
}
