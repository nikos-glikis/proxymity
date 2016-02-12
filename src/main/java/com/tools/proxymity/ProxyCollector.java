package com.tools.proxymity;

import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.tools.proxymity.phantomjs.PhantomJsJob;
import com.tools.proxymity.phantomjs.PhantomJsJobResult;
import com.tools.proxymity.phantomjs.PhantomJsManager;
import com.toortools.Utilities;
import com.toortools.os.OsHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class ProxyCollector extends  Thread
{
    String imageMagickPath = "bin\\imageMagick\\convert.exe";
    private Vector<ProxyInfo> proxies = new Vector<ProxyInfo>();

    protected Connection dbConnection;
    protected PhantomJSDriver driver;
    protected boolean useTor = false;
    //This exists as global
    int CURRENT_SLEEP_SECONDS_BETWEEN_SCANS =0;
    protected PhantomJsManager phantomJsManager;
    public ProxyCollector(CollectorParameters collectorParameters)
    {
        try
        {

            Thread.sleep(new Random().nextInt(1000));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        phantomJsManager = collectorParameters.getPhantomJsManager();
        //this.collectorParameters = collectorParameters;
        this.CURRENT_SLEEP_SECONDS_BETWEEN_SCANS = collectorParameters.getSleepBetweenScansSeconds();

        this.dbConnection = collectorParameters.getDbConnection();
        this.useTor = collectorParameters.isUseTor();

        if (!new File("tmp/").isDirectory())
        {
            new File("tmp").mkdir();
        }

        if (OsHelper.isWindows() && ! new File(imageMagickPath).exists())
        {
            System.out.println("Image magick is not installed, please install or update installation path. (http://www.imagemagick.org/download/binaries/ImageMagick-6.9.3-0-Q16-x64-dll.exe)");
        }
    }

    public ProxyCollector() throws Exception
    {
        throw new Exception("Default controller not allowed");
    }
    public abstract Vector<ProxyInfo> collectProxies();




    public void setSleepSecondsBetweenScans(int minutes)
    {
        this.CURRENT_SLEEP_SECONDS_BETWEEN_SCANS = minutes;
    }

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

                Thread.sleep(this.CURRENT_SLEEP_SECONDS_BETWEEN_SCANS * 1000);
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
                        "`fullanonymous`, " +
                        "`lastactive` " +

                        ") VALUES  (" +
                        "0, " +
                        "'"+sanitizeDatabaseInput(proxyInfo.getHost())+"', " +
                        "'"+sanitizeDatabaseInput(proxyInfo.getPort())+"', " +
                        "'"+sanitizeDatabaseInput(proxyInfo.getType())+"', " +
                        "NOW(), " +
                        "NULL, " +
                        "'pending', " +
                        "'no'," +
                        " NOW()" +
                        ")";
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

        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0";

        if (OsHelper.isWindows())
        {
            ((DesiredCapabilities) caps).setJavascriptEnabled(true);
            ((DesiredCapabilities) caps).setJavascriptEnabled(true);
            ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
            ((DesiredCapabilities) caps).setCapability("phantomjs.content.settings.resourceTimeout", 3000);
            ((DesiredCapabilities) caps).setCapability(
                    PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                    "bin\\phantomjs.exe"
            );
            ((DesiredCapabilities) caps).setCapability(
                    PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs
            );
            ((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", userAgent);
        }
        else
        {
            ((DesiredCapabilities) caps).setJavascriptEnabled(true);
            ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
            ((DesiredCapabilities) caps).setCapability("phantomjs.content.settings.resourceTimeout", 3000);
            ((DesiredCapabilities) caps).setCapability(
                    PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs
            );
            ((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", userAgent);
        }

        driver = new PhantomJSDriver(caps);
        driver.manage().timeouts()
                .implicitlyWait(3, TimeUnit.SECONDS);
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
                } else {
                    System.out.println("Else");
                    type=Proxy.Type.HTTP;
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

    public String ocrImage(String filename)
    {
        String text = "";
        try
        {
            String gocrPath = "gocr";
            String command;
            if (OsHelper.isWindows())
            {
                gocrPath = "bin/gocr049.exe";
            }
            command = gocrPath + " -C \"0123456789\" " + filename;
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line=null;
            StringBuffer sb = new StringBuffer();
            while((line=input.readLine()) != null)
            {
                sb.append(line);
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return text;
    }

    public void convertImageToPnm(String inputFilename, String outputFilename )
    {
        try
        {
            Runtime rt = Runtime.getRuntime();
            String command;
            String convertPath = "convert";
            if (OsHelper.isWindows())
            {
                convertPath = imageMagickPath;
            }

            command = convertPath+ " " +  inputFilename + " " +outputFilename+"";
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
            //System.out.println(pr.waitFor());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void convertImageToPnmDark(String inputFilename, String outputFilename )
    {
        try
        {
            Runtime rt = Runtime.getRuntime();
            String command;
            String convertPath = "convert";
            if (OsHelper.isWindows())
            {
                convertPath = imageMagickPath;
            }

            command = convertPath+ " -type Grayscale -depth 8 -black-threshold 87% -density 300 " +  inputFilename + " " +outputFilename+"";
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
            //System.out.println(pr.waitFor());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void genericParsingOfText(String page, String type)
    {
        try
        {

            Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+");
            Matcher m = p.matcher(page);

            while (m.find())
            {
                try
                {
                    String line = m.group();
                    //System.out.println(line);
                    StringTokenizer st = new StringTokenizer(line, ":");
                    String ip = st.nextToken();
                    String port = st.nextToken();
                    Integer.parseInt(port);
                    ProxyInfo proxyInfo = new ProxyInfo();
                    proxyInfo.setHost(ip);
                    proxyInfo.setPort(port);
                    proxyInfo.setType(type);
                    addProxy(proxyInfo);
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
    }
    protected void genericParsingOfUrl(String url, String type)
    {
        genericParsingOfUrl(url,type, false );
    }
    protected void genericParsingOfUrl(String url, String type, boolean proxify)
    {
        try
        {
            String page = readUrl(url, proxify);
            genericParsingOfText(page, type);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String anonReadUrl(String url) throws Exception
    {
        return readUrl(url, true);
    }

    public String readUrl(String url, boolean proxify) throws Exception
    {
        try
        {
            if (!proxify)
            {
                return Utilities.readUrl(url);
            }
            else
            {
                URL oracle = new URL(url);
                Proxy p = getRandomProxy();
                while (p == null) {
                    Thread.sleep(5000);
                    p = getRandomProxy();
                }
                HttpURLConnection conn = (HttpURLConnection) oracle.openConnection(p);
                conn.setReadTimeout(Proxymity.TIMEOUT_MS);
                conn.setConnectTimeout(Proxymity.TIMEOUT_MS);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:37.0) Gecko/20100101 Firefox/37.0");
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String inputLine;
                StringBuffer sb = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    sb.append(inputLine + "\n");

                }
                in.close();
                return sb.toString();
            }
        }
        catch (SocketException e)
        {
            //System.out.println("Timeout");
            return anonReadUrl(url);
        }
        catch (SocketTimeoutException e)
        {
            //System.out.println("Timeout");
            return anonReadUrl(url);
        }
        catch (FileNotFoundException e)
        {
            throw e;
            //System.out.println("_404 e");
        }

        catch(Exception e)
        {
            //e.printStackTrace();
            throw e;
        }
    }

    protected boolean genericParsingOfUrlSpace(String page, String type)
    {
        boolean foundAtLeastOne = false;
        try
        {
            Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+ \\d+");
            Matcher m = p.matcher(page);

            while (m.find())
            {
                try
                {
                    foundAtLeastOne=true;
                    String line = m.group();
                    //System.out.println(line);
                    StringTokenizer st = new StringTokenizer(line, " ");
                    String ip = st.nextToken();
                    String port = st.nextToken();
                    Integer.parseInt(port);
                    ProxyInfo proxyInfo = new ProxyInfo();
                    proxyInfo.setHost(ip);
                    proxyInfo.setPort(port);
                    proxyInfo.setType(type);
                    addProxy(proxyInfo);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            return foundAtLeastOne;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return foundAtLeastOne;
        }
    }
    public Vector<String> extractTableRows(String url)
    {
        Vector<String> rows = new Vector<>();
        try
        {
            String page = Utilities.readUrl(url);
            Pattern p = Pattern.compile("<tr.*?</tr>",Pattern.DOTALL);
            Matcher m = p.matcher(page);

            while (m.find())
            {
                String line = m.group();
                rows.add(line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return rows;
    }

    public synchronized String getUrlBodyTextWithPhantom(String url)
    {
        try
        {

            driver.manage().timeouts().pageLoadTimeout(Proxymity.PHANTOM_JS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            driver.manage().timeouts().setScriptTimeout(Proxymity.PHANTOM_JS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            try
            {
                driver.get(url);
            }
            catch (Exception e)
            {
                //e.printStackTrace();
            }

            driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");

            WebElement webElement = driver.findElement(By.tagName("body"));
            String page = webElement.getText();

            return page;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return"";
    }

    /* PhantomJs Stuff */


    protected String downloadPageWithPhantomJs(String url) throws Exception
    {
        PhantomJsJobResult phantomJsJobResult = downloadWithPhantomJs(url);
        if (phantomJsJobResult != null)
        {
            return phantomJsJobResult.getContent();
        }
        else
        {
            throw new Exception("PhantomJsJobResult is null");
        }
    }

    protected String downloadPageWithPhantomJs(String url, String postParameters) throws Exception
    {
        PhantomJsJobResult phantomJsJobResult = downloadWithPhantomJs(url, postParameters);
        if (phantomJsJobResult != null)
        {
            return phantomJsJobResult.getContent();
        }
        else
        {
            throw new Exception("PhantomJsJobResult is null");
        }
    }

    protected String downloadPageSourceWithPhantomJs(String url) throws Exception
    {
       return downloadPageWithPhantomJs(url, null);
    }
    private PhantomJsJobResult downloadWithPhantomJs(String url) throws Exception
    {
        return downloadWithPhantomJs(url, null);
    }
    private PhantomJsJobResult downloadWithPhantomJs(String url, String postParameters) throws Exception
    {
        try
        {
            PhantomJsJob phantomJsJob ;
            if (postParameters == null)
            {
                phantomJsJob = phantomJsManager.addJob(url);
            }
            else
            {
                phantomJsJob = phantomJsManager.addJob(url, postParameters);
            }

            while(!phantomJsJob.isFinished())
            {
                Thread.sleep(2000);
            }

            if (phantomJsJob.isSuccessful())
            {
                return phantomJsJob.getPhantomJsJobResult() ;
            }
            else
            {
                throw phantomJsJob.getException();
            }
        }
        catch (Exception e)
        {
            throw e;
        }
    }
}
