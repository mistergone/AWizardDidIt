package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.baseClasses.MagicChest;
import me.mistergone.AWizardDidIt.baseClasses.MagicPattern;
import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class WizardTransmute extends MagicPattern {

    public WizardTransmute() {
        patternName = "Wizard Transmute";
        keys = new Material[]{ Material.GOLD_INGOT };
        patterns =  new HashMap<>();
        patterns.put( "Wizard Dust", new String[]
                { "ANY", "ANY", "ANY",
                        "NONE", "GOLD_INGOT", "NONE",
                        "NONE", "NONE", "NONE"});

        patternFunction = new PatternFunction() {
            @Override
            public void run() {
                String[] pattern = magicChest.getPattern();
                String name = MagicPattern.getPatternName( pattern, patterns );

                if ( name == null ) {
                    player.sendMessage(ChatColor.RED + "No magic pattern was found inside this chest!");
                    return;
                }

                // Check if chest is on a gold block
                Block goldBlock = magicChest.getChest().getBlock().getRelative( BlockFace.DOWN );
                if ( goldBlock.getType() != Material.GOLD_BLOCK ) {
                    player.sendMessage( ChatColor.RED + "Wizard Transmute must be in a chest on top of a gold block!");
                    return;
                }

                Chest chest = magicChest.getChest();
                Inventory inv = chest.getInventory();
                // Check for transmute pattern
                Material matOne = inv.getItem(0) != null ? inv.getItem(0).getType() : null;
                Material matTwo = inv.getItem(1) != null ? inv.getItem(1).getType() : null;
                Material matThree = inv.getItem(2) != null ? inv.getItem(2).getType() : null;
                if ( matOne == null && matTwo == null && matThree == null ) {
                    player.sendMessage( ChatColor.RED + "Wizard Transmute requires an item to be transmuted, which should be placed in the top three slots of the chest!");
                    return;
                }

                if ( matOne != null && matTwo != null && matThree != null ) {
                    Bukkit.broadcastMessage("THIS PART WORK");
                }

                // Transmute each item
                for ( int i =0; i < 3; i++ ) {
                    ItemStack reagent = inv.getItem( i );
                    if ( reagent != null ) {
                        transmuteSlot( magicChest, i, player );
                    }
                }
            }
        };
    }

    private void transmuteSlot( MagicChest magicChest, int itemSlot, Player player ) {
        WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
        String message = ChatColor.GOLD + "You have transmuted ";
        String error = null;
        ItemStack reagent = magicChest.getChest().getInventory().getItem( itemSlot );
        HashMap<Material,Integer> totals = new HashMap<>();
        int count = 0;
        int index = 0;

        switch ( reagent.getType() ) {
            case BONE_MEAL:
                if ( reagent.getAmount() < 3 ) {
                    error = "It takes at least 3 bone meal to transmute!";
                    break;
                }

                int mealCount = 0;
                int dustCount = 0;
                while (reagent.getAmount() > 2 ) {
                    if ( !wizardPlayer.spendWizardPower( 25 , patternName ) ) break;
                    reagent.setAmount( reagent.getAmount() - 3 );
                    ItemStack dust = new ItemStack( Material.GLOWSTONE_DUST );
                    dust.setAmount( 1 );
                    player.getWorld().dropItem( player.getLocation(), dust );
                    mealCount += 3;
                    dustCount++;
                }
                message += String.valueOf(mealCount) + " bone meal into " + String.valueOf(dustCount)
                    + " glowstone dust!";
                break;
            case FLINT:
                int flintCount = 0;
                while ( reagent.getAmount() > 0 ) {
                    if ( !wizardPlayer.spendWizardPower( 10 , patternName ) ) break;
                    reagent.setAmount(reagent.getAmount() - 1 );
                    ItemStack gravel = new ItemStack( Material.GRAVEL );
                    gravel.setAmount(1);
                    flintCount++;
                    player.getWorld().dropItem( player.getLocation(), gravel );
                }
                String amt = String.valueOf( flintCount );
                message += amt + " flint into " + amt + " gravel";
                break;
            case COBBLESTONE:
                totals = new HashMap<>();
                count = 0;
                while ( reagent.getAmount() > 0 ) {
                    if ( !wizardPlayer.spendWizardPower( 5 , patternName ) ) break;
                    Material mat = transmuteCobble();
                    if ( totals.containsKey( mat ) ) {
                        totals.put( mat, totals.get(mat) + 1 );
                    } else {
                        totals.put( mat, 1 );
                    }
                    reagent.setAmount( reagent.getAmount() - 1);
                    count++;
                }
                message += String.valueOf( count ) + " cobblestone into ";
                index = 0;
                for ( Map.Entry<Material, Integer> m: totals.entrySet()) {
                    Material mat = m.getKey();
                    ItemStack i = new ItemStack( mat );
                    i.setAmount( m.getValue() );
                    player.getWorld().dropItem( player.getLocation(), i );
                    if ( index > 0 ) message += ", ";
                    if ( index == totals.size() - 1 ) message += " and ";
                    message += String.valueOf( m.getValue() ) + " "
                            + m.getKey().toString().toLowerCase().replace( "_", " ");
                    if ( index == totals.size() - 1 ) message += ".";
                    index++;
                }
                break;
            case ROTTEN_FLESH:
                totals = new HashMap<>();
                count = 0;

                while ( reagent.getAmount() > 0 ) {
                    if ( !wizardPlayer.spendWizardPower( 25 , patternName ) ) break;
                    Material mat = transmuteRottenFlesh();
                    if ( totals.containsKey( mat ) ) {
                        totals.put( mat, totals.get(mat) + 1 );
                    } else {
                        totals.put( mat, 1 );
                    }
                    reagent.setAmount( reagent.getAmount() - 1);
                    count++;
                }
                message += String.valueOf( count ) + " rotten flesh into ";
                index = 0;
                for ( Map.Entry<Material, Integer> m: totals.entrySet()) {
                    Material mat = m.getKey();
                    ItemStack i = new ItemStack( mat );
                    i.setAmount( m.getValue() );
                    player.getWorld().dropItem( player.getLocation(), i );
                    if ( index > 0 ) message += ", ";
                    if ( index == totals.size() - 1 ) message += " and ";
                    message += String.valueOf( m.getValue() ) + " "
                            + m.getKey().toString().toLowerCase().replace( "_", " ");
                    if ( index == totals.size() - 1 ) message += ".";
                    index++;
                }
                break;
            case BOW:
            case CROSSBOW:
            case TRIDENT:
            case WOODEN_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case GOLDEN_AXE:
            case DIAMOND_AXE:
            case NETHERITE_AXE:
            case WOODEN_HOE:
            case STONE_HOE:
            case IRON_HOE:
            case GOLDEN_HOE:
            case DIAMOND_HOE:
            case NETHERITE_HOE:
            case WOODEN_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case GOLDEN_PICKAXE:
            case DIAMOND_PICKAXE:
            case NETHERITE_PICKAXE:
            case WOODEN_SHOVEL:
            case STONE_SHOVEL:
            case IRON_SHOVEL:
            case GOLDEN_SHOVEL:
            case DIAMOND_SHOVEL:
            case NETHERITE_SHOVEL:
            case WOODEN_SWORD:
            case STONE_SWORD:
            case IRON_SWORD:
            case GOLDEN_SWORD:
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
            case FISHING_ROD:
                message += transmuteTool( magicChest, itemSlot, player );
                break;
            case LEATHER_BOOTS:
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
            case LEATHER_LEGGINGS:
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_LEGGINGS:
            case IRON_BOOTS:
            case IRON_CHESTPLATE:
            case IRON_HELMET:
            case IRON_LEGGINGS:
            case GOLDEN_BOOTS:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_HELMET:
            case GOLDEN_LEGGINGS:
            case DIAMOND_BOOTS:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_HELMET:
            case DIAMOND_LEGGINGS:
            case NETHERITE_BOOTS:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_HELMET:
            case NETHERITE_LEGGINGS:
                message += transmuteArmor( magicChest, itemSlot, player );
                break;
            default:
                error = reagent.getType().toString() + " is not transmutable!";
        }

        if ( error != null ) {
            player.sendMessage( ChatColor.RED + error );
        } else {
            player.sendMessage( message );
            Location loc = magicChest.getChest().getLocation().add(0, 1, 0);
            SpecialEffects.magicChest(loc);
        }
    }

    private String transmuteArmor (MagicChest magicChest, int itemSlot, Player player) {
        ItemStack target = magicChest.getChest().getInventory().getItem( itemSlot );
        String type = target.getType().toString();
        ArrayList<String> msgs = new ArrayList<>();

        Random seed = new Random();
        int bound = getItemMatMax( type );
        int r = seed.nextInt( bound ) + 1;
        ItemStack mat = new ItemStack( Material.LEATHER );
        String ingredientString = " leather ";
        String matString = " leather";
        if ( type.contains( "IRON" ) || type.contains( "CHAINMAIL") )  {
            mat.setType( Material.IRON_INGOT );
            matString = " iron ingot(s)";
            ingredientString = "iron ";
        } else if ( type.contains( "GOLDEN" ) ) {
            mat.setType( Material.GOLD_INGOT );
            matString = " gold ingot(s)";
            ingredientString = "golden ";
        } else if ( type.contains( "DIAMOND" ) ) {
            mat.setType( Material.DIAMOND );
            matString = " diamond(s)";
            ingredientString = "diamond ";
        } else if ( type.contains( "NETHERITE" ) ) {
            mat.setType( Material.NETHERITE_SCRAP );
            matString = " piece(s) of netherite scrap";
            ingredientString = "netherite ";
        }
        String[] split = type.split( "_" );
        if ( split[1] != null ) ingredientString += split[1].toLowerCase() + " ";
        mat.setAmount( r );
        player.getWorld().dropItem( player.getLocation(), mat );
        msgs.add( r  + matString );

        if ( target.getEnchantments().size() > 0 ) {
            Random s = new Random();
            int a = seed.nextInt( 2 ) + 1;
            ItemStack lapis = new ItemStack( Material.LAPIS_LAZULI );
            lapis.setAmount( a );
            player.getWorld().dropItem( player.getLocation(), lapis );
            msgs.add( String.valueOf( a ) + " lapis lazuli" );
        }

        if ( type.contains("CHESTPLATE") || type.contains("HELMET") ) {
            ingredientString = "a " + ingredientString;
        }
        String message = ingredientString + "into ";
        ListIterator<String> iter = msgs.listIterator();
        while ( iter.hasNext() ) {
            if ( iter.nextIndex() > 0 && msgs.size() > 2 ) {
                message += ", ";
            }
            if ( iter.nextIndex() > 0 && iter.nextIndex() + 1 == msgs.size() ) {
                message += " and ";
            }
            message += iter.next();
        }
        message = message.replace( "  ", " " );
        magicChest.getChest().getInventory().setItem( itemSlot, new ItemStack( Material.AIR ) );
        return message + "!";
    }

    private Material transmuteCobble () {
        Random seed = new Random();
        Material mat = Material.BLACKSTONE;
        int r = seed.nextInt( 100 ) + 1;
        if ( r > 10 && r <= 32 ) {
            mat = Material.ANDESITE;
        } else if ( r <= 54 ) {
            mat = Material.DIORITE;
        } else if ( r <= 76 ) {
            mat = Material.GRANITE;
        } else if ( r <= 98 ) {
            mat = Material.COBBLED_DEEPSLATE;
        } else if ( r > 99  ) {
            mat = Material.QUARTZ;
        }
        return mat;
    }



    private Material transmuteRottenFlesh() {
        Material mat = Material.PORKCHOP;
        Random seed = new Random();
        int r = seed.nextInt( 100 ) + 1;
        if ( r > 50 && r <= 80 ) {
            mat = Material.BEEF;
        } else if ( r > 80 && r <= 90  ) {
            mat = Material.CHICKEN;
        } else if ( r > 90 ) {
            mat = Material.MUTTON;
        }

        return mat;
    }

    private String transmuteTool(MagicChest magicChest, int itemSlot, Player player ) {
        ItemStack target = magicChest.getChest().getInventory().getItem( itemSlot );
        String type = target.getType().toString();
        String ingredientString = "";
        ArrayList<String> msgs = new ArrayList<>();
        if ( type.contains( "TRIDENT" ) ) {
            Random seed = new Random();
            int r = seed.nextInt( 3 ) + 1;
            ItemStack shards = new ItemStack( Material.PRISMARINE_SHARD );
            shards.setAmount( r );
            player.getWorld().dropItem( player.getLocation(), shards );
            msgs.add( String.valueOf( r ) + " prismarine shards" );
            ingredientString = type.toLowerCase() + " ";
        }
        if ( type.contains( "BOW" ) || type.contains("FISHING_ROD") ) {
            Random seed = new Random();
            int r = seed.nextInt( 2 ) + 1;
            ItemStack string = new ItemStack( Material.STRING );
            string.setAmount( r );
            player.getWorld().dropItem( player.getLocation(), string );
            msgs.add( String.valueOf( r ) + " piece(s) of string" );
            ingredientString = type.toLowerCase() + " ";
        }
        if ( !type.contains( "BOW") && !type.contains("FISHING_ROD") && !type.equals( "TRIDENT" ) ) {
            Random seed = new Random();
            int bound = getItemMatMax( type );
            int r = seed.nextInt( bound ) + 1;
            ItemStack mat = new ItemStack( Material.COBBLESTONE );
            String matString = " cobblestone";
            if ( type.contains( "IRON" ) ) {
                mat.setType( Material.IRON_INGOT );
                matString = " iron ingot(s)";
                ingredientString = "iron ";
            } else if ( type.contains( "GOLDEN" ) ) {
                mat.setType( Material.GOLD_INGOT );
                matString = " gold ingot(s)";
                ingredientString = "golden ";
            } else if ( type.contains( "DIAMOND" ) ) {
                mat.setType( Material.DIAMOND );
                matString = " diamond(s)";
                ingredientString = "diamond ";
            } else if ( type.contains( "NETHERITE" ) ) {
                mat.setType( Material.NETHERITE_SCRAP );
                matString = " piece(s) of netherite scrap";
                ingredientString = "netherite ";
            }
            mat.setAmount( r );
            player.getWorld().dropItem( player.getLocation(), mat );
            msgs.add( String.valueOf( r ) + matString );
        }
        if ( type.contains( "AXE" ) || type.contains( "HOE" ) || type.contains( "PICKAXE") || type.contains( "SHOVEL" )
                || type.contains( "SWORD") || type.contains("BOW") || type.contains("FISHING_ROD") ) {
            Random seed = new Random();
            int bound = 2;
            if ( type.contains( "SWORD" ) ) bound = 1;
            int r = seed.nextInt( bound ) + 1;
            if ( r > 0 ) {
                ItemStack stick = new ItemStack( Material.STICK );
                stick.setAmount( r );
                player.getWorld().dropItem( player.getLocation(), stick );
                msgs.add( String.valueOf( r ) + " stick(s)" );
            }
            String ing = type.contains("_") ? type.split( "_" )[1] : "";
            ingredientString += ing.toLowerCase() + " ";
        }

        if ( target.getEnchantments().size() > 0 ) {
            Random seed = new Random();
            int r = seed.nextInt( 2 ) + 1;
            ItemStack lapis = new ItemStack( Material.LAPIS_LAZULI );
            lapis.setAmount( r );
            player.getWorld().dropItem( player.getLocation(), lapis );
            msgs.add( String.valueOf( r ) + " lapis lazuli" );
        }

        String message = "a " + ingredientString + "into ";
        ListIterator<String> iter = msgs.listIterator();
        while ( iter.hasNext() ) {
            if ( iter.nextIndex() > 0 && msgs.size() > 2 ) {
                message += ", ";
            }
            if ( iter.nextIndex() > 0 && iter.nextIndex() + 1 == msgs.size() ) {
                message += " and ";
            }
            message += iter.next();
        }
        magicChest.getChest().getInventory().setItem( itemSlot, new ItemStack( Material.AIR ) );
        return message + "!";
    }

    public static int getItemMatMax( String type ) {
        if ( type.contains( "NETHERITE" ) ) {
            return 4;
        } else if ( type.contains( "HOE" ) || type.contains( "SWORD" ) ) {
            return 2;
        } else if ( type.contains( "SHOVEL" ) ) {
            return 1;
        } else if ( type.contains( "BOOTS") ) {
            return 4;
        } else if ( type.contains( "CHESTPLATE") ) {
            return 8;
        } else if ( type.contains( "HELMET" ) ) {
            return 5;
        } else if ( type.contains( "LEGGINGS" ) ) {
            return 7;
        }
        return 3;
    }
}