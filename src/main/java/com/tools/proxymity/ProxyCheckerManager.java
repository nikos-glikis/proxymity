package com.tools.proxymity;

import com.tools.proxymity.datatypes.ProxyInfo;
import com.tools.proxymity.helpers.ConsoleColors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProxyCheckerManager extends Thread
{
    Connection dbConnection;
    ExecutorService fixedPool;
    public ProxyCheckerManager(Connection dbConnection)
    {
        this.dbConnection = dbConnection;
    }
    public void shutDown()
    {
        fixedPool.shutdownNow();
    }
    public void run()
    {
        try
        {
            new ProxyChecker(new ProxyInfo(), dbConnection).setMyIp();
            while (true)
            {
                fixedPool = Executors.newFixedThreadPool(Proxymity.PROXY_CHECKERS_COUNT);
                Vector<ProxyInfo> proxyInfos = getProxiesToTest();

                for (ProxyInfo proxyInfo: proxyInfos)
                {
                    fixedPool.submit(new ProxyChecker(proxyInfo, dbConnection));
                }
                fixedPool.shutdown();
                markDead();
                try
                {
                    fixedPool.awaitTermination(10, TimeUnit.MINUTES);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                fixedPool = Executors.newFixedThreadPool(Proxymity.PROXY_CHECKERS_COUNT);

                proxyInfos  = getDeadProxiesForCheck(5000);
                for (ProxyInfo proxyInfo: proxyInfos)
                {
                    fixedPool.submit(new ProxyChecker(proxyInfo, dbConnection));
                }
                fixedPool.shutdown();
                try
                {
                    fixedPool.awaitTermination(3, TimeUnit.MINUTES);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void markDead()
    {
        try
        {
            Statement st = dbConnection.createStatement();
            st.executeUpdate("UPDATE `"+Proxymity.TABLE_NAME+"` SET status = '"+ProxyChecker.PROXY_STATUS_DEAD+"' WHERE `status` != '"+ProxyChecker.PROXY_STATUS_PENDING+"' AND lastactive < DATE_SUB(NOW(), INTERVAL "+ Proxymity.MARK_DEAD_AFTER_MINUTES +" MINUTE)");
            st.close();
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
        ConsoleColors.printGreen(getDateTimeAsString()+ ": "+message);
    }

    Vector<ProxyInfo> getProxyInfosFromResultSet(ResultSet rs)
    {
        Vector<ProxyInfo> proxyInfos = new Vector<ProxyInfo>();
        try
        {
            int i = 0;
            while (rs.next())
            {
                i++;
                ProxyInfo proxyInfo = new ProxyInfo();
                proxyInfo.setId(rs.getString(1));
                proxyInfo.setHost(rs.getString(2));
                proxyInfo.setPort(rs.getString(3));
                proxyInfo.setType(rs.getString(4));
                if (rs.getString(5).equals("yes"))
                {
                    proxyInfo.setCheckOnlyOnce();
                }
                else
                {
                    proxyInfo.setCheckOnlyOnce();
                }
                proxyInfos.add(proxyInfo);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return proxyInfos;
    }

    Vector<ProxyInfo> getRandomDeadProxies( int count)
    {
        Vector<ProxyInfo> proxyInfos = new Vector<ProxyInfo>();
        try
        {
            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, host, port, type, checkOnlyOnce FROM "+Proxymity.TABLE_NAME+" WHERE status = 'dead' AND checkOnlyOnce = 'no' ORDER BY priority DESC, RAND() LIMIT "+count);
            proxyInfos = getProxyInfosFromResultSet(rs);
            int i = proxyInfos.size();
            printMessage("Fetched "+i+ " random dead proxies for check.  ");
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

    /**
     * Returns the dead proxies to check. Those are the proxies that have not ben checked the longest.
     * @param count
     * @return
     */
    Vector<ProxyInfo> getDeadProxiesForCheck( int count)
    {
        Vector<ProxyInfo> proxyInfos = new Vector<ProxyInfo>();
        try
        {
            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, host, port, type, checkOnlyOnce FROM "+Proxymity.TABLE_NAME+" WHERE status = 'dead' AND checkOnlyOnce = 'no' ORDER BY lastchecked LIMIT "+count);
            proxyInfos = getProxyInfosFromResultSet(rs);
            int i = proxyInfos.size();
            printMessage("Fetched "+i+ " dead proxies for check.");
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

    Vector<ProxyInfo> getProxiesToTest()
    {
        Vector<ProxyInfo> proxyInfos = new Vector<ProxyInfo>();
        try
        {
            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, host, port, type, checkOnlyOnce FROM "+Proxymity.TABLE_NAME+" WHERE status = 'pending'  " +
                    "UNION SELECT id, host, port, type, checkOnlyOnce FROM "+Proxymity.TABLE_NAME+" WHERE lastchecked is NULL LIMIT 5000  " +
                    "UNION SELECT id, host, port, type, checkOnlyOnce FROM "+Proxymity.TABLE_NAME+" WHERE ( status != 'dead' ) AND (lastchecked not BETWEEN DATE_SUB(NOW(), INTERVAL "+ Proxymity.RECHECK_INTERVAL_MINUTES +" MINUTE) AND NOW())  ORDER BY RAND()  LIMIT 5000");

            proxyInfos = getProxyInfosFromResultSet(rs);
            int i = proxyInfos.size();
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
