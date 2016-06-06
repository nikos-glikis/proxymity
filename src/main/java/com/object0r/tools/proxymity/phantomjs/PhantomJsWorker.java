package com.object0r.tools.proxymity.phantomjs;


import com.object0r.tools.proxymity.Proxymity;
import com.object0r.toortools.Utilities;
import com.object0r.toortools.os.OsHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class PhantomJsWorker extends Thread
{
    protected PhantomJSDriver driver;
    PhantomJsManager phantomJsManager;
    String uuid = UUID.randomUUID().toString();
    boolean useTor = false;
    public PhantomJsWorker(PhantomJsManager phantomJsManager, boolean useTor)
    {
        this.phantomJsManager = phantomJsManager;
        this.useTor = useTor;
        initializePhantom();
    }

    public void initializePhantom()
    {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        //Utilities.readUrl("http://proxylist.hidemyass.com/2#listable");

        Capabilities caps = new DesiredCapabilities();
        String[] phantomArgs = new  String[] {
                "--webdriver-loglevel=NONE",
               // enable for tor.
                // "--proxy=127.0.0.1:9050",
               // "--proxy-type=socks5"
        };
        if (useTor)
        {
            phantomArgs = new  String[] {
                    "--webdriver-loglevel=NONE",
                    // enable for tor.
                     "--proxy=127.0.0.1:9050",
                     "--proxy-type=socks5"
            };
        }
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0";

        if (OsHelper.isWindows())
        {
            ((DesiredCapabilities) caps).setJavascriptEnabled(true);
            //((DesiredCapabilities) caps).setJavascriptEnabled(true);
            //((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
            ((DesiredCapabilities) caps).setCapability("phantomjs.content.settings.resourceTimeout", 3000);
            ((DesiredCapabilities) caps).setCapability(
                    PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                    "bin\\phantomjs.exe"
            );
            ((DesiredCapabilities) caps).setCapability(
                    PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs
            );
            ((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", userAgent);
        }
        else
        {
            ((DesiredCapabilities) caps).setJavascriptEnabled(true);
            //((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
            ((DesiredCapabilities) caps).setCapability("phantomjs.content.settings.resourceTimeout", 3000);
            ((DesiredCapabilities) caps).setCapability(
                    PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs
            );
            ((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", userAgent);
        }

        driver = new PhantomJSDriver(caps);
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(Proxymity.PHANTOM_JS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(Proxymity.PHANTOM_JS_TIMEOUT_SECONDS, TimeUnit.SECONDS);

    }

    public void run()
    {
        while (true)
        {

                PhantomJsJob phantomJsJob = phantomJsManager.getNextJob();
                while (phantomJsJob == null)
                {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    phantomJsJob = phantomJsManager.getNextJob();
                    //System.out.println("Job is null");
                }
            processJob(phantomJsJob);
        }
    }

    public void processJob(PhantomJsJob phantomJsJob)
    {
        processJob(phantomJsJob, 1);
    }

    public void processJob(PhantomJsJob phantomJsJob, int count)
    {
        try
        {
            try
            {
                phantomJsJob.setStatusProcessing();
                //TODO download with phantomjs
                String url = phantomJsJob.getUrl();
                try
                {
                    if (phantomJsJob.isRequestGet())
                    {
                        driver.get(url);
                    }
                    else if (phantomJsJob.isRequestPost())
                    {
                        HashMap<String, String> postParameters = phantomJsJob.getPostParameters();
                        String form= "<html><head></head><body><form id =\"form1\" action=\""+phantomJsJob.getUrl()+"\" method=\"post\">\n" +
                                "<input type=\"submit\" value=\"Submit\">";

                        for (Map.Entry<String, String> entry : postParameters.entrySet())
                        {

                            String key = entry.getKey();
                            String value = entry.getValue();

                            form+="    <input type=\"hodden\" name=\""+key+"\" value=\""+value+"\">\n";
                        }
                        form +="</form></body></html>";

                        if (!new File("tmp").isDirectory())
                        {
                            new File("tmp").mkdir();
                        }
                        String tmpFilename = "tmp/"+uuid+".html";
                        PrintWriter pr = new PrintWriter(new FileOutputStream(tmpFilename));
                        pr.print(form);
                        pr.close();

                        driver.get("file:///"+(new File(tmpFilename).getAbsolutePath()));


                        WebElement element = driver.findElement(By.id("form1"));
                        element.submit();
                    }
                }
                catch (org.openqa.selenium.TimeoutException e)
                {
                    //We can ignore timeout Exception.
                        /*phantomJsJob.setStatusFailed();
                        phantomJsJob.setException(e);
                        e.printStackTrace();*/

                }

                //driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");

                WebElement webElement = driver.findElement(By.tagName("body"));

                PhantomJsJobResult phantomJsJobResult = new PhantomJsJobResult();
                phantomJsJobResult.setContent(webElement.getText());
                phantomJsJobResult.setSourceCode(driver.getPageSource());
                phantomJsJob.setPhantomJsJobResult(phantomJsJobResult);

                //String page = Utilities.readUrl(phantomJsJob.getUrl());
                //phantomJsJob.setContent(page);
                phantomJsJob.setStatusSuccess();
            }
            catch (Exception e)
            {
                //if (e.toString().contains("The driver server has unexpectedly died"))
               // {
                    try { driver.quit(); } catch (Exception ee) { System.out.println(ee); }
                    initializePhantom();
                //}
                if (count <2)
                {
                    processJob(phantomJsJob, count+1);
                    return;
                }
                phantomJsJob.setStatusFailed();
                phantomJsJob.setException(e);
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
