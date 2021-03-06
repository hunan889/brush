// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.4.3.
// Generated from `autonews.jce'
// **********************************************************************

package com.duowan.autonews;

public final class GetCarDetailReq extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "autonews.GetCarDetailReq";
    }

    public String fullClassName()
    {
        return "com.duowan.autonews.GetCarDetailReq";
    }

    public int carId = 0;

    public int count = 0;

    public String attachInfo = "";

    public int getCarId()
    {
        return carId;
    }

    public void  setCarId(int carId)
    {
        this.carId = carId;
    }

    public int getCount()
    {
        return count;
    }

    public void  setCount(int count)
    {
        this.count = count;
    }

    public String getAttachInfo()
    {
        return attachInfo;
    }

    public void  setAttachInfo(String attachInfo)
    {
        this.attachInfo = attachInfo;
    }

    public GetCarDetailReq()
    {
        setCarId(carId);
        setCount(count);
        setAttachInfo(attachInfo);
    }

    public GetCarDetailReq(int carId, int count, String attachInfo)
    {
        setCarId(carId);
        setCount(count);
        setAttachInfo(attachInfo);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        GetCarDetailReq t = (GetCarDetailReq) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(carId, t.carId) && 
            com.duowan.taf.jce.JceUtil.equals(count, t.count) && 
            com.duowan.taf.jce.JceUtil.equals(attachInfo, t.attachInfo) );
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
        _os.write(carId, 0);
        _os.write(count, 1);
        if (null != attachInfo)
        {
            _os.write(attachInfo, 2);
        }
    }


    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setCarId((int) _is.read(carId, 0, false));

        setCount((int) _is.read(count, 1, false));

        setAttachInfo( _is.readString(2, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(carId, "carId");
        _ds.display(count, "count");
        _ds.display(attachInfo, "attachInfo");
    }

}

