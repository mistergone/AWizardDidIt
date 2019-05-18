package me.mistergone.AWizardDidIt.Listeners;

import me.mistergone.AWizardDidIt.*;
import me.mistergone.AWizardDidIt.helpers.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import me.mistergone.AWizardDidIt.patterns.EnchantWand;
import me.mistergone.AWizardDidIt.patterns.WizardFood;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class WandListener implements Listener {
    private Wizardry wizardry;

    public WandListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        EquipmentSlot h = e.getHand();

        if ( h != null && h == EquipmentSlot.HAND ) {
            ItemStack main =  e.getItem();
            if ( main != null &&  main.getType() == Material.STICK &&  main.getAmount() == 1) {
                WizardPlayer wizardPlayer = wizardry.getWizardPlayer(e.getPlayer().getUniqueId());

                // If a stick is used on a chest, it might be a Magic Pattern
                if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.CHEST) {
                    e.setCancelled(true);
                    Chest chest = (org.bukkit.block.Chest) e.getClickedBlock().getState();
                    MagicChest magicChest = new MagicChest(chest);
                    String[] pattern = magicChest.getPattern();

                    MagicPattern magicPattern = wizardry.getMagicPattern(pattern);

                    Boolean wandOrEnchant = magicPattern instanceof EnchantWand || MagicWand.isActuallyAWand(main);

                    // Run the MagicFunction
                    if (magicPattern != null && magicPattern.getMagicFunction() != null && wandOrEnchant) {
                        try {
                            PatternFunction function = magicPattern.getMagicFunction();
                            function.setPlayer(p);
                            function.setMagicWand(main);
                            function.setMagicChest(magicChest);
                            function.call();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    } else if (magicPattern == null) {
                        p.sendMessage(ChatColor.RED + "No magic pattern was found inside this chest!");
                    } else if (MagicWand.isActuallyAWand(main)) {
                        p.sendMessage(ChatColor.RED + "You are not wielding a magic wand!");
                    }

//                } else if ( e.getClickedBlock()!= null && e.getClickedBlock().getType() == Material.WALL_SIGN ) {
//                    // Signs might do cool stuff
//                    Block b = e.getClickedBlock();
//                    BlockState state = b.getState();
//                    Sign sign = (Sign)state;


                } else if ( MagicWand.isActuallyAWand( main )) {
                    // If you just wave a magic wand around, magic might happen!
                    ItemStack offItem = p.getInventory().getItemInOffHand();
                    MagicSpell magicSpell = null;

                    if ( offItem == null || offItem.getType() == Material.AIR ) {
                        e.setCancelled(true);
                        wizardPlayer.showWizardBar();
                    } else {
                        magicSpell = wizardry.getMagicSpell(offItem.getType().toString());
                        if (magicSpell == null) {
                            e.setCancelled(true);
                            // If there's Wizard Food in the off hand, eat it!
                            if ( WizardFood.isWizardFood( offItem ) ){
                                WizardFood.eatWizardFood( offItem, wizardPlayer );
                                if ( offItem.getAmount() > 1 ) {
                                    offItem.setAmount( offItem.getAmount() - 1 );
                                } else if ( offItem.getAmount() == 1 ) {
                                    p.getInventory().setItemInOffHand( null );
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "No spell found for this reagent!");
                            }
                        } else if ( magicSpell != null && magicSpell.getSpellFunction() != null ) {
                            e.setCancelled( true );
                            try {
                                SpellFunction function = magicSpell.getSpellFunction();
                                function.setPlayer(p);
                                function.setClickedBlock(e.getClickedBlock());
                                function.setEvent( e );
                                function.setMagicWand( main );
                                function.setReagent(offItem);
                                function.call();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }


                    }

                }

            }

        }
    }

}
