package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.toortools.Utilities;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaxiproxiesComCollector extends ProxyCollector
{
    public MaxiproxiesComCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            genericParsingOfUrl("http://maxiproxies.com/feed/atom/", ProxyInfo.PROXY_TYPES_HTTP);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }

    private void genericParsingOfUrl(String url, String type)
    {
        try
        {
            String page = Utilities.readUrl(url);
            Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+");
            Matcher m = p.matcher(page);

            while (m.find())
            {
                try
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
                    proxyInfo.setType(type);
                    addProxy(proxyInfo);
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
    }
}
