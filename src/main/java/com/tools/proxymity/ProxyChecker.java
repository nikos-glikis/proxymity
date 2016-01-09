package com.tools.proxymity;

import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.Scanner;

public class ProxyChecker implements Runnable
{
    Proxy proxy;
    Connection dbConnection;

    ProxyChecker(Proxy proxy, Connection dbConnection)
    {
        this.proxy = proxy;
        this.dbConnection = dbConnection;
    }

    public void run()
    {
        try
        {
            //http://cpanel.com/showip.shtml
            URLConnection conn = new URL("http://cpanel.com/showip.shtml").openConnection(proxy);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            Scanner sc = new Scanner(conn.getInputStream());
            StringBuffer sb = new StringBuffer();
            while (sc.hasNext())
            {
                sb.append(sc.nextLine());
            }
            System.out.println("Ny ip is: "+sb.toString());
            //TODO if all good save in database.


        }
        catch (Exception e)
        {
            System.out.print("e");
            //e.printStackTrace();
        }
    }
}
