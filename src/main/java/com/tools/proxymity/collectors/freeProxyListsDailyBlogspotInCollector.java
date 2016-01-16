package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;

import java.util.Vector;

public class freeProxyListsDailyBlogspotInCollector extends ProxyCollector
{
    public freeProxyListsDailyBlogspotInCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            genericParsingOfUrl("http://freeproxylistsdaily.blogspot.in/feeds/posts/default", ProxyInfo.PROXY_TYPES_HTTP);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }
}
