package me.mistergone.AWizardDidIt.listeners;

import me.mistergone.AWizardDidIt.baseClasses.ToolPattern;
import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.baseClasses.ToolFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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
        Action action = e.getAction();

        // Save BlockFace on tool interact
        if ( h != null && h == EquipmentSlot.HAND && ( action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR ) ) {
            ItemStack main = p.getInventory().getItemInMainHand();
            if ( main != null && main.getItemMeta() != null ) {
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

        if ( h != null &&  h == EquipmentSlot.OFF_HAND && ( action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR ) ) {
            ItemStack offhand = p.getInventory().getItemInOffHand();
            ItemStack main = p.getInventory().getItemInMainHand();
            if ( offhand != null && main.getItemMeta() != null ) {
                List<String> lore = main.getItemMeta().getLore();
                if ( lore != null ) {
                    String loreCheck = lore.get( 0 );
                    ToolPattern toolPattern = wizardry.getToolByLore( loreCheck );
                    if ( toolPattern != null &&  toolPattern.getSecondaryFunction() != null ) {
                        e.setCancelled( true );
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
