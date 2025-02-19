package me.mistergone.AWizardDidIt.signs;

import me.mistergone.AWizardDidIt.baseClasses.MagicSign;
import me.mistergone.AWizardDidIt.baseClasses.SignFunction;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class WizardVault extends MagicSign {

    public WizardVault() {
        signName = "Wizard Vault";
        signature = "[WizardVault]";
        cost = 25;

        signFunction = new SignFunction() {
            @Override
            public void run() {
                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                int vaultIndex = Integer.valueOf( lines[1] ) - 1;
                player.openInventory( wizardPlayer.getWizardVaultByNumber( vaultIndex ) );


                String command =  lines[2].toLowerCase();

                if ( command == null || command.equals( "" ) ) {

                } else {
                    // check if there's a DoubleChest first
//                    Block down = signBlock.getRelative( BlockFace.DOWN );
//                    if ( down.getType() != Material.CHEST ) {
//                        player.sendMessage( ChatColor.RED + "There must be a Double Chest below a Wizard Vault Withdrawal sign!");
//                        lines[0] = "Wizard Vault";
//                        lines[1] = "Withdrawal must be";
//                        lines[2] = "placed above a";
//                        lines[3] = "double chest.";
//                    }
//
//                    if ( command.equals( "withdrawal" ) || command.equals( "withdrawl" ) || command.equals( "withdraw" ) ) {
//
//                    }
                }




            }
        };

    }

    public static void handleSignEvent(SignChangeEvent event) {
        String[] lines = event.getLines();
        Player p = event.getPlayer();

        if ( !Tag.WALL_SIGNS.isTagged( event.getBlock().getType() ) ) {
            p.sendMessage( ChatColor.RED + "Wizard Vault must be a wall sign!");
            lines[0] = "Wizard Vault must";
            lines[1] = "be a wall sign.";
            return;
        }

        if ( ( lines[1].equals( "" ) )  || Integer.valueOf( lines[1] ) < 1 ) {
            lines[1] = "1";
        } else if ( Integer.valueOf( lines[1] ) > 12 ) {
            lines[1] = "12";
        }

//        String command =  lines[2].toLowerCase();
//        if ( command.equals( "withdrawal" ) || command.equals( "withdrawl" ) || command.equals( "withdraw" ) ) {
//            // check for double chest
//        };

        lines[0] = "[" + ChatColor.DARK_PURPLE + "WizardVault" + ChatColor.BLACK + "]";
        lines[2] = ChatColor.GOLD + "Use a wand to ";
        lines[3] = ChatColor.GOLD + "access.";
    }

    public void doAWithdrawal(  ) {

    }

}
