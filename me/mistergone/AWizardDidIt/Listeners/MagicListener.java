package me.mistergone.AWizardDidIt.Listeners;

import me.mistergone.AWizardDidIt.MagicWand;
import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import me.mistergone.AWizardDidIt.patterns.WizardFood;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.UUID;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;


public class MagicListener implements Listener {

    private Wizardry wizardry;

    public MagicListener(Wizardry wizardry ) {
        this.wizardry = wizardry;

    }

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {
        wizardry.addWizardPlayer( new WizardPlayer( event.getPlayer() ) );
        wizardry.getWizardPlayer( event.getPlayer().getUniqueId() ).loadSavedPlayerData();
    }

    @EventHandler
    public void onPlayerQuit( PlayerQuitEvent event ) {
        UUID uuid = event.getPlayer().getUniqueId();
        wizardry.getWizardPlayer( uuid ).savePlayerData();
        wizardry.removeWizardPlayer( uuid );
    }

    @EventHandler
    public void onEntityDamage( EntityDamageEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        Boolean isFall = cause == EntityDamageEvent.DamageCause.FALL;
        Boolean isSuff = cause == EntityDamageEvent.DamageCause.SUFFOCATION;
        Boolean isWall = cause == EntityDamageEvent.DamageCause.FLY_INTO_WALL;
        if ( ! ( event.getEntity() instanceof Player ) ) return;

        Player player = (Player)event.getEntity();
        WizardPlayer wizardPlayer = wizardry.getWizardPlayer( player.getUniqueId() );
        if ( event.getEntity() instanceof Player && ( isFall || isWall || isSuff) ) {

            if ( wizardPlayer.checkSpell("Mighty Leap") && isFall ) {
                int damageCost = (int)Math.floor( event.getDamage() / 2 );
                if ( wizardPlayer.spendWizardPower( damageCost ) ) {
                    event.setDamage( 0 );

                    event.setCancelled( true );
                    if ( wizardPlayer.checkMsgCooldown( "Mighty Leap (Damage)" ) == false ) {
                        wizardPlayer.getPlayer().sendMessage(ChatColor.YELLOW + "The magic of Mighty Leap has protected you from harm... this time...");
                        wizardPlayer.addMsgCooldown( "Mighty Leap (Damage)", 60 );
                    }
                } else {
                    wizardPlayer.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "You lack the Wizard Power to be protected from the fall!");
                }
                wizardPlayer.removeSpell("Mighty Leap");

            } else if ( isSuff && wizardPlayer.checkSpell( "Wizard Elevator" ) ) {
                event.setCancelled( true );

            } else if ( wizardPlayer.checkSpell( "Cloud Rider" ) || wizardPlayer.checkSpell( "Cloud Rider (Gliding)" )
                    || wizardPlayer.checkSpell( "Cloud Rider (Grounded)" ) ) {

                if ( event.getDamage() >= player.getHealth() ) {
                    event.setDamage( player.getHealth() - 0.5 );
                    player.sendMessage(  ChatColor.RED + "Ouch! " + ChatColor.AQUA + "The magic of Cloud Rider has protected you!" );
                }
                if ( wizardPlayer.checkSpell( "Cloud Rider" ) ) {
                    wizardPlayer.removeSpell( "Cloud Rider" );
                }
                if ( wizardPlayer.checkSpell( "Cloud Rider (Gliding)" ) ) {
                    wizardPlayer.removeSpell( "Cloud Rider (Gliding)" );
                }

            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if ( event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE ) {
            if (event.getDamager() instanceof Arrow ) {
                if ( event.getDamager().getCustomName() != null && event.getDamager().getCustomName().equals( "Alf's Action Arrow Projectile" ) ) {
                    if ( event.getEntity() instanceof Monster ) {
                        double damage =  Math.floor( ( Math.random() * 6 ) + 5 );
                        event.setDamage( damage );
                    }
                }
            }
        }
    }

    /**
     * If the server tries to toggle off glide, a player with Magic Leap gets to glide
     * @param event A toggleGlide event
     */
    @EventHandler
    public void onGlideToggle( EntityToggleGlideEvent event ) {
        if ( event.getEntity() instanceof Player ) {
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
            Boolean isWand = MagicWand.isActuallyAWand( main );
            if ( isWand && isReagent ) {
                event.setCancelled( true );
            } else if ( isWand && WizardFood.isWizardFood( off ) ) {
                off.setAmount( off.getAmount() - 1 );
                WizardFood.eatWizardFood( off, getWizardry().getWizardPlayer( event.getPlayer().getUniqueId() ) );
            }
        }

        // Check for Wizard Food
        WizardFood.eatWizardFood( event.getItem(), getWizardry().getWizardPlayer( event.getPlayer().getUniqueId() ) );

        // Potions give back Wizard Power
        if ( item.getType() == Material.POTION && item.getItemMeta() instanceof PotionMeta ) {
            final PotionMeta meta = (PotionMeta) item.getItemMeta();
            if ( meta.getBasePotionData().getType() != PotionType.WATER ) {
                event.getPlayer().sendMessage( "Gaining Wizard Power..." );
                getWizardry().getWizardPlayer( event.getPlayer().getUniqueId() ).gainWizardPower( 50 );
            }
        }

        // Poison Potatoes give back 100  Wizard Power!
        if ( item.getType() == Material.POISONOUS_POTATO ) {
            WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( event.getPlayer().getUniqueId() );
            wizardPlayer.gainWizardPower( 100 );
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
        if ( event.getWhoClicked() instanceof  Player && event.getClickedInventory() != null ) {
            Player p = (Player )event.getWhoClicked();
            WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( p.getUniqueId() );

            // Did you try to take Thunderhorse's saddle?!?!
            InventoryHolder holder = event.getClickedInventory().getHolder();
            Boolean isThunderhorse = holder instanceof SkeletonHorse &&  ((SkeletonHorse) holder).getName().equals( "Thunderhorse" );
            Boolean hasThunderhorse = wizardPlayer.checkSpell( "Thunderhorse" );
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
    public void onProjectileHit(ProjectileHitEvent event ) {
        Entity entity = event.getEntity();
        if ( entity != null ) {
            if ( entity instanceof SmallFireball && entity.getCustomName().equals( "Incinerate Projectile" ) ) {
                if ( event.getHitEntity() != null && event.getHitEntity() instanceof LivingEntity ) {
                    event.getHitEntity().setFireTicks( 60 );
                    ((LivingEntity) event.getHitEntity()).damage( 15 );
                } else if ( event.getHitBlock() != null ) {

                }
                entity.getWorld().createExplosion( entity.getLocation(), 0, false );
            }

        }

    }

}
