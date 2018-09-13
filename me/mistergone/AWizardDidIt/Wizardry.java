package me.mistergone.AWizardDidIt;

import me.mistergone.AWizardDidIt.helpers.SpellFinder;
import me.mistergone.AWizardDidIt.helpers.PatternFinder;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import me.mistergone.AWizardDidIt.patterns.*;
import me.mistergone.AWizardDidIt.spells.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Wizardry {
    private static Wizardry wizardry = new Wizardry();
    private SpellFinder spellFinder;
    private PatternFinder patternFinder;
    private Map< UUID, WizardPlayer> wizardList;

    private Wizardry() {
        this.spellFinder = new SpellFinder();
        this.patternFinder = new PatternFinder();
        this.wizardList = new HashMap< UUID, WizardPlayer >();
    }

    public static Wizardry getWizardry() {
        return wizardry;
    }

    public ArrayList<MagicPattern> getMagicPatterns() {
        ArrayList<MagicPattern> magicPatterns = new ArrayList<>();
        magicPatterns.add( new EnchantWand() );
        magicPatterns.add( new WizardPick() );
        magicPatterns.add( new WizardShovel() );
        magicPatterns.add( new WizardAxe() );

        return magicPatterns;
    }

    public PatternFinder getPatternFinder() {
        return this.patternFinder;
    }

    public Map< String, MagicSpell > getMagicSpells() {
        Map< String, MagicSpell > magicSpells = new HashMap<>();

        magicSpells.put( "WHEAT_SEEDS", new GrassCutter() );
        magicSpells.put( "FIREWORK_STAR", new Incinerate() );
        magicSpells.put( "FEATHER", new CloudRider() );
        magicSpells.put( "SLIME_BALL", new MightyLeap() );

        return magicSpells;
    }

    public SpellFinder getSpellFinder() {
        return this.spellFinder;
    }

    public ToolPattern getToolByLore( String lore ) {
        Map< String, ToolPattern > loreMap = new HashMap<>();
        loreMap.put( "Wizard Pick", new WizardPick() );
        loreMap.put( "Wizard Shovel", new WizardShovel() );
        loreMap.put( "Wizard Axe", new WizardAxe() );

        return loreMap.get( lore );
    }

    public WizardPlayer getWizardPlayer( UUID uuid ) {
        return this.wizardList.get( uuid );
    }

    public void addWizardPlayer( WizardPlayer wizardPlayer ) {
        this.wizardList.put(
                wizardPlayer.getPlayer().getUniqueId(),
                wizardPlayer
        );
    }

    public void removeWizardPlayer( UUID uuid ) {
        this.wizardList.remove( uuid );
    }

}
