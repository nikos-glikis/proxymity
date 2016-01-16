package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;

import java.util.Vector;

public class fineProxyOrgCollector extends ProxyCollector
{
    public fineProxyOrgCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            genericParsingOfUrl("http://fineproxy.org/eng/", ProxyInfo.PROXY_TYPES_HTTP);
            genericParsingOfUrl("http://fineproxy.org/eng/", ProxyInfo.PROXY_TYPES_SOCKS4);
            genericParsingOfUrl("http://fineproxy.org/eng/", ProxyInfo.PROXY_TYPES_SOCKS5);
            genericParsingOfUrl("http://fineproxy.org/eng/?p=6", ProxyInfo.PROXY_TYPES_HTTP);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }
}
