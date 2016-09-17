package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
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
            for (int i = 2; i < 200; i++)
            {
                boolean foundAtLeastOne = false;

                String url = "http://proxy.moo.jp/?page=" + i;
                //hl=en; pv=10; userno=20160917-000383; from=direct; visited=2016%2F09%2F17+04%3A10%3A13; __utma=127656268.1490915204.1474053009.1474053009.1474085228.2; __utmc=127656268; __utmz=127656268.1474053009.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __atuvc=10%7C37; __utmv=127656268.Cyprus
                HashMap<String, String> cookies = new HashMap<>();
                cookies.put("hl", "en");
                cookies.put("pv", "10");
                cookies.put("userno", "20160917-000383");
                cookies.put("from", "from");
                cookies.put("visited", "2016%2F09%2F17+04%3A10%3A13");

                String page = downloadPageWithPhantomJs(url, null, cookies, true);

                //System.out.println(page);
                page = Utilities.cut("<div class=\"page\">", "</tbody></table>", page);
                Pattern p = Pattern.compile("<tr class=\".*?</tr>", Pattern.DOTALL);
                Matcher m = p.matcher(page);
                while (m.find())
                {
                    String line = m.group();

                    if (line.contains("<tr class=\"Caption\">"))
                    {
                        continue;
                    }
                    //System.out.println(line);
                    if (!line.contains("IPDecode(\""))
                    {
                        continue;
                    }
                    String ip = Utilities.cut("IPDecode(\"", "\")</sc", line);
                    ip = URLDecoder.decode(ip, "utf-8");
                    //System.out.println(ip);

                    //String ip = st.nextToken();
                    String port = Utilities.cut("</td><td align=\"center\">", "</td><td align=\"c", line);
                    //System.out.println(port);

                    String type = Utilities.cut(port + "</td><td align=\"center\">", "</td><td align=\"center\">", line);
                    ;
                    //System.out.println(type);


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
                Thread.sleep(2000);
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
                Thread.sleep(60000);
            }
            catch (Exception we)
            {
                we.printStackTrace();
                ;
            }


        }
        return getProxies();
    }

    @Override
    protected String collectorName()
    {
        return "proxy.moo.jp";
    }
}
