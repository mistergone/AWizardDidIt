package me.mistergone.AWizardDidIt.data;

import me.mistergone.AWizardDidIt.helpers.WizardryData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UnseenProjectManager {
    private static UnseenProjectManager unseenProjectManager = new UnseenProjectManager();
    private static HashMap<String, Location[]> unseenProjectList;
    private static ArrayList<String> signTitles;

    private UnseenProjectManager() {
        unseenProjectList = new HashMap<>();
        ArrayList<String> badProjects = new ArrayList<>();
        loadUnseenProjectList();
        for ( String key : unseenProjectList.keySet() ) {
            Location[] points = unseenProjectList.get( key );
            if ( points[0] != null && points[0].getBlock().getType() != Material.SIGN ) {
                points[0] = null;
            }
            if ( points[1] != null && points[1].getBlock().getType() != Material.SIGN ) {
                points[1] = null;
            }
            if ( points[0] == null && points[1] == null ) {
                badProjects.add( key );
            }
        }
        for ( String badKey : badProjects ) {
            unseenProjectList.remove( badKey );
        }

        signTitles = new ArrayList<>();
        signTitles.add( ChatColor.DARK_RED + "[UnseenArchitect]" );


    }

    public static UnseenProjectManager getUnseenPM() {
        return unseenProjectManager;
    }

    /**
     * Add unseenProject to the HashMap
     */
    public void addUnseenProject( Player p, String name ) {
        String projectKey = makeProjectString( p, name );
        unseenProjectList.put( projectKey, new Location[2] );
        saveUnseenProjectList();
    }

    /**
     * Removes project from unseenProjectList
     * @param projectKey - Name of project to remove
     */
    public void removeUnseenProject( String projectKey ) {
        if ( unseenProjectList.containsKey( projectKey ) ) {
            unseenProjectList.remove( projectKey );
            saveUnseenProjectList();
        }
    }

    public void removeUnseenProject( Player p, String projectName ) {
        String projectKey = makeProjectString( p, projectName );
        if ( unseenProjectList.containsKey( projectKey ) ) {
            unseenProjectList.remove( projectKey );
            saveUnseenProjectList();
        }
    }

    public Boolean checkProjectExists( Player p, String name ) {
        String projectKey = makeProjectString( p, name.toLowerCase() );
        return unseenProjectList.containsKey( projectKey );
    }

    public Location[] getProjectPoints( Player p, String projectName ) {
        String projectKey = makeProjectString( p, projectName );
        return unseenProjectList.get( projectKey );
    }

    public Location[] getProjectPoints( String projectKey ) {
        return unseenProjectList.get( projectKey );
    }

    public void setProjectPoint( Player p, String projectName, int index, Location loc ) {
        String projectKey = makeProjectString( p, projectName );
        Location[] locs = unseenProjectList.get( projectKey );
        locs[index] = loc;
        unseenProjectList.put( projectKey, locs );
        saveUnseenProjectList();
    }

    /**
     * Loads UnseenProject list
     * @return HashMap of ids and UnseenProject objects
     */
    private void loadUnseenProjectList() {
        File path = WizardryData.openWizardryFolder( "/" );
        String fileName = "unseen-projects.yml";
        FileConfiguration unseenDataConfig;
        File unseenDataFile = WizardryData.openWizardryFile( path, fileName );
        if ( unseenDataFile.exists() ) {
            unseenDataConfig = YamlConfiguration.loadConfiguration( unseenDataFile );
            List<String> rawProjects = unseenDataConfig.getStringList("Unseen Projects" );

            for ( String line: rawProjects ) {
                String[] project = line.split( "," );
                String projectKey = project[0];
                Location loc1 = null;
                Location loc2 = null;
                if ( !project[1].equals("null") && !project[2].equals("null") && !project[3].equals("null") && !project[4].equals("null") ) {
                    loc1 = new Location( Bukkit.getWorld( project[1] ), Double.valueOf( project[2] ), Double.valueOf( project[3] ), Double.valueOf( project[4] ) );
                }
                if ( !project[5].equals("null") && !project[6].equals("null") && !project[7].equals("null") && !project[8].equals("null") ) {
                    loc2 = new Location( Bukkit.getWorld( project[5] ), Double.valueOf( project[6] ), Double.valueOf( project[7] ), Double.valueOf( project[8] ) );
                }
                Location[] locs = new Location[]{ loc1, loc2 };
                unseenProjectList.put( projectKey, locs );

            }
        }
    }

    /**
     * Save unseenProjectList to file
     */
    private void saveUnseenProjectList() {
        File path = WizardryData.openWizardryFolder( "/" );
        String fileName = "unseen-projects.yml";
        FileConfiguration unseenDataConfig;
        File unseenDataFile = WizardryData.openWizardryFile( path, fileName );
        if ( !unseenDataFile.exists() ) {
            unseenDataFile = WizardryData.createWizardryFile( path, fileName );
            unseenDataConfig = new YamlConfiguration();
            unseenDataConfig.createSection("Unseen Projects");
        } else {
            unseenDataConfig = YamlConfiguration.loadConfiguration( unseenDataFile );
        }
        try {
            List<String> entries = new ArrayList<>();
            for ( String key: unseenProjectList.keySet() ) {
                String line = key + ",";
                Location[] locs = unseenProjectList.get( key );
                if ( locs[0] != null ) {
                    line += locs[0].getWorld().getName() + "," + locs[0].getBlockX() + "," + locs[0].getBlockY() + "," + locs[0].getBlockZ() + ",";
                } else {
                    line += "null,null,null,null,";
                }
                if ( locs[1] != null ) {
                    line += locs[1].getWorld().getName() + "," + locs[1].getBlockX() + "," + locs[1].getBlockY() + "," + locs[1].getBlockZ();
                } else {
                    line += "null,null,null,null";
                }
                entries.add( line );
            }
            unseenDataConfig.set("Unseen Projects", entries );
            unseenDataConfig.save( unseenDataFile );
            System.out.println( fileName + " saved..." );
        } catch( Exception ex ) {
            ex.printStackTrace();
        }
    }

    public String makeProjectString(Player p, String name ) {
        return p.getName() + ":" + name.toLowerCase();
    }


    public Boolean isUASign( String title ) {
        return signTitles.contains( title );
    }

}
