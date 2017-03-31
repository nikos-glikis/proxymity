package com.object0r.tools.proxymity;


import com.object0r.toortools.os.RecurringProcessHelper;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainQualityTests
{

    public static void main(String[] args)
    {
        Properties properties = readProperties();
        Proxymity proxymity = new Proxymity(properties, false);
        proxymity.startQualityChecks();
    }

    static Properties readProperties()
    {
        Properties prop = new Properties();
        InputStream input = null;

        try
        {
            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            System.out.println(prop.getProperty("database"));

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }

}
