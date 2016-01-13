package com.tools.proxymity.collectors;

import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.tools.proxymity.ProxyCollector;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocksListNetCollector extends ProxyCollector
{


    public SocksListNetCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
        initializePhantom();
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            for (int i = 1; i<50; i++)
            {
                driver.get("http://sockslist.net/list/proxy-socks-5-list/"+i+"#proxylist");
                WebElement element = driver.findElement(By.tagName("body"));
                String page = element.getText();

                Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+ \\d+", Pattern.DOTALL);
                Matcher m = p.matcher(page);
                boolean foundAtLeastOne = false;
                while (m.find())
                {
                    foundAtLeastOne = true;
                    String line = m.group();
                    //System.out.println(line);
                    ProxyInfo proxyInfo = new ProxyInfo();
                    StringTokenizer st = new StringTokenizer(line, " ");
                    proxyInfo.setHost(st.nextToken());
                    String port = st.nextToken();
                    Integer.parseInt(port);
                    proxyInfo.setPort(port);
                    proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                    addProxy(proxyInfo);
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




        try
        {
            for (int i = 1; i<150; i++)
            {
                driver.get("http://proxyhttp.net/free-list/anonymous-server-hide-ip-address/"+i+"#proxylist");
                WebElement element = driver.findElement(By.tagName("body"));
                String page = element.getText();

                Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+ \\d+", Pattern.DOTALL);
                Matcher m = p.matcher(page);
                boolean foundAtLeastOne = false;
                while (m.find())
                {
                    foundAtLeastOne = true;
                    String line = m.group();
                    //System.out.println(line);
                    ProxyInfo proxyInfo = new ProxyInfo();
                    StringTokenizer st = new StringTokenizer(line, " ");
                    proxyInfo.setHost(st.nextToken());
                    String port = st.nextToken();
                    Integer.parseInt(port);
                    proxyInfo.setPort(port);
                    proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    addProxy(proxyInfo);
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

        //http://proxyhttp.net/free-list/anonymous-server-hide-ip-address/1#proxylist


        return getProxies();
    }
}
