package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyChecker;
import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.toortools.Utilities;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PremSocksCom extends ProxyCollector
{
    public PremSocksCom(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            String page = Utilities.readUrl("http://premsocks.com/socks-proxy");
            String json = Utilities.cut("data = [","];",page);
            //System.out.println(json);
            Pattern p = Pattern.compile("\\[.*?\\]",Pattern.DOTALL);
            Matcher m = p.matcher(json);

            while (m.find())
            {
                String line = m.group();
                if (line.contains(",")  )
                {
                   // System.out.println(line);
                    StringTokenizer st = new StringTokenizer(line,",");
                    st.nextToken();

                    String ip = (st.nextToken()).replace("\"","");
                    String host =(st.nextToken()).replace("[","").replace("\"","");
                    Pattern pp = Pattern.compile("\\*+.\\d+$");
                    Matcher mm = pp.matcher(host);
                    if(mm.find())
                    {
                        /*System.out.println(ip);
                        System.out.println(host);*/

                        String matched = mm.group();
                        int starCount = countCharsInString(matched);
                        String mergedIp = "";
                        if (starCount == 1)
                        {
                            mergedIp = ip.replace("*.***", matched);
                            mergedIp = mergedIp.replace("*.**", matched);
                            mergedIp = mergedIp.replace("*.*", matched);
                        }
                        else if (starCount == 2) {
                            mergedIp = ip.replace("**.***", matched);
                            mergedIp = mergedIp.replace("**.**", matched);
                            mergedIp = mergedIp.replace("**.*", matched);
                        }
                        else if (starCount == 3) {
                            mergedIp = ip.replace("***.***", matched);
                            mergedIp = mergedIp.replace("***.**", matched);
                            mergedIp = mergedIp.replace("***.*", matched);
                        }


                        processPartialIp(mergedIp);
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

    private void processPartialIp(String mergedIp)
    {
        try
        {
            int starCount = countCharsInString(mergedIp);



            int start = 0 , limit = 0;
            if (starCount == 3) {
                start = 100;
                limit = 255;
            } else if (starCount == 2) {
                start = 10;
                limit = 99;
            } else if (starCount == 1) {
                start = 1;
                limit = 9;
            }

            for (int i = start ; i<= limit; i++)
            {
                String ip = mergedIp.replace("***", new Integer(i).toString());
                ip = ip.replace("**", new Integer(i).toString());
                ip = ip.replace("*", new Integer(i).toString());
                ProxyInfo proxyInfo = new ProxyInfo();
                proxyInfo.setHost(ip);
                proxyInfo.setPort("10000");
                proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                addProxy(proxyInfo);
               /* Thread.sleep(100);
                Thread t = new Thread()
                {
                    public void run()
                    {
                        ProxyInfo proxyInfo = new ProxyInfo();
                        //new ProxyChecker();
                    }
                };
                t.start();*/
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private int countCharsInString(String mergedIp)
    {
        //count starts
        int counter = 0;
        for (int i = 0; i<mergedIp.length(); i++)
        {
            if (mergedIp.charAt(i) == '*')
            {
                counter ++;
            }
        }
        return counter;
    }
}
