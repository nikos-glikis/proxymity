package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;

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

    @Override
    protected String collectorName()
    {
        return "fineproxy.org";
    }
}
