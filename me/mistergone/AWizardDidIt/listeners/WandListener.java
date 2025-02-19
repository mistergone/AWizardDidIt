package me.mistergone.AWizardDidIt.listeners;

import me.mistergone.AWizardDidIt.*;
import me.mistergone.AWizardDidIt.baseClasses.*;
import me.mistergone.AWizardDidIt.helpers.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class WandListener implements Listener {
    private Wizardry wizardry;

    public WandListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void PlayerInteractEvent(PlayerInteractEvent e) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
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
            // First, let's handle Lectern hits
            if ( clickedMaterial == Material.LECTERN ) {
                Lectern lec = (Lectern)clickedBlock.getState();
                Inventory inv = lec.getInventory();
                ItemStack itemStack = inv.getItem( 0 );
                if ( itemStack == null ) return;
                if ( itemStack.getType() != Material.WRITTEN_BOOK ) return;
                BookMeta bookMeta = (BookMeta)itemStack.getItemMeta();
                String title = "";
                if ( bookMeta.hasTitle() ) {
                    title = bookMeta.getTitle();
                }
                title = title.toLowerCase();
                Boolean enchantWand = title.contains( "enchant") && title.contains( "wand" );
                if ( WandHelper.isJustAStick( main ) && enchantWand ) {
                    e.setCancelled( true );
                    WandHelper.enchantWand( p, clickedBlock );
                    inv.setItem( 0, new ItemStack(Material.AIR));
                    ItemStack book = new ItemStack( Material.BOOK );
                    book.setAmount( 1 );
                    wizardPlayer.getPlayer().getWorld().dropItem( wizardPlayer.getPlayer().getLocation(), book );
                } else if ( WandHelper.isActuallyAWand( main ) && enchantWand ) {
                    e.setCancelled( true );
                    p.sendMessage( ChatColor.RED + "Your wand is already enchanted!");
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
                String[] lines = sign.getSide(Side.FRONT).getLines();
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

            if ( clickedMaterial == Material.SMOKER ) {
//                Bukkit.broadcastMessage( "It's a SMOKER!" );
            }

            if ( clickedMaterial == Material.BLAST_FURNACE ) {
//                Bukkit.broadcastMessage( "It's a Blast Furnace!" );
//                BlastFurnace furnace = ((BlastFurnace)clickedBlock.getState());
//                Bukkit.broadcastMessage( furnace.getInventory().getSmelting().getType().toString() );
            }
        }

        // Not a wand? Bail!
        if ( !WandHelper.isActuallyAWand( main ) ) return;

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
                SpecialEffects.magicLine( ploc, deathVector, Particle.WITCH );
            }

            // Message player with info.
            int wp = wizardPlayer.getWizardPower();
            p.sendTitle( "Wizard Level " + String.valueOf( wizardPlayer.getWizardLevel() ),
                    ChatColor.LIGHT_PURPLE + "You have " + String.valueOf( wp ) +
                    " points of Wizard Power.", 10, 20, 20 );
            wizardPlayer.sendMsgWithCooldown( "spawn", ChatColor.YELLOW + "Your spawn location is at " + String.valueOf( spawn.getBlockX() ) +
                    ", " + String.valueOf( spawn.getBlockY() ) + ", " + String.valueOf( spawn.getBlockZ() ) +
                    ChatColor.ITALIC + " (Follow the line of hearts.)", 60 );
            if ( dLoc != null ) {
                wizardPlayer.sendMsgWithCooldown( "deathloc", ChatColor.RED + "Your last death was at " + String.valueOf( dLoc.getBlockX() ) +
                        ", " + String.valueOf( dLoc.getBlockY() ) + ", " + String.valueOf( dLoc.getBlockZ() ) +
                        ChatColor.ITALIC + " (Follow the line of purple sparks.)", 60 );
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
