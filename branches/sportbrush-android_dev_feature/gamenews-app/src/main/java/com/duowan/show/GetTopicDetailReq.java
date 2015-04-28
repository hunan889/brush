// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `show.jce'
// **********************************************************************

package com.duowan.show;

public final class GetTopicDetailReq extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "show.GetTopicDetailReq";
    }

    public String fullClassName()
    {
        return "com.duowan.show.GetTopicDetailReq";
    }

    public int topicId = 0;

    public int getTopicId()
    {
        return topicId;
    }

    public void  setTopicId(int topicId)
    {
        this.topicId = topicId;
    }

    public GetTopicDetailReq()
    {
        setTopicId(topicId);
    }

    public GetTopicDetailReq(int topicId)
    {
        setTopicId(topicId);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        GetTopicDetailReq t = (GetTopicDetailReq) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(topicId, t.topicId) );
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
        _os.write(topicId, 0);
    }


    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setTopicId((int) _is.read(topicId, 0, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(topicId, "topicId");
    }

}

