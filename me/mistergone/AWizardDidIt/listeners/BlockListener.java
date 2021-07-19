package me.mistergone.AWizardDidIt.listeners;

import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.helpers.ChestHelper;
import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;

public class BlockListener implements Listener {
    private Wizardry wizardry;

    public BlockListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    // Prevent hoppers from taking items from Locked Chests
    @EventHandler
    public void onItemMove ( InventoryMoveItemEvent e ) {
        InventoryHolder holder = e.getSource().getHolder();
        if ( holder instanceof Chest || holder instanceof DoubleChest) {
            Chest c = null;
            if ( holder instanceof  DoubleChest ) {
                c = (Chest)((DoubleChest) holder).getLeftSide();
            } else {
                c = (Chest) holder;
            }
            if (ChestHelper.isWizardLocked( c, null) ) {
                e.setCancelled( true );
            }
        }
    }
}
