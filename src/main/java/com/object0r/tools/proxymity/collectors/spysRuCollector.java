package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class spysRuCollector extends ProxyCollector
{
    //TODO post will get more results.
    public spysRuCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {

            try
            {


                String page = anonReadUrl("http://spys.ru/en/proxy-by-country/");

                Pattern p = Pattern.compile("href='.*?'");
                Matcher m = p.matcher(page);
                while (m.find())
                {
                    String line = m.group().replace("href='","").replace("'","");
                    if (!line.contains("http://"))
                    {
                        line = "http://spys.ru"+line;
                        processPage(line);
                    }
                    //System.out.println(line);
                }

                processPage("http://spys.ru/en/free-proxy-list/");

                processPage("http://spys.ru/en/anonymous-proxy-list/");
                processPage("http://spys.ru/en/https-ssl-proxy/");
                processPage("http://spys.ru/en/socks-proxy-list/");
                processPage("http://spys.ru/en/http-proxy-list/");
                processPage("http://spys.ru/en/non-anonymous-proxy-list/");


            }
            catch (Exception e)
            {
                e.printStackTrace();;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }

    private void processPage(String url)
    {
        for (int i = 1; i<10; i++)
        {
            try
            {
                url = url.replace("__page__", new Integer(i).toString());
                //TODO anonread with post, and read it with phantom.
                String page = downloadPageWithPhantomJs(url,"xpp=3&xf1=0&xf2=0&xf4=0");
                Pattern p = Pattern.compile("\\d++\\.\\d++\\.\\d++\\.\\d++:\\d+ .*? ",Pattern.DOTALL);

                Matcher m = p.matcher(page);

                while (m.find()) {
                    String line = m.group().replace(":", " ");
                    StringTokenizer st = new StringTokenizer(line, " ");
                    String ip = st.nextToken();
                    String port = st.nextToken();
                    String type = st.nextToken();
                    ProxyInfo proxyInfo = new ProxyInfo();
                    proxyInfo.setHost(ip);
                    proxyInfo.setPort(port);
                    proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    if (type.equals("SOCKS5"))
                    {
                        //System.out.println(type);
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                    }
                    addProxy(proxyInfo);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                //System.exit(0);
            }
            //remove this. (true)
            if (!url.contains("__page__") || true)
            {
                //System.out.println("!page" + url);
                break;
            }
        }
    }

}
