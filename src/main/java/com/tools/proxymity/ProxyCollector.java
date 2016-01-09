package com.tools.proxymity;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;

abstract public class ProxyCollector extends  Thread
{
    Connection dbConnection;

    public ProxyCollector(Connection dbConnection)
    {
        this.dbConnection = dbConnection;
    }

    public ProxyCollector() throws Exception
    {
        throw new Exception("Default controller not allowed");
    }
    public abstract Vector<ProxyInfo> collectProxies();

    final int SLEEP_SECONDS_BETWEEN_SCANS = 1;
    public void run()
    {
        while (true)
        {
            Vector<ProxyInfo> proxyInfos = collectProxies();
            System.out.println("Importing "+ proxyInfos.size()+ " proxies.");
            writeProxyInfoToDatabase(proxyInfos);
            try
            {
                Thread.sleep(SLEEP_SECONDS_BETWEEN_SCANS * 1000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void writeProxyInfoToDatabase(Vector<ProxyInfo> proxyInfos)
    {
        try
        {
            for (ProxyInfo proxyInfo : proxyInfos)
            {
                String query = "INSERT INTO `proxies`.`proxymity_proxies` (" +
                        "`id`, " +
                        "`host`, " +
                        "`port`, " +
                        "`type`, " +
                        "`inserted`, " +
                        "`lastchecked`, " +
                        "`status`, " +
                        "`fullanonymous` " +

                        ") VALUES  (" +
                        "0, " +
                        "'"+sanitizeDatabaseInput(proxyInfo.getUrl())+"', " +
                        "'"+sanitizeDatabaseInput(proxyInfo.getPort())+"', " +
                        "'"+sanitizeDatabaseInput(proxyInfo.getType())+"', " +
                        "NOW(), " +
                        "NULL, " +
                        "'pending', " +
                        "'no')";
                //System.out.println(query);

                Statement st = dbConnection.createStatement();
                try
                {
                    st.executeUpdate(query);
                }
                catch (Exception MySQLIntegrityConstraintViolationException)
                {

                }
                st.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    String sanitizeDatabaseInput(String value)
    {
        while (value.contains("''")) {
            value = value.replace("''","'");
        }
        return value.replace("'","''");
    }
}
