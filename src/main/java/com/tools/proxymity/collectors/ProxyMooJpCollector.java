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

public class ProxyMooJpCollector extends ProxyCollector
{
    public ProxyMooJpCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
        initializePhantom();
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            for (int i = 2; i< 200; i++)
            {
                driver.get("http://proxy.moo.jp/?page="+i);
                boolean foundAtLeastOne = false;
                WebElement element = driver.findElement(By.tagName("body"));
                String page = element.getText();
                //System.out.println(driver.getPageSource());
                Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+ \\d+ [^ ]* ");
                //System.out.println("Page: " +page);

                Matcher m = p.matcher(page);
                while (m.find())
                {
                    String line = m.group();
                    System.out.println(line);
                    StringTokenizer st = new StringTokenizer(line, " ");
                    String ip = st.nextToken();
                    String port = st.nextToken();
                    String type = st.nextToken();
                    Integer.parseInt(port);
                    ProxyInfo proxyInfo = new ProxyInfo();

                    if (type.equals("HTTPS"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTPS);
                    }
                    else
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    }

                    proxyInfo.setHost(ip);
                    proxyInfo.setPort(port);
                    foundAtLeastOne = true;
                    addProxy(proxyInfo);
                }
                Thread.sleep(60000);
                if (!foundAtLeastOne)
                {

                    return getProxies();
                }
            }
        }
        catch (Exception e)
        {
            try
            {
                Thread.sleep(60000);
            }
            catch (Exception we)
            {
                we.printStackTrace();;
            }
            e.printStackTrace();

        }
        return getProxies();
    }
}
