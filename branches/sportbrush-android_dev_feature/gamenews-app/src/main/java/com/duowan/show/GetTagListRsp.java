// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `show.jce'
// **********************************************************************

package com.duowan.show;

public final class GetTagListRsp extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "show.GetTagListRsp";
    }

    public String fullClassName()
    {
        return "com.duowan.show.GetTagListRsp";
    }

    public java.util.ArrayList<com.duowan.show.Tag> tagList = null;

    public java.util.ArrayList<com.duowan.show.Tag> getTagList()
    {
        return tagList;
    }

    public void  setTagList(java.util.ArrayList<com.duowan.show.Tag> tagList)
    {
        this.tagList = tagList;
    }

    public GetTagListRsp()
    {
        setTagList(tagList);
    }

    public GetTagListRsp(java.util.ArrayList<com.duowan.show.Tag> tagList)
    {
        setTagList(tagList);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        GetTagListRsp t = (GetTagListRsp) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(tagList, t.tagList) );
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
        if (null != tagList)
        {
            _os.write(tagList, 0);
        }
    }

    static java.util.ArrayList<com.duowan.show.Tag> cache_tagList;

    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        if(null == cache_tagList)
        {
            cache_tagList = new java.util.ArrayList<com.duowan.show.Tag>();
            com.duowan.show.Tag __var_12 = new com.duowan.show.Tag();
            ((java.util.ArrayList<com.duowan.show.Tag>)cache_tagList).add(__var_12);
        }
        setTagList((java.util.ArrayList<com.duowan.show.Tag>) _is.read(cache_tagList, 0, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(tagList, "tagList");
    }

}

