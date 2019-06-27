package me.mistergone.AWizardDidIt.baseClasses;

import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WeaponFunction extends PatternFunction {

    public ItemStack weapon;
    public EntityDamageByEntityEvent damageEvent;
    public PlayerInteractEvent playerInteractEvent;
    public ProjectileHitEvent projectileHitEvent;
    public String mode;

}
