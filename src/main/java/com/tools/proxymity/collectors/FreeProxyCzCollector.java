package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.toortools.Utilities;

import java.net.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FreeProxyCzCollector extends ProxyCollector
{
    int SLEEP_BETWEEN_FETCH = 120;
    int SLEEP_FAIL_SECONDS = 240;
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
                //Random proxy is not used.
                /*Proxy proxy = getRandomProxy();

                while (proxy == null)
                {
                    Thread.sleep(5000);
                    proxy = getRandomProxy();
                }*/
                StringBuffer sb = new StringBuffer();
                //System.out.println(proxy);
                String url = "http://free-proxy.cz/en/proxylist/main/"+i;

                try
                {
                    URLConnection c = new URL(url).openConnection();
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
                    System.out.println("free-proxy.cz failed" + url + " "+e.toString());

                    Thread.sleep(SLEEP_FAIL_SECONDS*1000);
                    //i--;
                    continue;
                }

                System.out.println("free-proxy.cz success");

                String page = sb.toString();


                //System.out.println(page);

                Pattern p = Pattern.compile("<tr>.*?</tr>", Pattern.DOTALL);
                Matcher m = p.matcher(page);
                boolean foundAtLeastOne = false;
                while (m.find())
                {
                    String line = m.group();
                    if (line.contains("document.write(Base64"))
                    {
                        try
                        {
                            foundAtLeastOne = true;
                            //System.out.println(line);
                            String type = Utilities.cut("</script></span></td><td><small>", "<", line);
                            //System.out.println(type);
                            Pattern pp = Pattern.compile("document.write\\(Base64.decode\\(\"[^\"]*\"");
                            Matcher mm = pp.matcher(line);
                            mm.find();
                            String ip = mm.group().replace("document.write(Base64.decode(\"","").replace("\"","");
                            ip = new String(Base64.getDecoder().decode(ip));
                            //System.out.println("Ip: "+ip);
                            mm.find();
                            String port = mm.group().replace("document.write(Base64.decode(\"","").replace("\"","");
                            port = new String(Base64.getDecoder().decode(port));
                            ProxyInfo proxyInfo = new ProxyInfo();
                            proxyInfo.setHost(ip);
                            proxyInfo.setPort(port);

                            proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                            if (type.equals("HTTPS"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTPS);
                            }
                            else if (type.equals("HTTP"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                            }
                            else if (type.equals("SOCKS5"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                            }
                            else if (type.equals("SOCKS4"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS4);
                            }

                            Integer.parseInt(port);
                            addProxy(proxyInfo);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        //System.exit(0);
                    }
                }
                Thread.sleep(SLEEP_BETWEEN_FETCH);
                if (!foundAtLeastOne)
                {
                    return getProxies();
                }
            }
        }
        catch (Exception e)
        {

            e.printStackTrace();
            try
            {
                Thread.sleep(SLEEP_FAIL_SECONDS*1000);
            }
            catch (Exception ee)
            {

            }
        }
        return getProxies();
    }
}
