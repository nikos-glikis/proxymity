package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.toortools.Utilities;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SamairRuCollector extends ProxyCollector
{


    public SamairRuCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }


    public Vector<ProxyInfo> collectProxies()
    {

        try
        {
            for (int i = 1 ; i < 100; i++)
            {
                String pageNumber = "";
                pageNumber = Integer.toString(i);

                if (pageNumber.length() == 1)
                {
                    pageNumber= "0"+pageNumber;
                }
                String page="";
                try
                {
                    page  = Utilities.readUrl("http://www.samair.ru/proxy/proxy-"+pageNumber+".htm");
                }
                catch (FileNotFoundException e)
                {
                    return getProxies();
                }
                String css = readCss(page, "http://www.samair.ru");
                Vector<ProxyInfo> tempProxies  = extractProxies(page, css);
                for (ProxyInfo proxyInfo: tempProxies)
                {
                    proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    addProxy(proxyInfo);
                }
            }

            for (int i = 1 ; i < 100; i++)
            {
                String pageNumber = "";
                pageNumber = Integer.toString(i);

                if (pageNumber.length() == 1)
                {
                    pageNumber= "0"+pageNumber;
                }
                String page="";
                try
                {
                    page  = Utilities.readUrl("http://www.samair.ru/proxy/socks"+pageNumber+".htm");
                }
                catch (FileNotFoundException e)
                {
                    return getProxies();
                }
                String css = readCss(page, "http://www.samair.ru");

                Vector<ProxyInfo> tempProxies  = extractProxies(page, css);

                for (ProxyInfo proxyInfo: tempProxies)
                {
                    proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                    //System.out.println(proxyInfo);
                    addProxy(proxyInfo);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }

    @Override
    protected String collectorName()
    {
        return "samair.ru";
    }

    private Vector<ProxyInfo> extractProxies(String page, String css)
    {
        Vector<ProxyInfo> proxies = new Vector<ProxyInfo>();
        try
        {
            Pattern p = Pattern.compile("<tr>.*?</tr>");
            Matcher m = p.matcher(page);

            while (m.find())
            {
                try
                {
                    String line = m.group();
                    /*System.out.println(line);
                    System.exit(0);*/
                    String ip=null;
                    int portInt=0;
                    if (line.contains("<span class="))
                    {
                        line = Utilities.cut("<span class=","</span",line);
                        ip = Utilities.cut("\">",":",line);
                        String portClass = Utilities.cut("\"","\"",line);
                        portInt = getPortFromClass(css, portClass);
                    }
                    else
                    {
                        if (line.contains(":") && line.contains("<tr><td>"))
                        {
                            ip = Utilities.cut("<tr><td>", ":", line);
                            String portString = Utilities.cut(":", "<", line);
                            portInt = Integer.parseInt(portString);
                        }
                    }
                    if (ip!= null)
                    {
                        ProxyInfo proxyInfo = new ProxyInfo();
                        proxyInfo.setHost(ip);
                        proxyInfo.setPort(Integer.toString(portInt));

                        proxies.add(proxyInfo);
                    }

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

    private int getPortFromClass(String css, String portClass) throws Exception
    {
        try
        {
            Scanner sc = new Scanner(css);
            while (sc.hasNext()) {
                String line = sc.nextLine();
                if (line.contains(portClass)) {
                    String port = Utilities.cut("content:\"","\"",line);
                    return Integer.parseInt(port);
                }
            }
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            throw new Exception("ExportPortFromCssProblem");
        }
        throw new Exception("ExportPortFromCssProblem");

    }

    private String readCss(String page, String append)
    {
        String css = "";
        try
        {
            Pattern p = Pattern.compile("<link rel=\"stylesheet\" href=\"[^\"]*\"");
            Matcher m = p.matcher(page);
            while (m.find())
            {
                String url = m.group().replace("<link rel=\"stylesheet\" href=\"","").replace("\"","");
                if (!url.contains("http://"))
                {
                    url = append + url;
                }
                css += Utilities.readUrl(url);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return css;
    }
}
