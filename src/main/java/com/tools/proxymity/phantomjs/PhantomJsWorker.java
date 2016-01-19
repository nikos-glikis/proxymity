package com.tools.proxymity.phantomjs;


import com.tools.proxymity.Proxymity;
import com.toortools.Utilities;
import com.toortools.os.OsHelper;
import org.openqa.jetty.html.Form;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class PhantomJsWorker extends Thread
{
    protected PhantomJSDriver driver;
    PhantomJsManager phantomJsManager;
    public PhantomJsWorker(PhantomJsManager phantomJsManager)
    {
        this.phantomJsManager = phantomJsManager;
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

        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0";

        if (OsHelper.isWindows())
        {
            ((DesiredCapabilities) caps).setJavascriptEnabled(true);
            ((DesiredCapabilities) caps).setJavascriptEnabled(true);
            ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
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
            ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
            ((DesiredCapabilities) caps).setCapability("phantomjs.content.settings.resourceTimeout", 3000);
            ((DesiredCapabilities) caps).setCapability(
                    PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs
            );
            ((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", userAgent);
        }

        driver = new PhantomJSDriver(caps);
        driver.manage().timeouts()
                .implicitlyWait(3, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(Proxymity.PHANTOM_JS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(Proxymity.PHANTOM_JS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void run()
    {
        while (true)
        {
            try
            {
                PhantomJsJob phantomJsJob = phantomJsManager.getNextJob();
                while (phantomJsJob == null)
                {
                    Thread.sleep(2000);
                    phantomJsJob = phantomJsManager.getNextJob();
                    //System.out.println("Job is null");
                }

                try
                {
                    phantomJsJob.setStatusProcessing();
                    //TODO download with phantomjs
                    String url = phantomJsJob.getUrl();
                    try
                    {
                        driver.get(url);
                        try
                        {
                            //TODO remove this, for spys.ru
                            //TODO Must find a way to send post or send custom code.
                            //TODO probably Callable.
                            Select dropdown = new Select(driver.findElement(By.name("xpp")));

                            dropdown.selectByVisibleText("200");
                            //driver.executeScript("document.getElementById('xpp').setAttribute('value', '4')");
                            WebElement form = driver.findElement(By.tagName("form"));
                            form.submit();
                        }
                        catch (Exception e)
                        {
                            //e.printStackTrace();
                        }

                    }
                    catch (org.openqa.selenium.TimeoutException e)
                    {
                        //We can ignore timeout Exception.
                        /*phantomJsJob.setStatusFailed();
                        phantomJsJob.setException(e);
                        e.printStackTrace();*/
                        e.printStackTrace();
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
}
