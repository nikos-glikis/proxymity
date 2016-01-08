package com.tools.proxymity;

public class ProxyInfo
{
    final static String PROXY_TYPES_SOCKS4 = "socks4";
    final static String PROXY_TYPES_SOCKS5 = "socks5";
    final static String PROXY_TYPES_HTTP = "http";
    final static String PROXY_TYPES_HTTPS = "https";

    private String url;
    private String port;
    private String type;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
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
}
