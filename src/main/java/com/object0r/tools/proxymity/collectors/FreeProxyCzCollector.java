package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.Base64;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FreeProxyCzCollector extends ProxyCollector
{
    public FreeProxyCzCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            for (int i = 1; i < 500; i++)
            {

                String url = "http://free-proxy.cz/en/proxylist/main/" + i;
                String page = "";
                try
                {
                    //page = anonReadUrl(url);
                    page= readUrl(url,false);
                }
                catch (Exception e)
                {
                    //System.out.println("free-proxy.cz failed: " + url + " "+e.toString());
                    i--;
                    continue;
                }

                //System.out.println("free-proxy.cz success");

                Pattern p = Pattern.compile("<tr>.*?</tr>", Pattern.DOTALL);
                Matcher m = p.matcher(page);
                boolean foundAtLeastOne = false;
                while (m.find())
                {
                    String line = m.group();
                    if (line.contains("document.write(Base64"))
                    {
                        try
                        {
                            foundAtLeastOne = true;
                            //System.out.println(line);
                            String type = Utilities.cut("</script></span></td><td><small>", "<", line);
                            //System.out.println(type);
                            Pattern pp = Pattern.compile("document.write\\(Base64.decode\\(\"[^\"]*\"");
                            Matcher mm = pp.matcher(line);
                            mm.find();
                            String ip = mm.group().replace("document.write(Base64.decode(\"", "").replace("\"", "");
                            ip = new String(Base64.getDecoder().decode(ip));
                            //System.out.println("Ip: "+ip);
                            mm.find();
                            String port = mm.group().replace("document.write(Base64.decode(\"", "").replace("\"", "");
                            port = new String(Base64.getDecoder().decode(port));
                            ProxyInfo proxyInfo = new ProxyInfo();
                            proxyInfo.setHost(ip);
                            proxyInfo.setPort(port);

                            proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                            if (type.equals("HTTPS"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTPS);
                            }
                            else if (type.equals("HTTP"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                            }
                            else if (type.equals("SOCKS5"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                            }
                            else if (type.equals("SOCKS4"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS4);
                            }

                            Integer.parseInt(port);
                            addProxy(proxyInfo);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        //System.exit(0);
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
            try
            {

            }
            catch (Exception ee)
            {

            }
        }
        return getProxies();
    }

    @Override
    protected String collectorName()
    {
        return "free-proxy.cz";
    }
}
