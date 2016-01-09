package com.tools.proxymity;

public class ProxyInfo
{
    final static public String PROXY_TYPES_SOCKS4 = "socks4";
    final static public String PROXY_TYPES_SOCKS5 = "socks5";
    final static public String PROXY_TYPES_HTTP = "http";
    final static public String PROXY_TYPES_HTTPS = "https";

    private String id;
    private String host;
    private String port;
    private String type;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type) throws Exception
    {
        if (
                !type.equals(ProxyInfo.PROXY_TYPES_HTTP) &&
                !type.equals(ProxyInfo.PROXY_TYPES_HTTPS) &&
                !type.equals(ProxyInfo.PROXY_TYPES_SOCKS4) &&
                !type.equals(ProxyInfo.PROXY_TYPES_SOCKS5)
                )
        {
            throw new Exception("Unknown Proxy Type");
        }
        this.type = type;
    }

    public String toString()
    {
        return host + ":"+port+"/"+type;
    }
}
