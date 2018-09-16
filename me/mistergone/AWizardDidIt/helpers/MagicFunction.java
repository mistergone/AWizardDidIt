package me.mistergone.AWizardDidIt.helpers;

import me.mistergone.AWizardDidIt.MagicChest;
import me.mistergone.AWizardDidIt.MagicWand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.Callable;

public class MagicFunction implements Callable<Boolean> {
    public Player player;
    public ItemStack magicWand;

    public MagicFunction(){
        super();
    }

    public void setPlayer( Player player) {
        this.player = player;
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

