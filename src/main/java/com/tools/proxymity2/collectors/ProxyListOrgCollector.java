package com.tools.proxymity.collectors;

import com.mysql.jdbc.Util;
import com.tools.proxymity.DataTypes.CollectorParameters;
import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.ProxyInfo;
import com.toortools.Utilities;
import com.toortools.os.OsHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Base64;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyListOrgCollector extends ProxyCollector
{
    public ProxyListOrgCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);

    }

    public Vector<ProxyInfo> collectProxies()
    {

        try
        {
            for (int i = 0; i<15; i++)
            {
                String page = Utilities.readUrl("https://proxy-list.org/english/index.php?p="+i);

                Pattern p = Pattern.compile("<ul>(.|\\n)*?</ul>");
                Matcher m = p.matcher(page);
                while (m.find())
                {
                    try
                    {
                        String line = m.group();
                        //System.out.println(line);
                        String urlEncoded = Utilities.cut("text/javascript\">Proxy('", "')</script>", line).trim();
                        String url = new String(Base64.getDecoder().decode(urlEncoded.getBytes()));
                        String ip = "";
                        String port = "";
                        if (url.contains(":"))
                        {
                            StringTokenizer st = new StringTokenizer(url, ":");
                            ip = st.nextToken();
                            port = st.nextToken();
                            try
                            {
                                Integer.parseInt(port);
                            }
                            catch (Exception e)
                            {
                                continue;
                            }

                            String type = Utilities.cut("</script></li>","</li", line);
                            type = type.substring(type.lastIndexOf(">")+1, type.length()).trim();
                            ProxyInfo proxyInfo = new ProxyInfo();
                            if (type.equals("HTTP"))
                            {
                                    proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                            }
                            else if (type.equals("HTTPS"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTPS);
                            }
                            else
                            {
                                continue;
                            }

                            proxyInfo.setHost(ip);
                            proxyInfo.setPort(port);
                            addProxy(proxyInfo);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
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
