package me.mistergone.AWizardDidIt.listeners;

import me.mistergone.AWizardDidIt.helpers.SignHelper;
import me.mistergone.AWizardDidIt.helpers.WandHelper;
import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import me.mistergone.AWizardDidIt.signs.WizardLock;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.UUID;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;


public class MagicListener implements Listener {

    private Wizardry wizardry;

    public MagicListener(Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {
        Player p = event.getPlayer();
        wizardry.addWizardPlayer( new WizardPlayer( p ) );
        WizardPlayer wizardPlayer = wizardry.getWizardPlayer( p.getUniqueId() );
        wizardPlayer.loadSavedPlayerData();
        if ( p.isOnGround() ) {
            wizardPlayer.removeSpell( "Teletransference" );
            wizardPlayer.removeSpell( "Wizard Elevator" );
        }
    }

    @EventHandler
    public void onPlayerQuit( PlayerQuitEvent event ) {
        Player p = event.getPlayer();
        UUID uuid = p.getUniqueId();
        WizardPlayer wizardPlayer = wizardry.getWizardPlayer( uuid );
        if ( wizardPlayer.getIntendedDestination() != null ) {
            p.teleport( wizardPlayer.getLastKnownLocation() );
            wizardPlayer.removeSpell( "Teletransference" );
            wizardPlayer.removeSpell( "Wizard Elevator" );
        }

        wizardPlayer.savePlayerData();
        wizardry.removeWizardPlayer( uuid );
    }



    /**
     * If the server tries to toggle off glide, a player with Magic Leap gets to glide
     * @param event A toggleGlide event
     */
    @EventHandler
    public void onGlideToggle( EntityToggleGlideEvent event ) {
        if ( event.getEntity() != null && event.getEntity() instanceof Player ) {
            WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( event.getEntity().getUniqueId() );
            if ( wizardPlayer.getSpells().contains( "Cloud Rider (Gliding)" ) && !wizardPlayer.getPlayer().isOnGround() ) {
                event.setCancelled( true );
            }
        }
    }

    @EventHandler
    public void onPlayerMove( PlayerMoveEvent event ) {
        WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( event.getPlayer().getUniqueId() );
        if ( event.getPlayer().isOnGround() && wizardPlayer.checkSpell( "Cloud Rider (Gliding)" ) ) {
            wizardPlayer.removeSpell( "Cloud Rider" );
            wizardPlayer.removeSpell( "Cloud Rider (Gliding)" );
            // "Grounded" status prevents a race condition with damage prevention event handler
            wizardPlayer.addSpell( "Cloud Rider (Grounded)" );
        } else if ( wizardPlayer.checkSpell( "Cloud Rider (Grounded)") ) {
            wizardPlayer.removeSpell("Cloud Rider (Grounded)" );
        } else if ( wizardPlayer.checkSpell( "Teletransference" ) ) {
            wizardPlayer.removeSpell( "Teletransference" );
        }
    }

    @EventHandler
    public void onPlayerXP( PlayerExpChangeEvent event ) {
        int amount = event.getAmount();
        WizardPlayer wizardPlayer = wizardry.getWizardPlayer( event.getPlayer().getUniqueId() );
        wizardPlayer.gainWizardPower( amount );
        wizardPlayer.showWizardBar();
    }

    @EventHandler
    public void onDismount( VehicleExitEvent event ) {
        if ( event.getExited() instanceof Player && event.getVehicle() instanceof SkeletonHorse
                && event.getVehicle().getCustomName() != null
                && event.getVehicle().getCustomName().equals( "Thunderhorse" ) ) {
            WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( event.getExited().getUniqueId() );
            if ( wizardPlayer.checkSpell( "Thunderhorse" ) ) {
                event.getVehicle().remove();
                wizardPlayer.removeSpell( "Thunderhorse" );
            }
        }
    }

    @EventHandler
    public void onConsume( PlayerItemConsumeEvent event ) {

        ItemStack item = event.getItem();
        ItemStack main = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack off = event.getPlayer().getInventory().getItemInOffHand();

        // Prevent consumption when wand is out
        if ( main.getType() == Material.STICK ) {
            Boolean isReagent = getWizardry().getReagentList().contains( off.getType().toString() );
            Boolean isWand = WandHelper.isActuallyAWand( main );
            if ( isWand && isReagent ) {
                event.setCancelled( true );
            }
        }

        // Potions give back Wizard Power
        if ( item.getType() == Material.POTION && item.getItemMeta() instanceof PotionMeta ) {
            final PotionMeta meta = (PotionMeta) item.getItemMeta();
            if ( meta.getBasePotionData().getType() != PotionType.WATER ) {
                event.getPlayer().sendMessage( "Gaining Wizard Power..." );
                getWizardry().getWizardPlayer( event.getPlayer().getUniqueId() ).gainWizardPower( 50 );
            }
        }

        // Poison Potatoes give back 200  Wizard Power!
        if ( item.getType() == Material.POISONOUS_POTATO ) {
            WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( event.getPlayer().getUniqueId() );
            wizardPlayer.gainWizardPower( 200 );
        }
    }

    @EventHandler
    public void onFoodChange( FoodLevelChangeEvent event ) {
        if ( event.getEntity() instanceof Player ) {
            Player p = (Player) event.getEntity();
            int foodChange = event.getFoodLevel() - p.getFoodLevel();
            if ( foodChange > 0 ) {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( p.getUniqueId() );
                wizardPlayer.gainWizardPower( foodChange * 3 );
            }
        }

    }

    @EventHandler
    public void onInventoryInteractEvent( InventoryClickEvent event ) {
        if ( event.getWhoClicked() instanceof Player && event.getClickedInventory() != null ) {
            Player p = (Player )event.getWhoClicked();
            WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( p.getUniqueId() );
            InventoryHolder holder = event.getClickedInventory().getHolder();

            // Did you try to take Thunderhorse's saddle?!?!
            Boolean isThunderhorse = holder instanceof SkeletonHorse &&  ((SkeletonHorse) holder).getName().equals( "Thunderhorse" );
            Boolean hasThunderhorse = wizardPlayer.checkSpell( "Thunderhorse" );
            if ( event.getCurrentItem() == null ) return;
            Boolean clickedSaddle = event.getCurrentItem().getType() == Material.SADDLE;
            if ( isThunderhorse && hasThunderhorse && clickedSaddle ) {
                event.setCancelled( true );
                p.sendMessage( ChatColor.DARK_RED + "You dare touch the saddle of Thunderhorse?!?!? Suffer the curse of darkness!");
                PotionEffect curse = new PotionEffect( PotionEffectType.BLINDNESS, 200, 1 );
                p.addPotionEffect( curse );
            }

        }
    }

    @EventHandler
    public void onPlayerInteractEvent( PlayerInteractEvent e ) {
        Player p = e.getPlayer();
        // Block interaction events
        if ( e != null && e.hasBlock() ) {
            Block b = e.getClickedBlock();
            // Chest interactions
            if ( b.getType() == Material.CHEST || b.getType() == Material.TRAPPED_CHEST ) {
                Chest c = (Chest) b.getState();
                Inventory i = c.getInventory();
                ArrayList<Block> ups = new ArrayList<>();
                if ( i instanceof DoubleChestInventory ) {
                    DoubleChestInventory doubleChest = (DoubleChestInventory) i;
                    ups.add( doubleChest.getLeftSide().getLocation().getBlock().getRelative( BlockFace.UP ) );
                    ups.add( doubleChest.getRightSide().getLocation().getBlock().getRelative( BlockFace.UP ) );
                } else {
                    ups.add( c.getBlock().getRelative( BlockFace.UP ) );
                }

                for ( Block u: ups ) {
                    if ( WizardLock.isWizardLockSign( u ) ) {
                        Sign s = (Sign) u.getState();
                        String owner = SignHelper.getSignOwner( s );
                        if ( owner.equals( p.getName() ) ) {
                        } else {
                            p.sendMessage( ChatColor.RED + "This chest was locked by " + ChatColor.LIGHT_PURPLE +
                                owner + ChatColor.RED + " and cannot be opened by other players.");
                            e.setCancelled( true );
                        }

                    }
                }
            }
        }
    }
}
