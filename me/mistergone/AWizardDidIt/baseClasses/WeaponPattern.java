package me.mistergone.AWizardDidIt.baseClasses;

import java.util.List;

public class WeaponPattern extends MagicPattern {

    public WeaponFunction weaponFunction;
    public WeaponFunction secondaryFunction;
    public List<String> weaponModes;
    protected int weaponCost;

    public WeaponFunction getWeaponFunction() {
        return this.weaponFunction;
    }

    public WeaponFunction getSecondaryFunction() {
        return this.secondaryFunction;
    }

}
