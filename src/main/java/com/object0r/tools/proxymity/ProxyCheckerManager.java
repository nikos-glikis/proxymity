package com.object0r.tools.proxymity;

import com.object0r.tools.proxymity.helpers.ConsoleColors;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProxyCheckerManager extends Thread
{
    private Connection dbConnection;
    //Pending proxies to be processed.
    private ArrayDeque<ProxyInfo> proxies = new ArrayDeque<>();
    private Vector<ProxyChecker> proxyCheckers;
    private int globalProxyCount = 0;
    //Total count of inactive ProxyChecker threads discovered in the total runtime.
    private int totalInactive = 0;

    public ProxyCheckerManager(Connection dbConnection)
    {
        this.dbConnection = dbConnection;
    }

    public void run()
    {
        init();

        try
        {
            while (true)
            {
                Vector<ProxyInfo> proxyInfos = getProxiesToTest();

                synchronized (this)
                {
                    proxies.addAll(proxyInfos);
                }

                markDead();

                sleepWhileHaveProxies();
                synchronized (this)
                {
                    proxies.addAll(getDeadProxiesForCheck(1000));
                }
                sleepWhileHaveProxies();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void init()
    {
        proxyCheckers = new Vector<>();
        ProxyChecker pc;
        for (; globalProxyCount < Proxymity.PROXY_CHECKERS_COUNT; globalProxyCount++)
        {
            pc = new ProxyChecker(this, dbConnection, globalProxyCount);
            pc.start();
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            proxyCheckers.add(pc);
        }
        new IdleChecker().start();

    }


    class IdleChecker extends Thread
    {
        public void run()
        {
            try
            {
                Thread.sleep(30000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            while (true)
            {
                try
                {
                    for (ProxyChecker pc : proxyCheckers)
                    {
                        pc.setActive(false);
                    }
                    int secondsToSleep = 180;
                    try
                    {
                        Thread.sleep(secondsToSleep * 1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    int inactive = 0;
                    Vector<ProxyChecker> toRemove = new Vector<>();
                    for (ProxyChecker pc : proxyCheckers)
                    {
                        try
                        {
                            if (!pc.isActive())
                            {
                                inactive++;
                                totalInactive++;
                                toRemove.add(pc);
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    ConsoleColors.printRed("After a wait of " + secondsToSleep + " seconds " + inactive + "/" + proxyCheckers.size() + " proxycheckers are inactive. Total inactive count is: " + totalInactive);

                    for (ProxyChecker proxyChecker : toRemove)
                    {
                        try
                        {
                            proxyChecker.stop();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                    proxyCheckers.removeAll(toRemove);

                    for (int i = proxyCheckers.size(); i <= Proxymity.PROXY_CHECKERS_COUNT; i++)
                    {
                        try
                        {
                            Thread.sleep(50);
                            ProxyChecker pc = new ProxyChecker(ProxyCheckerManager.this, dbConnection, ++globalProxyCount);
                            pc.start();
                            proxyCheckers.add(pc);
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
        }
    }

    public synchronized ProxyInfo getNextProxy()
    {
        return proxies.pollFirst();
    }

    public void shutDown()
    {
        for (ProxyChecker proxyChecker : proxyCheckers)
        {
            proxyChecker.stop();
        }
        this.stop();
    }

    private void sleepWhileHaveProxies()
    {
        try
        {
            while (proxies.size() > 0)
            {
                Thread.sleep(1000);
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
            st.executeUpdate("UPDATE `" + Proxymity.TABLE_NAME + "` SET status = '" + ProxyChecker.PROXY_STATUS_DEAD + "' WHERE `status` != '" + ProxyChecker.PROXY_STATUS_ACTIVE + "' AND `status` != '" + ProxyChecker.PROXY_STATUS_PENDING + "' AND lastactive < DATE_SUB(NOW(), INTERVAL " + Proxymity.MARK_DEAD_AFTER_MINUTES + " MINUTE)");
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
        ConsoleColors.printGreen(getDateTimeAsString() + ": " + message);
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
                    proxyInfo.unsetCheckOnlyOnce();
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

    Vector<ProxyInfo> getRandomDeadProxies(int count)
    {
        Vector<ProxyInfo> proxyInfos = new Vector<ProxyInfo>();
        try
        {
            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, host, port, type, checkOnlyOnce FROM " + Proxymity.TABLE_NAME + " WHERE status = 'dead' AND checkOnlyOnce = 'no' ORDER BY priority DESC, RAND() LIMIT " + count);
            proxyInfos = getProxyInfosFromResultSet(rs);
            int i = proxyInfos.size();
            printMessage("Fetched " + i + " random dead proxies for check.  ");
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
     *
     * @param count
     * @return
     */
    Vector<ProxyInfo> getDeadProxiesForCheck(int count)
    {
        Vector<ProxyInfo> proxyInfos = new Vector<ProxyInfo>();
        try
        {
            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, host, port, type, checkOnlyOnce FROM " + Proxymity.TABLE_NAME + " WHERE status = 'dead' AND checkOnlyOnce = 'no' ORDER BY lastchecked LIMIT " + count);
            proxyInfos = getProxyInfosFromResultSet(rs);
            int i = proxyInfos.size();
            printMessage("Fetched " + i + " dead proxies for check.");
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
     * Returns proxies to test. Proxies that were not checked for RECHECK_INTERVAL_MINUTES have higher priority.
     *
     * @return
     */
    Vector<ProxyInfo> getProxiesToTest()
    {
        Vector<ProxyInfo> proxyInfos = new Vector<ProxyInfo>();
        try
        {
            Statement st = dbConnection.createStatement();
            String query = "(SELECT id, host, port, type, checkOnlyOnce, priority FROM " + Proxymity.TABLE_NAME + " WHERE status = 'pending' )  " +
                    "UNION " +
                    "( SELECT id, host, port, type, checkOnlyOnce, priority FROM " + Proxymity.TABLE_NAME + " WHERE lastchecked is NULL LIMIT 5000 )  " +
                    "UNION " +
                    //"( SELECT id, host, port, type, checkOnlyOnce, priority FROM " + Proxymity.TABLE_NAME + " WHERE ( status != 'dead' ) AND (lastchecked not BETWEEN DATE_SUB(NOW(), INTERVAL " + Proxymity.RECHECK_INTERVAL_MINUTES + " MINUTE) AND NOW()) )  " +
                    "( SELECT id, host, port, type, checkOnlyOnce, 10 FROM " + Proxymity.TABLE_NAME + " WHERE ( status != 'dead' ) AND (lastchecked not BETWEEN DATE_SUB(NOW(), INTERVAL " + Proxymity.RECHECK_INTERVAL_MINUTES + " MINUTE) AND NOW()) )  " +

                    "ORDER BY priority DESC, RAND()  LIMIT 5000";
            ResultSet rs = st.executeQuery(query);

            proxyInfos = getProxyInfosFromResultSet(rs);
            int i = proxyInfos.size();
            printMessage("Fetched " + i + " proxies for check. ");
            if (i < 10)
            {
                Thread.sleep(5000);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        return proxyInfos;
    }
}
