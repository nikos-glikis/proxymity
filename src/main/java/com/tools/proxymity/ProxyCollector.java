package com.tools.proxymity;

import com.toortools.os.OsHelper;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;

abstract public class ProxyCollector extends  Thread
{
    protected Connection dbConnection;
    protected PhantomJSDriver driver;
    public ProxyCollector(Connection dbConnection)
    {
        this.dbConnection = dbConnection;
    }

    public ProxyCollector() throws Exception
    {
        throw new Exception("Default controller not allowed");
    }
    public abstract Vector<ProxyInfo> collectProxies();

    final int SLEEP_SECONDS_BETWEEN_SCANS = 1;
    public void run()
    {
        while (true)
        {
            Vector<ProxyInfo> proxyInfos = collectProxies();
            System.out.println("Importing "+ proxyInfos.size()+ " proxies.");
            writeProxyInfoToDatabase(proxyInfos);
            try
            {
                Thread.sleep(SLEEP_SECONDS_BETWEEN_SCANS * 1000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void writeProxyInfoToDatabase(Vector<ProxyInfo> proxyInfos)
    {
        try
        {
            for (ProxyInfo proxyInfo : proxyInfos)
            {
                String query = "INSERT INTO `proxies`.`proxymity_proxies` (" +
                        "`id`, " +
                        "`host`, " +
                        "`port`, " +
                        "`type`, " +
                        "`inserted`, " +
                        "`lastchecked`, " +
                        "`status`, " +
                        "`fullanonymous` " +

                        ") VALUES  (" +
                        "0, " +
                        "'"+sanitizeDatabaseInput(proxyInfo.getHost())+"', " +
                        "'"+sanitizeDatabaseInput(proxyInfo.getPort())+"', " +
                        "'"+sanitizeDatabaseInput(proxyInfo.getType())+"', " +
                        "NOW(), " +
                        "NULL, " +
                        "'pending', " +
                        "'no')";
                //System.out.println(query);

                Statement st = dbConnection.createStatement();
                try
                {
                    st.executeUpdate(query);
                }
                catch (Exception MySQLIntegrityConstraintViolationException)
                {

                }
                st.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    String sanitizeDatabaseInput(String value)
    {
        while (value.contains("''")) {
            value = value.replace("''","'");
        }
        return value.replace("'","''");
    }

    public void initializePhantom()
    {
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
            ((DesiredCapabilities) caps).setCapability(
                    PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs
            );
        }
        driver = new PhantomJSDriver(caps);
    }
}
