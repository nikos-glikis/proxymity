package com.tools.proxymity.phantomjs;


import com.tools.proxymity.Proxymity;

import java.util.HashMap;
import java.util.Map;

public class PhantomJsManager extends Thread
{
    public PhantomJsManager(boolean useTor)
    {
        this.useTor = useTor;
    }
    HashMap<String, PhantomJsJob> jobs = new HashMap<String, PhantomJsJob>();
    boolean useTor = false;
    synchronized public PhantomJsJob addJob(String url)
    {
        if (jobs.containsKey(url))
        {
            return jobs.get(url);
        }
        else
        {
            PhantomJsJob phantomJsJob = new PhantomJsJob(url);
            jobs.put(url,  phantomJsJob);
            return phantomJsJob;
        }
    }

    synchronized public PhantomJsJob addJob(String url, String body)
    {
        if (jobs.containsKey(url))
        {
            return jobs.get(url);
        }
        else
        {
            PhantomJsJob phantomJsJob = new PhantomJsJob(url, body);
            jobs.put(url,  phantomJsJob);
            return phantomJsJob;
        }
    }

    public void run()
    {
        for (int i = 0; i< Proxymity.PHANTOM_JS_WORKERS_COUNT; i++)
        {
            new PhantomJsWorker(this, useTor).start();
            try { Thread.sleep(1000); } catch (Exception e) { }
        }

        while (true)
        {
            try
            {
                Thread.sleep(20000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Similar to pop.
     * @return
     */
    synchronized PhantomJsJob getNextJob()
    {
        for (Map.Entry<String, PhantomJsJob> entry : jobs.entrySet())
        {
            String url = entry.getKey();
            PhantomJsJob phantomJsJob = entry.getValue();
            if (phantomJsJob.isPending())
            {
                phantomJsJob.setStatusProcessing();
                return phantomJsJob;
            }
        }

        return null;
    }
}
