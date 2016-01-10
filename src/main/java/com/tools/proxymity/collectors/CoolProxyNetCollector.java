package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.ProxyInfo;
import com.toortools.Utilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.sql.Connection;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoolProxyNetCollector extends ProxyCollector
{


    public CoolProxyNetCollector(Connection dbConnection)
    {
        super(dbConnection);

    }

    public Vector<ProxyInfo> collectProxies()
    {
        Vector<ProxyInfo> proxies = new Vector<ProxyInfo>();
        try
        {

            for (int i = 1; i<50; i++)
            {
                  String page = Utilities.readUrl("http://www.cool-proxy.net/proxies/http_proxy_list/sort:score/direction:desc/page:"+i);
                Pattern p = Pattern.compile("");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return proxies;
    }
}
