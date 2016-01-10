package com.tools.proxymity;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProxyCheckerManager extends Thread
{

    static final int PROXY_CHECKERS_COUNT = 150;
    Connection dbConnection;
    public ProxyCheckerManager(Connection dbConnection)
    {
        this.dbConnection = dbConnection;
    }

    public void run()
    {
        try
        {


            new ProxyChecker(new ProxyInfo(), dbConnection).setMyIp();
            while (true)
            {
                ExecutorService fixedPool = Executors.newFixedThreadPool(PROXY_CHECKERS_COUNT);
                Vector<ProxyInfo> proxyInfos = getProxiesToTest();

                for (ProxyInfo proxyInfo: proxyInfos)
                {
                    fixedPool.submit(new ProxyChecker(proxyInfo, dbConnection));
                }
                fixedPool.shutdown();
                try {
                    fixedPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
            System.out.println("Getting proxies to check");
            ResultSet rs = st.executeQuery("SELECT id, host, port, type FROM "+Proxymity.TABLE_NAME+" WHERE status = 'pending'  UNION SELECT id, host, port, type FROM "+Proxymity.TABLE_NAME+" WHERE lastchecked is NULL  UNION SELECT id, host, port, type FROM "+Proxymity.TABLE_NAME+" WHERE lastchecked BETWEEN DATE_SUB(NOW(), INTERVAL 10 MINUTE) AND NOW() ORDER BY RAND()");
            int i = 0;
            while (rs.next())
            {
                i++;
                ProxyInfo proxyInfo = new ProxyInfo();
                proxyInfo.setId(rs.getString(1));
                proxyInfo.setHost(rs.getString(2));
                proxyInfo.setPort(rs.getString(3));
                proxyInfo.setType(rs.getString(4));
                proxyInfos.add(proxyInfo);
            }
            System.out.println("In total "+i+ " proxies fetched. ");
            if (i < 10)
            {
                Thread.sleep(5000);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return proxyInfos;
    }
}
