// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class StoreServiceType
{
    private static StoreServiceType[] __values = new StoreServiceType[1];
    private int __value;
    private String __T = new String();

    public static final int _STORE_SERVICE_GETTDOU_TYPE = 1;
    public static final StoreServiceType STORE_SERVICE_GETTDOU_TYPE = new StoreServiceType(0,_STORE_SERVICE_GETTDOU_TYPE,"STORE_SERVICE_GETTDOU_TYPE");

    public static StoreServiceType convert(int val)
    {
        for(int __i = 0; __i < __values.length; ++__i)
        {
            if(__values[__i].value() == val)
            {
                return __values[__i];
            }
        }
        assert false;
        return null;
    }

    public static StoreServiceType convert(String val)
    {
        for(int __i = 0; __i < __values.length; ++__i)
        {
            if(__values[__i].toString().equals(val))
            {
                return __values[__i];
            }
        }
        assert false;
        return null;
    }

    public int value()
    {
        return __value;
    }

    public String toString()
    {
        return __T;
    }

    private StoreServiceType(int index, int val, String s)
    {
        __T = s;
        __value = val;
        __values[index] = this;
    }

}
