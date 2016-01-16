package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Vector;

public class happyProxyComCollector extends ProxyCollector
{
    public happyProxyComCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
        initializePhantom();
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            genericParsingOfUrl("https://happy-proxy.com/fresh_proxies?key=2230e82110412f1b", ProxyInfo.PROXY_TYPES_HTTP);
            genericParsingOfUrl("http://01hitfaker.blogspot.com/feeds/posts/default?alt=rss", ProxyInfo.PROXY_TYPES_SOCKS5);
            genericParsingOfUrl("http://proxysearcher.sourceforge.net/Proxy%20List.php?type=socks", ProxyInfo.PROXY_TYPES_SOCKS5);
            genericParsingOfUrl("http://proxysearcher.sourceforge.net/Proxy%20List.php?type=http", ProxyInfo.PROXY_TYPES_HTTP);
            genericParsingOfUrl("http://dogdev.net/Proxy/all", ProxyInfo.PROXY_TYPES_HTTP);
            genericParsingOfUrl("http://www.reliableproxylist.com/feeds/posts/default", ProxyInfo.PROXY_TYPES_HTTP);
            driver.get("http://www.proxylists.net");

            genericParsingOfUrl("http://www.proxylists.net", ProxyInfo.PROXY_TYPES_HTTP);

            //TODO threads
            for (int i=0; i<50; i++)
            {
                genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/us_"+i+".html"), ProxyInfo.PROXY_TYPES_HTTP);
            }
            for (int i=0; i<10; i++)
            {
                genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/gb_"+i+".html"), ProxyInfo.PROXY_TYPES_HTTP);
            }

            for (int i=0; i<6; i++)
            {
                genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/ca_"+i+".html"), ProxyInfo.PROXY_TYPES_HTTP);
            }

            for (int i=0; i<50; i++)
            {
                genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/3128_"+i+".html"), ProxyInfo.PROXY_TYPES_HTTP);
            }

            for (int i=0; i<50; i++)
            {
                genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/8080_"+i+".html"), ProxyInfo.PROXY_TYPES_HTTP);
            }

            for (int i=0; i<80; i++)
            {
                genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/1080_"+i+".html"), ProxyInfo.PROXY_TYPES_HTTP);
            }

            for (int i=0; i<450; i++)
            {
                genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/80_"+i+".html"), ProxyInfo.PROXY_TYPES_HTTP);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }
}
