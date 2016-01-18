package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyChecker;
import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.Proxymity;
import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class happyProxyComCollector extends ProxyCollector
{
    public happyProxyComCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
        initializePhantom();
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {

            //TODO check if everything runs ok.
            genericParsingOfUrl("http://01hitfaker.blogspot.com/feeds/posts/default?alt=rss", ProxyInfo.PROXY_TYPES_SOCKS5);
            genericParsingOfUrl("http://proxysearcher.sourceforge.net/Proxy%20List.php?type=socks", ProxyInfo.PROXY_TYPES_SOCKS5);
            genericParsingOfUrl("http://proxysearcher.sourceforge.net/Proxy%20List.php?type=http", ProxyInfo.PROXY_TYPES_HTTP);
            genericParsingOfUrl("http://dogdev.net/Proxy/all", ProxyInfo.PROXY_TYPES_HTTP);
            genericParsingOfUrl("http://www.reliableproxylist.com/feeds/posts/default", ProxyInfo.PROXY_TYPES_HTTP);


            genericParsingOfUrl("http://www.proxylists.net", ProxyInfo.PROXY_TYPES_HTTP);
            //TODO we have only one driver.
            Thread t = new Thread(){
                public void run() {

                    for (int i=0; i<50; i++)
                    {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/us_"+i+".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };


            ExecutorService fixedPool = Executors.newFixedThreadPool(20);
            fixedPool.submit(t);
            Thread.sleep(3000);



            t = new Thread(){
                public void run() {

                    for (int i = 0; i < 10; i++) {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/gb_" + i + ".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };
            fixedPool.submit(t);
            Thread.sleep(3000);

            t = new Thread() {
                public void run() {

                    for (int i = 0; i < 6; i++) {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/ca_" + i + ".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };

            fixedPool.submit(t);
            Thread.sleep(3000);

            t = new Thread() {
                public void run() {

                    for (int i = 0; i < 50; i++) {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/3128_" + i + ".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };
            fixedPool.submit(t);
            Thread.sleep(3000);


            t = new Thread() {
                public void run() {


                    for (int i = 0; i < 50; i++) {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/8080_" + i + ".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };

            fixedPool.submit(t);
            Thread.sleep(3000);

            t = new Thread() {
                public void run() {

                    for (int i = 0; i < 80; i++) {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/1080_" + i + ".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };
            fixedPool.submit(t);
            Thread.sleep(3000);

            t = new Thread() {
                public void run() {

                    for (int i = 0; i < 50; i++) {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/80_" + i + ".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };
            fixedPool.submit(t);
            Thread.sleep(3000);

            t = new Thread() {
                public void run() {

                    for (int i = 50; i < 100; i++) {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/80_" + i + ".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };
            fixedPool.submit(t);
            Thread.sleep(3000);

            t = new Thread() {
                public void run() {

                    for (int i = 101; i < 150; i++) {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/80_" + i + ".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };
            fixedPool.submit(t);
            Thread.sleep(3000);

            t = new Thread() {
                public void run() {

                    for (int i = 151; i < 200; i++) {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/80_" + i + ".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };
            fixedPool.submit(t);
            Thread.sleep(3000);

            t = new Thread() {
                public void run() {

                    for (int i = 201; i < 250; i++) {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/80_" + i + ".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };
            fixedPool.submit(t);
            Thread.sleep(3000);

            t = new Thread() {
                public void run() {

                    for (int i = 251; i < 300; i++) {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/80_" + i + ".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };
            fixedPool.submit(t);
            Thread.sleep(3000);

            t = new Thread() {
                public void run() {

                    for (int i = 301; i < 350; i++) {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/80_" + i + ".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };
            fixedPool.submit(t);
            Thread.sleep(3000);

            t = new Thread() {
                public void run() {

                    for (int i = 351; i < 400; i++) {
                        genericParsingOfUrlSpace(getUrlBodyTextWithPhantom("http://www.proxylists.net/80_" + i + ".html"), ProxyInfo.PROXY_TYPES_HTTP);
                    }
                }
            };
            fixedPool.submit(t);
            Thread.sleep(3000);

            fixedPool.shutdown();

            try
            {
                fixedPool.awaitTermination(10, TimeUnit.MINUTES);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            genericParsingOfUrl("https://happy-proxy.com/fresh_proxies?key=2230e82110412f1b", ProxyInfo.PROXY_TYPES_HTTP, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }

}