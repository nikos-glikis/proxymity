package com.tools.proxymity.collectors;

import com.tools.proxymity.ProxyCollector;
import com.tools.proxymity.ProxyInfo;
import com.toortools.Utilities;
import com.toortools.os.OsHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InCloakCollector extends ProxyCollector
{
    public InCloakCollector(Connection dbConnection)
    {
        super(dbConnection);

    }

    public Vector<ProxyInfo> collectProxies()
    {
        Vector<ProxyInfo> proxies = new Vector<ProxyInfo>();
        try
        {
            String page = Utilities.readUrl("https://incloak.com/proxy-list/");

            Pattern p = Pattern.compile("<tr><td class=tdl.*?</tr>");
            Matcher m = p.matcher(page);

            while (m.find())
            {
                try
                {
                    String line = m.group();
                    //System.out.println(line);
                    String ip = Utilities.cut("class=tdl>","<", line).trim();
                    //System.out.println("Ip: "+ ip);
                    String portImage = "https://incloak.com"+Utilities.cut("<img src=\"","\"", line).trim();
                    //System.out.println("PortImage: "+ portImage);
                    Utilities.downloadFile(portImage, "tmp/image.gif");

                    convertImageToPnm("tmp/image.gif", "tmp/image.pnm");
                    //System.out.println(ocrImage("tmp/image.pnm"));
                    String portText = ocrImage("tmp/image.pnm").trim().replace("O","0").replace("o","0");
                    //System.out.println("-"+portText+"-");
                    int port = 0;
                    try
                    {
                        port = Integer.parseInt(portText);
                    }
                    catch (Exception e)
                    {
                        //System.out.println("Con "+ portText);
                        continue;
                    }

                    String typeText = Utilities.cut("</div></div></td><td>","<", line).trim();

                    ProxyInfo proxyInfo = new ProxyInfo();
                    proxyInfo.setHost(ip);
                    proxyInfo.setPort(portText);
                    if (typeText.equals("SOCKS5"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                    }
                    else if (typeText.equals("SOCKS4"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS4);
                    }
                    else if (typeText.equals("HTTP"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    }
                    else if (typeText.equals("HTTP, HTTPS"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTP);
                    }
                    else if (typeText.equals("SOCKS4, SOCKS5"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_SOCKS5);
                    }
                    else if (typeText.equals("HTTPS"))
                    {
                        proxyInfo.setType(ProxyInfo.PROXY_TYPES_HTTPS);
                    }
                    else
                    {
                        System.out.println("Type: "+typeText);
                        continue;
                    }

                    proxies.add(proxyInfo);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    System.exit(0);
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return proxies;
    }

    private String ocrImage(String filename)
    {
        String text = "";
        try
        {
            String gocrPath = "gocr";
            String command;
            if (OsHelper.isWindows())
            {
                gocrPath = "bin/gocr049.exe";
            }
            command = gocrPath + " -C \"0123456789\" " + filename;
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line=null;
            StringBuffer sb = new StringBuffer();
            while((line=input.readLine()) != null)
            {
                sb.append(line);
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return text;
    }

    private void convertImageToPnm(String inputFilename, String outputFilename )
    {
        try
        {
            Runtime rt = Runtime.getRuntime();
            String command;
            String convertPath = "convert";
            if (OsHelper.isWindows())
            {
                convertPath = "C:\\Program Files\\ImageMagick-6.9.3-Q16\\convert.exe";

            }

            command = convertPath+ " \"" +  inputFilename + "\" \"" +outputFilename+"\"";
             Process pr = rt.exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line=null;

            while((line=input.readLine()) != null) {
                System.out.println(line);
            }

            input = new BufferedReader(new InputStreamReader(pr.getErrorStream()));

            line=null;

            while((line=input.readLine()) != null) {
                System.out.println(line);
            }
            //System.out.println(pr.waitFor());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
