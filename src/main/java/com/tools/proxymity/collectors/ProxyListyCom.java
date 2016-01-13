package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.toortools.Utilities;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyListyCom extends ProxyCollector
{
    public ProxyListyCom(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {


            for (int i = 1; i<150; i++)
            {
                //TODO disabled. Last time I checked last checked was 5 months ago. Inactive Website
                if (true) { return getProxies(); }
                String page = Utilities.readUrl("http://www.proxylisty.com/ip-proxylist-"+i);
                Pattern p = Pattern.compile("<tr>.*?</tr>",Pattern.DOTALL);
                Matcher m = p.matcher(page);
                boolean foundAtLeastOne = false;
                while (m.find())
                {
                    String line = m.group();
                    Pattern pp = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
                    Matcher mm = pp.matcher(line);
                    if (mm.find())
                    {
                        try
                        {
                            foundAtLeastOne = true;
                            String ip = mm.group();
                            //System.out.println("Ip: " + ip);
                            String port = Utilities.cut("Proxy List'>", "<", line);
                            Integer.parseInt(port);
                            ProxyInfo proxyInfo = new ProxyInfo();
                            proxyInfo.setPort(port);
                            proxyInfo.setHost(ip);
                            proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                            addProxy(proxyInfo);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
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
}
