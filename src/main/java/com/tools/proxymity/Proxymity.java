package com.tools.proxymity;

import com.tools.proxymity.collectors.HmaCollector;

public class Proxymity
{
    public void startCollectors()
    {
        try
        {
            new HmaCollector().collectProxies();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
