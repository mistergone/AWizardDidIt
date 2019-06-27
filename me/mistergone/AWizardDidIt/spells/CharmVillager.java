package me.mistergone.AWizardDidIt.spells;


import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

import java.util.ArrayList;


public class CharmVillager extends MagicSpell {

    public CharmVillager() {
        spellName = "Charm Villager";
        cost = 25;
        reagents = new ArrayList<String>();
        reagents.add( "EMERALD" );

        spellFunction = new SpellFunction(){
          @Override
          public void run() {
              WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
              if ( reagent.getAmount() >= 1 ) {
                  if ( !wizardPlayer.spendWizardPower( cost, spellName ) ) return;
                  Location loc = clickedBlock.getRelative(BlockFace.UP, 1).getLocation();
                  loc.add(.5, 0, .5);
                  double theta = 0;
                  double radius = .2;

                  for (int i = 0; i < 50; i++) {
                      theta = theta + Math.PI / 8;
                      radius += .05;
                      double x = radius * Math.cos(theta);
                      double y = .25;
                      double z = radius * Math.sin(theta);
                      loc.add(x, y, z);
                      player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 10);
                      loc.subtract(x, y, z);
                  }

                  ArrayList<Entity> entities = new ArrayList(loc.getWorld().getNearbyEntities(loc, 2, 2, 2));
                  for (Entity e : entities) {
                      if (e instanceof Villager) {
                          Boolean refreshed = false;
                          for (MerchantRecipe recipe : ((Villager) e).getRecipes()) {
                              int max = recipe.getMaxUses();
                              int uses = recipe.getUses();
                              if (uses == max) {
                                  recipe.setMaxUses(recipe.getMaxUses() + 5);
                                  refreshed = true;
                              }
                          }
                          if (refreshed) {
                              if (reagent.getAmount() >= 1) {
                                  if (reagent.getAmount() > 1) {
                                      reagent.setAmount(reagent.getAmount() - 1);
                                  } else if (reagent.getAmount() == 1) {
                                      player.getInventory().setItemInOffHand(null);
                                  }
                                  player.sendMessage(ChatColor.AQUA + "You have charmed a villager!");

                              } else {
                                  String name = ((Villager) e).getProfession().toString().toLowerCase();
                                  if (((Villager) e).getCustomName() != null) {
                                      name = ((Villager) e).getCustomName();
                                  }
                                  player.sendMessage(ChatColor.RED + "You do not have the emerald needed in your off hand to charm "
                                          + name + ".");
                              }
                          }
                      }
                  }
              } else if ( reagent.getAmount() < 1 ) {
                  player.sendMessage( ChatColor.RED + "You don't have the emerald in your offhand needed to charm villagers." );
                  return;
              }

        }
        };
    }
}
