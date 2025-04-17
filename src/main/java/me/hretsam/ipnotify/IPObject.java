package me.hretsam.ipnotify;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Hretsam
 */
public class IPObject {

    private String value;
    private Long date;

    /**
     * Constructor of IIP
     * ip underscores will be set to dots, if any
     * @param value (IP or name)
     * @param date 
     */
    public IPObject(String value, Long date) {
        if (value.matches("([0-9]{1,3}_){0,3}[0-9]{1,3}")) {
            value = value.replaceAll("_", "\\.");
        }
        this.value = value;
        this.date = date;
    }

    /**
     * Returns the data as a long
     * @return 
     */
    public Long getDateLong() {
        return date;
    }

    /**
     * Returns the data as a Date object
     * @return 
     * @see Date
     */
    public Date getDate() {
        return new Date(date);
    }

    /**
     * Returns the data as a string
     * @return 
     */
    public String getDateString() {
        DateFormat dateFormat = new SimpleDateFormat(IPNotify.getConfig().dateSyntax);
        return dateFormat.format(date);
    }

    /**
     * Returns the value
     * Van be IP or a name
     * @return 
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the ip with the date
     * @return 
     */
    @Override
    public String toString() {
        return value + " '" + getDateString() + "'";
    }
    
    public int compare(IPObject o2) {
        if (getDateLong() < o2.getDateLong()) {
            return 1;
        } else if (getDateLong() == o2.getDateLong()){
            return 0;
        } else {
            return -1;
        }
    }
}
