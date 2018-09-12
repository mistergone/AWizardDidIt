package me.mistergone.AWizardDidIt.timedTasks;

import me.mistergone.AWizardDidIt.AWizardDidIt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class TimedTask implements Runnable {
    Player player;
    AWizardDidIt plugin;

    void delayTask( TimedTask task, long delay){
        Bukkit.getScheduler().runTaskLater( plugin, task, delay);
    }


}
