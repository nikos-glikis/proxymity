package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.Vector;

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
                if (port.contains(">")) {
                    continue;
                }
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
