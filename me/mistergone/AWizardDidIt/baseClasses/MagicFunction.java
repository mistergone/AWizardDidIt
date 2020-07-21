package me.mistergone.AWizardDidIt.baseClasses;

import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.Callable;

public class MagicFunction implements Callable<Boolean> {
    public Player player;
    public WizardPlayer wizardPlayer;
    public ItemStack magicWand;

    public MagicFunction(){
        super();
    }

    public void setPlayer( Player player) {
        this.player = player;
    }

    public void setWizardPlayer( WizardPlayer wizardPlayer ) {
        this.wizardPlayer = wizardPlayer;
    }

    public void setMagicWand( ItemStack magicWand ) {
        this.magicWand = magicWand;
    }

    public void run(){
        // Override this function to create magicFunction effects
    }

    @Override
    public Boolean call() throws Exception {
        try{
            run();
        }
        catch(Throwable e){
            return false;
        }

        return true;
    }
}

