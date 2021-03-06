// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class Image extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "gamenews.Image";
    }

    public String fullClassName()
    {
        return "com.duowan.gamenews.Image";
    }

    public java.util.Map<Integer, com.duowan.gamenews.PicInfo> urls = null;

    public int id = 0;

    public java.util.Map<Integer, com.duowan.gamenews.PicInfo> getUrls()
    {
        return urls;
    }

    public void  setUrls(java.util.Map<Integer, com.duowan.gamenews.PicInfo> urls)
    {
        this.urls = urls;
    }

    public int getId()
    {
        return id;
    }

    public void  setId(int id)
    {
        this.id = id;
    }

    public Image()
    {
        setUrls(urls);
        setId(id);
    }

    public Image(java.util.Map<Integer, com.duowan.gamenews.PicInfo> urls, int id)
    {
        setUrls(urls);
        setId(id);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        Image t = (Image) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(urls, t.urls) && 
            com.duowan.taf.jce.JceUtil.equals(id, t.id) );
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
        if (null != urls)
        {
            _os.write(urls, 0);
        }
        _os.write(id, 1);
    }

    static java.util.Map<Integer, com.duowan.gamenews.PicInfo> cache_urls;

    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        if(null == cache_urls)
        {
            cache_urls = new java.util.HashMap<Integer, com.duowan.gamenews.PicInfo>();
            Integer __var_3 = 0;
            com.duowan.gamenews.PicInfo __var_4 = new com.duowan.gamenews.PicInfo();
            cache_urls.put(__var_3, __var_4);
        }
        setUrls((java.util.Map<Integer, com.duowan.gamenews.PicInfo>) _is.read(cache_urls, 0, false));

        setId((int) _is.read(id, 1, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(urls, "urls");
        _ds.display(id, "id");
    }

}

