package com.tools.proxymity;

public class DbInformation
{
    DbInformation(String url, String username, String password,  int port, String database)
    {
        this.username = username;
        this.password = password;
        this.database = database;
        this.url = url;
        this.port = port;
    }

    private String username;
    private String password;
    private String url;
    private int port;
    private String database;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
