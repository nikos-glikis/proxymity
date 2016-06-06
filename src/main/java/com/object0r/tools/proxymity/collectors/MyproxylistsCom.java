package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyproxylistsCom extends ProxyCollector
{
    public MyproxylistsCom(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            String page = Utilities.readUrl("http://myproxylists.com/free-proxy-list");
            Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+");
            Matcher m = p.matcher(page);
            while (m.find())
            {
                String line = m.group();
                StringTokenizer st = new StringTokenizer(line, ":");
                ProxyInfo proxyInfo = new ProxyInfo();
                String ip = st.nextToken();
                String port = st.nextToken();
                Integer.parseInt(port);
                proxyInfo.setHost(ip);
                proxyInfo.setPort(port);
                proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                addProxy(proxyInfo);
            }

            page = Utilities.readUrl("http://myproxylists.com/socks-proxy-lists");
             p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+");
             m = p.matcher(page);
            while (m.find())
            {
                String line = m.group();
                StringTokenizer st = new StringTokenizer(line, ":");
                ProxyInfo proxyInfo = new ProxyInfo();
                String ip = st.nextToken();
                String port = st.nextToken();
                Integer.parseInt(port);
                proxyInfo.setHost(ip);
                proxyInfo.setPort(port);
                proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
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
