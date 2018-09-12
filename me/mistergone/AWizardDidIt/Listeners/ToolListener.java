package me.mistergone.AWizardDidIt.Listeners;

import me.mistergone.AWizardDidIt.ToolPattern;
import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.helpers.ToolFunction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ToolListener implements Listener {
    private Wizardry wizardry;

    public ToolListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler(priority= EventPriority.HIGH)
    public void BlockBreakEvent( BlockBreakEvent e ) {
        Player p = e.getPlayer();
        if ( p != null ) {
            ItemStack hand = p.getInventory().getItemInMainHand();
            if ( hand != null && hand.getItemMeta() != null ) {
                List<String> lore = hand.getItemMeta().getLore();
                if ( lore != null ) {
                    String loreCheck = lore.get( 0 );
                    ToolPattern toolPattern = wizardry.getToolByLore( loreCheck );
                    if ( toolPattern != null ) {
                        try {
                            ToolFunction toolFunction = toolPattern.getToolFunction();
                            toolFunction.blockBreakEvent = e;
                            toolFunction.player = p;
                            toolFunction.call();
                        } catch ( Exception ex ) {
                            ex.printStackTrace( );
                        }

                    }
                }
            }
        }
    }

}
