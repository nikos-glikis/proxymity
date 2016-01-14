package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.toortools.Utilities;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class enProxyNetPlCollector extends ProxyCollector
{
    public enProxyNetPlCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            String page = Utilities.readUrl("http://en.proxy.net.pl/fresh.html");
            Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+");
            Matcher m = p.matcher(page);

            while (m.find())
            {
                String line = m.group();
                //System.out.println(line);
                StringTokenizer st = new StringTokenizer(line, ":");
                String ip = st.nextToken();
                String port = st.nextToken();
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
