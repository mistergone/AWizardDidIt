package me.mistergone.AWizardDidIt;

import me.mistergone.AWizardDidIt.helpers.ToolFunction;

import javax.tools.Tool;

public class ToolPattern extends MagicPattern {

    public ToolFunction toolFunction;

    public ToolFunction secondaryFunction;

    public ToolFunction getToolFunction() {
        return this.toolFunction;
    }

    public ToolFunction getSecondaryFunction() {
        return this.secondaryFunction;
    }

}
