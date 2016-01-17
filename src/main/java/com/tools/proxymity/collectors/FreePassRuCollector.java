package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.toortools.Utilities;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FreePassRuCollector extends ProxyCollector
{
    public FreePassRuCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            String page = Utilities.readUrl("http://free-pass.ru/forum/79");
            Pattern p = Pattern.compile("http://free-pass.ru/forum/79-\\d+-\\d+");
            Matcher m = p.matcher(page);

            while (m.find())
            {
                //System.out.println(m.group());
                String url = m.group();
                genericParsingOfUrl(url,ProxyInfo.PROXY_TYPES_HTTP);
                genericParsingOfUrl(url,ProxyInfo.PROXY_TYPES_HTTPS);
                genericParsingOfUrl(url,ProxyInfo.PROXY_TYPES_SOCKS4);
                genericParsingOfUrl(url,ProxyInfo.PROXY_TYPES_SOCKS5);
                Thread.sleep(30000);
            }

        }
        catch (Exception e)
        {
            if (e.toString().contains("IOException: Server returned HTTP response code: 500 "))
            {
                System.out.println(e);
            }
        }
        return getProxies();
    }
}
