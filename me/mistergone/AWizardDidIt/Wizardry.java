package me.mistergone.AWizardDidIt;

import me.mistergone.AWizardDidIt.baseClasses.*;
import me.mistergone.AWizardDidIt.helpers.*;
import me.mistergone.AWizardDidIt.patterns.*;
import me.mistergone.AWizardDidIt.signs.*;
import me.mistergone.AWizardDidIt.signs.SortingChest;
import me.mistergone.AWizardDidIt.spells.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.mistergone.AWizardDidIt.data.UnseenProjectManager.getUnseenPM;

public class Wizardry {
    private static Wizardry wizardry = new Wizardry();
    private Map< UUID, WizardPlayer> wizardList;
    private Map< String, MagicSpell> spellList;
    private Map< Material, MagicPattern> patternList;
    private Map< String, MagicSign> signList;
    private ArrayList<String> reagentList;
    Map< String, ToolPattern> toolLoreMap;
    Map< String, WeaponPattern> weaponLoreMap;

    private Wizardry() {
        this.wizardList = new HashMap< UUID, WizardPlayer >();
        this.spellList = new HashMap< String, MagicSpell>();
        this.patternList = new HashMap< Material, MagicPattern>();
        this.signList = new HashMap< String, MagicSign>();
        this.toolLoreMap = new HashMap<>();
        this.weaponLoreMap = new HashMap<>();
        this.reagentList = new ArrayList<>();
        this.addToolLore();
        this.addWeaponLore();
        this.addSpells();
        this.addPatterns();
        this.addSigns();
        getUnseenPM().loadUnseenProjectList();
    }

    public static Wizardry getWizardry() {
        return wizardry;
    }

    public MagicPattern getMagicPattern( Material key ) {
        return patternList.get( key );
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
        return toolLoreMap.get( lore );
    }

    public WeaponPattern getWeaponByLore( String lore ) {
        return weaponLoreMap.get( lore );
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
        // TODO: Is there a better way to access all these classes? Should they be static?
        ArrayList< MagicSpell > spellRegistry = new ArrayList<>();
        spellRegistry.add( new AlfsActionArrow() );
        spellRegistry.add( new BringTheDawn() );
        spellRegistry.add( new CharmVillager() );
        spellRegistry.add( new CloudRider() );
        spellRegistry.add( new EnderPocket() );
        spellRegistry.add( new FreezeOver() );
        spellRegistry.add( new GrassCutter() );
        spellRegistry.add( new HolyDiver() );
        spellRegistry.add( new HungerForPower() );
        spellRegistry.add( new Incinerate() );
        spellRegistry.add( new LampLighter() );
        spellRegistry.add( new LayerLayer() );
        spellRegistry.add( new MightyLeap() );
        spellRegistry.add( new Recyclotron() );
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
        patternRegistry.add( new WizardAnvil() );
        patternRegistry.add( new WizardAxe() );
        patternRegistry.add( new WizardBow() );
        patternRegistry.add( new WizardCrossbow() );
        patternRegistry.add( new WizardDust() );
        patternRegistry.add( new WizardHoe() );
        patternRegistry.add( new WizardPick() );
        patternRegistry.add( new WizardRod() );
        patternRegistry.add( new WizardShovel() );
        patternRegistry.add( new WizardSword() );
        patternRegistry.add( new WizardTrident() );

        patternRegistry.add( new WizardHelmet() );
        patternRegistry.add( new WizardChestplate() );
        patternRegistry.add( new WizardLeggings() );
        patternRegistry.add( new WizardBoots() );

        for ( MagicPattern magicPattern : patternRegistry ) {
            Material[] keys = magicPattern.getKeys();
            for ( Material m: keys ) {
                this.patternList.put( m, magicPattern );
            }
        }

    }

    private void addSigns() {
        ArrayList<MagicSign> signRegistry = new ArrayList<>();
        signRegistry.add( new SortingChest() );
        // signRegistry.add( new UnseenArchitect() );
        signRegistry.add( new WizardElevator() );
        signRegistry.add( new WizardPassage() );
        signRegistry.add( new WizardLock() );

        for ( MagicSign magicSign: signRegistry) {
            this.signList.put( magicSign.signature, magicSign );
        }
    }

    private void addToolLore() {
        this.toolLoreMap.put( "Wizard Pick", new WizardPick() );
        this.toolLoreMap.put( "Wizard Shovel", new WizardShovel() );
        this.toolLoreMap.put( "Wizard Axe", new WizardAxe() );
        this.toolLoreMap.put( "Wizard Hoe", new WizardHoe() );
    }

    private void addWeaponLore() {
        this.weaponLoreMap.put( "Wizard Bow", new WizardBow() );
        this.weaponLoreMap.put( "Wizard Crossbow", new WizardCrossbow() );
        this.weaponLoreMap.put( "Wizard Sword", new WizardSword() );
        this.weaponLoreMap.put( "Wizard Trident", new WizardTrident() );
    }

}
