package com.tools.proxymity;

import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.collectors.*;
import com.tools.proxymity.phantomjs.PhantomJsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProxyCollectorManager extends Thread
{
    CollectorParameters collectorParameters;
    PhantomJsManager phantomJsManager;
    public ProxyCollectorManager(CollectorParameters collectorParameters)
    {
        this.collectorParameters = collectorParameters;
        this.phantomJsManager = new PhantomJsManager();
        this.phantomJsManager.start();
        collectorParameters.setPhantomJsManager(this.phantomJsManager);
    }

    public ProxyCollectorManager() throws Exception
    {
        throw new Exception("Default constructor disabled");
    }

    public void run()
    {
        try
        {

            List<ProxyCollector> collectors = new ArrayList<ProxyCollector>();


               collectors.add(new InCloakCollector(collectorParameters));
               collectors.add(new HmaCollector(collectorParameters));
               collectors.add(new ProxyListOrgCollector(collectorParameters));
               collectors.add(new SSLProxiesOrgCollector(collectorParameters));
               collectors.add(new SamairRuCollector(collectorParameters));
               collectors.add(new SocksProxyNetCollector(collectorParameters));
               collectors.add(new SocksProxyNetCollector(collectorParameters));
               collectors.add(new FreeProxyListNetCollector(collectorParameters));
               collectors.add(new CoolProxyNetCollector(collectorParameters));
               collectors.add(new XroxyComCollector(collectorParameters));
               collectors.add(new ProxyNovaComCollector(collectorParameters));
               collectors.add(new Socks24OrgCollector(collectorParameters));
               collectors.add(new MrHinkyDinkCollector(collectorParameters));
               collectors.add(new ProxyMooJpCollector(collectorParameters));
               collectors.add(new SocksListNetCollector(collectorParameters));
               collectors.add(new TorVpnComCollector(collectorParameters));
               collectors.add(new GatherproxyCom(collectorParameters));
               collectors.add(new FastproxyserversOrg(collectorParameters));
               collectors.add(new MyproxylistsCom(collectorParameters));
               collectors.add(new Proxies4googleCom(collectorParameters));
               collectors.add(new ProxyFireNet(collectorParameters));
               collectors.add(new CnproxyCom(collectorParameters));
               collectors.add(new ProxyroxComCollector(collectorParameters));
               collectors.add(new enProxyNetPlCollector(collectorParameters));
               collectors.add(new MaxiproxiesComCollector(collectorParameters));
               collectors.add(new UltraProxiesComCollector(collectorParameters));
               collectors.add(new fiftyna50NetCollector(collectorParameters));
               collectors.add(new socks24RuCollector(collectorParameters));
               collectors.add(new theProxyListComCollector(collectorParameters));
               collectors.add(new fineProxyOrgCollector(collectorParameters));
               collectors.add(new freeProxyListsDailyBlogspotInCollector(collectorParameters));
               collectors.add(new UsProxyOrgCollector(collectorParameters));
               collectors.add(new ProxyRssComCollector(collectorParameters)); ;
               collectors.add(new proxzComCollector(collectorParameters));
               collectors.add(new FreeProxyCzCollector(collectorParameters));

                //TODO Many results, lower priority
                collectors.add(new FreePassRuCollector(collectorParameters));

                //TODO To many results only a few good. (8000 and only 60 are good)
                collectors.add(new Socks24OrgCollector(collectorParameters));
                collectors.add(new happyProxyComCollector(collectorParameters));
                collectors.add(new ProxyListMeCollector(collectorParameters));
                collectors.add(new ProxyDbNetCollector(collectorParameters));
                collectors.add(new spysRuCollector(collectorParameters));

                // Problematic
                //collectors.add(new IdcloakComCollector(collectorParameters));


                //seems inactive
                //collectors.add(new ProxyListyCom(collectorParameters));

            //not much success
            //collectors.add(new SpyIpComCollector(collectorParameters));

            Collections.shuffle(collectors);
            for (ProxyCollector collector : collectors)
            {
                collector.start();
            }

            while (true)
            {
                Thread.sleep(15000);
                for (ProxyCollector collector : collectors)
                {
                    collector.writeProxyInfoToDatabase();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
