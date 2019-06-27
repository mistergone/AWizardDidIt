package me.mistergone.AWizardDidIt.baseClasses;

import java.util.ArrayList;

public class MagicPattern {
    protected String patternName;
    protected ArrayList<String[]> patterns;
    protected PatternFunction patternFunction;

    public ArrayList<String[]> getPatterns() {
        return this.patterns;
    }

    public String getPatternName() {
        return this.patternName;
    }

    public PatternFunction getMagicFunction() {
        return this.patternFunction;
    }

}
