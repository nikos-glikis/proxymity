package com.object0r.tools.proxymity;


import com.object0r.toortools.os.RecurringProcessHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainCollect
{

    public static void main(String[] args)
    {
        RecurringProcessHelper.checkAndRun("proxymity");

        Properties properties = Main.readProperties();

        try
        {
            if (properties.getProperty("exitAfterMinutes") != null)
            {
                RecurringProcessHelper.exitAfterSeconds(Integer.parseInt(properties.getProperty("exitAfterMinutes")) * 60);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Proxymity proxymity = new Proxymity(properties);

        //proxymity.useTor();

        //proxymity.startCheckers();
        proxymity.startCollectors();
    }


}
