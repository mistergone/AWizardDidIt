package me.mistergone.AWizardDidIt;

import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import me.mistergone.AWizardDidIt.patterns.*;
import me.mistergone.AWizardDidIt.signs.UnseenArchitect;
import me.mistergone.AWizardDidIt.signs.WizardElevator;
import me.mistergone.AWizardDidIt.spells.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.mistergone.AWizardDidIt.data.UnseenProjectManager.getUnseenPM;

public class Wizardry {
    private static Wizardry wizardry = new Wizardry();
    private Map< UUID, WizardPlayer> wizardList;
    private Map< String, MagicSpell > spellList;
    private Map< String, MagicPattern > patternList;
    private Map< String, MagicSign > signList;
    private ArrayList<String> reagentList;
    Map< String, ToolPattern > loreMap;

    private Wizardry() {
        this.wizardList = new HashMap< UUID, WizardPlayer >();
        this.spellList = new HashMap< String, MagicSpell>();
        this.patternList = new HashMap< String, MagicPattern>();
        this.signList = new HashMap< String, MagicSign>();
        this.loreMap = new HashMap<>();
        this.reagentList = new ArrayList<>();
        this.addLore();
        this.addSpells();
        this.addPatterns();
        this.addSigns();
        getUnseenPM().loadUnseenProjectList();
    }


    public static Wizardry getWizardry() {
        return wizardry;
    }

    public MagicPattern getMagicPattern( String[] needle ) {
        String pattern = new String();
        for ( int i = 0; i < needle.length; i++ ) {
            pattern += needle[i];
            if (i < needle.length - 1) {
                pattern += ",";
            }
        }
        return patternList.get( pattern );
    }

    public MagicSpell getMagicSpell( String reagent ) {
        return this.spellList.get( reagent );
    }

    public MagicSign getMagicSign( String signature ) {
        return this.signList.get( signature );
    }

    public ArrayList<String> getReagentList() {
        return this.reagentList;
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

    public Map<UUID, WizardPlayer> getWizardList() {
        return this.wizardList;
    }

    private void addSpells( ) {
        ArrayList< MagicSpell > spellRegistry = new ArrayList<>();
        spellRegistry.add( new AlfsActionArrow()    );
        spellRegistry.add( new CharmVillager() );
        spellRegistry.add( new CloudRider() );
        spellRegistry.add( new FreezeOver() );
        spellRegistry.add( new GrassCutter() );
        spellRegistry.add( new HolyDiver() );
        spellRegistry.add( new HungerForPower() );
        spellRegistry.add( new Incinerate() );
        spellRegistry.add( new LampLighter() );
        spellRegistry.add( new LayerLayer() );
        spellRegistry.add( new MightyLeap() );
        spellRegistry.add( new RoadToNowhere() );
        spellRegistry.add( new RusalkasTouch() );
        spellRegistry.add( new TerracottaTurner() );
        spellRegistry.add( new Thunderhorse() );
        spellRegistry.add( new TollOfMadness() );
        spellRegistry.add( new XomirsQuiver() );

        for ( MagicSpell spell : spellRegistry ) {
            for ( String reagent : spell.reagents ) {
                this.spellList.put( reagent, spell );
                this.reagentList.add( reagent );
            }
        }
    }

    private void addPatterns( ) {
        ArrayList<MagicPattern> patternRegistry = new ArrayList<>();
        patternRegistry.add( new EnchantWand() );
        patternRegistry.add( new FontOfPower() );
        patternRegistry.add( new SortingChest() );
        patternRegistry.add( new WizardAnvil() );
        patternRegistry.add( new WizardAxe() );
        patternRegistry.add( new WizardHoe() );
        patternRegistry.add( new WizardFood() );
        patternRegistry.add( new WizardPick() );
        patternRegistry.add( new WizardShovel() );

        for ( MagicPattern magicPattern : patternRegistry ) {
            for ( String[] p : magicPattern.getPatterns() ) {
                String pattern = new String();
                for ( int i = 0; i < p.length; i++ ) {
                    pattern += p[i];
                    if (i < p.length - 1) {
                        pattern += ",";
                    }
                }
                this.patternList.put( pattern, magicPattern);
            }
        }

    }

    private void addSigns() {
        ArrayList<MagicSign> signRegistry = new ArrayList<>();
        signRegistry.add( new WizardElevator() );
        signRegistry.add( new UnseenArchitect() );

        for ( MagicSign magicSign: signRegistry) {
            this.signList.put( magicSign.signature, magicSign );
        }
    }

    private void addLore() {
        this.loreMap.put( "Wizard Pick", new WizardPick() );
        this.loreMap.put( "Wizard Shovel", new WizardShovel() );
        this.loreMap.put( "Wizard Axe", new WizardAxe() );
        this.loreMap.put( "Wizard Hoe", new WizardHoe() );
    }

}
