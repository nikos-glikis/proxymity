package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

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
            String page = anonReadUrl("http://free-pass.ru/forum/79");

            Pattern p = Pattern.compile("http://free-pass.ru/forum/79-\\d+-\\d+");
            Matcher m = p.matcher(page);

            while (m.find())
            {

                String url = m.group();
                String page2 = myRead(url);
                genericParsingOfText(page2,ProxyInfo.PROXY_TYPES_HTTP);
                genericParsingOfText(page2,ProxyInfo.PROXY_TYPES_HTTPS);
                genericParsingOfText(page2,ProxyInfo.PROXY_TYPES_SOCKS4);
                genericParsingOfText(page2,ProxyInfo.PROXY_TYPES_SOCKS5);
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

    private String myRead(String url) throws Exception
    {
        while (true)
        {
            try
            {
                return anonReadUrl(url);
            }
            catch (Exception e)
            {
                if (e.toString().contains("IOException: Server returned HTTP response code: 500"))
                {
                    return anonReadUrl(url);
                }

            }

        }
    }
}
