package com.tools.proxymity.datatypes;


import java.sql.Connection;

public class CollectorParameters
{
    Connection dbConnection;
    boolean useTor;
    int sleepBetweenScansSeconds;

    public int getSleepBetweenScansSeconds() {
        return sleepBetweenScansSeconds;
    }

    public void setSleepBetweenScansSeconds(int sleepBetweenScansSeconds) {
        this.sleepBetweenScansSeconds = sleepBetweenScansSeconds;
    }

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
