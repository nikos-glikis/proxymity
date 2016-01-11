package com.tools.proxymity;

import com.toortools.Utilities;
import com.toortools.tor.TorHelper;

public class Main
{

    public static void main(String[] args)
    {

        DbInformation dbInformation = new DbInformation("localhost","root","toor",3306,"proxies");

        Proxymity proxymity = new Proxymity(dbInformation);
        //proxymity.useTor();

        proxymity.startCheckers();
        proxymity.startCollectors();
    }
}
