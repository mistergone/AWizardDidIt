package me.mistergone.AWizardDidIt.baseClasses;

import me.mistergone.AWizardDidIt.baseClasses.MagicFunction;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpellFunction extends MagicFunction {
    public ItemStack reagent;
    public Block clickedBlock;
    public Entity clickedEntity;
    public PlayerInteractEvent event;

    public SpellFunction() {}

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

    public void setClickedEntity( Entity entity ) {
        this.clickedEntity = entity;
    }

    public void setEvent( PlayerInteractEvent event ) {
        this.event = event;
    }


}
