package com.object0r.tools.proxymity.collectors;

import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UltraProxiesComCollector extends ProxyCollector
{
    public UltraProxiesComCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
        //initializePhantom();
        //initializePhantom();
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            /*driver.get("http://www.ultraproxies.com/");
            WebElement webElement = driver.findElement(By.tagName("body"));
            String page = webElement.getText();*/

            String page = downloadPageWithPhantomJs("http://www.ultraproxies.com/");

            //System.out.println(page);
            Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+: \\d+");
            Matcher m = p.matcher(page);

            while (m.find())
            {
                String line = m.group().replace(" ","");
                StringTokenizer st = new StringTokenizer(line, ":");
                String ip = st.nextToken();
                String port = st.nextToken();
                Integer.parseInt(port);
                ProxyInfo proxyInfo = new ProxyInfo();
                proxyInfo.setHost(ip);
                proxyInfo.setPort(port);
                proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                addProxy(proxyInfo);
                //System.out.println(line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }
}
