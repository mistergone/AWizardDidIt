package me.mistergone.AWizardDidIt.listeners;

import me.mistergone.AWizardDidIt.Wizardry;
import me.mistergone.AWizardDidIt.baseClasses.CraftFunction;
import me.mistergone.AWizardDidIt.baseClasses.CraftPattern;
import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.WandHelper;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class CraftListener implements Listener {
    private Wizardry wizardry;

    public CraftListener( Wizardry wizardry ) {
        this.wizardry = wizardry;
    }

    @EventHandler
    public void onPrepareItemCraftEvent( PrepareItemCraftEvent event ) {
    }

    @EventHandler
    public void onCraftItemEvent( CraftItemEvent event ) {
        ItemStack result = event.getInventory().getResult();
        // Make sure wands have a stacksize of 1
        if ( WandHelper.isActuallyAWand( result ) ) {
            ItemMeta meta = result.getItemMeta();
            meta.setMaxStackSize(1);
            result.setItemMeta(meta);
            event.getInventory().setResult( result );
            if ( event.isShiftClick() == true ) {
                Player p = (Player)event.getWhoClicked();
                if ( p == null ) return;
                WizardPlayer wizardPlayer = Wizardry.getWizardry().getWizardPlayer( p.getUniqueId() );
                event.setCancelled( true );
                p.closeInventory();
            }
        }
    }
}
