package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GatherproxyCom extends ProxyCollector
{
    public GatherproxyCom(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            //processUrl("http://www.gatherproxy.com/", ProxyInfo.PROXY_TYPES_HTTP);
            processUrlSocks("http://www.gatherproxy.com/sockslist" );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }

    private void processUrlSocks(String url )
    {
        try
        {
            String page = Utilities.readUrl(url);
            Pattern p = Pattern.compile("<tr>.*?</tr>",Pattern.DOTALL);
            Matcher m = p.matcher(page);

            while (m.find())
            {
                String line = m.group();

                if (line.contains("document.write('"))
                {
                    //System.out.println(m.group());
                    //System.exit(0);
                    Pattern pp = Pattern.compile("document.write\\('.*?'", Pattern.DOTALL);
                    Matcher mm = pp.matcher(line);
                    mm.find();
                    String ip = mm.group().replace("document.write('","").replace("'","");



                    mm.find();
                    String port = mm.group().replace("document.write('","").replace("'","");
                    Integer.parseInt(port);

                    ProxyInfo proxyInfo = new ProxyInfo();
                    proxyInfo.setHost(ip);
                    proxyInfo.setPort(port);
                    proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                    addProxy(proxyInfo);

                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void processUrl(String url, String type)
    {
        try
        {
            String page = Utilities.readUrl(url);
            Pattern p = Pattern.compile("<script type=\"text/javascript\">.*?</script>",Pattern.DOTALL);
            Matcher m = p.matcher(page);

            while (m.find())
            {
                String line = m.group();
                if (line.contains("gp.insertPrx("))
                {
                    String ip = Utilities.cut("\"PROXY_IP\":\"","\"",line);
                    String portHex = Utilities.cut("\"PROXY_PORT\":\"","\"",line);
                    String port = new Integer(Integer.parseInt(portHex,16)).toString();
                    Integer.parseInt(port);
                    //System.out.println(port);
                    ProxyInfo proxyInfo = new ProxyInfo();
                    proxyInfo.setHost(ip);
                    proxyInfo.setPort(port);
                    proxyInfo.setType(type);
                    addProxy(proxyInfo);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
