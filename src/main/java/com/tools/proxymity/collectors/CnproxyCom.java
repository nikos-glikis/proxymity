package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.toortools.Utilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CnproxyCom extends ProxyCollector
{
    public CnproxyCom(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
        initializePhantom();
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {

            /*for (int i = 1; i<50; i++)
            {
                boolean foundAtLeastOne = false;

                driver.get("http://www.cnproxy.com/proxy"+i+".html");
                WebElement body = driver.findElement(By.tagName("body"));
                String page = body.getText();


                Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+ [^ ]* ");
                Matcher m = p.matcher(page);

                while (m.find())
                {
                    try
                    {
                        foundAtLeastOne = true;
                        String line = m.group();
                        //System.out.println(line);
                        StringTokenizer st = new StringTokenizer(line, " ");
                        String ip = st.nextToken();

                        String type = st.nextToken();
                        st = new StringTokenizer(ip, ":");
                        ip = st.nextToken();
                        String port = st.nextToken();
                        //System.out.println(type);
                        ProxyInfo proxyInfo = new ProxyInfo();
                        proxyInfo.setHost(ip);
                        proxyInfo.setPort(port);

                        if (type.equals("HTTP") )
                        {
                            proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                        } else if (type.equals("SOCKS4") )
                        {
                            proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS4);
                        } else if (type.equals("SOCKS5") )
                        {
                            proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                        }

                        addProxy(proxyInfo);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                if (!foundAtLeastOne)
                {
                    return getProxies();
                }
            }*/

            for (int i = 1; i<50; i++)
            {
                boolean foundAtLeastOne = false;

                driver.get("http://www.cnproxy.com/proxyedu"+i+".html");
                WebElement body = driver.findElement(By.tagName("body"));
                String page = body.getText();


                Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+ [^ ]* ");
                Matcher m = p.matcher(page);

                while (m.find())
                {
                    try
                    {
                        foundAtLeastOne = true;
                        String line = m.group();
                        //System.out.println(line);
                        StringTokenizer st = new StringTokenizer(line, " ");
                        String ip = st.nextToken();

                        String type = st.nextToken();
                        st = new StringTokenizer(ip, ":");
                        ip = st.nextToken();
                        String port = st.nextToken();
                        //System.out.println(type);
                        ProxyInfo proxyInfo = new ProxyInfo();
                        proxyInfo.setHost(ip);
                        proxyInfo.setPort(port);

                        if (type.equals("HTTP") )
                        {
                            proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                        } else if (type.equals("SOCKS4") )
                        {
                            proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS4);
                        } else if (type.equals("SOCKS5") )
                        {
                            proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                        }

                        addProxy(proxyInfo);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                if (!foundAtLeastOne)
                {
                    return getProxies();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }
}