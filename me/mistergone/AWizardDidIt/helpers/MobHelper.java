package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.entity.*;

public class MobHelper {

    public static Boolean isMonster( Entity e ) {

        if ( e instanceof Monster ) return true;
        if ( e instanceof PigZombie ) return true;
        if ( e instanceof Phantom ) return true;
        if ( e instanceof MagmaCube ) return true;
        if ( e instanceof Slime ) return true;
        if ( e instanceof Shulker ) return true;

        return false;
    }
}
