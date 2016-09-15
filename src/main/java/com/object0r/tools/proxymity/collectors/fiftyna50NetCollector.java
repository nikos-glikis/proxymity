package com.object0r.tools.proxymity.collectors;

import com.object0r.toortools.Utilities;
import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class fiftyna50NetCollector extends ProxyCollector
{
    public fiftyna50NetCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            String page = Utilities.readUrl("http://proxy50-50.blogspot.in/");
            Pattern p = Pattern.compile("<tr >.*?</tr>",Pattern.DOTALL);
            Matcher m = p.matcher(page);
            while (m.find())
            {
                String line = m.group();
                //System.out.println(line);
                if (line.contains("http://50na50.net/src/img/foxyproxy.png"))
                {
                    String ip = Utilities.cut("&host=","&", line);
                    String port = Utilities.cut("&port=","&", line);
                    Integer.parseInt(port);
                    ProxyInfo proxyInfo = new ProxyInfo();

                    proxyInfo.setHost(ip);
                    proxyInfo.setPort(port);
                    proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    addProxy(proxyInfo);
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
        return "proxy50-50.blogspot.in";
    }
}
