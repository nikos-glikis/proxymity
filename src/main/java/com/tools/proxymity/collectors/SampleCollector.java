package com.tools.proxymity.collectors;

import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.phantomjs.PhantomJsJob;

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

            String page = downloadPageWithPhantomJs("http://spys.ru/free-proxy-list/CN/","xpp=3&xf1=0&xf2=0&xf4=0");
            System.out.println(page);
            System.exit(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }
}
