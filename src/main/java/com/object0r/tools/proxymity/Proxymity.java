package com.object0r.tools.proxymity;


import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.helpers.ConsoleColors;
import com.object0r.toortools.Utilities;
import com.object0r.toortools.os.OsHelper;
import com.object0r.toortools.tor.TorHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.StringTokenizer;

public class Proxymity
{


    //TODO implement multiple ip sources
    //TODO implement muntiple anonymous detectors
    //TODO reset attributes on start
    //TODO use tor
    //TODO global get functions / anonymize.
    //TODO delete dead after some time
    //TODO transform random 500 to last order by checked
    static public int PROXY_CHECKERS_COUNT = 60;
    static final public String TABLE_NAME = "proxymity_proxies";
    public static int RECHECK_INTERVAL_MINUTES = 20;
    public static final long SLEEP_BETWEEN_REPORTS_SECONDS = 15;
    public static long MARK_DEAD_AFTER_MINUTES = 60;
    public static final long PHANTOM_JS_TIMEOUT_SECONDS = 15;
    public static final int SLEEP_SECONDS_BETWEEN_SCANS = 120;
    public static int PHANTOM_JS_WORKERS_COUNT = 10;
    public static String HTTPS_CHECK_URL = "httpbin.org/ip";
    public static String HTTPS_CHECK_STRING = "\"origin\": \"";
    public static final int TIMEOUT_MS = 10000;
    ;
    public boolean useTor = false;
    ProxyCheckerManager proxyCheckerManager;

    public Proxymity(Properties properties)
    {
        try
        {
            dbInformation = new DbInformation(
                    properties.getProperty("databaseHost"),
                    properties.getProperty("databaseUser"),
                    properties.getProperty("databasePassword"),
                    Integer.parseInt(properties.getProperty("databasePort")),
                    properties.getProperty("database")
            );

            OsHelper.deleteFolderContentsRecursive(new File("tmp"));
            connectToDatabase();

            if (!isTableInDatabase(dbConnection, TABLE_NAME))
            {
                Statement st = dbConnection.createStatement();
                String dbSchema = Utilities.readFile("dbSchema.sql");
                StringTokenizer tokenizer = new StringTokenizer(dbSchema, ";");
                while (tokenizer.hasMoreTokens())
                {
                    String line = tokenizer.nextToken().trim();
                    System.out.println("Executing " + line);
                    if (!line.equals(""))
                    {
                        st.execute(line);
                    }
                }

                if (isTableInDatabase(dbConnection, TABLE_NAME))
                {
                    System.out.println("Table Automatically created.");
                }
                else
                {
                    throw new Exception("Proxies table doesn't exist, and automatic creation failed.");
                }
            }

            if (this.proxyCheckerManager == null)
            {
                proxyCheckerManager = new ProxyCheckerManager(dbConnection);
            }

            readParams(properties);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Something went wrong, probably with the database. Check that database exists and that credentials are valid.");
            System.exit(0);
        }

        //resetProxiesAttributes();
        new Thread()
        {
            public void run()
            {

                while (true)
                {
                    try
                    {
                        printStatusReport();
                        checkIfIdle();
                        Thread.sleep(SLEEP_BETWEEN_REPORTS_SECONDS * 1000);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        ;
                    }
                }
            }

        }.start();
    }

