package me.mistergone.AWizardDidIt.helpers;

import me.mistergone.AWizardDidIt.MagicSpell;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Map;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class SpellFinder {
    public MagicSpell matchReagent( String needle ) {
        Map< String, MagicSpell > magicSpells = getWizardry().getMagicSpells();
        MagicSpell magicSpell = magicSpells.get( needle );

        return magicSpell;
    }
}