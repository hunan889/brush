// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class ChannelPiCTopInfo extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "gamenews.ChannelPiCTopInfo";
    }

    public String fullClassName()
    {
        return "com.duowan.gamenews.ChannelPiCTopInfo";
    }

    public long id = 0;

    public String title = "";

    public int channelPicTopType = 0;

    public String img = "";

    public String url = "";

    public long getId()
    {
        return id;
    }

    public void  setId(long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void  setTitle(String title)
    {
        this.title = title;
    }

    public int getChannelPicTopType()
    {
        return channelPicTopType;
    }

    public void  setChannelPicTopType(int channelPicTopType)
    {
        this.channelPicTopType = channelPicTopType;
    }

    public String getImg()
    {
        return img;
    }

    public void  setImg(String img)
    {
        this.img = img;
    }

    public String getUrl()
    {
        return url;
    }

    public void  setUrl(String url)
    {
        this.url = url;
    }

    public ChannelPiCTopInfo()
    {
        setId(id);
        setTitle(title);
        setChannelPicTopType(channelPicTopType);
        setImg(img);
        setUrl(url);
    }

    public ChannelPiCTopInfo(long id, String title, int channelPicTopType, String img, String url)
    {
        setId(id);
        setTitle(title);
        setChannelPicTopType(channelPicTopType);
        setImg(img);
        setUrl(url);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        ChannelPiCTopInfo t = (ChannelPiCTopInfo) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(id, t.id) && 
            com.duowan.taf.jce.JceUtil.equals(title, t.title) && 
            com.duowan.taf.jce.JceUtil.equals(channelPicTopType, t.channelPicTopType) && 
            com.duowan.taf.jce.JceUtil.equals(img, t.img) && 
            com.duowan.taf.jce.JceUtil.equals(url, t.url) );
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
        _os.write(id, 0);
        if (null != title)
        {
            _os.write(title, 1);
        }
        _os.write(channelPicTopType, 2);
        if (null != img)
        {
            _os.write(img, 3);
        }
        if (null != url)
        {
            _os.write(url, 4);
        }
    }


    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setId((long) _is.read(id, 0, false));

        setTitle( _is.readString(1, false));

        setChannelPicTopType((int) _is.read(channelPicTopType, 2, false));

        setImg( _is.readString(3, false));

        setUrl( _is.readString(4, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(id, "id");
        _ds.display(title, "title");
        _ds.display(channelPicTopType, "channelPicTopType");
        _ds.display(img, "img");
        _ds.display(url, "url");
    }

}
