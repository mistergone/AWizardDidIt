package me.mistergone.AWizardDidIt;

import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import me.mistergone.AWizardDidIt.patterns.*;
import me.mistergone.AWizardDidIt.spells.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Wizardry {
    private static Wizardry wizardry = new Wizardry();
    private Map< UUID, WizardPlayer> wizardList;
    private Map< String, MagicSpell > spellList;
    private Map< String[], MagicPattern > patternList;
    Map< String, ToolPattern > loreMap;

    private Wizardry() {
        this.wizardList = new HashMap< UUID, WizardPlayer >();
        this.spellList = new HashMap< String, MagicSpell>();
        this.patternList = new HashMap< String[], MagicPattern>();
        this.loreMap = new HashMap<>();

        this.addLore();
        this.addSpells();
        this.addPatterns();
    }


    public static Wizardry getWizardry() {
        return wizardry;
    }

    public MagicPattern getMagicPattern( String[] needle ) {
        return patternList.get( needle );
    }

    public MagicSpell getMagicSpell( String reagent ) {
        return this.spellList.get( reagent );
    }

    public ToolPattern getToolByLore( String lore ) {
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

    private void addSpells( ) {
        ArrayList< MagicSpell > spellRegistry = new ArrayList<>();
        spellRegistry.add( new CloudRider() );
        spellRegistry.add( new GrassCutter() );
        spellRegistry.add( new Incinerate() );
        spellRegistry.add( new MightyLeap() );
        spellRegistry.add( new RoadToNowhere() );
        spellRegistry.add( new TollOfMadness() );

        for ( MagicSpell spell : spellRegistry ) {
            for ( String reagent : spell.reagents ) {
                this.spellList.put( reagent, spell );
            }
        }
    }

    private void addPatterns( ) {
        ArrayList<MagicPattern> patternRegistry = new ArrayList<>();
        patternRegistry.add( new EnchantWand() );
        patternRegistry.add( new WizardPick() );
        patternRegistry.add( new WizardShovel() );
        patternRegistry.add( new WizardAxe() );

        for ( MagicPattern magicPattern : patternRegistry ) {
            for ( String[] pattern : magicPattern.getPatterns() ) {
                this.patternList.put( pattern, magicPattern);
            }

        }

    }

    private void addLore() {
        this.loreMap.put( "Wizard Pick", new WizardPick() );
        this.loreMap.put( "Wizard Shovel", new WizardShovel() );
        this.loreMap.put( "Wizard Axe", new WizardAxe() );
    }

}
