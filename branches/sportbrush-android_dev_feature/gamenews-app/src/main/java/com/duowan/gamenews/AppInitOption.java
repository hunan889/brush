// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class AppInitOption
{
    private static AppInitOption[] __values = new AppInitOption[1];
    private int __value;
    private String __T = new String();

    public static final int _WEBPAGE_URL_MTQ = 0;
    public static final AppInitOption WEBPAGE_URL_MTQ = new AppInitOption(0,_WEBPAGE_URL_MTQ,"WEBPAGE_URL_MTQ");

    public static AppInitOption convert(int val)
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

    public static AppInitOption convert(String val)
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

    private AppInitOption(int index, int val, String s)
    {
        __T = s;
        __value = val;
        __values[index] = this;
    }

}
