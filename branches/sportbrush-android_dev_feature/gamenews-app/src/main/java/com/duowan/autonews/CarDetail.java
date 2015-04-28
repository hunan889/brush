// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.4.3.
// Generated from `autonews.jce'
// **********************************************************************

package com.duowan.autonews;

public final class CarDetail extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "autonews.CarDetail";
    }

    public String fullClassName()
    {
        return "com.duowan.autonews.CarDetail";
    }

    public int id = 0;

    public String name = "";

    public String icon = "";

    public String level = "";

    public String price = "";

    public java.util.ArrayList<com.duowan.autonews.CarDetailItemDetail> itemList = null;

    public String image = "";

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

    public String getLevel()
    {
        return level;
    }

    public void  setLevel(String level)
    {
        this.level = level;
    }

    public String getPrice()
    {
        return price;
    }

    public void  setPrice(String price)
    {
        this.price = price;
    }

    public java.util.ArrayList<com.duowan.autonews.CarDetailItemDetail> getItemList()
    {
        return itemList;
    }

    public void  setItemList(java.util.ArrayList<com.duowan.autonews.CarDetailItemDetail> itemList)
    {
        this.itemList = itemList;
    }

    public String getImage()
    {
        return image;
    }

    public void  setImage(String image)
    {
        this.image = image;
    }

    public CarDetail()
    {
        setId(id);
        setName(name);
        setIcon(icon);
        setLevel(level);
        setPrice(price);
        setItemList(itemList);
        setImage(image);
    }

    public CarDetail(int id, String name, String icon, String level, String price, java.util.ArrayList<com.duowan.autonews.CarDetailItemDetail> itemList, String image)
    {
        setId(id);
        setName(name);
        setIcon(icon);
        setLevel(level);
        setPrice(price);
        setItemList(itemList);
        setImage(image);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        CarDetail t = (CarDetail) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(id, t.id) && 
            com.duowan.taf.jce.JceUtil.equals(name, t.name) && 
            com.duowan.taf.jce.JceUtil.equals(icon, t.icon) && 
            com.duowan.taf.jce.JceUtil.equals(level, t.level) && 
            com.duowan.taf.jce.JceUtil.equals(price, t.price) && 
            com.duowan.taf.jce.JceUtil.equals(itemList, t.itemList) && 
            com.duowan.taf.jce.JceUtil.equals(image, t.image) );
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
        if (null != level)
        {
            _os.write(level, 3);
        }
        if (null != price)
        {
            _os.write(price, 4);
        }
        if (null != itemList)
        {
            _os.write(itemList, 5);
        }
        if (null != image)
        {
            _os.write(image, 6);
        }
    }

    static java.util.ArrayList<com.duowan.autonews.CarDetailItemDetail> cache_itemList;

    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setId((int) _is.read(id, 0, false));

        setName( _is.readString(1, false));

        setIcon( _is.readString(2, false));

        setLevel( _is.readString(3, false));

        setPrice( _is.readString(4, false));

        if(null == cache_itemList)
        {
            cache_itemList = new java.util.ArrayList<com.duowan.autonews.CarDetailItemDetail>();
            com.duowan.autonews.CarDetailItemDetail __var_9 = new com.duowan.autonews.CarDetailItemDetail();
            ((java.util.ArrayList<com.duowan.autonews.CarDetailItemDetail>)cache_itemList).add(__var_9);
        }
        setItemList((java.util.ArrayList<com.duowan.autonews.CarDetailItemDetail>) _is.read(cache_itemList, 5, false));

        setImage( _is.readString(6, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(id, "id");
        _ds.display(name, "name");
        _ds.display(icon, "icon");
        _ds.display(level, "level");
        _ds.display(price, "price");
        _ds.display(itemList, "itemList");
        _ds.display(image, "image");
    }

}

