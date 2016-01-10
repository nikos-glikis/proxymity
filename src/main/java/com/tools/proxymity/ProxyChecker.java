package com.tools.proxymity;

import com.toortools.Utilities;

import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyChecker implements Runnable
{
    static String myIp;
    Proxy proxy;
    Connection dbConnection;

    ProxyChecker(Proxy proxy, Connection dbConnection)
    {
        this.proxy = proxy;
        this.dbConnection = dbConnection;
    }

    public void setMyIp()
    {
        try
        {
            //TODO check ip format
            myIp = Utilities.readUrl("http://cpanel.com/showip.shtml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void run()
    {
        try
        {
            Thread.sleep(new Random().nextInt(3000));

            URLConnection conn = new URL("http://cpanel.com/showip.shtml").openConnection(proxy);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            Scanner sc = new Scanner(conn.getInputStream());
            StringBuffer sb = new StringBuffer();
            while (sc.hasNext())
            {
                sb.append(sc.nextLine());
            }
            String ip = sb.toString().trim();
            Pattern p = Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+$");
            Matcher m = p.matcher(ip);

            if (ip.length() > 13 || ip.length() < 5 || !m.find())
            {
                throw new Exception("Invalid Ip returned.");
            }

            if (ip.equals(myIp))
            {
                System.out.println("Proxy Not Anonymous.");
            }
            else
            {
                System.out.println("My ip is: "+ip);
            }
            //TODO if all good save in database.
        }
        catch (Exception e)
        {
            //System.out.print("e");
            //e.printStackTrace();
        }
    }
}
