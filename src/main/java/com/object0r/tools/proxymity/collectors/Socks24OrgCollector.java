package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Socks24OrgCollector extends ProxyCollector
{
    public Socks24OrgCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            doUrl("http://socksproxylist24.blogspot.com.cy/search?&max-results=40&start=0&by-date=false");
            doUrl("http://www.socks24.org/search?&max-results=40&start=0&by-date=false");
            doUrl("http://sslproxies24.blogspot.com.cy/search?&max-results=40&start=0&by-date=false");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }

    private void doUrl(String initialUrl) {
        try
        {
            String page = Utilities.readUrl(initialUrl);
            Pattern p = Pattern.compile("<h3 class='post-title entry-title' itemprop='name'>.*?</h3>", Pattern.DOTALL);
            Matcher m = p.matcher(page);

            while (m.find())
            {
                try
                {
                    String line = m.group();
                    String url = Utilities.cut("<a href='", "'", line);
                    page = Utilities.readUrl(url);
                    Pattern pp = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+");
                    Matcher mm = pp.matcher(page);
                    while (mm.find()) {
                        String proxy = mm.group();
                        StringTokenizer st = new StringTokenizer(proxy, ":");
                        ProxyInfo proxyInfo = new ProxyInfo();
                        proxyInfo.setHost(st.nextToken());
                        String port = st.nextToken();
                        Integer.parseInt(port);
                        proxyInfo.setPort(port);
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                        addProxy(proxyInfo);
                        //System.out.println(proxyInfo);
                    }
                } catch (Exception e) {
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
