/*
    Log Emailer sends log files to your email as an attachment
    Copyright (C) 2010  Jason Bryan

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


package logemailer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 *
 * @author Jason
 */
public class LogFile {

    private File contents;
    private String hostName;
    private String path;
    private String name;
    private ArrayList<String> keywords;
    private boolean exists = false;


    /**
     * Default Constructor
     * @throws Exception
     */
    public LogFile() throws Exception{
        hostName = "";
        path = "";
        name = "";
        keywords = new ArrayList<String>();
     }


     /**
      * Constructor
      * @param logLocation
      * @throws Exception
      */
    public LogFile(String logLocationLine) throws Exception{
        hostName = "";
        path = "";
        name = "";
        keywords = new ArrayList<String>();

        /* Parses the logLocationLine into logFile info */
        parseLine(logLocationLine);

        name = convertDateHolders(name);

        retrieveLogFile();
    }


    /*
     * ------ PUBLIC MEHTODS ------
     */


    /**
     * search each line for key words
     * @return list of the line #'s and the line in html for the email
     * @throws Exception
     */
    public String keywordSearch() throws Exception {
        if(isExists() == true){
            BufferedReader fileReader = new BufferedReader(new FileReader(contents));

            int lineNumber = 0;
            String line = "";
            String keywordResults = "";

            /* search each log file line for keywords */
            while ( (line = fileReader.readLine()) != null ) {
                ++lineNumber;

                for (int i = 0; i < getKeywords().size(); ++i) {
                    String word = getKeywords().get(i);

                    if (line.contains(word)) {
                        keywordResults += ("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;line #" + lineNumber + " - " + line + "<br />");
                    }
                }
            }

            return keywordResults;
            }
            else{
                return "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;File Not Found! <br />";
            }
        }


    /*
     * ------ PRIVATE METHODS ------
     */


    /**
     * Divides the logLocationsLine into different variables
     * @param logLocation
     */
    private void parseLine(String logLocation){
         StringTokenizer logTokens = new StringTokenizer(logLocation, ",");

         if (logTokens.hasMoreTokens()) {
             hostName = logTokens.nextToken();
             hostName = hostName.replace(" ", "");
         }

         if (logTokens.hasMoreTokens()) {
             path = logTokens.nextToken();
             path = path.replace(" ", "");
         }

         if (logTokens.hasMoreTokens()) {
             name = logTokens.nextToken();
             name = name.replace(" ", "");
         }

         if (logTokens.hasMoreTokens()) {
             String words = logTokens.nextToken();
             words = words.replace(" ", "");

             StringTokenizer wordTokens = new StringTokenizer(words, ":");
             while (wordTokens.hasMoreTokens()) {
                 String word = wordTokens.nextToken();
                 getKeywords().add(word);
             }
         }
    }


    /**
     * Retrieves Log File
     * @throws Exception
     */
    private void retrieveLogFile() throws Exception{
        File file = new File(path.replaceAll("\\\\", "\\\\\\\\") + name);
        if(file.exists()){
            contents = file;
            setExists(true);
        }
        else{
            setExists(false);
        }
    }

    /**
     * replace holder values
     * @param holder
     * @return
     */
    private String convertDateHolders(String holder) {
        Calendar today = Calendar.getInstance();

        holder = holder.replace("YYYY", String.valueOf(today.get(Calendar.YEAR)));
        holder = holder.replace("MM", String.valueOf(today.get(Calendar.MONTH) + 1));
        holder = holder.replace("DD", String.valueOf(today.get(Calendar.DAY_OF_MONTH)));

        return holder;
    }


    /*
     * ------ GETTERS AND SETTERS -----
     */



    /**
     * @return the contents
     */
    public File getContents() {
        return contents;
    }

    /**
     * @param contents the contents to set
     */
    public void setContents(File contents) {
        this.contents = contents;
    }

    /**
     * @return the hostName
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @param hostName the hostName to set
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the keywords
     */
    public ArrayList<String> getKeywords() {
        return keywords;
    }

    /**
     * @param keywords the keywords to set
     */
    public void setKeywords(ArrayList<String> keywords) {
        this.setKeywords(keywords);
    }

    /**
     * @return the exists
     */
    public boolean isExists() {
        return exists;
    }

    /**
     * @param exists the exists to set
     */
    public void setExists(boolean exists) {
        this.exists = exists;
    }

}

