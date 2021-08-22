package me.mistergone.AWizardDidIt.patterns;

import me.mistergone.AWizardDidIt.baseClasses.MagicChest;
import me.mistergone.AWizardDidIt.baseClasses.MagicPattern;
import me.mistergone.AWizardDidIt.baseClasses.PatternFunction;
import me.mistergone.AWizardDidIt.helpers.SpecialEffects;
import me.mistergone.AWizardDidIt.helpers.WizardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class WizardTransmute extends MagicPattern {

    public WizardTransmute() {
        patternName = "Wizard Transmute";
        keys = new Material[]{ Material.GOLD_INGOT };
        patterns =  new HashMap<String, String[]>();
        patterns.put( "Wizard Dust", new String[]
                {"NONE", "ANY", "NONE",
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

                ItemStack reagent = magicChest.getChest().getInventory().getItem( 1 );
                if ( reagent == null ) {
                    player.sendMessage( ChatColor.RED + "Wizard Transmute requires an item to be transmuted, which should be placed above the gold ingot in the chest!");
                    return;
                }

                WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
                String message = ChatColor.GOLD + "You have transmuted ";
                String error = null;

                switch ( reagent.getType() ) {
                    case BONE_MEAL:
                        if ( reagent.getAmount() < 3 ) {
                            error = "It takes at least 3 bone meal to transmute!";
                            break;
                        }
                        if ( !wizardPlayer.spendWizardPower( 50 , patternName ) ) return;
                        reagent.setAmount( reagent.getAmount() - 3 );
                        ItemStack dust = new ItemStack( Material.GLOWSTONE_DUST );
                        dust.setAmount( 1 );
                        player.getWorld().dropItem( player.getLocation(), dust );
                        message += "3 bone meal into 1 glowstone dust!";
                        break;
                    case COBBLESTONE:
                        if ( !wizardPlayer.spendWizardPower( 10 , patternName ) ) return;
                        message += transmuteCobble( magicChest, player );
                        break;
                    case ROTTEN_FLESH:
                        if ( !wizardPlayer.spendWizardPower( 75 , patternName ) ) return;
                        message += transmuteRottenFlesh( magicChest, player );
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
                        message += transmuteTool( magicChest, player );
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
                        message += transmuteArmor( magicChest, player );
                        break;
                    default:
                        error = "The item above the gold ingot in this chest is not transmutable!";
                }

                if ( error != null ) {
                    player.sendMessage( ChatColor.RED + error );
                } else {
                    player.sendMessage( message );
                    Location loc = magicChest.getChest().getLocation().add(0, 1, 0);
                    SpecialEffects.magicChest(loc);
                }

            }
        };
    }

    private String transmuteArmor (MagicChest magicChest, Player player) {
        ItemStack target = magicChest.getChest().getInventory().getItem( 1 );
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
        msgs.add( String.valueOf( r ) + matString );

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
        Bukkit.broadcastMessage( message );
        magicChest.getChest().getInventory().setItem( 1, new ItemStack( Material.AIR ) );
        return message + "!";
    }

    private String transmuteCobble (MagicChest magicChest, Player player) {
        ItemStack target = magicChest.getChest().getInventory().getItem( 1 );
        Random seed = new Random();
        int r = seed.nextInt( 100 ) + 1;
        ItemStack mat = new ItemStack( Material.BLACKSTONE );
        mat.setAmount( 1 );
        if ( r > 10 && r <= 32 ) {
            mat.setType( Material.ANDESITE );
        } else if ( r <= 54 ) {
            mat.setType( Material.DIORITE );
        } else if ( r <= 76 ) {
            mat.setType( Material.GRANITE );
        } else if ( r <= 98 ) {
            mat.setType( Material.COBBLED_DEEPSLATE );
        } else if ( r > 99  ) {
            mat.setType( Material.QUARTZ );
        }
        player.getWorld().dropItem( player.getLocation(), mat );
        target.setAmount( target.getAmount() - 1 );
        return "cobblestone into " + mat.getType().toString().toLowerCase().replace( "_", " ") + "!";
    }



    private String transmuteRottenFlesh(MagicChest magicChest, Player player ) {
        String message = "";
        WizardPlayer wizardPlayer = getWizardry().getWizardPlayer( player.getUniqueId() );
        ItemStack target = magicChest.getChest().getInventory().getItem( 1 );
        target.setAmount( target.getAmount() - 1 );
        ItemStack meat = new ItemStack( Material.PORKCHOP );
        meat.setAmount( 1 );
        message += "rotten flesh into ";
        String meatType = "raw pork";
        Random seed = new Random();
        int r = seed.nextInt( 100 ) + 1;
        if ( r > 50 && r <= 85 ) {
            meat.setType( Material.BEEF );
            meatType = "raw beef";
        } else if ( r > 85 ) {
            meat.setType( Material.CHICKEN );
            meatType = "raw chicken";
        }
        message += meatType + "!";
        player.getWorld().dropItem( player.getLocation(), meat );

        return message;
    }

    private String transmuteTool(MagicChest magicChest, Player player ) {
        ItemStack target = magicChest.getChest().getInventory().getItem( 1 );
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
        if ( type.contains( "BOW" ) ) {
            Random seed = new Random();
            int r = seed.nextInt( 2 ) + 1;

            ItemStack string = new ItemStack( Material.STRING );
            string.setAmount( r );
            player.getWorld().dropItem( player.getLocation(), string );
            msgs.add( String.valueOf( r ) + " piece(s) of string" );
            ingredientString = type.toLowerCase() + " ";
        }
        if ( !type.contains( "BOW") && !type.equals( "TRIDENT" ) ) {
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
                || type.contains( "SWORD") ) {
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
            String split[] = type.split( "_" );
            ingredientString += split[1].toLowerCase() + " ";
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
                message += "and ";
            }
            message += iter.next();
        }
        magicChest.getChest().getInventory().setItem( 1, new ItemStack( Material.AIR ) );
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