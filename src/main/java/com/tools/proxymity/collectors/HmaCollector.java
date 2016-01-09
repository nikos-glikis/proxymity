package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.ProxyInfo;
import com.toortools.Utilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.Vector;
import java.util.logging.Level;

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
            PhantomJSDriver  driver = new PhantomJSDriver ();

            driver.get("http://proxylist.hidemyass.com/2#listable");

            WebElement body = driver.findElement(By.className("flat-page"));
            System.out.println(body.getText());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return proxies;
    }
}
