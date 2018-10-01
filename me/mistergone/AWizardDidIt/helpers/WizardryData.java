package me.mistergone.AWizardDidIt.helpers;

import org.bukkit.Bukkit;

import java.io.File;

public class WizardryData {
    public WizardryData() {

    }

    /**
     * Try to open the folder, or create it if it doesn't exist
     * @param path - String of the file path, should start with "/"
     * @return File of directory opened
     */
    public static File openWizardryFolder( String path ) {
        String fullPath = Bukkit.getPluginManager().getPlugin( "AWizardDidIt" ).getDataFolder().toString() + path;
        File filePath = new File( fullPath );

        if( !filePath.exists() ){
            System.out.println("File path does not exist, creating it...");
            filePath.mkdirs();
            try {
                filePath.createNewFile();
                System.out.println("...successfully created file path!");
            } catch( Exception ex ) {
                System.out.println("EXCEPTION!");
                ex.printStackTrace();
            }
        }

        return filePath;
    }

    /**
     * Attempt to open the file and create it if it doesn't exist
     * @param filePath - Directory where the file is
     * @param fileName - Name of the file
     * @return - File object
     */
    public static File openWizardryFile( File filePath, String fileName ) {
        File dataFile = new File(  filePath + "/" + fileName );

        return dataFile;
    }

    public static File createWizardryFile( File filePath, String fileName ) {
        File dataFile = new File(  filePath + "/" + fileName );
        if( !dataFile.exists() ){
            System.out.println( "File doesnt exist, creating it..." );
            try {
                dataFile.createNewFile();
                System.out.println("...successfully created file!");
            } catch( Exception ex ) {
                ex.printStackTrace();
            }
        }

        return dataFile;
    }

}
