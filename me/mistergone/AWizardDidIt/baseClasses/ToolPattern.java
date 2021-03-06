package me.mistergone.AWizardDidIt.baseClasses;

import java.util.List;

public class ToolPattern extends MagicPattern {

    public ToolFunction toolFunction;
    public ToolFunction secondaryFunction;
    public List<String> toolModes;
    protected int toolCost;

    public ToolFunction getToolFunction() {
        return this.toolFunction;
    }

    public ToolFunction getSecondaryFunction() {
        return this.secondaryFunction;
    }

}
