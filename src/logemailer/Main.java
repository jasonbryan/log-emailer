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

/**
 *
 * @author Jason
 */
public class Main {

    /**
    * @param args the command line arguments
    */
    public static void main(String[] args) throws Exception{
        File file = new File("logLocations.txt");
        BufferedReader fileReader = new BufferedReader(new FileReader(file));

        /* Pulles the first log information from the log_locations document */
        ArrayList<LogFile> logFiles = new ArrayList<LogFile>();

        String logLocation = "";
        while ( (logLocation = fileReader.readLine()) != null ) {
            /* do not include comments */
            if ( (!logLocation.contains("#")) && (logLocation.length() != 0) ) {
                logFiles.add( new LogFile(logLocation) );
            }
        }
        
        /* Sends contents of LogFiles to recipients in mailingList */
        Email mailSender = new Email(logFiles);        
        mailSender.postMail();
    }

}

