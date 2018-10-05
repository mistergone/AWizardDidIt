package me.mistergone.AWizardDidIt.Listeners;

import me.mistergone.AWizardDidIt.ToolPattern;
import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.helpers.ToolFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class ToolListener implements Listener {
    private Wizardry wizardry;

    public ToolListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler(priority= EventPriority.HIGH)
    public void BlockBreakEvent( BlockBreakEvent e ) {
        Player p = e.getPlayer();
        if ( p != null ) {
            ItemStack main = p.getInventory().getItemInMainHand();
            if ( main != null && main.getItemMeta() != null ) {
                List<String> lore = main.getItemMeta().getLore();
                if ( lore != null ) {
                    String loreCheck = lore.get( 0 );
                    ToolPattern toolPattern = wizardry.getToolByLore( loreCheck );
                    if ( toolPattern != null ) {
                        try {
                            ToolFunction toolFunction = toolPattern.getToolFunction();
                            toolFunction.tool = main;
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

    @EventHandler
    public void onPlayerInteractEvent( PlayerInteractEvent e ) {
        Player p = e.getPlayer();
        EquipmentSlot h = e.getHand();

        // Save BlockFace on tool interact
        if ( h != null && h == EquipmentSlot.HAND ) {
            ItemStack main = p.getInventory().getItemInMainHand();
            if ( main != null && main.getItemMeta() !=null ) {
                List<String> lore = main.getItemMeta().getLore();
                if ( lore != null ) {
                    String loreCheck = lore.get(0);
                    ToolPattern toolPattern = wizardry.getToolByLore( loreCheck );
                    if ( toolPattern != null ) {
                        WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( p.getUniqueId() );
                        wizardPlayer.setLastFaceClicked( e.getBlockFace() );
                    }
                }
            }
        }

        if ( h != null && h == EquipmentSlot.OFF_HAND ) {
            ItemStack offhand = p.getInventory().getItemInOffHand();
            ItemStack main = p.getInventory().getItemInMainHand();
            if ( offhand != null && main.getItemMeta() !=null ) {
                List<String> lore = main.getItemMeta().getLore();
                if ( lore != null ) {
                    String loreCheck = lore.get( 0 );
                    ToolPattern toolPattern = wizardry.getToolByLore( loreCheck );
                    if ( toolPattern != null &&  toolPattern.getSecondaryFunction() != null ) {
                        try {
                            ToolFunction toolFunction = toolPattern.getSecondaryFunction();
                            toolFunction.tool = main;
                            toolFunction.playerInteractEvent = e;
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
