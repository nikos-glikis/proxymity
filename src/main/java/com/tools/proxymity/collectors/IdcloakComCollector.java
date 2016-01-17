package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.toortools.Utilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdcloakComCollector extends ProxyCollector
{
    public IdcloakComCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
        initializePhantom();
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {

            for (int i = 1; i<400; i++)
            {
                /*String page = postRequest("http://www.idcloak.com/proxylist/proxy-list.html#sort","port%5B%5D=all&protocol-http=true&protocol-https=true&protocol-socks4=true&protocol-socks5=true&anonymity-low=true&anonymity-medium=true&anonymity-high=true&connection-low=true&connection-medium=true&connection-high=true&speed-low=true&speed-medium=true&speed-high=true&order=desc&by=updated&page="+i,"Cookie: _ga=GA1.2.1580931146.1452867786; _dc_gtm_UA-36933135-1=1; __zlcmid=YhfFr1U2CRoBCl");
                ;*/
                String page;
                try
                {
                    page = anonReadUrl("http://www.idcloak.com/proxylist/proxy-list.html");

                }
                catch (Exception e)
                {
                    System.out.println(e);
                    i--;
                    continue;
                    //e.printStackTrace();
                }

                Pattern p = Pattern.compile("<tr>.*?</tr>");
                Matcher m = p.matcher(page);
                System.out.println(page);
                System.exit(0);
                while (m.find())
                {
                    String line = m.group();
                    if (line.contains("proxylist_flag"))
                    {
                        //proxylist_flag
                        System.out.println(line);
                        System.exit(0);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }

    public static String postRequest(String targetURL, String postParams, String givenCookies)
    {
        boolean returnCookies= false;

        URL url;
        HttpURLConnection connection = null;
        try {

            // Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",  "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
            connection.setRequestProperty("Referer", "http://www.idcloak.com/proxylist/proxy-list.html");
            connection.setRequestProperty("Cache-Control", "max-age=0\n");
            if ( givenCookies != null && !givenCookies.equals(""))
            {
                givenCookies = givenCookies.replace("Cookie: ","");
                connection.setRequestProperty("Cookie",givenCookies);
            }
            connection.setRequestProperty("Content-Length", ""
                    + Integer.toString(postParams.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            if (returnCookies)
            {
                connection.setInstanceFollowRedirects(false);
            }
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(connection
                    .getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            // Get Response
            InputStream is = connection.getInputStream();
            Scanner sc = new Scanner(is);
            StringBuffer response = new StringBuffer();
            while (sc.hasNext())
            {
                response.append(sc.nextLine()+"\n");
            }

            if (!returnCookies)
            {
                return response.toString();
            }
            else
            {
                String cookies = "";
                Map<String,List<String>> map = connection.getHeaderFields();
                for (Map.Entry<String, List<String>> entry : map.entrySet())
                {
                    //System.out.println(entry.getKey() + "/" + entry.getValue());
                    if (entry.getKey() != null && entry.getKey().equals("Set-Cookie"))
                    {
                        List<String> l = entry.getValue();
                        Iterator<String> iter = l.iterator();
                        for (String c : l)
                        {
                            cookies += c +"; ";
                        }
                    }
                }
                if (cookies.equals(""))
                {
                    return null;
                }
                else
                {
                    return cookies;
                }
            }

        }
        catch (Exception e)
        {

            e.printStackTrace();
            System.out.println("e");
            return null;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
