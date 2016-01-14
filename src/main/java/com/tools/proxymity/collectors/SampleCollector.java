package com.tools.proxymity.collectors;

import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.tools.proxymity.ProxyCollector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.Vector;

public class SampleCollector extends ProxyCollector
{
    public SampleCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {

            for (int i = 1; i<50; i++)
            {


            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }
}
