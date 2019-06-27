package me.mistergone.AWizardDidIt.baseClasses;

import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ToolFunction extends PatternFunction {
    public ItemStack tool;
    public BlockBreakEvent blockBreakEvent;
    public PlayerInteractEvent playerInteractEvent;

}
