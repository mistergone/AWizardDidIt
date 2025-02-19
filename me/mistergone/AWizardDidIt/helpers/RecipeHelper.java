package me.mistergone.AWizardDidIt.helpers;

import me.mistergone.AWizardDidIt.AWizardDidIt;
import me.mistergone.AWizardDidIt.Wizardry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class RecipeHelper {

    public static void AddRecipes() {
        Plugin plugin = (AWizardDidIt) Bukkit.getServer().getPluginManager().getPlugin("AWizardDidIt");
        ItemStack magicWand = new ItemStack( Material.STICK, 1 );
        magicWand = WandHelper.turnIntoWand( magicWand );
        ItemMeta meta = magicWand.getItemMeta();
        meta.setMaxStackSize( 1 );
        magicWand.setItemMeta( meta );

        ShapedRecipe magicWandRecipe = new ShapedRecipe(
                new NamespacedKey( plugin , "magic_wand" ),
                magicWand
        );
        magicWandRecipe.shape("AAA","ABA","AAA");
        magicWandRecipe.setIngredient('A', Material.BONE_MEAL);
        magicWandRecipe.setIngredient('B', Material.STICK );
        Bukkit.getServer().addRecipe( magicWandRecipe );
    }
}
