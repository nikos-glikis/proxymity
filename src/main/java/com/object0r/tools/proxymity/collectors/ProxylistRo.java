package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;

import java.util.Vector;

public class ProxylistRo extends ProxyCollector
{
    public ProxylistRo(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {

            for (int i = 0; i < 100; i++)
            {
                String page = this.downloadPageWithPhantomJs("http://www.proxylist.ro/search-free-proxy.php?country=&port=&anon=&ssl=&start=" + i);
                this.genericParsingOfPageSpace(page, ProxyInfo.PROXY_TYPES_HTTP);

            }
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
        return "proxylist.ro";
    }
}
