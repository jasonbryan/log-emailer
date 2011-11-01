/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logemailer;

/**
 *
 * @author wbryan
 */
public class Utility {
    
    /**
     * Append a zero to the front of a single digit number
     * @param number - integer number
     * @return return a string with an append zero if the number is a single digit
     */
    public static String appendZeroOnSingles(int number) {
        if (number < 10) {
            return ("0" + number);
        }
        
        return String.valueOf(number);
    }
    
    
    /**
     * remove space if they exist at the front and back of a string
     * @param str - string
     * @return string with removed spaces at front and/or back
     */
    public static String removeFrontAndBackSpaces(String str) {
        /* remove front space */
        if (str.charAt(0) == ' ') {
            str = str.substring(1, str.length());            
        }
        
        /* remove trailing space */
        if (str.endsWith(" ")) {
            str = str.substring(0, str.length() - 1);
        }
        
        return str;
    }
    
}
