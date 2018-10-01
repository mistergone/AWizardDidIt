package me.mistergone.AWizardDidIt;

import me.mistergone.AWizardDidIt.helpers.SpellFunction;

import java.util.ArrayList;

public class MagicSpell {
    protected String spellName;
    protected int cost;
    protected ArrayList<String> reagents;
    protected SpellFunction spellFunction;

    public String getSpellName() {
        return this.spellName;
    }

    public ArrayList<String> getReagents() {
        return this.reagents;
    }

    public SpellFunction getSpellFunction() {
        return this.spellFunction;
    }


}
