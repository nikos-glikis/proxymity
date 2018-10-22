package com.object0r.tools.proxymity;

import com.object0r.tools.proxymity.collectors.*;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.phantomjs.PhantomJsManager;
import com.object0r.toortools.DB;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProxyCollectorManager extends Thread
{
    static Logger log = Logger.getLogger(ProxyCollectorManager.class.getName());

    CollectorParameters collectorParameters;
    PhantomJsManager phantomJsManager;
    DB counts = new DB("proxymity", "counts");

    public ProxyCollectorManager(CollectorParameters collectorParameters, boolean useTor)
    {
        this.collectorParameters = collectorParameters;
        this.phantomJsManager = new PhantomJsManager(useTor);
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
            //recheck capthca
            collectors.add(new ProxyMooJpCollector(collectorParameters));
            collectors.add(new SocksListNetCollector(collectorParameters));
            collectors.add(new TorVpnComCollector(collectorParameters));
            collectors.add(new GatherproxyCom(collectorParameters));
            collectors.add(new FastproxyserversOrg(collectorParameters));
            collectors.add(new MyproxylistsCom(collectorParameters));
            collectors.add(new ProxyFireNet(collectorParameters));
            collectors.add(new ProxyroxComCollector(collectorParameters));
            collectors.add(new enProxyNetPlCollector(collectorParameters));
            collectors.add(new MaxiproxiesComCollector(collectorParameters));
            collectors.add(new UltraProxiesComCollector(collectorParameters));
            collectors.add(new fiftyna50NetCollector(collectorParameters));
            collectors.add(new theProxyListComCollector(collectorParameters));
            collectors.add(new fineProxyOrgCollector(collectorParameters));
            collectors.add(new freeProxyListsDailyBlogspotInCollector(collectorParameters));
            collectors.add(new UsProxyOrgCollector(collectorParameters));
            collectors.add(new ProxyRssComCollector(collectorParameters));
            ;
            collectors.add(new proxzComCollector(collectorParameters));
            collectors.add(new FreeProxyCzCollector(collectorParameters));

//            //TODO Many results, lower priority
            collectors.add(new FreePassRuCollector(collectorParameters));
//
//            //TODO To many results only a few good. (8000 and only 60 are good)
            collectors.add(new Socks24OrgCollector(collectorParameters));
            collectors.add(new HappyProxyComCollector(collectorParameters));
            collectors.add(new ProxyListMeCollector(collectorParameters));
            collectors.add(new ProxyDbNetCollector(collectorParameters));
            collectors.add(new spysRuCollector(collectorParameters));
            collectors.add(new KingProxiesCollector(collectorParameters));
            collectors.add(new NnTimeComCollector(collectorParameters));
            collectors.add(new NnTimeComCollector(collectorParameters));
            collectors.add(new SampleCollector(collectorParameters));



            collectors.add(new IpAdressCom(collectorParameters));

            collectors.add(new FreeSocksIn(collectorParameters));
            //collectors.add(new ShodanCollector(collectorParameters));
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
                    int collectorCount = collector.getProxies().size();
                    //log.info(collector.collectorName() + " gave me " + collectorCount);
                    collector.writeProxyInfoToDatabase();
                    updateCounts(collector.collectorName(), collectorCount);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateCounts(String collectorName, int collectorCount)
    {
        setCollectorCount(collectorName, getExistingCount(collectorName) + collectorCount);
    }

    private void setCollectorCount(String collectorName, int count)
    {
        counts.put(collectorName, Integer.toString(count));
    }

    private int getExistingCount(String collectorName)
    {
        String existingCount = counts.get(collectorName);
        int existingCountInt = 0;
        try
        {
            existingCountInt = Integer.parseInt(existingCount);
        }
        catch (Exception e)
        {
            //noop;
        }
        return existingCountInt;
    }
}
