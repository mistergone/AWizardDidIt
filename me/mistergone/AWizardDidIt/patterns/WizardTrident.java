package me.mistergone.AWizardDidIt.patterns;
import me.mistergone.AWizardDidIt.AWizardDidIt;
import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import me.mistergone.AWizardDidIt.baseClasses.WeaponFunction;
import me.mistergone.AWizardDidIt.baseClasses.WeaponPattern;
import me.mistergone.AWizardDidIt.helpers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WizardTrident extends WeaponPattern {

    public static HashMap<String, Integer> modeCosts;

    public WizardTrident() {
        patternName = "Wizard Trident";
        patterns =  new ArrayList<String[]>();
        weaponCost = 0;
        modeCosts = new HashMap<String, Integer >();

        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "TRIDENT", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );

        modeCosts.put( "Fiery Pitchfork", 2 );
        modeCosts.put( "Monster Slayer", 5 );
        modeCosts.put( "Hunting Spear", 5 );
        modeCosts.put( "Teletransference Trident", 25 );

        patternFunction = new PatternFunction(){
            @Override
            public void run() {
                ItemStack trident = magicChest.getChest().getInventory().getItem( 10 );
                ItemMeta meta = trident.getItemMeta();

                List<String> loreCheck = meta.getLore();
                if ( meta.getLore() == null ) {
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add( "Wizard Trident" );
                    lore.add( "Mode: Normal" );
                    meta.setLore( lore );
                    trident.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "This trident has been empowered!" );
                    SpecialEffects.enchantEffect( magicChest.getChest().getLocation() );

                    int[] skipCenter = { 10 };
                    magicChest.clearPattern( skipCenter );
                } else {
                    player.sendMessage( ChatColor.RED + "This trident cannot be further empowered!" );
                }
            }
        };

        weaponFunction= new WeaponFunction() {
            @Override
            public void run() {
                if ( mode.equals( "Teletransference Trident" ) ) {
//                    if ( wizardPlayer.checkSpell( "Teletransference" ) ) return;
                    String spellName = "Teletransference Trident";
                    Block hitBlock = projectileHitEvent.getHitBlock();
                    BlockFace hitFace = projectileHitEvent.getHitBlockFace();
                    Trident trident = (Trident)projectileHitEvent.getEntity();
                    if ( trident == null ) return;
                    Vector v = new Vector();
                    Location start = player.getLocation();

                    // Find the start location and vector from the original trident.
                    List<MetadataValue> vectors = trident.getMetadata( "vector") ;
                    for ( MetadataValue m : vectors ) {
                        if ( !m.getOwningPlugin().toString().contains("AWizardDidIt") ) continue;
                        if ( !( m.value() instanceof Vector ) ) return;
                        v = (Vector)m.value();
                    }
                    List<MetadataValue> locations = trident.getMetadata( "location") ;
                    for ( MetadataValue m : locations ) {
                        if ( !m.getOwningPlugin().toString().contains("AWizardDidIt") ) continue;
                        if ( !( m.value() instanceof Location ) ) return;
                        start = (Location)m.value();
                    }

                    Location destination = findSafeLanding( hitBlock, hitFace );

                    if ( destination != null ) {
                        if ( !wizardPlayer.spendWizardPower( getModeCost( spellName), spellName ) ) return;
                        wizardPlayer.sendMsgWithCooldown( spellName,
                                ChatColor.AQUA + "You have invoked " + spellName + "! Prepare for transport!",
                                10 );
                        teletransfer( wizardPlayer, start, destination, v );
                    } else {
                        player.sendMessage( ChatColor.RED + "A safe place for Teletransference could not be found!" );
                    }

                }
            }
        };

        this.weaponModes = new ArrayList<>();
        weaponModes.add( "Mode: Normal" );
        weaponModes.add( "Mode: Fiery Pitchfork" );
        weaponModes.add( "Mode: Monster Slayer" );
        weaponModes.add( "Mode: Hunting Spear" );
        weaponModes.add( "Mode: Teletransference Trident" );

        secondaryFunction= new WeaponFunction() {
            @Override
            public void run() {
                ItemStack offHand = playerInteractEvent.getPlayer().getInventory().getItemInOffHand();
                if ( MagicWand.isActuallyAWand( offHand ) ) {
                    ItemMeta meta = weapon.getItemMeta();
                    List<String> lore = meta.getLore();
                    if ( lore.size() == 1 ) {
                        lore.add( "Mode: Normal" );
                    } else {
                        int index = weaponModes.indexOf( lore.get( 1 ) ) + 1;
                        if ( index == weaponModes.size() ) {
                            index = 0;
                        }
                        lore.set( 1, weaponModes.get( index ) );
                    }
                    meta.setLore( lore );
                    weapon.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "Wizard Trident set to " + lore.get( 1 ) );
                }
            }
        };

    }

    public static int getModeCost( String mode ) {
        return (int) modeCosts.get( mode );
    }

    private Location findSafeLanding( Block hitBlock, BlockFace hitFace ) {
        Block b = hitBlock.getRelative( BlockFace.UP );
        int limit = 20;

        while ( !MoveManager.isItSafe( b ) ) {
            if ( b == null ) {
                return null;
            }
            if ( limit >= 11 ) {
                b = b.getRelative( BlockFace.UP );
            } else if ( limit == 10 ) {
                b = hitBlock.getRelative( hitFace);
            } else if ( limit > 0 ) {
                b = b.getRelative( BlockFace.DOWN );
            } else {
                return null;
            }

            limit--;
        }

        return b.getLocation();

    }

    private void teletransfer( WizardPlayer wizardPlayer, Location start, Location destination, Vector vector ) {
        AWizardDidIt plugin = (AWizardDidIt) Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt");
        Player p = wizardPlayer.getPlayer();
        final AtomicInteger stuckCount = new AtomicInteger();
        Location startLoc = p.getLocation();
        p.setAllowFlight( true );
        wizardPlayer.addSpell( "Teletransference" );

        Trident flyer = (Trident) p.getWorld().spawnEntity( start, EntityType.TRIDENT );
        flyer.setCustomName( "Teletransference Trident (Riding)" );
        flyer.addPassenger( p );
        flyer.setVelocity( vector );

        new BukkitRunnable(){
            @Override
            public void run() {
                p.setFallDistance(0f);
                Location newLoc = p.getLocation();
                Boolean movingAway = false;
                Boolean notMoving = newLoc.equals( wizardPlayer.getLastKnownLocation() );
                if (  movingAway || notMoving ) {
                    flyer.eject();
                    destination.add( .5, .5, .5 );
                    destination.setYaw( p.getLocation().getYaw() );
                    destination.setPitch( p.getLocation().getPitch() );
                    p.teleport( destination );
                    if ( p.getGameMode() != GameMode.CREATIVE ) {
                        p.setAllowFlight( false );
                    }
                    flyer.remove();
                    wizardPlayer.setSpellTimer( "Teletransference", 40 );
                    cancel();
                    return;
                }

                wizardPlayer.setLastKnownLocation( newLoc );

            }
        }.runTaskTimer( plugin, 5, 1 );
    }
}
