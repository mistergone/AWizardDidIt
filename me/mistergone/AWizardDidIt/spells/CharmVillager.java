package me.mistergone.AWizardDidIt.spells;


import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

import java.util.ArrayList;


public class CharmVillager extends MagicSpell {

    public CharmVillager() {
        spellName = "Charm Villager";
        cost = 100;
        int bribeCost = 1;
        reagents = new ArrayList<String>();
        reagents.add( "EMERALD" );

        spellFunction = new SpellFunction(){
          @Override
          public void run() {
              WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
              if ( event == null || !(clickedEntity instanceof  Villager) ) return;
              Villager villager = ((Villager)clickedEntity);
              event.setCancelled( true );

              if ( reagent.getAmount() >= bribeCost ) {

                  if ( villager.getRecipes() == null ) return;

                  for (MerchantRecipe recipe : ((Villager) clickedEntity).getRecipes()) {
                      int max = recipe.getMaxUses();
                      int uses = recipe.getUses();
                      if (uses == max) {
                          recipe.setMaxUses(recipe.getMaxUses() + 5);
                          reagent.setAmount( reagent.getAmount() - bribeCost);
                          wizardPlayer.spellAlert( spellName, "");
                          Location loc = villager.getLocation();loc.add(.5, 0, .5);
                          double theta = 0;
                          double radius = .2;

                          for (int i = 0; i < 50; i++) {
                              theta = theta + Math.PI / 8;
                              radius += .05;
                              double x = radius * Math.cos(theta);
                              double y = .25;
                              double z = radius * Math.sin(theta);
                              loc.add(x, y, z);
                              player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 10);
                              loc.subtract(x, y, z);
                          }
                      }
                  }
              } else if ( reagent.getAmount() < bribeCost ) {
                  player.sendTitle( "",ChatColor.RED + "You don't have the emeralds in your offhand needed to charm villagers." );
                  return;
              }

        }
        };
    }
}
