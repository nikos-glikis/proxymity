package com.tools.proxymity;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProxyCheckerManager extends Thread
{

    Connection dbConnection;
    public ProxyCheckerManager(Connection dbConnection)
    {
        this.dbConnection = dbConnection;
    }

    public void run()
    {
        try
        {
            ExecutorService fixedPool = Executors.newFixedThreadPool(20);

            while (true)
            {
                Vector<ProxyInfo> proxyInfos =getProxiesToTest();

                for (ProxyInfo proxyInfo: proxyInfos)
                {

                    Proxy.Type type = null;
                    //new Proxy(Proxy.Type.HTTP, new InetSocketAddress("123.0.0.1", 8080));
                    if (proxyInfo.getType().equals( ProxyInfo.PROXY_TYPES_SOCKS4 ) )
                    {
                        type = Proxy.Type.SOCKS;
                    }
                    else if (proxyInfo.getType().equals( ProxyInfo.PROXY_TYPES_SOCKS5 ) )
                    {
                        type = Proxy.Type.SOCKS;
                    }
                    else if (proxyInfo.getType().equals( ProxyInfo.PROXY_TYPES_HTTP ) )
                    {
                        type = Proxy.Type.HTTP;
                    }
                    else if (proxyInfo.getType().equals( ProxyInfo.PROXY_TYPES_HTTPS ) )
                    {
                        type = Proxy.Type.HTTP;
                    }
                    if (type != null)
                    {
                        Proxy proxy = new Proxy(type,  new InetSocketAddress(proxyInfo.getHost(), Integer.parseInt(proxyInfo.getPort()))) ;
                        fixedPool.submit(new ProxyChecker(proxy, dbConnection));
                    }
                }
                //TODO remove me
                break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    Vector<ProxyInfo> getProxiesToTest()
    {
        Vector<ProxyInfo> proxyInfos = new Vector<ProxyInfo>();

        try
        {
            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, host, port, type FROM proxymity_proxies WHERE status = 'pending' UNION SELECT id, host, port, type FROM proxymity_proxies WHERE lastchecked is NULL UNION SELECT id, host, port, type FROM proxymity_proxies WHERE lastchecked BETWEEN DATE_SUB(NOW(), INTERVAL 10 MINUTE) AND NOW()");

            while (rs.next())
            {
                ProxyInfo proxyInfo = new ProxyInfo();
                proxyInfo.setId(rs.getString(1));
                proxyInfo.setHost(rs.getString(2));
                proxyInfo.setPort(rs.getString(3));
                proxyInfo.setType(rs.getString(4));
                proxyInfos.add(proxyInfo);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return proxyInfos;
    }
}
