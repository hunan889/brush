// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class raceTopicFlag
{
    private static raceTopicFlag[] __values = new raceTopicFlag[3];
    private int __value;
    private String __T = new String();

    public static final int _RACE_TOPIC_FLAG_BIGIMAGE = 1;
    public static final raceTopicFlag RACE_TOPIC_FLAG_BIGIMAGE = new raceTopicFlag(0,_RACE_TOPIC_FLAG_BIGIMAGE,"RACE_TOPIC_FLAG_BIGIMAGE");
    public static final int _RACE_TOPIC_FLAG_BIGIMAGE_WIDTH = 2;
    public static final raceTopicFlag RACE_TOPIC_FLAG_BIGIMAGE_WIDTH = new raceTopicFlag(1,_RACE_TOPIC_FLAG_BIGIMAGE_WIDTH,"RACE_TOPIC_FLAG_BIGIMAGE_WIDTH");
    public static final int _RACE_TOPIC_FLAG_BIGIMAGE_HEIGHT = 4;
    public static final raceTopicFlag RACE_TOPIC_FLAG_BIGIMAGE_HEIGHT = new raceTopicFlag(2,_RACE_TOPIC_FLAG_BIGIMAGE_HEIGHT,"RACE_TOPIC_FLAG_BIGIMAGE_HEIGHT");

    public static raceTopicFlag convert(int val)
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

    public static raceTopicFlag convert(String val)
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

    private raceTopicFlag(int index, int val, String s)
    {
        __T = s;
        __value = val;
        __values[index] = this;
    }

}
