package me.mistergone.AWizardDidIt.baseClasses;

import java.util.ArrayList;

public class MagicSign {

    protected String signName;
    protected int cost;
    protected ArrayList<String> reagents;
    protected SignFunction signFunction;
    public String signature;

    public String getSignName() {
        return this.signName;
    }

    public String getSignature() {
        return this.signature;
    }

    public SignFunction getSignFunction() { return this.signFunction; }

}
