package me.mistergone.AWizardDidIt.spells;

import com.sun.org.apache.xpath.internal.operations.Bool;
import me.mistergone.AWizardDidIt.baseClasses.MagicSpell;
import me.mistergone.AWizardDidIt.baseClasses.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import org.bukkit.*;
import org.bukkit.entity.IronGolem;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class Recyclotron extends MagicSpell {
    public Recyclotron() {
        spellName = "Recyclotron";
        reagents = new ArrayList<String>();
        reagents.add("IRON_INGOT");
        cost = 50;

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                Boolean isGolem = (clickedEntity instanceof IronGolem &&
                        event != null);
                if (isGolem) {
                    Location loc = clickedEntity.getLocation();
                    ItemStack i = new ItemStack(Material.IRON_INGOT);
                    Random rand = new Random();
                    int amount = rand.nextInt(2) + 2;
                    i.setAmount(amount);
                    clickedEntity.getWorld().dropItem(loc, i);
                    clickedEntity.remove();
                    for (int x = 0; x < 6; x++) {
                        loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 5);
                    }
                    loc.getWorld().playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, .3F, 2F);
                    player.sendMessage( ChatColor.AQUA + "You have invoked " + spellName );
                    clickedEntity = null;
                }

            }
        };
    }
}
