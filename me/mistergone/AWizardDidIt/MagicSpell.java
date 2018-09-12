package me.mistergone.AWizardDidIt;

import me.mistergone.AWizardDidIt.helpers.SpellFunction;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MagicSpell {
    protected String spellName;
    protected double cost;
    protected Material reagent;
    protected SpellFunction spellFunction;

    public String getSpellName() {
        return this.spellName;
    }

    public Material getReagent() {
        return this.reagent;
    }

    public SpellFunction getSpellFunction() {
        return this.spellFunction;
    }

}
