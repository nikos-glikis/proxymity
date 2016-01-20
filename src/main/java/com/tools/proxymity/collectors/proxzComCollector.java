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
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {

            for (int i = 0; i<150; i++)
            {
                try
                {
                    System.out.println("Sika Sika");
                    String page = downloadPageWithPhantomJs("http://www.proxz.com/proxy_list_high_anonymous_"+i+".html"); //Utilities.readUrl("http://www.proxz.com/proxy_list_high_anonymous_"+i+".html");

                    System.out.println(page);
                    System.out.println("Skafi skafi");
                    boolean foundAtLeastOne =genericParsingOfUrlSpace(page, ProxyInfo.PROXY_TYPES_HTTP);
                    if (!foundAtLeastOne)
                    {
                        return getProxies();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();;
                }

            }

            for (int i = 0; i<150; i++)
            {
                try
                {

                    String page = downloadPageWithPhantomJs("http://www.proxz.com/proxy_list_port_std_" + i + ".html"); //Utilities.readUrl("http://www.proxz.com/proxy_list_high_anonymous_"+i+".html");
                    //System.out.println(page);
                    boolean foundAtLeastOne = genericParsingOfUrlSpace(page, ProxyInfo.PROXY_TYPES_HTTP);
                    if (!foundAtLeastOne) {
                        return getProxies();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i<150; i++)
            {
                try
                {
                    String page = downloadPageWithPhantomJs("http://www.proxz.com/proxy_list_port_nonstd_" + i + ".html"); //Utilities.readUrl("http://www.proxz.com/proxy_list_high_anonymous_"+i+".html");
                    //System.out.println(page);
                    boolean foundAtLeastOne = genericParsingOfUrlSpace(page, ProxyInfo.PROXY_TYPES_HTTP);
                    if (!foundAtLeastOne) {
                        return getProxies();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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
