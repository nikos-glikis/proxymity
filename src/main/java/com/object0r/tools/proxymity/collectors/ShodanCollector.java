package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.Collections;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShodanCollector extends ProxyCollector
{
    String cookie = "__cfduid=d216a341634eb333d0131299ca3e1f0d71455779013; _ga=GA1.2.1267034398.1455779015; _gat=1; polito=\"32065234dccc57340f064844a4d652f5576bac0056ce0326e44985606644274b!\"; _LOCALE_=en";

    public ShodanCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    String[] countries = {"AF", "AX", "AL", "DZ", "AS", "AD", "AO", "AI", "AQ", "AG", "AR", "AM", "AW", "AU", "AT", "AZ", "BS", "BH", "BD", "BB", "BY", "BE", "BZ", "BJ", "BM", "BT", "BO", "BQ", "BA", "BW", "BV", "BR", "IO", "BN", "BG", "BF", "BI", "KH", "CM", "CA", "CV", "KY", "CF", "TD", "CL", "CN", "CX", "CC", "CO", "KM", "CG", "CD", "CK", "CR", "CI", "HR", "CU", "CW", "CY", "CZ", "DK", "DJ", "DM", "DO", "EC", "EG", "SV", "GQ", "ER", "EE", "ET", "FK", "FO", "FJ", "FI", "FR", "GF", "PF", "TF", "GA", "GM", "GE", "DE", "GH", "GI", "GR", "GL", "GD", "GP", "GU", "GT", "GG", "GN", "GW", "GY", "HT", "HM", "VA", "HN", "HK", "HU", "IS", "IN", "ID", "IR", "IQ", "IE", "IM", "IL", "IT", "JM", "JP", "JE", "JO", "KZ", "KE", "KI", "KP", "KR", "KW", "KG", "LA", "LV", "LB", "LS", "LR", "LY", "LI", "LT", "LU", "MO", "MK", "MG", "MW", "MY", "MV", "ML", "MT", "MH", "MQ", "MR", "MU", "YT", "MX", "FM", "MD", "MC", "MN", "ME", "MS", "MA", "MZ", "MM", "NA", "NR", "NP", "NL", "NC", "NZ", "NI", "NE", "NG", "NU", "NF", "MP", "NO", "OM", "PK", "PW", "PS", "PA", "PG", "PY", "PE", "PH", "PN", "PL", "PT", "PR", "QA", "RE", "RO", "RU", "RW", "BL", "SH", "KN", "LC", "MF", "PM", "VC", "WS", "SM", "ST", "SA", "SN", "RS", "SC", "SL", "SG", "SX", "SK", "SI", "SB", "SO", "ZA", "GS", "SS", "ES", "LK", "SD", "SR", "SJ", "SZ", "SE", "CH", "SY", "TW", "TJ", "TZ", "TH", "TL", "TG", "TK", "TO", "TT", "TN", "TR", "TM", "TC", "TV", "UG", "UA", "AE", "GB", "US", "UM", "UY", "UZ", "VU", "VE", "VN", "VG", "VI", "WF", "EH", "YE", "ZM", "ZW"};
    Vector<Thread> shodanThreads = new Vector<>();

    public boolean checkShodanUrl(String url, String cookie, String type, String port)
    {
        try
        {
            //TODO we can do the cities.
            String page = Utilities.readUrl(url, cookie);
            if (
                    page.contains("<p>Result limit reached.</p>")
                            || page.contains("<div class=\"msg alert alert-info\">No results found</div>")
                            || page.contains("Please login to use search filters")
                            || page.contains("lease purchase a Shodan membership to access more ")
                    )

            {
                return false;
            }
            boolean foundAtLeastOne = false;
            if (port.equals("-1"))
            {
                Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+\"");
                Matcher m = p.matcher(page);

                while (m.find())
                {
                    foundAtLeastOne = true;
                    String line = m.group().replace("\"", "");
                    ProxyInfo proxyInfo = new ProxyInfo();
                    proxyInfo.setHost(line);
                    proxyInfo.setPort("80");
                    proxyInfo.setPriority(-100);
                    proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    proxyInfo.setCheckOnlyOnce();
                    addProxy(proxyInfo);

                }
                p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+");
                m = p.matcher(page);

                while (m.find())
                {
                    foundAtLeastOne = true;
                    String line = m.group();
                    //System.out.println(line);
                    StringTokenizer st = new StringTokenizer(line, ":");

                    ProxyInfo proxyInfo = new ProxyInfo();
                    proxyInfo.setHost(st.nextToken());
                    proxyInfo.setPort(st.nextToken());
                    proxyInfo.setPriority(-100);
                    proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    proxyInfo.setCheckOnlyOnce();
                    addProxy(proxyInfo);
                }
            }
            else
            {
                Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
                Matcher m = p.matcher(page);

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

                }

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

    public void checkShodanText(String text, String cookie, String type)
    {
        try
        {
            for (String country : countries)
            {
                Thread t = new Thread()
                {
                    public void run()
                    {
                        //System.out.println("Starting port/country: "+port+"/"+country);
                        for (int i = 0; i < 1001; i++)
                        {
                            String url = "https://www.shodan.io/search?query=" + text + "+country%3A\"" + country + "\"" + "&page=" + i;
                            if (!checkShodanUrl(url, cookie, type, "-1"))
                            {
                                //System.out.println("Shodan breaking on: "+url);
                                break;
                            }
                        }


                    }
                };

                shodanThreads.add(t);
            }

            Thread t = new Thread()
            {
                public void run()
                {

                    for (int i = 0; i < 1001; i++)
                    {
                        //System.out.println("Starting port genera: "+port);
                        String url = "https://www.shodan.io/search?query=" + text + "&page=" + i;
                        if (!checkShodanUrl(url, cookie, type, "-1"))
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

    public void checkShodanPort(String port, String cookie, String type)
    {
        try
        {
            for (String country : countries)
            {
                Thread t = new Thread()
                {
                    public void run()
                    {
                        //System.out.println("Starting port/country: "+port+"/"+country);
                        for (int i = 0; i < 1001; i++)
                        {
                            String url = "https://www.shodan.io/search?query=port%3A" + port + "+country%3A\"" + country + "\"" + "&page=" + i;
                            if (!checkShodanUrl(url, cookie, type, port))
                            {
                                //System.out.println("Shodan breaking on: "+url);
                                break;
                            }
                        }


                    }
                };

                shodanThreads.add(t);
            }

            Thread t = new Thread()
            {
                public void run()
                {

                    for (int i = 0; i < 1001; i++)
                    {
                        //System.out.println("Starting port genera: "+port);
                        String url = "https://www.shodan.io/search?query=port%3A" + port + "&page=" + i;
                        if (!checkShodanUrl(url, cookie, type, port))
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

        //TODO use ShodanScanner ku
        //if (true) return getProxies();
        if (done)
        {
            return getProxies();
        }
        try
        {
            checkShodanText("wingate", cookie, ProxyInfo.PROXY_TYPES_HTTP);
            checkShodanText("Server: squid", cookie, ProxyInfo.PROXY_TYPES_HTTP);
            checkShodanPort("8118",
                    cookie
                    , ProxyInfo.PROXY_TYPES_HTTP
            );

            checkShodanPort("81",
                    cookie
                    , ProxyInfo.PROXY_TYPES_HTTP
            );

            checkShodanPort("8000",
                    cookie
                    , ProxyInfo.PROXY_TYPES_SOCKS4
            );
            checkShodanPort("3128",
                    cookie
                    , ProxyInfo.PROXY_TYPES_HTTP
            );

            checkShodanPort("8080",
                    cookie
                    , ProxyInfo.PROXY_TYPES_HTTP
            );

            checkShodanPort("1080",
                    cookie
                    , ProxyInfo.PROXY_TYPES_HTTP
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

    @Override
    protected String collectorName()
    {
        return "shodan";
    }
}
