// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.4.3.
// Generated from `autonews.jce'
// **********************************************************************

package com.duowan.autonews;

public final class CarListInfo extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "autonews.CarListInfo";
    }

    public String fullClassName()
    {
        return "com.duowan.autonews.CarListInfo";
    }

    public int id = 0;

    public String name = "";

    public String icon = "";

    public String price = "";

    public int getId()
    {
        return id;
    }

    public void  setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void  setName(String name)
    {
        this.name = name;
    }

    public String getIcon()
    {
        return icon;
    }

    public void  setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getPrice()
    {
        return price;
    }

    public void  setPrice(String price)
    {
        this.price = price;
    }

    public CarListInfo()
    {
        setId(id);
        setName(name);
        setIcon(icon);
        setPrice(price);
    }

    public CarListInfo(int id, String name, String icon, String price)
    {
        setId(id);
        setName(name);
        setIcon(icon);
        setPrice(price);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        CarListInfo t = (CarListInfo) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(id, t.id) && 
            com.duowan.taf.jce.JceUtil.equals(name, t.name) && 
            com.duowan.taf.jce.JceUtil.equals(icon, t.icon) && 
            com.duowan.taf.jce.JceUtil.equals(price, t.price) );
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
        if (null != name)
        {
            _os.write(name, 1);
        }
        if (null != icon)
        {
            _os.write(icon, 2);
        }
        if (null != price)
        {
            _os.write(price, 3);
        }
    }


    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setId((int) _is.read(id, 0, false));

        setName( _is.readString(1, false));

        setIcon( _is.readString(2, false));

        setPrice( _is.readString(3, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(id, "id");
        _ds.display(name, "name");
        _ds.display(icon, "icon");
        _ds.display(price, "price");
    }

}

