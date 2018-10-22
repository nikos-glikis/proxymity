package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.Vector;

public class FreeSocksIn extends ProxyCollector
{
    public FreeSocksIn(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            String page = Utilities.readUrl("https://free-socks.in/");
            genericParsingOfText(page, ProxyInfo.PROXY_TYPES_SOCKS5);
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
        return "cool-proxy.net";
    }


}
