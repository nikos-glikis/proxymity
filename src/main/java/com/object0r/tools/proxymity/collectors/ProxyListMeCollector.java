package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyListMeCollector extends ProxyCollector
{
    public ProxyListMeCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {

            for (int i = 0; i < 150; i++)
            {

                Vector<String> rows = extractTableRows("http://proxylist.me/proxys/index/" + (i * 20));
                boolean foundAtLeastOne = false;
                for (String line : rows)
                {
                    Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
                    Matcher m = p.matcher(line);
                    if (m.find())
                    {
                        String ip = m.group();
                        Pattern pp = Pattern.compile("<td>\\d+</td>");
                        Matcher mm = pp.matcher(line);
                        mm.find();
                        String port = mm.group().replace("<td>", "").replace("</td>", "");
                        Integer.parseInt(port);
                        ProxyInfo proxyInfo = new ProxyInfo();
                        proxyInfo.setHost(ip);
                        proxyInfo.setPort(port);
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                        addProxy(proxyInfo);
                        foundAtLeastOne = true;
                    }
                }
                if (!foundAtLeastOne)
                {
                    return getProxies();
                }
            }
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
        return "proxylist.me";
    }
}
