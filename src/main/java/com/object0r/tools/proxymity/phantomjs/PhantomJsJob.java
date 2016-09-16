package com.object0r.tools.proxymity.phantomjs;

import java.util.HashMap;
import java.util.StringTokenizer;

public class PhantomJsJob
{
    final static String STATUS_PENDING = "pending";
    final static String STATUS_PROCESSING = "processing";
    final static String STATUS_SUCCESS = "success";
    final static String STATUS_FAILED = "failed";
    final static String REQUEST_ACTION_POST = "POST";
    final static String REQUEST_ACTION_GET = "GET";

    final static String ELEMENT_TYPES_TAG_NAME = "tag_name";
    final static String ELEMENT_TYPES_CLASS = "class";

    static String elementType = ELEMENT_TYPES_TAG_NAME;
    static String elementName = "body";

    public static void setElementTypeTagName()
    {
        elementType = ELEMENT_TYPES_TAG_NAME;
    }

    public static void setElementTypeClass()
    {
        elementType = ELEMENT_TYPES_CLASS;
    }

    public boolean isElementTypeTagName()
    {
        return elementName.equals(ELEMENT_TYPES_TAG_NAME);
    }

    public boolean isElementTypeClass()
    {
        return elementName.equals(ELEMENT_TYPES_CLASS);
    }


    public static void setElementName(String name)
    {
        elementName = name;
    }

    public String getElementName()
    {
        return elementName;
    }


    String url;
    PhantomJsJobResult phantomJsJobResult;
    String request = REQUEST_ACTION_GET;
    String status = STATUS_PENDING;
    Exception exception;

    public HashMap<String, String> postParameters = new HashMap<String, String>();

    public void addPostParameter(String key, String value)
    {
        postParameters.put(key, value);
    }

    public HashMap<String, String> getPostParameters()
    {
        return postParameters;
    }

    public PhantomJsJob(String url)
    {
        this.setUrl(url);
    }

    public PhantomJsJob(String url, String postBody)
    {
        this.setUrl(url);
        StringTokenizer st = new StringTokenizer(postBody, "&");
        while (st.hasMoreTokens())
        {
            String line = st.nextToken();
            if (line.contains("="))
            {
                StringTokenizer st2 = new StringTokenizer(line, "=");
                addPostParameter(st2.nextToken(), st2.nextToken());
            }
        }
        this.setRequestPost();
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public PhantomJsJobResult getPhantomJsJobResult()
    {
        return phantomJsJobResult;
    }

    public void setPhantomJsJobResult(PhantomJsJobResult phantomJsJobResult)
    {
        this.phantomJsJobResult = phantomJsJobResult;
    }

    public void setRequestPost()
    {
        this.request = REQUEST_ACTION_POST;
    }

    public void setRequestGet()
    {
        this.request = REQUEST_ACTION_GET;
    }

    public boolean isRequestPost()
    {
        return this.request == REQUEST_ACTION_POST;
    }

    public boolean isRequestGet()
    {
        return this.request == REQUEST_ACTION_GET;
    }

    public boolean isPending()
    {
        return status.equals(STATUS_PENDING);
    }

    public boolean isFinished()
    {
        return status.equals(STATUS_SUCCESS) || status.equals(STATUS_FAILED);
    }

    public boolean isSuccessful()
    {
        return status.equals(STATUS_SUCCESS);
    }

    public void setStatusFailed()
    {
        this.status = STATUS_FAILED;
    }

    public void setStatusSuccess()
    {
        this.status = STATUS_SUCCESS;
    }

    public void setStatusPending()
    {
        this.status = STATUS_PENDING;
    }

    public void setStatusProcessing()
    {
        this.status = STATUS_PROCESSING;
    }

    public Exception getException()
    {
        return exception;
    }

    public void setException(Exception exception)
    {
        this.exception = exception;
    }
}
