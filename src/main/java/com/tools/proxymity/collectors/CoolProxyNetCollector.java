package com.tools.proxymity.collectors;

import com.tools.proxymity.datatypes.CollectorParameters;
import com.tools.proxymity.datatypes.ProxyInfo;
import com.tools.proxymity.ProxyCollector;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoolProxyNetCollector extends ProxyCollector
{


    public CoolProxyNetCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }
    
    public Vector<ProxyInfo> collectProxies()
    {
        try
        {

            for (int i = 1; i<50; i++)
            {
                String url = "http://www.cool-proxy.net/proxies/http_proxy_list/sort:score/direction:desc/page:"+i;
                String page = downloadPageWithPhantomJs(url);
               /* driver.get(url);
                WebElement body = driver.findElement(By.tagName("Body"));
                String page = body.getText();*/

                Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+ \\d+", Pattern.DOTALL);
                boolean foundAtLeastOne = false;
                Matcher m = p.matcher(page);
                while (m.find())
                {
                    foundAtLeastOne = true;
                    String line = m.group();
                    //System.out.println(line);
                    ProxyInfo proxyInfo = new ProxyInfo();
                    StringTokenizer st = new StringTokenizer(line, " ");
                    proxyInfo.setHost(st.nextToken());
                    String port = st.nextToken();
                    Integer.parseInt(port);
                    proxyInfo.setPort(port);
                    proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    addProxy(proxyInfo);
                }
                if (!foundAtLeastOne)
                {
                    break;
                }
            }
        }
        /*catch (FileNotFoundException e)
        {
            return proxies;
        }*/
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }

    String strRot13(String string)
    {

        try
        {
            StringBuffer sb = new StringBuffer();
            for (int i = 0 ; i<string.length(); i++)
            {
                char c = string.charAt(i);
                if (
                        (c>='a' && c <='z')
                        || (c>='A' && c <='Z')
                        )
                {
                    //System.out.print(c);
                    if ((c > 'a' && c<'n') || (c > 'z' && c<'N'))
                    {
                        int charInt = (int)c;
                        c = Character.toChars(charInt+13)[0];
                    }
                    else
                    {
                        int charInt = (int)c;
                        c = Character.toChars(charInt-13)[0];
                    }
                    sb.append(c);
                }
                else
                {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
