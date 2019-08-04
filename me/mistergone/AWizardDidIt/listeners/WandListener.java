package me.mistergone.AWizardDidIt.listeners;

import me.mistergone.AWizardDidIt.*;
import me.mistergone.AWizardDidIt.baseClasses.*;
import me.mistergone.AWizardDidIt.helpers.*;
import me.mistergone.AWizardDidIt.patterns.EnchantWand;
import me.mistergone.AWizardDidIt.patterns.WizardFood;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
        Action action = e.getAction();
        Boolean isLeftClick = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;

        if ( h != null && h == EquipmentSlot.HAND && isLeftClick ) {
            ItemStack main =  e.getItem();
            Block clickedBlock = e.getClickedBlock();
            Material clickedMaterial = clickedBlock != null  ? clickedBlock.getType() : null;

            if ( main != null &&  main.getType() == Material.STICK &&  main.getAmount() == 1 ) {
                WizardPlayer wizardPlayer = wizardry.getWizardPlayer(e.getPlayer().getUniqueId());

                // If a stick is used on a chest, it might be a Magic Pattern
                if ( clickedMaterial == Material.CHEST ) {
                    e.setCancelled(true);
                    Chest chest = (Chest) clickedBlock.getState();
                    MagicChest magicChest = new MagicChest(chest);
                    String[] pattern = magicChest.getPattern();
                    MagicPattern magicPattern = wizardry.getMagicPattern(pattern);
                    Boolean wandOrEnchant = magicPattern instanceof EnchantWand || MagicWand.isActuallyAWand(main);

                    // Run the MagicFunction
                    if ( magicPattern != null && magicPattern.getMagicFunction() != null && wandOrEnchant ) {
                        try {
                            PatternFunction function = magicPattern.getMagicFunction();
                            function.setPlayer(p);
                            function.setMagicWand(main);
                            function.setMagicChest(magicChest);
                            function.call();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return;
                    } else if (magicPattern == null) {
                        p.sendMessage(ChatColor.RED + "No magic pattern was found inside this chest!");
                        return;
                    } else if (MagicWand.isActuallyAWand(main)) {
                        p.sendMessage(ChatColor.RED + "You are not wielding a magic wand!");
                        return;
                    }

                }
                if ( MagicWand.isActuallyAWand( main ) ) {

                    // If you just wave a magic wand around, magic might happen!
                    ItemStack offItem = p.getInventory().getItemInOffHand();
                    MagicSpell magicSpell = null;

                    if ( clickedBlock != null ) {

                        // If you hit a SIGN, then do a sign thing
                        // If you hit a block with your wand, let's see if there's a WizardPassage on the other side
                        Boolean clickedSign = Tag.WALL_SIGNS.isTagged( clickedMaterial ) || Tag.SIGNS.isTagged( clickedMaterial );
                        Boolean signOtherSide = BlockManager.hasSignOpposite( clickedBlock, e.getBlockFace() );

                        if ( clickedSign || signOtherSide ) {
                            BlockState state;
                            Block signBlock = clickedBlock;
                            if ( clickedSign ) {
                                state = clickedBlock.getState();
                            } else {
                                signBlock = clickedBlock.getRelative( e.getBlockFace().getOppositeFace(), 1 );
                                state = signBlock.getState();
                            }
                            Sign sign = (Sign) state;
                            String[] lines = sign.getLines();
                            if (lines[0] == null) return;
                            String signature = ChatColor.stripColor(lines[0].trim());

                            MagicSign magicSign = wizardry.getMagicSign(signature);
                            if (magicSign != null) {
                                e.setCancelled(true);
                                try {
                                    SignFunction function = magicSign.getSignFunction();
                                    function.setPlayer(p);
                                    function.setSignBlock(signBlock);
                                    function.setState(state);
                                    function.setLines(lines);
                                    function.setEvent(e);
                                    function.setMagicWand(main);
                                    function.call();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                return;
                            }
                        }
                    }
                    // If you swing a wand in the air with no reagent, show the WizardBar
                    if ( offItem == null || offItem.getType() == Material.AIR ) {
//                            e.setCancelled( true );
                        wizardPlayer.showWizardBar();
                        return;
                    }

                    // Now we check for a magic spell with the reagent
                    magicSpell = wizardry.getMagicSpell(offItem.getType().toString());
                    if (magicSpell == null) {
                        p.sendMessage(ChatColor.RED + "No spell found for this reagent!");
                        return;
                    } else if ( magicSpell != null && magicSpell.getSpellFunction() != null ) {
                        e.setCancelled( true );
                        try {
                            SpellFunction function = magicSpell.getSpellFunction();
                            function.setPlayer(p);
                            function.setClickedBlock( clickedBlock );
                            function.setEvent( e );
                            function.setMagicWand( main );
                            function.setReagent( offItem );
                            function.call();
                        } catch ( Exception ex ) {
                            ex.printStackTrace();
                        }
                        return;
                    }

                }

            }

        }
    }

}
