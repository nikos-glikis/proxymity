package com.tools.proxymity;

public class Main {

    public static void main(String[] args)
    {

        DbInformation dbInformation = new DbInformation("127.0.0.1","root","toor",3306,"proxies");
        Proxymity proxymity = new Proxymity(dbInformation);
        proxymity.startCheckers();
        proxymity.startCollectors();
    }
}
