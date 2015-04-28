// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class StoreAppStatus
{
    private static StoreAppStatus[] __values = new StoreAppStatus[6];
    private int __value;
    private String __T = new String();

    public static final int _STORE_APP_NOT_INSTALL = 0;
    public static final StoreAppStatus STORE_APP_NOT_INSTALL = new StoreAppStatus(0,_STORE_APP_NOT_INSTALL,"STORE_APP_NOT_INSTALL");
    public static final int _STORE_APP_PENDING = 1;
    public static final StoreAppStatus STORE_APP_PENDING = new StoreAppStatus(1,_STORE_APP_PENDING,"STORE_APP_PENDING");
    public static final int _STORE_APP_DOWNLOADING = 2;
    public static final StoreAppStatus STORE_APP_DOWNLOADING = new StoreAppStatus(2,_STORE_APP_DOWNLOADING,"STORE_APP_DOWNLOADING");
    public static final int _STORE_APP_HAS_DOWNLOAD = 3;
    public static final StoreAppStatus STORE_APP_HAS_DOWNLOAD = new StoreAppStatus(3,_STORE_APP_HAS_DOWNLOAD,"STORE_APP_HAS_DOWNLOAD");
    public static final int _STORE_APP_HAS_EXIST = 4;
    public static final StoreAppStatus STORE_APP_HAS_EXIST = new StoreAppStatus(4,_STORE_APP_HAS_EXIST,"STORE_APP_HAS_EXIST");
    public static final int _STORE_APP_HAS_REWARD = 5;
    public static final StoreAppStatus STORE_APP_HAS_REWARD = new StoreAppStatus(5,_STORE_APP_HAS_REWARD,"STORE_APP_HAS_REWARD");

    public static StoreAppStatus convert(int val)
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

    public static StoreAppStatus convert(String val)
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

    private StoreAppStatus(int index, int val, String s)
    {
        __T = s;
        __value = val;
        __values[index] = this;
    }

}
