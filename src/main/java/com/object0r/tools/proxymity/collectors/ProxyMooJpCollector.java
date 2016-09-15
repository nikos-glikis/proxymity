package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyMooJpCollector extends ProxyCollector
{
    public ProxyMooJpCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            for (int i = 2; i< 200; i++)
            {
                boolean foundAtLeastOne = false;

                String url = "http://proxy.moo.jp/?page="+i;
                String page = downloadPageWithPhantomJs(url);
                System.out.println(page);

                Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+ \\d+ [^ ]* ");


                Matcher m = p.matcher(page);
                while (m.find())
                {
                    String line = m.group();
                    System.out.println(line);
                    StringTokenizer st = new StringTokenizer(line, " ");
                    String ip = st.nextToken();
                    String port = st.nextToken();
                    String type = st.nextToken();
                    Integer.parseInt(port);
                    ProxyInfo proxyInfo = new ProxyInfo();

                    if (type.equals("HTTPS"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTPS);
                    }
                    else
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    }

                    proxyInfo.setHost(ip);
                    proxyInfo.setPort(port);
                    foundAtLeastOne = true;
                    addProxy(proxyInfo);
                }
                Thread.sleep(60000);
                if (!foundAtLeastOne)
                {

                    return getProxies();
                }
            }
        }
        catch (Exception e)
        {
            try
            {
                Thread.sleep(60000);
            }
            catch (Exception we)
            {
                we.printStackTrace();;
            }
            e.printStackTrace();

        }
        return getProxies();
    }

    @Override
    protected String collectorName()
    {
        return "proxy.moo.jp";
    }
}
