// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.4.3.
// Generated from `autonews.jce'
// **********************************************************************

package com.duowan.autonews;

public final class SubItemDetail extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "autonews.SubItemDetail";
    }

    public String fullClassName()
    {
        return "com.duowan.autonews.SubItemDetail";
    }

    public String name = "";

    public String value = "";

    public String getName()
    {
        return name;
    }

    public void  setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void  setValue(String value)
    {
        this.value = value;
    }

    public SubItemDetail()
    {
        setName(name);
        setValue(value);
    }

    public SubItemDetail(String name, String value)
    {
        setName(name);
        setValue(value);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        SubItemDetail t = (SubItemDetail) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(name, t.name) && 
            com.duowan.taf.jce.JceUtil.equals(value, t.value) );
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
        if (null != name)
        {
            _os.write(name, 0);
        }
        if (null != value)
        {
            _os.write(value, 1);
        }
    }


    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setName( _is.readString(0, false));

        setValue( _is.readString(1, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(name, "name");
        _ds.display(value, "value");
    }

}

