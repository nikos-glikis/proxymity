package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GithubCollector extends ProxyCollector
{
    public GithubCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            List<UrlProxyType> urls = new ArrayList<>();
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/vmheaven/VMHeaven-Free-Proxy-Updated/refs/heads/main/socks5.txt", ProxyInfo.PROXY_TYPES_SOCKS5));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/claude89757/free_https_proxies/refs/heads/main/https_proxies.txt", ProxyInfo.PROXY_TYPES_HTTPS));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/MrMarble/proxy-list/refs/heads/main/all.txt", ProxyInfo.PROXY_TYPES_HTTPS));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/hookzof/socks5_list/refs/heads/master/proxy.txt", ProxyInfo.PROXY_TYPES_SOCKS5));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/themiralay/Proxy-List-World/refs/heads/master/data.txt", ProxyInfo.PROXY_TYPES_HTTPS));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/Vadim287/free-proxy/refs/heads/main/proxies/socks5.txt", ProxyInfo.PROXY_TYPES_SOCKS5));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/Vadim287/free-proxy/refs/heads/main/proxies/socks4.txt", ProxyInfo.PROXY_TYPES_SOCKS4));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/Vadim287/free-proxy/refs/heads/main/proxies/http.txt", ProxyInfo.PROXY_TYPES_HTTPS));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/dpangestuw/Free-Proxy/refs/heads/main/http_proxies.txt", ProxyInfo.PROXY_TYPES_HTTPS));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/dpangestuw/Free-Proxy/refs/heads/main/socks4_proxies.txt", ProxyInfo.PROXY_TYPES_SOCKS4));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/dpangestuw/Free-Proxy/refs/heads/main/socks5_proxies.txt", ProxyInfo.PROXY_TYPES_SOCKS5));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/saisuiu/Lionkings-Http-Proxys-Proxies/refs/heads/main/free.txt", ProxyInfo.PROXY_TYPES_HTTPS));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/zloi-user/hideip.me/refs/heads/master/https.txt", ProxyInfo.PROXY_TYPES_HTTPS));
            urls.add(new UrlProxyType("https://github.com/zloi-user/hideip.me/raw/refs/heads/master/socks4.txt", ProxyInfo.PROXY_TYPES_SOCKS4));
            urls.add(new UrlProxyType("https://github.com/zloi-user/hideip.me/raw/refs/heads/master/socks5.txt", ProxyInfo.PROXY_TYPES_SOCKS5));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/trio666/proxy-checker/refs/heads/main/socks5.txt", ProxyInfo.PROXY_TYPES_SOCKS5));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/trio666/proxy-checker/refs/heads/main/socks4.txt", ProxyInfo.PROXY_TYPES_SOCKS4));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/trio666/proxy-checker/refs/heads/main/https.txt", ProxyInfo.PROXY_TYPES_HTTPS));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/Anbeh/Proxify/refs/heads/main/socks5.txt", ProxyInfo.PROXY_TYPES_SOCKS5));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/Anbeh/Proxify/refs/heads/main/socks4.txt", ProxyInfo.PROXY_TYPES_SOCKS4));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/Anbeh/Proxify/refs/heads/main/https.txt", ProxyInfo.PROXY_TYPES_HTTPS));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/M-logique/Proxies/refs/heads/main/proxies/regular/socks5.txt", ProxyInfo.PROXY_TYPES_SOCKS5));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/M-logique/Proxies/refs/heads/main/proxies/regular/socks4.txt", ProxyInfo.PROXY_TYPES_SOCKS4));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/M-logique/Proxies/refs/heads/main/proxies/regular/http.txt", ProxyInfo.PROXY_TYPES_HTTPS));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/vakhov/fresh-proxy-list/refs/heads/master/socks5.txt", ProxyInfo.PROXY_TYPES_SOCKS5));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/vakhov/fresh-proxy-list/refs/heads/master/socks4.txt", ProxyInfo.PROXY_TYPES_SOCKS4));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/vakhov/fresh-proxy-list/refs/heads/master/https.txt", ProxyInfo.PROXY_TYPES_HTTPS));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/tboy1337/hideip.me-proxy-tester/refs/heads/main/proxy_check_results_socks5.md", ProxyInfo.PROXY_TYPES_SOCKS5));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/tboy1337/hideip.me-proxy-tester/refs/heads/main/proxy_check_results_socks4.md", ProxyInfo.PROXY_TYPES_SOCKS4));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/tboy1337/hideip.me-proxy-tester/refs/heads/main/proxy_check_results_http.md", ProxyInfo.PROXY_TYPES_HTTPS));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/FifzzSENZE/Master-Proxy/refs/heads/master/proxies/https.txt", ProxyInfo.PROXY_TYPES_HTTPS));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/FifzzSENZE/Master-Proxy/refs/heads/master/proxies/socks4.txt", ProxyInfo.PROXY_TYPES_SOCKS4));
            urls.add(new UrlProxyType("https://raw.githubusercontent.com/FifzzSENZE/Master-Proxy/refs/heads/master/proxies/socks5.txt", ProxyInfo.PROXY_TYPES_SOCKS5));

            // Shuffle the list to process URLs in random order
            Collections.shuffle(urls);

            // Create a thread pool with 4 threads
            ExecutorService executorService = Executors.newFixedThreadPool(4);

            for (UrlProxyType urlProxyType : urls)
            {
                executorService.submit(() -> {
                    try
                    {
                        genericParsingOfUrl(urlProxyType.url, urlProxyType.proxyType);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                });
            }

            // Shutdown the executor and wait for all tasks to finish
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
       