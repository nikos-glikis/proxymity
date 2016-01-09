package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.ProxyInfo;
import com.toortools.Utilities;
import com.toortools.os.OsHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HmaCollector extends ProxyCollector
{
    @Override
    public Vector<ProxyInfo> collectProxies()
    {
        Vector<ProxyInfo> proxies = new Vector<ProxyInfo>();
        try
        {
  /*          System.out.println(Utilities.readUrl("http://proxylist.hidemyass.com/2#listable"));
            System.exit(0);*/
            java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

            //Utilities.readUrl("http://proxylist.hidemyass.com/2#listable");
            Capabilities caps = new DesiredCapabilities();
            String[] phantomArgs = new  String[] {
                    "--webdriver-loglevel=NONE"
            };

            if (OsHelper.isWindows())
            {
                ((DesiredCapabilities) caps).setJavascriptEnabled(true);
                ((DesiredCapabilities) caps).setJavascriptEnabled(true);
                ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
                ((DesiredCapabilities) caps).setCapability(
                        PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                        "bin\\phantomjs.exe"
                );
                ((DesiredCapabilities) caps).setCapability(
                        PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs
                );
            }
            else
            {
                ((DesiredCapabilities) caps).setJavascriptEnabled(true);
                ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
                /*((DesiredCapabilities) caps).setCapability(
                        PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                        "your custom path\\phantomjs.exe"
                );*/
                ((DesiredCapabilities) caps).setCapability(
                        PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs
                );
            }
            PhantomJSDriver driver = new PhantomJSDriver (caps);
            for (int i = 1; i<50; i++)
            {
                driver.get("http://proxylist.hidemyass.com/"+i);
                WebElement body = driver.findElement(By.className("flat-page"));
                String page = body.getText();
                Scanner sc = new Scanner(page);
                System.out.println(page);

                while (sc.hasNext())
                {
                    try {


                        String line = sc.nextLine().trim();
                        Pattern p = Pattern.compile("\\d*\\.\\d*\\.\\d*.\\d*");
                        Matcher m = p.matcher(line);
                        if (m.find()) {

                            String ip = m.group();
                            System.out.println(line);
                            String nextLine = sc.nextLine().trim();
                            System.out.println(nextLine);
                            //System.out.println("-");
                            String port = Utilities.cut(ip + " ", " ", line);
                            String type = nextLine.substring(0, nextLine.indexOf(" ")).trim();
                            /*System.out.println("Ip: " + ip);
                            System.out.println("Port: " + port);
                            */System.out.println("Type: " + type);

                            //System.out.println("-------------");
                        } else {
                            //System.out.println("Not");
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                System.exit(0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return proxies;
    }
}
