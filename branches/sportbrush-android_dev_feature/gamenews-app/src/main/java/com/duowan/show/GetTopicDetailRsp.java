// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `show.jce'
// **********************************************************************

package com.duowan.show;

public final class GetTopicDetailRsp extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "show.GetTopicDetailRsp";
    }

    public String fullClassName()
    {
        return "com.duowan.show.GetTopicDetailRsp";
    }

    public com.duowan.show.Topic topicInfo = null;

    public com.duowan.show.Topic getTopicInfo()
    {
        return topicInfo;
    }

    public void  setTopicInfo(com.duowan.show.Topic topicInfo)
    {
        this.topicInfo = topicInfo;
    }

    public GetTopicDetailRsp()
    {
        setTopicInfo(topicInfo);
    }

    public GetTopicDetailRsp(com.duowan.show.Topic topicInfo)
    {
        setTopicInfo(topicInfo);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        GetTopicDetailRsp t = (GetTopicDetailRsp) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(topicInfo, t.topicInfo) );
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
        if (null != topicInfo)
        {
            _os.write(topicInfo, 0);
        }
    }

    static com.duowan.show.Topic cache_topicInfo;

    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        if(null == cache_topicInfo)
        {
            cache_topicInfo = new com.duowan.show.Topic();
        }
        setTopicInfo((com.duowan.show.Topic) _is.read(cache_topicInfo, 0, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(topicInfo, "topicInfo");
    }

}

