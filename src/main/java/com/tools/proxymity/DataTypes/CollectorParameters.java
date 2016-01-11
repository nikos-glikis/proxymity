package com.tools.proxymity.DataTypes;


import java.sql.Connection;

public class CollectorParameters
{
    Connection dbConnection;
    boolean useTor;

    public Connection getDbConnection()
    {
        return dbConnection;
    }

    public void setDbConnection(Connection dbConnection)
    {
        this.dbConnection = dbConnection;
    }

    public boolean isUseTor()
    {
        return useTor;
    }

    public void setUseTor(boolean useTor)
    {
        this.useTor = useTor;
    }
}
