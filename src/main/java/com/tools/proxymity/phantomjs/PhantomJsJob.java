package com.tools.proxymity.phantomjs;

public class PhantomJsJob
{
    final static String STATUS_PENDING = "pending";
    final static String STATUS_PROCESSING = "processing";
    final static String STATUS_SUCCESS = "success";
    final static String STATUS_FAILED = "failed";
    String url;
    PhantomJsJobResult phantomJsJobResult;

    String status = STATUS_PENDING;
    Exception exception;

    public PhantomJsJob(String url)
    {
        this.setUrl(url);
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

    public boolean isPending()
    {
        return status.equals(STATUS_PENDING);
    }

    public boolean isFinished()
    {
        return status.equals(STATUS_SUCCESS ) || status.equals(STATUS_FAILED);
    }

    public boolean isSuccessful()
    {
        return status.equals(STATUS_SUCCESS );
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
