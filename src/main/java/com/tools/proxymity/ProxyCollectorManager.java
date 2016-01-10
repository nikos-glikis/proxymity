package com.tools.proxymity;

import com.tools.proxymity.collectors.*;

import java.sql.Connection;
import java.util.Vector;

public class ProxyCollectorManager extends Thread
{
    public Connection dbConnection;

    public ProxyCollectorManager(Connection connection)
    {
        this.dbConnection = connection;
    }

    public ProxyCollectorManager() throws Exception
    {
        throw new Exception("Default constructor disabled");
    }

    public void run()
    {
        try
        {
            Vector<ProxyCollector> collectors = new Vector<ProxyCollector>();

            collectors.add(new InCloakCollector(dbConnection));
            collectors.add(new HmaCollector(dbConnection));
            collectors.add(new ProxyListOrgCollector(dbConnection));
            collectors.add(new ProxyListOrgCollector(dbConnection));
            collectors.add(new SSLProxiesOrgCollector(dbConnection));
            collectors.add(new SamairRuCollector(dbConnection));
            collectors.add(new SocksProxyNetCollector(dbConnection));
            collectors.add(new SocksProxyNetCollector(dbConnection));
            collectors.add(new FreeProxyListNetCollector(dbConnection));
            collectors.add(new CoolProxyNetCollector(dbConnection));
            collectors.add(new SocksListNetCollector(dbConnection));
            collectors.add(new XroxyComCollector(dbConnection));
            collectors.add(new ProxyNovaComCollector(dbConnection));

            for (ProxyCollector collector : collectors) {
                collector.start();
            }

            while (true) {
                Thread.sleep(15000);
                for (ProxyCollector collector : collectors) {
                    collector.writeProxyInfoToDatabase();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
