package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastproxyserversOrg extends ProxyCollector
{
    public FastproxyserversOrg(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            String page = Utilities.readUrl("http://fastproxyservers.org/socks5-servers.htm");
            Pattern p = Pattern.compile("<div>.*?\n" +
                    "\t\t\t\t</div>", Pattern.DOTALL);
            Matcher m = p.matcher(page);
            while (m.find())
            {
                String line = m.group();
                if (line.contains("<img src=\"/socks5-servers.htm?hidden="))
                {
                    //System.out.println(line);
                    String imageUrl = "http://fastproxyservers.org"+Utilities.cut("<img src=\"","\"", line);
                    Utilities.downloadFile(imageUrl, "tmp/dontremeber.png");
                    convertImageToPnm("tmp/dontremeber.png", "tmp/dontremeber.pnm");
                    String ip = ocrImage("tmp/dontremeber.pnm").replace("_",".");

                    //System.out.println(ip);
                    String port = Utilities.cut("<div class=\"port cell bb\">","<", line);
                    Integer.parseInt(port);
                    ProxyInfo proxyInfo = new ProxyInfo();
                    proxyInfo.setHost(ip);
                    proxyInfo.setPort(port);
                    proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    addProxy(proxyInfo);
                    //System.exit(0);
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
