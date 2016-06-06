package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.Vector;

public class theProxyListComCollector extends ProxyCollector
{
    public theProxyListComCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            genericParsingOfUrl("http://the-proxy-list.com/proxies-by-type/socks-proxies/", ProxyInfo.PROXY_TYPES_SOCKS5);
            genericParsingOfUrl("http://the-proxy-list.com/proxies-by-type/socks-proxies/", ProxyInfo.PROXY_TYPES_SOCKS4);
            genericParsingOfUrl("http://the-proxy-list.com/proxies-by-type/high-anonymous-elite-proxies-l1/", ProxyInfo.PROXY_TYPES_HTTP);
            genericParsingOfUrl("http://the-proxy-list.com/proxies-by-type/anonymous-proxies-l2/", ProxyInfo.PROXY_TYPES_HTTP);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }
}
