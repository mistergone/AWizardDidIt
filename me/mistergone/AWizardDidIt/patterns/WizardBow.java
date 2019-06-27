package me.mistergone.AWizardDidIt.patterns;

import com.google.common.util.concurrent.AtomicDouble;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.netty.handler.codec.http2.Http2FrameLogger;
import me.mistergone.AWizardDidIt.AWizardDidIt;
import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import me.mistergone.AWizardDidIt.baseClasses.WeaponFunction;
import me.mistergone.AWizardDidIt.baseClasses.WeaponPattern;
import me.mistergone.AWizardDidIt.helpers.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WizardBow extends WeaponPattern {

    public static HashMap<String, Integer> modeCosts;

    public WizardBow() {
        patternName = "Wizard Bow";
        patterns =  new ArrayList<String[]>();
        weaponCost = 0;
        modeCosts = new HashMap<String, Integer >();

        patterns.add( new String[]
                { "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "BOW", "GLOWSTONE_DUST",
                        "GLOWSTONE_DUST", "GLOWSTONE_DUST", "GLOWSTONE_DUST" } );

        modeCosts.put( "Bow of Teletransference", 15 );
        modeCosts.put( "Bane of Darkness", 3 );

        patternFunction = new PatternFunction(){
            @Override
            public void run() {
                ItemStack bow = magicChest.getChest().getInventory().getItem( 10 );
                ItemMeta meta = bow.getItemMeta();

                List<String> loreCheck = meta.getLore();
                if ( meta.getLore() == null ) {
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add( "Wizard Bow" );
                    lore.add( "Mode: Normal" );
                    meta.setLore( lore );
                    bow.setItemMeta( meta );
                    player.sendMessage( ChatColor.GOLD + "This bow has been empowered!" );
                    SpecialEffects.enchantEffect( magicChest.getChest().getLocation() );

                    int[] skipCenter = { 10 };
                    magicChest.clearPattern( skipCenter );
                } else {
                    player.sendMessage( ChatColor.RED + "This bow cannot be further empowered!" );
                }
            }
        };

        weaponFunction= new WeaponFunction() {
            @Override
            public void run() {
                if ( mode.equals( "Bow of Teletransference" ) ) {
//                    if ( wizardPlayer.checkSpell( "Teletransference" ) ) return;
                    String spellName = "Bow of Teletransference";
                    Block hitBlock = projectileHitEvent.getHitBlock();
                    Arrow arrow = (Arrow)projectileHitEvent.getEntity();
                    if ( arrow == null ) return;
                    Vector v = new Vector();
                    Location start = player.getLocation();

                    // Find the start location and vector from the original arrow.
                    List<MetadataValue> vectors = arrow.getMetadata( "vector") ;
                    for ( MetadataValue m : vectors ) {
                        if ( !m.getOwningPlugin().toString().contains("AWizardDidIt") ) continue;
                        if ( !( m.value() instanceof Vector ) ) return;
                        v = (Vector)m.value();
                    }
                    List<MetadataValue> locations = arrow.getMetadata( "location") ;
                    for ( MetadataValue m : locations ) {
                        if ( !m.getOwningPlugin().toString().contains("AWizardDidIt") ) continue;
                        if ( !( m.value() instanceof Location ) ) return;
                        start = (Location)m.value();
                    }

                    Location destination = findSafeLanding( hitBlock );

                    if ( destination != null ) {
                        if ( !wizardPlayer.spendWizardPower( getModeCost( spellName), spellName ) ) return;
                        wizardPlayer.sendMsgWithCooldown( spellName,
                                ChatColor.AQUA + "You have invoked " + spellName + "! Prepare for transport!",
                                10 );
                        teletransfer( wizardPlayer, start, destination, v );
                    } else {
                        player.sendMessage( ChatColor.RED + "A safe place for Teletransference could not be found!" );
                    }

                } else if ( mode.equals( "Bane of Darkness" ) ) {
                    String spellName = "Bane of Darkness";
                    BlockFace faceHit = projectileHitEvent.getHitBlockFace();
                    Block torch = projectileHitEvent.getHitBlock().getRelative( faceHit );

                    if ( faceHit == BlockFace.UP || faceHit == BlockFace.DOWN ) {
                        if ( BlockManager.airTypes.contains( projectileHitEvent.getHitBlock().getRelative( BlockFace.UP ).getType() ) ) {
                            if ( !wizardPlayer.spendWizardPower( getModeCost( spellName ), spellName ) ) return;
                            wizardPlayer.sendMsgWithCooldown( spellName,
                                    ChatColor.AQUA + "You have incoked " + spellName + "! A torch has been placed where your arrow landed!",
                                    5 );
                            torch = projectileHitEvent.getHitBlock().getRelative( BlockFace.UP );
                            torch.setType( Material.TORCH );
                        }
                    } else {
                        if ( !wizardPlayer.spendWizardPower( getModeCost( spellName ), spellName ) ) return;
                        wizardPlayer.sendMsgWithCooldown( spellName,
                                ChatColor.AQUA + "You have incoked " + spellName + "! A torch has been placed where your arrow landed!",
                                5 );
                        torch.setType( Material.WALL_TORCH );
                        Directional direction = (Directional)torch.getBlockData();
                        direction.setFacing( faceHit );
                        torch.setBlockData( direction );
                    }
                    projectileHitEvent.getEntity().remove();
                }

            }
        };

        this.weaponModes = new ArrayList<>();
        weaponModes.add( "Mode: Normal" );
        weaponModes.add( "Mode: Bow of Teletransference" );
        weaponModes.add( "Mode: Bane of Darkness" );

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
                    player.sendMessage( ChatColor.GOLD + "Wizard Bow set to " + lore.get( 1 ) );
                }
            }
        };
    }

    public static int getModeCost( String mode ) {
        return (int) modeCosts.get( mode );
    }

    private Location findSafeLanding( Block hitBlock ) {
        Block b = hitBlock.getRelative( BlockFace.UP );
        int limit = 10;
        while ( !MoveManager.isItSafe( b ) ) {
            if ( b == null ) {
                return null;
            }
            b = b.getRelative( BlockFace.UP );

            limit--;
            if ( limit == 0 ) {
                return null;
            }
        }
        return b.getLocation();

    }

    private void teletransfer( WizardPlayer wizardPlayer, Location start, Location destination, Vector vector ) {
        AWizardDidIt plugin = (AWizardDidIt)Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt");
        Player p = wizardPlayer.getPlayer();
        final AtomicInteger stuckCount = new AtomicInteger();
        Location startLoc = p.getLocation();
        p.setAllowFlight( true );
        wizardPlayer.addSpell( "Teletransference" );

        Arrow flyer = (Arrow) p.getWorld().spawnEntity( start, EntityType.ARROW );
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
