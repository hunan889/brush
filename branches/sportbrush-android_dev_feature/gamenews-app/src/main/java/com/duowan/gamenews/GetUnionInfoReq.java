// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class GetUnionInfoReq extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "gamenews.GetUnionInfoReq";
    }

    public String fullClassName()
    {
        return "com.duowan.gamenews.GetUnionInfoReq";
    }

    public long unionId = 0;

    public String attachInfo = "";

    public int refreshType = 0;

    public long getUnionId()
    {
        return unionId;
    }

    public void  setUnionId(long unionId)
    {
        this.unionId = unionId;
    }

    public String getAttachInfo()
    {
        return attachInfo;
    }

    public void  setAttachInfo(String attachInfo)
    {
        this.attachInfo = attachInfo;
    }

    public int getRefreshType()
    {
        return refreshType;
    }

    public void  setRefreshType(int refreshType)
    {
        this.refreshType = refreshType;
    }

    public GetUnionInfoReq()
    {
        setUnionId(unionId);
        setAttachInfo(attachInfo);
        setRefreshType(refreshType);
    }

    public GetUnionInfoReq(long unionId, String attachInfo, int refreshType)
    {
        setUnionId(unionId);
        setAttachInfo(attachInfo);
        setRefreshType(refreshType);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        GetUnionInfoReq t = (GetUnionInfoReq) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(unionId, t.unionId) && 
            com.duowan.taf.jce.JceUtil.equals(attachInfo, t.attachInfo) && 
            com.duowan.taf.jce.JceUtil.equals(refreshType, t.refreshType) );
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
        if (null != attachInfo)
        {
            _os.write(attachInfo, 1);
        }
        _os.write(refreshType, 2);
    }


    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setUnionId((long) _is.read(unionId, 0, false));

        setAttachInfo( _is.readString(1, false));

        setRefreshType((int) _is.read(refreshType, 2, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(unionId, "unionId");
        _ds.display(attachInfo, "attachInfo");
        _ds.display(refreshType, "refreshType");
    }

}

