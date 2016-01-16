package com.tools.proxymity.collectors;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.toortools.Utilities;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsProxyOrgCollector extends ProxyCollector
{
    public UsProxyOrgCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            //String page = Utilities.readUrl("http://www.us-proxy.org/");
            Vector<String> rows = extractTableRows("http://www.us-proxy.org/");
            for (String row : rows)
            {
                //System.out.println(row);
                String ip = Utilities.cut("<tr><td>","<",row);
                String port = Utilities.cut("</td><td>","<",row);
                Integer.parseInt(port);

                ProxyInfo proxyInfo = new ProxyInfo();
                proxyInfo.setHost(ip);
                proxyInfo.setPort(port);
                proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                addProxy(proxyInfo);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }

}
