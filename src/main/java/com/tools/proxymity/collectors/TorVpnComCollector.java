package com.tools.proxymity.collectors;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;
import org.apache.commons.lang3.StringUtils;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TorVpnComCollector extends ProxyCollector
{
    public TorVpnComCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            Utilities.trustEverybody();
            String page = Utilities.readUrl("https://www.torvpn.com/en/proxy-list");
            Pattern p = Pattern.compile("<tr>.*?</tr>",Pattern.DOTALL);
            Matcher m = p.matcher(page);
            while (m.find())
            {
                try
                {
                    String line = m.group();
                    if (line.contains("alt=\"IP address\"")) {
                        String image = "https://www.torvpn.com" + Utilities.cut("<img src=\"", "\"", line);
                        Utilities.downloadFile(image, "tmp/imageTorVpn.png");
                        convertImageToPnm("tmp/imageTorVpn.png", "tmp/imageTorVpn.pnm");
                        String ipText = ocrImage("tmp/imageTorVpn.pnm").trim().replace("O","0").replace("o","0").replace("_",".").replace("..",".");
                        ipText = ipText.replace("..",".");
                        ipText = StringUtils.strip(ipText, ".");
                        /*System.out.println(ipText);
                        if (true) { continue; }*/
                        Pattern pp = Pattern.compile("<td>\\d+</td>");
                        Matcher mm = pp.matcher(line);
                        mm.find();
                        String portText = mm.group();
                        String port = portText.replace("<td>","").replace("</td>","");
                        Integer.parseInt(port);

                        ProxyInfo proxyInfo = new ProxyInfo();
                        proxyInfo.setPort(port);
                        proxyInfo.setHost(ipText);
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);

                        addProxy(proxyInfo);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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
