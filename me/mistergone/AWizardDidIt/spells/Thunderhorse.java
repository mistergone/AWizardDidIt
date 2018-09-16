package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

import java.util.ArrayList;

/**
 * THUNDERHORSE
 * Summon a temporary mount
 */

public class Thunderhorse extends MagicSpell {
    public Thunderhorse() {
        spellName = "Thunderhorse";
        reagents = new ArrayList<>();
        reagents.add( "SADDLE" );
        cost = .02;

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                if ( wizardPlayer.spendWizardPower( cost ) ) {
                    if ( wizardPlayer.checkMsgCooldown( spellName ) == false ) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "You have summoned forth the Thunderhose!");
                        wizardPlayer.addMsgCooldown( spellName, 30 );
                    }

                    SkeletonHorse horsie = (SkeletonHorse) player.getWorld().spawnEntity( player.getLocation(), EntityType.SKELETON_HORSE );
                    player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.6F, .8F);
                    horsie.setTamed( true );
                    horsie.setAdult();
                    horsie.setCustomName( "Thunderhorse" );
                    horsie.setOwner( player );
                    horsie.setGlowing( true );
                    horsie.getInventory().setSaddle( new ItemStack(Material.SADDLE, 1) );
                    horsie.addPassenger( player );

                    wizardPlayer.addSpell( "Thunderhorse" );
                }

            }
        };
    }
}