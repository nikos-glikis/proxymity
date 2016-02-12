package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;

import java.util.Vector;

public class NnTimeComCollector extends ProxyCollector
{
    public NnTimeComCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            for (int i = 1; i< 31; i++)
            {
                String url = "";
                if (i<10)
                {
                    url = "http://nntime.com/proxy-list-0"+i+".htm";
                }
                else
                {
                    url = "http://nntime.com/proxy-list-"+i+".htm";
                }

                String page = this.downloadPageWithPhantomJs(url);

                genericParsingOfText(page, ProxyInfo.PROXY_TYPES_HTTP);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }
}
