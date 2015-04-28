// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class SubType
{
    private static SubType[] __values = new SubType[6];
    private int __value;
    private String __T = new String();

    public static final int _SUBTYPE_ZIXUN = 1;
    public static final SubType SUBTYPE_ZIXUN = new SubType(0,_SUBTYPE_ZIXUN,"SUBTYPE_ZIXUN");
    public static final int _SUBTYPE_GONGLUE = 2;
    public static final SubType SUBTYPE_GONGLUE = new SubType(1,_SUBTYPE_GONGLUE,"SUBTYPE_GONGLUE");
    public static final int _SUBTYPE_FALL = 3;
    public static final SubType SUBTYPE_FALL = new SubType(2,_SUBTYPE_FALL,"SUBTYPE_FALL");
    public static final int _SUBTYPE_VIDEO = 4;
    public static final SubType SUBTYPE_VIDEO = new SubType(3,_SUBTYPE_VIDEO,"SUBTYPE_VIDEO");
    public static final int _SUBTYPE_GIFT = 5;
    public static final SubType SUBTYPE_GIFT = new SubType(4,_SUBTYPE_GIFT,"SUBTYPE_GIFT");
    public static final int _SUBTYPE_SHOW = 6;
    public static final SubType SUBTYPE_SHOW = new SubType(5,_SUBTYPE_SHOW,"SUBTYPE_SHOW");

    public static SubType convert(int val)
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

    public static SubType convert(String val)
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

    private SubType(int index, int val, String s)
    {
        __T = s;
        __value = val;
        __values[index] = this;
    }

}