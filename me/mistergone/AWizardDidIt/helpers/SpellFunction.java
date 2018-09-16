package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpellFunction extends MagicFunction {
    public ItemStack reagent;
    public Block clickedBlock;
    public PlayerInteractEvent event;

    public Boolean expendReagent() {
        ItemStack offhand = player.getInventory().getItemInOffHand();
        if ( offhand.getType() != this.reagent.getType() ) {
            return false;
        } else {
            if ( offhand.getAmount() > 1 ) {
                offhand.setAmount( offhand.getAmount() - 1 );
            } else if ( offhand.getAmount() == 1 ) {
                player.getInventory().setItemInOffHand( null );
            }
            return true;
        }
    }

    public void setReagent( ItemStack item) {
        this.reagent = item;
    }

    public void setClickedBlock( Block block) {
        this.clickedBlock = block;
    }

    public void setEvent( PlayerInteractEvent event ) {
        this.event = event;
    }


}
