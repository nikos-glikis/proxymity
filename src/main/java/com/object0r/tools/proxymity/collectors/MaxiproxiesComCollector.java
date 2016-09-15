package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.Vector;

public class MaxiproxiesComCollector extends ProxyCollector
{
    public MaxiproxiesComCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            genericParsingOfUrl("http://maxiproxies.com/feed/atom/", ProxyInfo.PROXY_TYPES_HTTP);
            genericParsingOfUrl("http://maxiproxies.com/proxy-lists/feed/", ProxyInfo.PROXY_TYPES_HTTP);
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
        return "maxiproxies.com";
    }
}
