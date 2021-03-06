// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class GetColumnChannelListRsp extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "gamenews.GetColumnChannelListRsp";
    }

    public String fullClassName()
    {
        return "com.duowan.gamenews.GetColumnChannelListRsp";
    }

    public java.util.ArrayList<com.duowan.gamenews.Channel> channelList = null;

    public String attachInfo = "";

    public boolean hasMore = true;

    public java.util.ArrayList<com.duowan.gamenews.Channel> getChannelList()
    {
        return channelList;
    }

    public void  setChannelList(java.util.ArrayList<com.duowan.gamenews.Channel> channelList)
    {
        this.channelList = channelList;
    }

    public String getAttachInfo()
    {
        return attachInfo;
    }

    public void  setAttachInfo(String attachInfo)
    {
        this.attachInfo = attachInfo;
    }

    public boolean getHasMore()
    {
        return hasMore;
    }

    public void  setHasMore(boolean hasMore)
    {
        this.hasMore = hasMore;
    }

    public GetColumnChannelListRsp()
    {
        setChannelList(channelList);
        setAttachInfo(attachInfo);
        setHasMore(hasMore);
    }

    public GetColumnChannelListRsp(java.util.ArrayList<com.duowan.gamenews.Channel> channelList, String attachInfo, boolean hasMore)
    {
        setChannelList(channelList);
        setAttachInfo(attachInfo);
        setHasMore(hasMore);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        GetColumnChannelListRsp t = (GetColumnChannelListRsp) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(channelList, t.channelList) && 
            com.duowan.taf.jce.JceUtil.equals(attachInfo, t.attachInfo) && 
            com.duowan.taf.jce.JceUtil.equals(hasMore, t.hasMore) );
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
        if (null != channelList)
        {
            _os.write(channelList, 0);
        }
        if (null != attachInfo)
        {
            _os.write(attachInfo, 1);
        }
        _os.write(hasMore, 2);
    }

    static java.util.ArrayList<com.duowan.gamenews.Channel> cache_channelList;

    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        if(null == cache_channelList)
        {
            cache_channelList = new java.util.ArrayList<com.duowan.gamenews.Channel>();
            com.duowan.gamenews.Channel __var_21 = new com.duowan.gamenews.Channel();
            ((java.util.ArrayList<com.duowan.gamenews.Channel>)cache_channelList).add(__var_21);
        }
        setChannelList((java.util.ArrayList<com.duowan.gamenews.Channel>) _is.read(cache_channelList, 0, false));

        setAttachInfo( _is.readString(1, false));

        setHasMore((boolean) _is.read(hasMore, 2, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(channelList, "channelList");
        _ds.display(attachInfo, "attachInfo");
        _ds.display(hasMore, "hasMore");
    }

}

