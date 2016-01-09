package com.tools.proxymity.collectors;

import com.mysql.jdbc.Util;
import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.ProxyInfo;
import com.toortools.Utilities;
import com.toortools.os.OsHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InCloakCollector extends ProxyCollector
{
    public InCloakCollector(Connection dbConnection)
    {
        super(dbConnection);

    }

    public Vector<ProxyInfo> collectProxies()
    {
        Vector<ProxyInfo> proxies = new Vector<ProxyInfo>();
        try
        {
            String page = Utilities.readUrl("https://incloak.com/proxy-list/");

            Pattern p = Pattern.compile("<tr><td class=tdl.*?</tr>");
            Matcher m = p.matcher(page);

            while (m.find())
            {
                try
                {
                    String line = m.group();
                    System.out.println(line);
                    String ip = Utilities.cut("class=tdl>","<", line).trim();
                    System.out.println("Ip: "+ ip);
                    String portImage = "https://incloak.com"+Utilities.cut("<img src=\"","\"", line).trim();
                    System.out.println("PortImage: "+ portImage);
                    Utilities.downloadFile(portImage, "tmp/image.gif");

                    convertImageToPnm("tmp/image.gif", "tmp/image.pnm");

                    System.exit(0);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return proxies;
    }

    private void convertImageToPnm(String inputFilename, String outputFilename )
    {
        try
        {
            Runtime rt = Runtime.getRuntime();
            String command = "convert \"" +  inputFilename + "\" \"" +outputFilename+"\"";
            System.out.println(command);
            Process pr = rt.exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line=null;

            while((line=input.readLine()) != null) {
                System.out.println(line);
            }

            input = new BufferedReader(new InputStreamReader(pr.getErrorStream()));

            line=null;

            while((line=input.readLine()) != null) {
                System.out.println(line);
            }

            System.out.println(pr.waitFor());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
