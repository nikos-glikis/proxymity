package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;

import java.util.Vector;

public class DummyCollector extends ProxyCollector
{
    public DummyCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            Thread.sleep(20000);
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
        return "dummy collector.";
    }
}
