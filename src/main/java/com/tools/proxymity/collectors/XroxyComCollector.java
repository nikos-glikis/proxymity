package com.tools.proxymity.collectors;

import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.tools.proxymity.ProxyCollector;
import com.toortools.Utilities;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XroxyComCollector extends ProxyCollector
{

    public XroxyComCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
        //initializePhantom();
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            for (int i = 1; i< 100; i++)
            {

                String url = "http://www.xroxy.com/proxylist.php?port=&type=&ssl=&country=&latency=&reliability=&sort=reliability&desc=true&pnum="+i+"#table";

                String page = "";
                for (int j = 0 ; j<4 ; j++)
                {
                    try
                    {
                        page = timeoutReadUrl(url);
                        Thread.sleep(15000);
                    }
                    catch (Exception e)
                    {
                        //System.out.println(e.toString());
                        continue;
                    }
                }

                if (page.equals(""))
                {
                    continue;
                }

                Pattern p = Pattern.compile("<tr.*?tr>",Pattern.DOTALL);
                Matcher m = p.matcher(page);
                boolean foundAtLeastOne = false;
                while (m.find())
                {
                    try
                    {
                        String line = m.group();
                        if (line.contains("View this Proxy details"))
                        {
                            foundAtLeastOne = true;
                            //System.out.println(line);
                            String ip = Utilities.cut("Proxy details'>", "<", line).trim();
                            //System.out.println(ip);
                            String port = Utilities.cut("title='Select proxies with port number", "<", line);
                            port = port.substring(port.indexOf(">")+1, port.length());
                            //System.out.println(port);
                            Integer.parseInt(port);
                            String type = Utilities.cut("Select proxies of "," type", line).trim();
                            ProxyInfo proxyInfo = new ProxyInfo();

                            if (type.equals("Socks5"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                            }
                            else if (type.equals("Socks4"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                            }
                            else
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                            }
                            proxyInfo.setHost(ip);
                            proxyInfo.setPort(port);
                            addProxy(proxyInfo);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    Thread.sleep(35000);
                }
                if (!foundAtLeastOne)
                {
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }

    private String timeoutReadUrl(String url) throws Exception
    {

        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
        connection.setRequestProperty("Accept-Language","en-US,en;q=0.5");
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Referer","http://www.xroxy.com/proxylist.php?port=&type=&ssl=&country=&latency=&reliability=");
        connection.setRequestProperty("Cookie","__utma=104024137.1545078594.1452420488.1452420488.1452420488.1; __utmb=104024137.30.10.1452420489; __utmc=104024137; __utmz=104024137.1452420489.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); phpbb2mysql_data=a%3A2%3A%7Bs%3A11%3A%22autologinid%22%3Bs%3A0%3A%22%22%3Bs%3A6%3A%22userid%22%3Bi%3A-1%3B%7D; phpbb2mysql_sid=3cb153a540416261b9d7f2f16d52479d; __utmt=1");
        connection.setRequestProperty("Connection","keep-alive");
        connection.setRequestProperty("If-Modified-Since","Sun, 10 Jan 2016 10:33:03 GMT");
        connection.setRequestProperty("If-None-Match","108e5cdb902f9b262f2c890e7ff2abc0");
        connection.setRequestProperty("Cache-Control", "max-age=0");

        connection.setReadTimeout(5000);
        connection.setConnectTimeout(5000);

        Scanner sc = new Scanner(connection.getInputStream());
        StringBuffer sb = new StringBuffer();
        while (sc.hasNext())
        {
            sb.append(sc.nextLine());
        }
        return sb.toString();
    }
}
