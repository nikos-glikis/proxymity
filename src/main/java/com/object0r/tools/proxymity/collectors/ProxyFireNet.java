package com.object0r.tools.proxymity.collectors;


import com.object0r.tools.proxymity.ProxyCollector;
import com.object0r.tools.proxymity.datatypes.CollectorParameters;
import com.object0r.tools.proxymity.datatypes.ProxyInfo;
import com.object0r.toortools.Utilities;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyFireNet extends ProxyCollector
{
    public ProxyFireNet(CollectorParameters collectorParameters)
    {
        super(collectorParameters);
        setSleepSecondsBetweenScans(600);
    }

    public Vector<ProxyInfo> collectProxies()
    {
        try
        {
            processPorts();
            processLink("http://www.proxyfire.net/index.php?pageid=eliteproxylist", ProxyInfo.PROXY_TYPES_HTTP);
            processLink("http://www.proxyfire.net/index.php?pageid=anonymousproxylist", ProxyInfo.PROXY_TYPES_HTTP);
            processLink("http://www.proxyfire.net/index.php?pageid=socks4proxylist", ProxyInfo.PROXY_TYPES_SOCKS4);
            processLink("http://www.proxyfire.net/index.php?pageid=socks5proxylist", ProxyInfo.PROXY_TYPES_SOCKS5);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getProxies();
    }

    @Override
    protected String collectorName()
    {
        return "proxyfire.net";
    }

    private void processLink(String url, String type)
    {
        try
        {
            String page = Utilities.readUrl(url);
            Pattern p = Pattern.compile("<tr.*?</tr>",Pattern.DOTALL);
            Matcher m = p.matcher(page);
            //System.out.println(page);
            while (m.find())
            {
                String line = m.group();
                if (line.contains("<img border=0 src="))
                {
                    try
                    {

                        String ipUrl = "http://www.proxyfire.net"+Utilities.cut("<img border=0 src=","></td><td>", line);
                        String tempFilename = "tmp/ProxyFireNet.jpg";
                        String finalFilename = "tmp/ProxyFireNet.pnm";
                        saveUrl(tempFilename,ipUrl);
                        convertImageToPnmDark(tempFilename, finalFilename);
                        String ip =ocrImage(finalFilename).replace("_",".").replace("....",".").replace("...",".").replace("..",".");
                        ip = StringUtils.stripStart(ip, ".");
                        ip = StringUtils.stripEnd(ip, ".");
                        String port = Utilities.cut("</td><td>","<", line);
                        //System.out.println(port);
                        Integer.parseInt(port);

                        ProxyInfo proxyInfo = new ProxyInfo();
                        proxyInfo.setHost(ip);
                        proxyInfo.setPort(port);
                        proxyInfo.setType(type);
                        addProxy(proxyInfo);

                    }
                    catch (Exception e)
                    {
                        System.out.println(e);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void saveUrl(final String filename, final String urlString)
            throws MalformedURLException, IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            URLConnection conn = new URL(urlString).openConnection();
            conn.setRequestProperty("Referer","http://www.proxyfire.net/index.php?pageid=socks4proxylist");
            conn.setRequestProperty("Cookie","bbsessionhash=f89e8d4faeed7852c1444c1fee64c5b9; bblastvisit=1452802123; bblastactivity=0; HstCfa282282=1452802137558; HstCla282282=1452803822156; HstCmu282282=1452802137558; HstPn282282=32; HstPt282282=32; HstCnv282282=1; HstCns282282=2; _ga=GA1.2.867836857.1452802138; bbthread_lastview=9b4187c8f0c1a261cfc283a6dc4c112da-5-%7Bi-74228_i-1452505091_i-33_i-1200671869_i-74044_i-1449946292_i-74082_i-1450419351_i-74220_i-1452405073_%7D; __unam=1166a61-15241c17f96-2282b55c-8");

            in = new BufferedInputStream(conn.getInputStream());

            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    private void processPorts()
    {
        try
        {
            String page = readMain("http://www.proxyfire.net/forum/forumdisplay.php?f=14",0);

            Pattern p = Pattern.compile("showthread\\.php\\?[^\"]*\"");
            Matcher m = p.matcher(page);

            while (m.find())
            {
                String url = "http://www.proxyfire.net/forum/"+m.group().replace("\"","");
                if (url.contains("&amp;") && url.contains("?s=")) {
                    String toCut = Utilities.cut("?s=", "&amp;", url);
                    url = url.replace(toCut, "").replace("s=&amp;", "");
                    page = readMain(url,0);
                    Pattern pp = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+");
                    Matcher mm = pp.matcher(page);
                    while (mm.find()) {
                        String line = mm.group();
                        StringTokenizer st = new StringTokenizer(line, ":");
                        String ip = st.nextToken();
                        String port = st.nextToken();
                        Integer.parseInt(port);

                        ProxyInfo proxyInfo = new ProxyInfo();
                        proxyInfo.setHost(ip);
                        proxyInfo.setPort(port);
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                        addProxy(proxyInfo);

                        proxyInfo = new ProxyInfo();
                        proxyInfo.setHost(ip);
                        proxyInfo.setPort(port);
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                        addProxy(proxyInfo);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String readMain(String url, int count)
    {
        try {
            String page =anonReadUrl(url);
            return page;
        }
        catch (Exception e) {
            if (e.toString().contains("IOException: Server returned HTTP resp")) {
                if (count < 30) {
                    return readMain(url, count++);
                } else {
                    e.printStackTrace();
                    return "";
                }
            }
        }
        return "";
    }
}
