package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.toortools.Utilities;

import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShodanCollector extends ProxyCollector
{
    String cookie = "__cfduid=dc5423f43e6dd5e4ed64fe309227fb8ba1456049527; _ga=GA1.2.1230545018.1456049528; _gat=1; polito=\"ff2e4f6987ac9513ca12271a44fc68eb56cb34f056096e5ee44985157c363f22!\"; _LOCALE_=en";
    public ShodanCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }
    String []countries = {"AF","AX","AL","DZ","AS","AD","AO","AI","AQ","AG","AR","AM","AW","AU","AT","AZ","BS","BH","BD","BB","BY","BE","BZ","BJ","BM","BT","BO","BQ","BA","BW","BV","BR","IO","BN","BG","BF","BI","KH","CM","CA","CV","KY","CF","TD","CL","CN","CX","CC","CO","KM","CG","CD","CK","CR","CI","HR","CU","CW","CY","CZ","DK","DJ","DM","DO","EC","EG","SV","GQ","ER","EE","ET","FK","FO","FJ","FI","FR","GF","PF","TF","GA","GM","GE","DE","GH","GI","GR","GL","GD","GP","GU","GT","GG","GN","GW","GY","HT","HM","VA","HN","HK","HU","IS","IN","ID","IR","IQ","IE","IM","IL","IT","JM","JP","JE","JO","KZ","KE","KI","KP","KR","KW","KG","LA","LV","LB","LS","LR","LY","LI","LT","LU","MO","MK","MG","MW","MY","MV","ML","MT","MH","MQ","MR","MU","YT","MX","FM","MD","MC","MN","ME","MS","MA","MZ","MM","NA","NR","NP","NL","NC","NZ","NI","NE","NG","NU","NF","MP","NO","OM","PK","PW","PS","PA","PG","PY","PE","PH","PN","PL","PT","PR","QA","RE","RO","RU","RW","BL","SH","KN","LC","MF","PM","VC","WS","SM","ST","SA","SN","RS","SC","SL","SG","SX","SK","SI","SB","SO","ZA","GS","SS","ES","LK","SD","SR","SJ","SZ","SE","CH","SY","TW","TJ","TZ","TH","TL","TG","TK","TO","TT","TN","TR","TM","TC","TV","UG","UA","AE","GB","US","UM","UY","UZ","VU","VE","VN","VG","VI","WF","EH","YE","ZM","ZW"};
    Vector<Thread> shodanThreads = new Vector<>();
    public boolean checkShodanUrl(String url, String cookie, String type, String port)
    {
        try
        {

            String page = Utilities.readUrl(url, cookie);
            if (page.contains("<p>Result limit reached.</p>")
                    ||page.contains("<div class=\"msg alert alert-info\">No results found</div>")
                    ||page.contains("Please login to use search filters")
                    ||page.contains("lease purchase a Shodan membership to access more ")
                    )

            {
                return false;
            }
            Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
            Matcher m = p.matcher(page);
            boolean foundAtLeastOne = false;
            while (m.find())
            {
                foundAtLeastOne = true;
                String line = m.group();
                ProxyInfo proxyInfo = new ProxyInfo();
                proxyInfo.setHost(line);
                proxyInfo.setPort(port);
                proxyInfo.setPriority(-100);
                proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                proxyInfo.setCheckOnlyOnce();
                addProxy(proxyInfo);
/*                proxyInfo = new ProxyInfo();
                proxyInfo.setHost(line);
                proxyInfo.setPort(port);
                proxyInfo.setPriority(-100);
                proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                proxyInfo.setCheckOnlyOnce();
                addProxy(proxyInfo);*/

                //System.out.println(line);
            }
            if (!foundAtLeastOne)
            {
                return false;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public void checkShodanPort(String port, String cookie, String type)
    {
        try
        {
            for (String country: countries)
            {
                 Thread t = new Thread(){
                    public void run()
                    {
                        //System.out.println("Starting port/country: "+port+"/"+country);
                        for (int i = 0; i < 1001; i++)
                        {
                            String url = "https://www.shodan.io/search?query=port%3A" + port + "+country%3A\""+country+"\""+"&page=" + i;
                            if (!checkShodanUrl(url,cookie,type,port))
                            {
                                //System.out.println("Shodan breaking on: "+url);
                                break;
                            }
                        }


                    }
                };

                shodanThreads.add(t);
            }

            Thread t = new Thread(){
                public void run()
                {

                    for (int i = 0; i < 1001; i++)
                    {
                        //System.out.println("Starting port genera: "+port);
                        String url = "https://www.shodan.io/search?query=port%3A" + port + "&page=" + i;
                        if (!checkShodanUrl(url,cookie,type,port))
                        {
                            //System.out.println("Shodan breaking on: "+url);
                            break;
                        }
                    }


                }
            };

            shodanThreads.add(t);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    ThreadPoolExecutor shodanExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    //Scan shodan once per session.
    boolean done = false;
    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            if (done)
            {
                return getProxies();
            }
            checkShodanPort("8118",
                    cookie
                    ,ProxyInfo.PROXY_TYPES_HTTP
            );

            checkShodanPort("81",
                    cookie
                    ,ProxyInfo.PROXY_TYPES_HTTP
            );

            checkShodanPort("8000",
                    cookie
                    ,ProxyInfo.PROXY_TYPES_SOCKS4
            );
            checkShodanPort("3128",
                    cookie
                    ,ProxyInfo.PROXY_TYPES_HTTP
            );

            checkShodanPort("8080",
                    cookie
                    ,ProxyInfo.PROXY_TYPES_HTTP
            );

            checkShodanPort("1080",
                    cookie
                    ,ProxyInfo.PROXY_TYPES_HTTP
            );

            Collections.shuffle(shodanThreads);

            for (Thread t : shodanThreads)
            {
                shodanExecutor.execute(t);
            }

            shodanExecutor.shutdown();

            done = true;



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }
}
