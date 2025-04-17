
package me.hretsam.ipnotify.data;

import java.util.Comparator;
import me.hretsam.ipnotify.IPObject;

/**
 * Comparator for the IIP class
 * @author Hretsam
 */
class IIPComparator implements Comparator<IPObject> {

    /**
     * Compares 2 IPP objects on their date, to see which one is more recent
     * @param o1
     * @param o2
     * @return 
     */
    @Override
    public int compare(IPObject o1, IPObject o2) {
        if (o1.getDateLong() < o2.getDateLong()) {
            return 1;
        } else {
            return 0;
        }
    }
}