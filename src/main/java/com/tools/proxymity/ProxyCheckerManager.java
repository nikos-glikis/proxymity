package com.tools.proxymity;

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

    String getDateTimeAsString()
    {
        java.util.Date date = new java.util.Date();
        return date.toString();
    }

    void printMessage(String message)
    {
        System.out.println(getDateTimeAsString()+ ": "+message);
    }

    Vector<ProxyInfo> getProxiesToTest()
    {
        Vector<ProxyInfo> proxyInfos = new Vector<ProxyInfo>();

        try
        {
            Statement st = dbConnection.createStatement();

            ResultSet rs = st.executeQuery("SELECT id, host, port, type FROM "+Proxymity.TABLE_NAME+" WHERE status = 'pending'  UNION SELECT id, host, port, type FROM "+Proxymity.TABLE_NAME+" WHERE lastchecked is NULL  UNION SELECT id, host, port, type FROM "+Proxymity.TABLE_NAME+" WHERE lastchecked not BETWEEN DATE_SUB(NOW(), INTERVAL "+ Proxymity.RECHECK_INTERVAL_MINUTES +" MINUTE) AND NOW() ORDER BY RAND()");
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
            printMessage("Fetched "+i+ " proxies for check. ");
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
