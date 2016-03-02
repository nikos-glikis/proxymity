package com.tools.proxymity.collectors;

import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.tools.proxymity.ProxyCollector;
import com.object0r.toortools.Utilities;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InCloakCollector extends ProxyCollector
{
    //String imageMagickPath = "C:\\Program Files\\ImageMagick-6.9.3-Q16\\convert.exe";

    public InCloakCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            String page = Utilities.readUrl("https://incloak.com/proxy-list/");

            Pattern p = Pattern.compile("<tr><td class=tdl.*?</tr>");
            Matcher m = p.matcher(page);

            while (m.find())
            {
                try
                {
                    String line = m.group();
                    //System.out.println(line);
                    String ip = Utilities.cut("class=tdl>","<", line).trim();
                    //System.out.println("Ip: "+ ip);
                    String portImage = "https://incloak.com"+Utilities.cut("<img src=\"","\"", line).trim();
                    //System.out.println("PortImage: "+ portImage);
                    Utilities.downloadFile(portImage, "tmp/image.gif");

                    convertImageToPnm("tmp/image.gif", "tmp/image.pnm");
                    //System.out.println(ocrImage("tmp/image.pnm"));
                    String portText = ocrImage("tmp/image.pnm").trim().replace("O","0").replace("o","0");
                    //System.out.println("-"+portText+"-");
                    int port = 0;
                    try
                    {
                        port = Integer.parseInt(portText);
                    }
                    catch (Exception e)
                    {
                        //System.out.println("Con "+ portText);
                        continue;
                    }

                    String typeText = Utilities.cut("</div></div></td><td>","<", line).trim();

                    ProxyInfo proxyInfo = new ProxyInfo();
                    proxyInfo.setHost(ip);
                    proxyInfo.setPort(portText);
                    if (typeText.equals("SOCKS5"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                    }
                    else if (typeText.equals("SOCKS4"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS4);
                    }
                    else if (typeText.equals("HTTP"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    }
                    else if (typeText.equals("HTTP, HTTPS"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    }
                    else if (typeText.equals("SOCKS4, SOCKS5"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                    }
                    else if (typeText.equals("HTTPS"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTPS);
                    }
                    else
                    {
                        if (typeText.contains("HTTP"))
                        {
                            proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                        }
                        else if (typeText.contains("SOCKS4"))
                        {
                            proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS4);
                        }
                        else if (typeText.contains("SOCKS5"))
                        {
                            proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                        }
                        else
                        {
                            System.out.println("nType: "+typeText);
                            continue;
                        }
                    }

                    addProxy(proxyInfo);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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
