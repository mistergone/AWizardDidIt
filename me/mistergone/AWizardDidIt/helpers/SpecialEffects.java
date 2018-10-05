package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.Locale;

public class SpecialEffects {

    public static void magicPoof( Location loc ) {
        loc.add( .5, .5, .5 );
        for ( int i = 0; i < 4; i++ ) {
            loc.getWorld().spawnParticle( Particle.SPELL_MOB, loc, 10 );
            loc.getWorld().spawnParticle( Particle.SPELL_MOB_AMBIENT, loc, 10 );
        }
    }

    public static void flamesEffect( Location loc ) {
        loc.add( .5, 1, .5 );
        for ( int i = 0; i < 6; i++ ) {
            loc.getWorld().spawnParticle( Particle.FLAME, loc, 5 );
        }
    }

    public static void portalCollapse( Location loc ) {
        loc.add(.5, 0, .5);
        double theta = 0;
        double radius = .25;

        for (int i = 0; i < 16; i++) {
            theta = theta + Math.PI / 8;
            radius = .75;
            double x = radius * Math.cos( theta );
            double y = .5;
            double z = radius * Math.sin( theta );
            loc.add(x, y, z);
            loc.getWorld().spawnParticle( Particle.PORTAL, loc, 20);
            loc.subtract(x, y, z);
        }
    }

    public static void enchantEffect( Location loc ) {
        loc.add( .5, 1, .5 );
        for ( int i = 0; i < 6; i++ ) {
            loc.getWorld().spawnParticle( Particle.ENCHANTMENT_TABLE, loc, 20 );
        }
    }

    public static void magicChest( Location loc ) {
        loc.add( .5, 0, .5 );
        for ( int i = 0; i < 6; i++ ) {
            loc.getWorld().spawnParticle( Particle.SPELL_MOB, loc, 20 );
        }
    }


    public static void magicSpiral( Location loc ) {
        loc.add(.25, 0, .25);
        double theta = 0;
        double radius = 1;

        for (int i = 0; i < 20; i++) {
            theta = theta + Math.PI / 8;
            radius -= .05;
            double x = radius * Math.cos( theta );
            double y = .1 + ( (double)i / 20 );
            double z = radius * Math.sin( theta );
            loc.add(x, y, z);
            loc.getWorld().spawnParticle( Particle.VILLAGER_HAPPY, loc, 2);
            loc.subtract(x, y, z);
        }
    }
}
