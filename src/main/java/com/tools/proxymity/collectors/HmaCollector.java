package com.tools.proxymity.collectors;

import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.tools.proxymity.ProxyCollector;
import com.toortools.Utilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HmaCollector extends ProxyCollector
{


    public HmaCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
        //TODO for office
        //useTor = true;
        //initializePhantom();
        //TODO deleteme
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {

            for (int i = 1; i<50; i++)
            {
                String page = downloadPageWithPhantomJs("http://proxylist.hidemyass.com/"+i);

                Scanner sc = new Scanner(page);
                boolean found = false;
                while (sc.hasNext())
                {
                    try
                    {
                        String line = sc.nextLine().trim();
                        Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+.\\d+");
                        Matcher m = p.matcher(line);
                        if (m.find())
                        {
                            found = true;
                            String ip = m.group();
                            //System.out.println(line);
                            String nextLine = sc.nextLine().trim();
                            //System.out.println(nextLine);

                            String port = Utilities.cut(ip + " ", " ", line);
                            String type = nextLine.substring(0, nextLine.indexOf(" ")).trim();
                            /*System.out.println("Ip: " + ip);
                            System.out.println("Port: " + port);
                            */
                            //System.out.println("Type: " + type);

                            ProxyInfo proxyInfo = new ProxyInfo();
                            proxyInfo.setHost(ip);
                            proxyInfo.setPort(port);

                            if (type.equals("socks4/5"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                            }
                            else if (type.equals("HTTP"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                            }
                            else if (type.equals("HTTPS"))
                            {
                                proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTPS);
                            } else {
                                continue;
                            }
                            addProxy(proxyInfo);

                        } else {
                            //System.out.println("Not");
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                if (!found)
                {
                    //System.out.println("Break, end "+i);
                    break;
                }
                //System.exit(0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }
}
