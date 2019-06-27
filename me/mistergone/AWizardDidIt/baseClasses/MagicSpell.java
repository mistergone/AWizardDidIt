package me.mistergone.AWizardDidIt.baseClasses;

import java.util.ArrayList;

public class MagicSpell {
    public String spellName;
    protected int cost;
    public ArrayList<String> reagents;
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
