package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class MoveHelper {

    public static Boolean isItSafe( Block b ) {
        Material footType = b.getType();
        Material headType = b.getRelative( BlockFace.UP ).getType();
        Material floorType = b.getRelative( BlockFace.DOWN ).getType();
        if ( !footType.isSolid() && !BlockHelper.isDangerous( footType ) &&
                !headType.isSolid() && !BlockHelper.isDangerous( headType ) &&
                floorType.isSolid() && floorType != Material.MAGMA_BLOCK ) {
            return true;
        } else {
            return false;
        }

    }
}
