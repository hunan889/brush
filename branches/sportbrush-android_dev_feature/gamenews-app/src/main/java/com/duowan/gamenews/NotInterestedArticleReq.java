// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class NotInterestedArticleReq extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "gamenews.NotInterestedArticleReq";
    }

    public String fullClassName()
    {
        return "com.duowan.gamenews.NotInterestedArticleReq";
    }

    public long articleId = 0;

    public long getArticleId()
    {
        return articleId;
    }

    public void  setArticleId(long articleId)
    {
        this.articleId = articleId;
    }

    public NotInterestedArticleReq()
    {
        setArticleId(articleId);
    }

    public NotInterestedArticleReq(long articleId)
    {
        setArticleId(articleId);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        NotInterestedArticleReq t = (NotInterestedArticleReq) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(articleId, t.articleId) );
    }

    public int hashCode()
    {
        try
        {
            throw new Exception("Need define key first!");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }
    public java.lang.Object clone()
    {
        java.lang.Object o = null;
        try
        {
            o = super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return o;
    }

    public void writeTo(com.duowan.taf.jce.JceOutputStream _os)
    {
        _os.write(articleId, 0);
    }


    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setArticleId((long) _is.read(articleId, 0, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(articleId, "articleId");
    }

}

