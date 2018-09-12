package me.mistergone.AWizardDidIt.spells;

import me.mistergone.AWizardDidIt.MagicSpell;
import me.mistergone.AWizardDidIt.helpers.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GrassCutter extends MagicSpell {

    public GrassCutter() {
        spellName = "Grass Cutter";

        spellFunction = new SpellFunction() {
            @Override
            public void run() {
                player.sendMessage(ChatColor.DARK_GREEN + "You have invoked " + spellName + "!" );
                player.playSound( player.getLocation(), Sound.ENTITY_PARROT_IMITATE_ELDER_GUARDIAN, .6F, .02F  );
                Location loc = player.getLocation();

                int radius = 5;

                List<Block> blocks = new ArrayList<Block>();
                for ( int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++ ) {
                    for( int y = loc.getBlockY() - radius; y <= loc.getBlockY() + radius; y++ ) {
                        for ( int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++ ) {
                            Block block = loc.getWorld().getBlockAt( x, y, z );
                            if ( block.getType() == Material.GRASS || block.getType() == Material.TALL_GRASS || block.getType() == Material.FERN || block.getType() == Material.LARGE_FERN ) {
                                block.breakNaturally();
                            }
                        }
                    }
                }


            }
        };
    }

}
