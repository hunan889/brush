// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class UnionVoteReq extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "gamenews.UnionVoteReq";
    }

    public String fullClassName()
    {
        return "com.duowan.gamenews.UnionVoteReq";
    }

    public long unionId = 0;

    public long getUnionId()
    {
        return unionId;
    }

    public void  setUnionId(long unionId)
    {
        this.unionId = unionId;
    }

    public UnionVoteReq()
    {
        setUnionId(unionId);
    }

    public UnionVoteReq(long unionId)
    {
        setUnionId(unionId);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        UnionVoteReq t = (UnionVoteReq) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(unionId, t.unionId) );
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
        _os.write(unionId, 0);
    }


    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setUnionId((long) _is.read(unionId, 0, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(unionId, "unionId");
    }

}

