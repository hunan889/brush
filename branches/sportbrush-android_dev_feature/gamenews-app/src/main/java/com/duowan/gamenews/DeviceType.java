// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class DeviceType
{
    private static DeviceType[] __values = new DeviceType[2];
    private int __value;
    private String __T = new String();

    public static final int _DEVICE_TYPE_ANDROID = 1;
    public static final DeviceType DEVICE_TYPE_ANDROID = new DeviceType(0,_DEVICE_TYPE_ANDROID,"DEVICE_TYPE_ANDROID");
    public static final int _DEVICE_TYPE_IOS = 2;
    public static final DeviceType DEVICE_TYPE_IOS = new DeviceType(1,_DEVICE_TYPE_IOS,"DEVICE_TYPE_IOS");

    public static DeviceType convert(int val)
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

    public static DeviceType convert(String val)
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

    private DeviceType(int index, int val, String s)
    {
        __T = s;
        __value = val;
        __values[index] = this;
    }

}
