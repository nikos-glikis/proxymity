package com.tools.proxymity;


import com.toortools.Utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringTokenizer;

public class Proxymity
{
    static final public String TABLE_NAME = "proxymity_proxies";
    public static final int RECHECK_INTERVAL_MINUTES = 10;

    public Proxymity(DbInformation dbInformation)
    {
        try
        {
            this.dbInformation = dbInformation;

            connectToDatabase();
            if (!isTableInDatabase(dbConnection, TABLE_NAME))
            {
                Statement st = dbConnection.createStatement();
                String dbSchema = Utilities.readFile("dbSchema.sql");
                StringTokenizer tokenizer = new StringTokenizer(dbSchema, ";");
                while (tokenizer.hasMoreTokens())
                {
                    st.execute(tokenizer.nextToken());
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
            try
            {
                dbConnection = DriverManager
                        .getConnection("jdbc:mysql://"+dbInformation.getUrl()+":"+dbInformation.getPort()+"/?"
                                + "user="+dbInformation.getUsername()
                                +"&password="+dbInformation.getPassword());
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
                        .getConnection("jdbc:mysql://"+dbInformation.getUrl()+":"+dbInformation.getPort()+"/"+dbInformation.getDatabase()+"?"
                                + "user="+dbInformation.getUsername()
                                +"&password="+dbInformation.getPassword());
            }
            catch (Exception e)
            {
                System.out.println("Cannot connect to database, probably the database does not exist, I will try creating it.");
                System.out.println(e.toString());
                try
                {
                    dbConnection = DriverManager
                            .getConnection("jdbc:mysql://"+dbInformation.getUrl()+":"+dbInformation.getPort()+"/?"
                                    + "user="+dbInformation.getUsername()
                                    +"&password="+dbInformation.getPassword());
                    Statement st = dbConnection.createStatement();
                    String query = "CREATE DATABASE "+dbInformation.getDatabase();

                    st.execute(query);
                    System.out.println(query);

                    st.close();
                    dbConnection.close();

                    dbConnection = DriverManager
                            .getConnection("jdbc:mysql://"+dbInformation.getUrl()+":"+dbInformation.getPort()+"/"+dbInformation.getDatabase()+"?"
                                    + "user="+dbInformation.getUsername()
                                    +"&password="+dbInformation.getPassword());

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
            new ProxyCollectorManager(dbConnection).start();
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

    public boolean isTableInDatabase(Connection connection, String table )
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
