package me.mistergone.AWizardDidIt.baseClasses;

import java.util.ArrayList;

public class MagicSpell {
    public String spellName;
    public ArrayList<String> reagents;
    protected int cost;
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
