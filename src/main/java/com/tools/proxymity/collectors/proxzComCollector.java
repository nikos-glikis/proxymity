package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.toortools.Utilities;

import java.util.Vector;

public class proxzComCollector extends ProxyCollector
{
    public proxzComCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
        initializePhantom();
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {

            for (int i = 0; i<150; i++)
            {
                String page = getUrlBodyTextWithPhantom("http://www.proxz.com/proxy_list_high_anonymous_"+i+".html"); //Utilities.readUrl("http://www.proxz.com/proxy_list_high_anonymous_"+i+".html");
                //System.out.println(page);
                boolean foundAtLeastOne =genericParsingOfUrlSpace(page, ProxyInfo.PROXY_TYPES_HTTP);
                if (!foundAtLeastOne)
                {
                    return getProxies();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }
}
