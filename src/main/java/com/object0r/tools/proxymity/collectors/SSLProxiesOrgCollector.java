package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SSLProxiesOrgCollector extends ProxyCollector
{
    public SSLProxiesOrgCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {

        try
        {
            String page = Utilities.readUrl("http://www.sslproxies.org/");
            Pattern p = Pattern.compile("<tr><td>.*?</td></tr>");
            Matcher m = p.matcher(page);
            while (m.find())
            {
                try
                {
                    String line = m.group();
                    Pattern pp = Pattern.compile("<tr><td>.*</td><td>\\d*</td>");
                    Matcher mm = pp.matcher(line);
                    if (mm.find())
                    {
                        line = mm.group().trim();
                        String ip = Utilities.cut("<tr><td>", "<", line);
                        String port = Utilities.cut("</td><td>", "<", line);
                        Integer.parseInt(port);
                        ProxyInfo proxyInfo = new ProxyInfo();
                        proxyInfo.setHost(ip);
                        proxyInfo.setPort(port);
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTPS);
                        addProxy(proxyInfo);
                    }
                }
                catch (Exception ee)
                {
                    ee.printStackTrace();
                }
                //System.exit(0);
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
        return "sslproxies.org";
    }
}
