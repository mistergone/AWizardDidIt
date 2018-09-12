package me.mistergone.AWizardDidIt.helpers;

import com.mysql.fabric.xmlrpc.base.Array;
import me.mistergone.AWizardDidIt.MagicPattern;

import java.util.ArrayList;

import static me.mistergone.AWizardDidIt.Wizardry.getWizardry;

public class PatternFinder {

    public MagicPattern matchPattern( String[] needle ) {
        ArrayList<MagicPattern> AllMagicPatterns = getWizardry().getMagicPatterns();

        for (MagicPattern magicPattern : AllMagicPatterns) {
            ArrayList<String[]> patterns = magicPattern.getPatterns();
            for (String[] haystackPattern : patterns) {
                for (int i = 0; i < needle.length; i++) {
                    if (needle[i] != haystackPattern[i]) {
                        break;
                    } else if (i == needle.length - 1) {
                        return magicPattern;
                    }
                }
            }
        }

        return null;
    }
}
