package me.mistergone.AWizardDidIt.helpers;

import me.mistergone.AWizardDidIt.baseClasses.MagicSign;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.WallSign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class BlockManager {
    // BlockManager is a set of methods for making a "box" of Blocks, which is an ArrayList
    // that represents a rectangular prism of Blocks in the World. It also contains some helpful
    // statics

    // Things that Silk Touch works on, for a pick
    public static final List SILKY_PICK_TYPES = Arrays.asList( new Material[] {
            Material.BLUE_ICE,
            Material.BOOKSHELF,
            Material.CAMPFIRE,
            Material.CLAY,
            Material.COAL_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.ENDER_CHEST,
            Material.GLASS,
            Material.GLASS_PANE,
            Material.GLOWSTONE,
            Material.GRASS_BLOCK,
            Material.ICE,
            Material.LAPIS_ORE,
            Material.ENDER_CHEST,
            Material.MELON,
            Material.BROWN_MUSHROOM_BLOCK,
            Material.RED_MUSHROOM_BLOCK,
            Material.REDSTONE_ORE,
            Material.MYCELIUM,
            Material.NETHER_QUARTZ_ORE,
            Material.PODZOL,
            Material.REDSTONE_ORE,
            Material.SEA_LANTERN,
            Material.STONE,
            Material.TURTLE_EGG
    } );

    public static List<Material> airTypes = new ArrayList<Material>( Arrays.asList( Material.AIR, Material.CAVE_AIR ) );

    public static List<BlockFace> sides = new ArrayList<>( Arrays.asList( BlockFace.NORTH, BlockFace.EAST,
            BlockFace.SOUTH, BlockFace.WEST ) );

    /**
     * Returns whether a Material is a "silky pick type"
     * @param material The Material in question
     * @return Boolean True if material is a silky pick type
     */

    public static Boolean isSilkyPickType( Material material ) {
        if ( SILKY_PICK_TYPES.contains( material ) ) {
            return true;
        } else if ( Tag.CORALS.isTagged( material ) || Tag.CORAL_BLOCKS.isTagged( material ) || Tag.CORAL_PLANTS.isTagged( material )  ) {
            // Coral is silk pickable!
            return true;
        } else if ( Tag.LEAVES.isTagged( material ) ) {
            return true;
        }

        return false;
    }

    /**
     * Gets a box of Blocks based on a block and a facing. The box selection is in the OPPOSITE direction
     * of the BlockFace 'face' parameter, so the clicked face can be passed easily as a parameter. The box is
     * 'square' on one side, because the width/height of the box are indistinguishable.
     * @param block The Block to start with
     * @param face The facing OPPOSITE of the direction of the box
     * @param size The height and width of the box, which are perpendicular to the 'face' direction
     * @param depth The depth of the box, which parallel to the 'face' direction
     * @return ArrayList<Block> An Array of Blocks
     */
    public static ArrayList<Block> getSquareBoxFromFace( Block block, BlockFace face, int size, int depth ) {
        ArrayList<Block> blockBox = new ArrayList<>();
        BlockFace opp = face.getOppositeFace();
        // Even number size will be rounded up
        if ( size % 2 == 0 ) {
            size += 1;
        }
        int x = -1 * (int)Math.floor( size / 2 );
        int xMod = 1;
        int xMax = x + size - 1;
        int y = -1 * (int)Math.floor( size / 2 );
        int yMod = 1;
        int yMax = y + size - 1;
        int z = -1 * (int)Math.floor( size / 2 );
        int zMod = 1;
        int zMax = z + size - 1;
        if ( opp.getModX() != 0 ) {
            x = 0;
            xMod = xMod * opp.getModX();
            xMax = depth - 1;
        } else if ( opp.getModY() != 0 ) {
            y = 0;
            yMod = yMod * opp.getModY();
            yMax = depth - 1;
        } else if ( opp.getModZ() != 0 ) {
            z = 0;
            zMod = zMod * opp.getModZ();
            zMax = depth - 1;
        }
        for ( int a = x; a <= xMax; a++ ) {
            for ( int b = y; b <= yMax; b++ ) {
                for ( int c = z; c <= zMax; c++ ) {
                    blockBox.add( block.getRelative( a * xMod, b * yMod, c * zMod ) );
                }
            }
        };

        return blockBox;
    }

    public static ArrayList<Block> getCubeByRadius( Block block, int radius ) {
        ArrayList<Block> blockBox = new ArrayList<>();
        Location loc = block.getLocation();
        for ( int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++  ) {
            for ( int y = loc.getBlockY() - radius; y <= loc.getBlockY() + radius; y++ ) {
                for ( int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++ ) {
                    blockBox.add( loc.getWorld().getBlockAt( x, y, z ) );
                }
            }
        }
        return blockBox;
    }

    public static ArrayList<Block> getBoxByDimensions( Block block, int xSize, int ySize, int zSize) {
        ArrayList<Block> blockBox = new ArrayList<>();
        Location loc = block.getLocation();
        for ( int x = loc.getBlockX() - xSize; x <= loc.getBlockX() + xSize; x++  ) {
            for ( int y = loc.getBlockY() - ySize; y <= loc.getBlockY() + ySize; y++ ) {
                for ( int z = loc.getBlockZ() - zSize; z <= loc.getBlockZ() + zSize; z++ ) {
                    blockBox.add( loc.getWorld().getBlockAt( x, y, z ) );
                }
            }
        }
        return blockBox;
    }

    public static BlockFace yawToFace( float yaw ) {
        double angle = Math.round( yaw );
        // Turn the angle if it's less than zero
        if ( angle < 0 ) angle += 360;
        if ( angle > 315 || angle <= 45 ) {
            return BlockFace.SOUTH;
        } else if ( angle > 45 && angle <= 135 ) {
            return BlockFace.WEST;
        } else if ( angle > 135 && angle <= 225 ) {
            return BlockFace.NORTH;
        } else {
            return BlockFace.EAST;
        }
    }

    public static String locToString( Location loc ) {
        String str = loc.getBlockX() + ", "
                + loc.getBlockY() + ", "
                + loc.getBlockZ();
        return str;
    }

    public static Boolean isDangerous( Material m ) {
        if ( m == Material.LAVA || m == Material.FIRE ) {
            return true;
        } else {
            return false;
        }
    }


    // sign helpers
    public static Boolean isWizardSign( Block b ) {
        if ( !( b.getType().toString().contains( "SIGN" ) ) ) return false;
        Sign sign = (Sign) b.getState();
        String[] lines = sign.getLines();
        String signature = ChatColor.stripColor(lines[0].trim());
        MagicSign magicSign = getWizardry().getMagicSign(signature);
        if ( magicSign != null ) return true;

        return false;
    }

    public static ArrayList<Block> getAttachedSigns( Block b ) {
        ArrayList<Block> signs = new ArrayList<>();
        for ( BlockFace face: sides ) {
            Block check = b.getRelative( face );
            if ( Tag.WALL_SIGNS.isTagged( check.getType() ) ) {
                WallSign sign = (WallSign) check.getBlockData();
                if ( sign.getFacing() == face ) {
                    signs.add( check );
                }
            }
        }
        return signs;
    }

    public static Boolean hasAttachedWizardSigns( Block b ) {
        ArrayList<Block> signs = getAttachedSigns( b );
        for ( Block signBlock : signs ) {
            if ( isWizardSign( signBlock) ) return true;
        }

        return false;
    }


    public static Boolean hasSignOpposite( Block b, BlockFace clickedFace ) {
        Block check = b.getRelative( clickedFace.getOppositeFace() );
        if ( Tag.WALL_SIGNS.isTagged( check.getType() ) ) {
            WallSign sign = (WallSign) check.getBlockData();
            if ( sign.getFacing() == clickedFace.getOppositeFace() ) {
                return true;
            }
        }
        return false;
    }

    public static String getSignOwner( Sign s ) {
        String[] lines = s.getLines();
        return  ChatColor.stripColor( lines[3] );
    }

}
