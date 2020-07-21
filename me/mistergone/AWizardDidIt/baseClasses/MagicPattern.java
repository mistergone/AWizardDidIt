package me.mistergone.AWizardDidIt.baseClasses;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;

public class MagicPattern {
    protected String patternName;
    protected Material[] keys;
    protected HashMap<String, String[]> patterns;
    protected PatternFunction patternFunction;

    public Material[] getKeys() { return this.keys; }

    public HashMap<String, String[]> getPatterns() {
        return this.patterns;
    }

    public String getPatternName() {
        return this.patternName;
    }

    public PatternFunction getMagicFunction() {
        return this.patternFunction;
    }

    // Check if pattern matches template
    public static Boolean checkPattern( String[] template, String[] pattern ) {
        if ( template.length != pattern.length ) return false;
        for ( int i = 0; i < pattern.length; i++ ) {
            if ( template[i].equals( "ANY" ) ) {
                continue;
            } else if ( !template[i].equals( pattern[i] ) ) {
                return false;
            }
        }

        return true;
    }

    public static String getPatternName( String[] pattern, HashMap<String, String[]> patternList ) {
        for ( HashMap.Entry<String, String[]> e: patternList.entrySet() ) {
            if ( MagicPattern.checkPattern( e.getValue(), pattern ) ) {
                return e.getKey();
            }
        }

        return null;
    }

}
