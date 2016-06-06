package com.object0r.tools.proxymity;


import com.object0r.toortools.os.RecurringProcessHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main
{

    public static void main(String[] args)
    {
        RecurringProcessHelper.checkAndRun("proxymity");

        Properties properties = readProperties();

        /*DbInformation dbInformation = new DbInformation(
                properties.getProperty(properties.getProperty("databaseHost")),
                properties.getProperty(properties.getProperty("databaseUser")),
                properties.getProperty("databasePassword"),
                Integer.parseInt(properties.getProperty("databasePort")),
                properties.getProperty("database"));
        */
        try
        {
            if (properties.getProperty("exitAfterMinutes")!=null)
            {
                RecurringProcessHelper.exitAfterSeconds(Integer.parseInt(properties.getProperty("exitAfterMinutes"))*60);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Proxymity proxymity = new Proxymity(properties);

        //proxymity.useTor();

        proxymity.startCheckers();
        proxymity.startCollectors();
    }
    static Properties readProperties()
    {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            System.out.println(prop.getProperty("database"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }

}