    private void readParams(Properties properties)
    {
        if (properties.get("checkerThreadsCount") != null)
        {
            try
            {
                PROXY_CHECKERS_COUNT = Integer.parseInt((String) properties.get("checkerThreadsCount"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (properties.get("checkerRecheckInterval") != null)
        {
            try
            {
                RECHECK_INTERVAL_MINUTES = Integer.parseInt((String) properties.get("checkerRecheckInterval"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (properties.get("markDeadInterval") != null)
        {
            try
            {
                MARK_DEAD_AFTER_MINUTES = Integer.parseInt((String) properties.get("markDeadInterval"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (properties.get("phantomJsInstances") != null)
        {
            try
            {
                PHANTOM_JS_WORKERS_COUNT = Integer.parseInt((String) properties.get("phantomJsInstances"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (properties.get("phantomJsInstances") != null)
        {
            try
            {
                PHANTOM_JS_WORKERS_COUNT = Integer.parseInt((String) properties.get("phantomJsInstances"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (properties.get("httpsCheckUrl") != null)
        {
            try
            {
                HTTPS_CHECK_URL = (String) properties.get("httpsCheckUrl");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (properties.get("httpsVerificationString") != null)
        {
            try
            {
                HTTPS_CHECK_STRING = (String) properties.get("httpsVerificationString");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (properties.get("useTor") != null)
        {
            String useTor = (String) properties.get("useTor") + "".toLowerCase();
            if (useTor.equals("true"))
            {
                useTor();
            }

        }
    }

    int oldPendingCount, oldCheckedCount, oldActiveCount;

    private void checkIfIdle()
    {
        //Bad idea
        /*boolean idle = false;

        int pendingCount = getPendingProxiesCount();
        int checkedCount = getCheckedProxiesCount();
        int activeCount = getActiveProxiesCount();
        int totalCount = getTotalProxiesCount();

        if (pendingCount == oldPendingCount &&
                checkedCount == oldCheckedCount &&
                activeCount == oldActiveCount
                )
        {
            if (totalCount != checkedCount)
            {
                //idle for some time.
                System.out.println("Idle for some time, restartin proxy checkers");
                restartProxyCheckerManager();
            }
        }
        else
        {
            oldActiveCount = activeCount;
            oldCheckedCount = checkedCount;
            oldPendingCount = pendingCount;
        }*/
    }

    private void restartProxyCheckerManager()
    {
        proxyCheckerManager.shutDown();
        proxyCheckerManager = new ProxyCheckerManager(dbConnection);
        proxyCheckerManager.start();
    }

    public void useTor()
    {
        System.out.println("Using Tor.");
        TorHelper.torifySimple(true);
        this.useTor = true;
    }

    private void resetProxiesAttributes()
    {
        try
        {
            Statement st = dbConnection.createStatement();
            st.execute("UPDATE `" + TABLE_NAME + "` SET status = 'pending', fullanonymous = 'no', remoteIp = NULL, lastactive = NOW(), https = 'no'");
            st.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void printStatusReport()
    {
        try
        {
            int totalCount = getTotalProxiesCount();
            int pendingCount = getPendingProxiesCount();
            int checkedCount = getCheckedProxiesCount();
            int activeCount = getActiveProxiesCount();
            int anonCount = getAnonymousProxiesCount();
            int deadCount = getDeadProxiesCount();
            int socksAllCount = getSocksActive();
            int socksHttpsActive = getSocksHttpsActive();
            int httpsActive = getHttps();
            int httpHttpsActive = getHttpHttpsActive();

            ConsoleColors.printBlue("Proxies: Total/Checked/Active(Anonymous)/(Elite-NotCurrentlyChecked): " + totalCount + "/" + checkedCount + "/" + activeCount + "/" + anonCount + " Dead: " + deadCount);
            ConsoleColors.printBlue("SocksAll/Socks+Https/HTTPS/HTTP+S " + socksAllCount + "/" + socksHttpsActive + "/" + httpsActive + "/" + httpHttpsActive);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public int getDeadProxiesCount()
    {
        String where = "status = 'dead' ";
        return getWhereCount(where);
    }

    public int getHttpHttpsActive()
    {
        String where = "status = 'active' AND https = 'yes' AND type = 'http' ";
        return getWhereCount(where);
    }

    public int getSocksHttpsActive()
    {
        String where = "status = 'active' AND https = 'yes' AND (type = 'socks4' OR type ='socks5')";
        return getWhereCount(where);
    }

    public int getSocksActive()
    {
        String where = "status = 'active'  AND (type = 'socks4' OR type ='socks5')";
        return getWhereCount(where);
    }

    public int getHttps()
    {
        String where = "status = 'active' AND https = 'yes' ";
        return getWhereCount(where);
    }

    public int getPendingProxiesCount()
    {
        String where = "status = 'pending' ";
        return getWhereCount(where);
    }

    public int getCheckedProxiesCount()
    {
        String where = " status != 'pending' ";
        return getWhereCount(where);
    }

    int getTotalProxiesCount()
    {
        String where = " 1 ";
        return getWhereCount(where);
    }

    int getActiveProxiesCount()
    {
        String where = " status = 'active'";
        return getWhereCount(where);
    }

    int getAnonymousProxiesCount()
    {
        String where = " fullanonymous  = 'yes' AND status = 'active' ";
        return getWhereCount(where);
    }

    public int getWhereCount(String where)
    {
        int count = 0;
        try
        {
            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SELECT count(*) from " + TABLE_NAME + " WHERE " + where);
            rs.next();
            count = rs.getInt(1);
            st.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return count;
    }

    private void connectToDatabase()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            try
            {
                dbConnection = DriverManager
                        .getConnection("jdbc:mysql://" + dbInformation.getUrl() + ":" + dbInformation.getPort() + "/?"
                                + "user=" + dbInformation.getUsername()
                                + "&password=" + dbInformation.getPassword());
            }
            catch (Exception e)
            {
                System.out.println("Cannot connect to database, probably credentials are incorrect.");
                System.out.println(e.toString());
                System.exit(0);
            }

            dbConnection.close();
            try
            {
                dbConnection = DriverManager
                        .getConnection("jdbc:mysql://" + dbInformation.getUrl() + ":" + dbInformation.getPort() + "/" + dbInformation.getDatabase() + "?"
                                + "user=" + dbInformation.getUsername()
                                + "&password=" + dbInformation.getPassword());
            }
            catch (Exception e)
            {
                System.out.println("Cannot connect to database, probably the database does not exist, I will try creating it.");
                System.out.println(e.toString());
                try
                {
                    dbConnection = DriverManager
                            .getConnection("jdbc:mysql://" + dbInformation.getUrl() + ":" + dbInformation.getPort() + "/?"
                                    + "user=" + dbInformation.getUsername()
                                    + "&password=" + dbInformation.getPassword());
                    Statement st = dbConnection.createStatement();
                    String query = "CREATE DATABASE " + dbInformation.getDatabase();

                    st.execute(query);
                    System.out.println(query);

                    st.close();
                    dbConnection.close();

                    dbConnection = DriverManager
                            .getConnection("jdbc:mysql://" + dbInformation.getUrl() + ":" + dbInformation.getPort() + "/" + dbInformation.getDatabase() + "?"
                                    + "user=" + dbInformation.getUsername()
                                    + "&password=" + dbInformation.getPassword());

                }
                catch (Exception ee)
                {
                    System.out.println("Automatic creation of the database failed.");
                    System.out.println(ee.toString());
                    System.exit(0);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Proxymity() throws Exception
    {
        throw new Exception("Cannot use default constructor");
    }

    DbInformation dbInformation;
    Connection dbConnection;

    public void startCollectors()
    {
        try
        {
            CollectorParameters collectorParameters = new CollectorParameters();
            collectorParameters.setUseTor(useTor);
            collectorParameters.setDbConnection(dbConnection);
            collectorParameters.setSleepBetweenScansSeconds(Proxymity.SLEEP_SECONDS_BETWEEN_SCANS);

            new ProxyCollectorManager(collectorParameters, useTor).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Something went wrong, probably with the database. Check that database exists and that credentials are valid.");
            System.exit(0);
        }
    }

    public void startCheckers()
    {

        this.proxyCheckerManager.start();
    }

    public boolean isTableInDatabase(Connection connection, String table)
    {
        try
        {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SHOW TABLES");

            boolean foundProxiesTable = false;
            while (rs.next())
            {
                if (rs.getString(1).equals(table))
                {
                    foundProxiesTable = true;
                }
            }
            st.close();
            return foundProxiesTable;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        return false;
    }
}
