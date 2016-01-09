package com.tools.proxymity;


import com.tools.proxymity.collectors.HmaCollector;
import com.tools.proxymity.collectors.InCloakCollector;
import com.tools.proxymity.collectors.ProxyListOrgCollector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Proxymity
{
    static final public String TABLE_NAME = "proxymity_proxies";
    public Proxymity(DbInformation dbInformation)
    {
        try
        {
            this.dbInformation = dbInformation;

            connectToDatabase();
            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SHOW TABLES");

            boolean foundProxiesTable = false;
            while (rs.next())
            {
                if (rs.getString(1).equals(TABLE_NAME))
                {
                    foundProxiesTable = true;
                }
            }
            st.close();

            if (!foundProxiesTable)
            {
                throw new Exception("Proxies table doesn't exist.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Something went wrong, probably with the database. Check that database exists and that credentials are valid.");
            System.exit(0);
        }

    }

    private void connectToDatabase()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            dbConnection = DriverManager
                    .getConnection("jdbc:mysql://"+dbInformation.getUrl()+":"+dbInformation.getPort()+"/"+dbInformation.getDatabase()+"?"
                            + "user="+dbInformation.getUsername()+"&password="+dbInformation.getPassword());

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
            //new HmaCollector(dbConnection).start();
            //new InCloakCollector(dbConnection).start();
            //new ProxyListOrgCollector(dbConnection).start();

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
        new ProxyCheckerManager(dbConnection).start();;
    }
}
