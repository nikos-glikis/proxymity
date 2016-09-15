package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Proxies4googleCom extends ProxyCollector
{
    public Proxies4googleCom(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            String page = Utilities.readUrl("http://proxies4google.com/export/browse.php");
            Pattern p = Pattern.compile("<tr>.*?</tr>", Pattern.DOTALL);
            Matcher m = p.matcher(page);

            while (m.find())
            {
                String line = m.group();
                if (line.contains(" width=\"100\""))
                {
                    String ip = Utilities.cut("width=\"100\">","<",line);
                    StringTokenizer st = new StringTokenizer(ip, ":");
                    ip = st.nextToken();
                    String port = st.nextToken();
                    Integer.parseInt(port);

                    ProxyInfo proxyInfo = new ProxyInfo();
                    proxyInfo.setHost(ip);
                    proxyInfo.setPort(port);
                    if (line.contains("HTTP"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    }
                    else
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                    }
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
        return "proxies4google.com";
    }
}
