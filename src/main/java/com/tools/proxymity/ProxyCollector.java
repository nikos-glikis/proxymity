package com.tools.proxymity;

import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.toortools.os.OsHelper;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;

abstract public class ProxyCollector extends  Thread
{
    private Vector<ProxyInfo> proxies = new Vector<ProxyInfo>();

    protected Connection dbConnection;
    protected PhantomJSDriver driver;
    protected boolean useTor = false;
    public ProxyCollector(CollectorParameters collectorParameters)
    {
        //this.collectorParameters = collectorParameters;
        this.dbConnection = collectorParameters.getDbConnection();
        this.useTor = collectorParameters.isUseTor();
    }

    public ProxyCollector() throws Exception
    {
        throw new Exception("Default controller not allowed");
    }
    public abstract Vector<ProxyInfo> collectProxies();

    final int SLEEP_SECONDS_BETWEEN_SCANS = 30;


    public void run()
    {
        while (true)
        {

            try
            {
                Thread.sleep(new Random().nextInt(5000));
                initProxies();
                Vector<ProxyInfo> proxyInfos = collectProxies();

                writeProxyInfoToDatabase(proxyInfos);

                Thread.sleep(SLEEP_SECONDS_BETWEEN_SCANS * 1000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private synchronized void initProxies()
    {
        proxies  = new Vector<ProxyInfo>();
    }

    protected synchronized void addProxy(ProxyInfo proxyInfo)
    {
        proxies.add(proxyInfo);
    }

    protected Vector<ProxyInfo> getProxies()
    {
        return proxies;
    }

    public synchronized void writeProxyInfoToDatabase()
    {
        this.writeProxyInfoToDatabase(this.proxies);
    }

    private void writeProxyInfoToDatabase(Vector<ProxyInfo> proxyInfos)
    {
        try
        {
            for (ProxyInfo proxyInfo : proxyInfos)
            {
                if (
                        proxyInfo.getHost() == null
                        || proxyInfo.getPort() == null
                        || proxyInfo.getType() == null

                        )
                {
                    continue;
                }
                String query = "INSERT INTO `proxies`.`"+Proxymity.TABLE_NAME+"` (" +
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
        String[] phantomArgs = new String[] {

        };
        if (!useTor)
        {
            phantomArgs = new  String[] {
                    "--webdriver-loglevel=NONE"
            };
        }
        else
        {
            phantomArgs = new  String[] {
                    "--webdriver-loglevel=NONE",
                    "--proxy=127.0.0.1:9050",
                    "--proxy-type=socks5"
            };
        }



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

    protected Proxy getRandomProxy() throws Exception
    {
        Proxy proxy = null ;
        try
        {
            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SELECT host,port,type FROM " + Proxymity.TABLE_NAME + " WHERE status = 'active' ORDER BY RAND() LIMIT 1");
            if (rs.next())
            {

                String host = rs.getString(1);
                String port = rs.getString(2);
                String proxyType = rs.getString(3);
                st.close();

                Proxy.Type type = null;

                if (proxyType.equals(ProxyInfo.PROXY_TYPES_SOCKS4)) {
                    type = Proxy.Type.SOCKS;
                } else if (proxyType.equals(ProxyInfo.PROXY_TYPES_SOCKS5)) {
                    type = Proxy.Type.SOCKS;
                } else if (proxyType.equals(ProxyInfo.PROXY_TYPES_HTTP)) {
                    type = Proxy.Type.HTTP;
                } else if (proxyType.equals(ProxyInfo.PROXY_TYPES_HTTPS)) {
                    type = Proxy.Type.HTTP;
                }
                return new Proxy(type, new InetSocketAddress(host, Integer.parseInt(port) ));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
        return null;
    }
}
