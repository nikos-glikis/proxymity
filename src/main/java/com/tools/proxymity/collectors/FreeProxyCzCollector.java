package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.toortools.Utilities;

import java.net.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FreeProxyCzCollector extends ProxyCollector
{
    //TODO protections, try with Phantomjs with proxies.
    public FreeProxyCzCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
        //initializePhantom();
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            for (int i = 1; i<500; i++)
            {
                if (true) return getProxies();
                Proxy proxy = getRandomProxy();

                while (proxy == null)
                {
                    Thread.sleep(5000);
                    proxy = getRandomProxy();
                }
                StringBuffer sb = new StringBuffer();
                //System.out.println(proxy);
                String url = "http://free-proxy.cz/en/proxylist/main/"+i;
                //url ="http://cpanel.com/showip.shtml";
                try
                {
                    URLConnection c = new URL(url).openConnection(proxy);
                    c.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");


                    Thread.sleep(3000);

                    c.setReadTimeout(10000);
                    c.setConnectTimeout(10000);
                    Scanner sc =  new Scanner(c.getInputStream());

                    while (sc.hasNext())
                    {
                        sb.append(sc.nextLine());
                        sb.append("\n");
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Idiotropo proxy failed " + url + " "+e.toString());
                    Thread.sleep(5000);
                    //i--;
                    continue;
                }

                System.out.println("Idiotropo proxy success mothafaka");

                String page = sb.toString();

                System.out.println(page);
                if (true) continue;
                Pattern p = Pattern.compile("<tr>.*?</tr>", Pattern.DOTALL);
                Matcher m = p.matcher(page);
                while (m.find())
                {
                    String line = m.group();
                    if (line.contains("document.write(Base64"))
                    {
                        //System.out.println(line);
                        String type = Utilities.cut("</script></span></td><td><small>","<",line);
                        System.out.println(type);
                        //System.exit(0);
                    }
                }
                Thread.sleep(15000);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }
}
