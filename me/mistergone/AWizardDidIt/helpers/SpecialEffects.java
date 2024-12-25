package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.Locale;

public class SpecialEffects {

    public static void magicPoof( Location loc ) {
        loc.add( .5, .5, .5 );
        for ( int i = 0; i < 4; i++ ) {
            loc.getWorld().spawnParticle( Particle.ELECTRIC_SPARK, loc, 10 );
            loc.getWorld().spawnParticle( Particle.CAMPFIRE_COSY_SMOKE, loc, 10 );
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
            loc.getWorld().spawnParticle( Particle.ENCHANT, loc, 20 );
        }
    }

    public static void magicChest( Location loc ) {
        loc.add( .5, 0, .5 );
        for ( int i = 0; i < 6; i++ ) {
            loc.getWorld().spawnParticle( Particle.EFFECT, loc, 20 );
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
            loc.getWorld().spawnParticle( Particle.WITCH, loc, 2);
            loc.subtract(x, y, z);
        }
    }

    public static void magicLine(Location startLoc, Vector v, Particle p ) {
        v.multiply( 10 );
        double y = startLoc.getY();
        Location end = startLoc.clone().add( v );
        v.normalize();
        for ( int i = 0; i < 10; i++ ) {
            Location loc = startLoc.add( v );
            loc.getWorld().spawnParticle( p, loc.getX(), y, loc.getZ(),
                    4, 0, 0, 0, 0 );

        }
    }
}
