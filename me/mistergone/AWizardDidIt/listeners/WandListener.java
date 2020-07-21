package me.mistergone.AWizardDidIt.listeners;

import me.mistergone.AWizardDidIt.*;
import me.mistergone.AWizardDidIt.baseClasses.*;
import me.mistergone.AWizardDidIt.helpers.*;
import me.mistergone.AWizardDidIt.patterns.EnchantWand;
import me.mistergone.AWizardDidIt.patterns.WizardDust;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;

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

            if ( main != null && ( main.getType() == Material.STICK ) &&  main.getAmount() == 1 ) {
                WizardPlayer wizardPlayer = wizardry.getWizardPlayer(e.getPlayer().getUniqueId());

                // If a stick is used on a chest, it might be a Magic Pattern
                if ( clickedMaterial == Material.CHEST ) {
                    e.setCancelled(true);
                    Chest chest = (Chest) clickedBlock.getState();

                    MagicChest magicChest = new MagicChest(chest);
                    Material key = magicChest.getKey();
                    MagicPattern magicPattern = wizardry.getMagicPattern( key );
                    Boolean wandOrEnchantOrDust = magicPattern instanceof EnchantWand || WandHelper.isActuallyAWand(main)
                            || magicPattern instanceof WizardDust;

                    // Run the MagicFunction
                    if ( magicPattern != null && magicPattern.getMagicFunction() != null && wandOrEnchantOrDust ) {
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
                    } else if (WandHelper.isActuallyAWand(main)) {
                        p.sendMessage(ChatColor.RED + "You are not wielding a magic wand!");
                        return;
                    }

                }
                if ( WandHelper.isActuallyAWand( main ) ) {

                    // If you just wave a magic wand around, magic might happen!
                    ItemStack offItem = p.getInventory().getItemInOffHand();
                    MagicSpell magicSpell = null;

                    if ( clickedBlock != null ) {
                         // If you hit a SIGN, then do a sign thing
                        // If you hit a block with your wand, let's see if there's a WizardPassage on the other side
                        Boolean clickedSign = Tag.WALL_SIGNS.isTagged( clickedMaterial ) || Tag.SIGNS.isTagged( clickedMaterial );
                        Boolean signOtherSide = SignHelper.hasSignOpposite( clickedBlock, e.getBlockFace() );
                        ArrayList<Block> signs = SignHelper.getAttachedSigns( clickedBlock );

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
                    // If you swing a wand in the air with no reagent, do some stuff
                    if ( offItem == null || offItem.getType() == Material.AIR ) {
                        // Show the wizard bar
                        wizardPlayer.showWizardBar();
                        // Show the direction to your spawn
                        Location spawn = p.getBedSpawnLocation();
                        if ( spawn == null ) spawn = p.getWorld().getSpawnLocation();
                        Location ploc = p.getEyeLocation();
                        double y = ploc.getY() - .25;
                        ploc = ploc.add( ploc.getDirection().multiply( 3 ) );
                        ploc.setY( y );
                        Vector v = spawn.toVector().subtract( ploc.toVector() );
                        SpecialEffects.magicLine( ploc, v, Particle.SPELL_WITCH );
                        return;
                    }

                    // Now we check for a magic spell with the reagent
                    magicSpell = wizardry.getMagicSpell(offItem.getType().toString());
                    if (magicSpell == null) {
                        p.sendMessage( offItem.getType().toString());
                        p.sendMessage(ChatColor.RED + "No spell found for this reagent!");
                        return;
                    } else if ( magicSpell != null && magicSpell.getSpellFunction() != null ) {
                        e.setCancelled( true );
                        try {
                            SpellFunction function = magicSpell.getSpellFunction();
                            function.setPlayer(p);
                            function.setWizardPlayer( wizardPlayer );
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
