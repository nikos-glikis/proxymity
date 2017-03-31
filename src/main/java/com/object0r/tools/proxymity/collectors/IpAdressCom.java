package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.tools.proxymity.phantomjs.PhantomJsManager;
import com.object0r.toortools.Utilities;

import java.util.Vector;

public class IpAdressCom extends ProxyCollector
{
    public IpAdressCom(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            //genericParsingOfUrl("http://www.ip-adress.com/proxy_list/?k=time&d=desc", ProxyInfo.PROXY_TYPES_HTTP);

            genericParsingOfPageSpace(downloadPageWithPhantomJs("http://proxies.org/"), ProxyInfo.PROXY_TYPES_HTTP);
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
        return "ip-adress.com";
    }
}
