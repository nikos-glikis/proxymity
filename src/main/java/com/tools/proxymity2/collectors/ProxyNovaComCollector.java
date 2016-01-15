package com.tools.proxymity.collectors;

import com.tools.proxymity.DataTypes.CollectorParameters;
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

public class ProxyNovaComCollector extends ProxyCollector
{
    public ProxyNovaComCollector(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            String codes[] = getCountryCodes();

            for (String code: codes)
            {
                String page = Utilities.readUrl("http://www.proxynova.com/proxy-server-list/country-"+code+"/");
                Pattern p = Pattern.compile("<tr>.*?</tr>",Pattern.DOTALL);

                Matcher m = p.matcher(page);

                while (m.find())
                {
                    String line = m.group();
                    if (line.contains("row_proxy_ip\""))
                    {
                        String ip = Utilities.cut("\"row_proxy_ip\">","<",line);

                        Pattern pp = Pattern.compile("<td align=\"left\">.*?</td>",Pattern.DOTALL);
                        Matcher mm = pp.matcher(line);
                        mm.find();
                        mm.find();
                        String portText = mm.group();
                        pp = Pattern.compile("\\d+");
                        mm = pp.matcher(portText);
                        mm.find();
                        String port = mm.group().trim();
                        Integer.parseInt(port);
                        ProxyInfo proxyInfo = new ProxyInfo();
                        proxyInfo.setPort(port);
                        proxyInfo.setHost(ip);
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                        addProxy(proxyInfo);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }

    public String[] getCountryCodes()
    {
        String[] countries = new String[103];

        String codes =  "ae\n" +
                "al\n" +
                "am\n" +
                "ar\n" +
                "at\n" +
                "au\n" +
                "az\n" +
                "ba\n" +
                "bd\n" +
                "be\n" +
                "bf\n" +
                "bg\n" +
                "bo\n" +
                "br\n" +
                "by\n" +
                "ca\n" +
                "cd\n" +
                "ch\n" +
                "ck\n" +
                "cl\n" +
                "cm\n" +
                "cn\n" +
                "co\n" +
                "cy\n" +
                "cz\n" +
                "de\n" +
                "dk\n" +
                "do\n" +
                "ec\n" +
                "ee\n" +
                "eg\n" +
                "es\n" +
                "eu\n" +
                "fi\n" +
                "fr\n" +
                "gb\n" +
                "ge\n" +
                "gh\n" +
                "hk\n" +
                "hr\n" +
                "hu\n" +
                "id\n" +
                "il\n" +
                "in\n" +
                "iq\n" +
                "ir\n" +
                "is\n" +
                "it\n" +
                "jm\n" +
                "jp\n" +
                "ke\n" +
                "kh\n" +
                "kr\n" +
                "kz\n" +
                "lb\n" +
                "lr\n" +
                "lt\n" +
                "lu\n" +
                "lv\n" +
                "ma\n" +
                "md\n" +
                "mg\n" +
                "mm\n" +
                "mn\n" +
                "mv\n" +
                "mx\n" +
                "my\n" +
                "mz\n" +
                "ng\n" +
                "nl\n" +
                "no\n" +
                "np\n" +
                "nz\n" +
                "pe\n" +
                "ph\n" +
                "pk\n" +
                "pl\n" +
                "pr\n" +
                "pt\n" +
                "py\n" +
                "ro\n" +
                "rs\n" +
                "ru\n" +
                "sa\n" +
                "se\n" +
                "sg\n" +
                "si\n" +
                "sk\n" +
                "sr\n" +
                "th\n" +
                "tn\n" +
                "tr\n" +
                "tw\n" +
                "tz\n" +
                "ua\n" +
                "us\n" +
                "uy\n" +
                "ve\n" +
                "vn\n" +
                "ye\n" +
                "za\n" +
                "zm\n" +
                "zw";

        Scanner sc = new Scanner(codes);

        int i =0;

        while (sc.hasNext())
        {
            countries[i++] = sc.next();
        }

        return countries;

    }
}
