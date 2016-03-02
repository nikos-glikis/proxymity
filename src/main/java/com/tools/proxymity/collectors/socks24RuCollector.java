package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.Vector;

public class socks24RuCollector extends ProxyCollector
{
    public socks24RuCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            processUrl("http://socks24.ru/proxy/httpProxies.txt",ProxyInfo.PROXY_TYPES_HTTP);
            processUrl("http://socks24.ru/proxy/socksProxies.txt",ProxyInfo.PROXY_TYPES_SOCKS4);
            processUrl("http://socks24.ru/proxy/socksProxies.txt",ProxyInfo.PROXY_TYPES_SOCKS5);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }

    private void processUrl(String url, String proxyTypesHttp)
    {
        try
        {
            genericParsingOfUrl(url,proxyTypesHttp);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
