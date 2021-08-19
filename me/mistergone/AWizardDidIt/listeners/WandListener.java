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
import org.bukkit.entity.Projectile;
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

        // if the hand is null or not HAND, or it's not LeftClick, then no magic.
        if ( h == null ) return;
        if ( h != EquipmentSlot.HAND ) return;
        if ( !isLeftClick ) return;

        // Let's check out the item and the clickedBlock
        ItemStack main =  e.getItem();
        Block clickedBlock = e.getClickedBlock();
        Material clickedMaterial = clickedBlock != null  ? clickedBlock.getType() : null;
        WizardPlayer wizardPlayer = wizardry.getWizardPlayer(e.getPlayer().getUniqueId());

        // If there's no item in the main, then return
        if ( main == null ) return;

        // Let's say you hit a block...

        if ( clickedBlock != null ) {
            // First, handle the very specific case where clickedBlock is chest and main is normal stick
            if ( clickedMaterial == Material.CHEST && main.getType() == Material.STICK && main.getAmount() == 1
                    && WandHelper.isJustAStick( main ) ) {
                e.setCancelled(true);


                Chest chest = (Chest) clickedBlock.getState();

                MagicChest magicChest = new MagicChest(chest);
                Material key = magicChest.getKey();
                if ( key == null ) return;

                MagicPattern magicPattern = wizardry.getMagicPattern( key );
                if ( magicPattern instanceof EnchantWand || magicPattern instanceof WizardDust ) {
                    if ( magicPattern != null && magicPattern.getMagicFunction() != null ) {
                        try {
                            PatternFunction function = magicPattern.getMagicFunction();
                            function.setPlayer(p);
                            function.setWizardPlayer( wizardPlayer );
                            function.setMagicWand(main);
                            function.setMagicChest(magicChest);
                            function.call();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return;
                    }
                }

            }

            // Now bail if it's not a magic wand
            if ( !WandHelper.isActuallyAWand( main ) ) return;

            // Now handle all other chest clicks
            if ( clickedMaterial == Material.CHEST ) {
                e.setCancelled(true);
                Chest chest = (Chest) clickedBlock.getState();

                MagicChest magicChest = new MagicChest(chest);
                Material key = magicChest.getKey();
                if ( key == null ) {
                    p.sendMessage(ChatColor.RED + "No magic pattern was found inside this chest!");
                    return;
                }
                MagicPattern magicPattern = wizardry.getMagicPattern( key );

                // Run the MagicFunction
                if ( magicPattern != null && magicPattern.getMagicFunction() != null ) {
                    try {
                        PatternFunction function = magicPattern.getMagicFunction();
                        function.setPlayer(p);
                        function.setWizardPlayer( wizardPlayer );
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
                }
            }

            // If you hit a SIGN, then do a sign thing
            // Also, if you hit a block with your wand, let's see if there's a WizardPassage on the other side
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
        } else if ( !WandHelper.isActuallyAWand( main ) ) {
            // Not a wand? Bail!
            return;
        } else {
            // So you waved a wand but didn't hit a block...
            ItemStack offItem = p.getInventory().getItemInOffHand();
            MagicSpell magicSpell = null;

            // If you swing a wand in the air with no reagent, do some stuff
            if ( offItem == null || offItem.getType() == Material.AIR ) {
                // Show the wizard bar
                wizardPlayer.showWizardBar();
                // Show the direction to your spawn
                Location spawn = p.getBedSpawnLocation();
                if ( spawn == null ) spawn = p.getWorld().getSpawnLocation();
                Location ploc = p.getEyeLocation();
                ploc.setY( ploc.getY() - .25 );
                ploc = ploc.add( ploc.getDirection().multiply( 3 ) );
                Vector spawnVector = spawn.toVector().subtract( ploc.toVector() );
                SpecialEffects.magicLine( ploc, spawnVector, Particle.HEART );

                Location dLoc = wizardPlayer.getLastDeathLocation();
                if ( dLoc != null ) {
                    ploc = p.getEyeLocation();
                    ploc = ploc.add( ploc.getDirection().multiply( 3 ) );
                    ploc.setY( ploc.getY() - 0 );
                    Vector deathVector = dLoc.toVector().subtract( ploc.toVector() );
                    SpecialEffects.magicLine( ploc, deathVector, Particle.SPELL_WITCH );
                }

                // Message player with info.
                int wp = wizardPlayer.getWizardPower();
                p.sendMessage( ChatColor.LIGHT_PURPLE + "You have " + String.valueOf( wp ) +
                        " points of Wizard Power." );
                p.sendMessage( ChatColor.YELLOW + "Your spawn location is at " + String.valueOf( spawn.getBlockX() ) +
                        ", " + String.valueOf( spawn.getBlockY() ) + ", " + String.valueOf( spawn.getBlockZ() ) +
                        ChatColor.ITALIC + " (Follow the line of hearts.)");
                if ( dLoc != null ) {
                    p.sendMessage( ChatColor.RED + "Your last death was at " + String.valueOf( dLoc.getBlockX() ) +
                            ", " + String.valueOf( dLoc.getBlockY() ) + ", " + String.valueOf( dLoc.getBlockZ() ) +
                            ChatColor.ITALIC + " (Follow the line of purple sparks.)");
                }

                return;
            } else {
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
