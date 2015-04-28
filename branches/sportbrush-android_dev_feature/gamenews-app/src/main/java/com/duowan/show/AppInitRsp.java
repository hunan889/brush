// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `show.jce'
// **********************************************************************

package com.duowan.show;

public final class AppInitRsp extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "show.AppInitRsp";
    }

    public String fullClassName()
    {
        return "com.duowan.show.AppInitRsp";
    }

    public java.util.ArrayList<com.duowan.show.AdvInfo> advInfo = null;

    public int flag = 0;

    public java.util.Map<Integer, String> option = null;

    public java.util.Map<Integer, String> imageSize = null;

    public java.util.ArrayList<com.duowan.show.AdvInfo> getAdvInfo()
    {
        return advInfo;
    }

    public void  setAdvInfo(java.util.ArrayList<com.duowan.show.AdvInfo> advInfo)
    {
        this.advInfo = advInfo;
    }

    public int getFlag()
    {
        return flag;
    }

    public void  setFlag(int flag)
    {
        this.flag = flag;
    }

    public java.util.Map<Integer, String> getOption()
    {
        return option;
    }

    public void  setOption(java.util.Map<Integer, String> option)
    {
        this.option = option;
    }

    public java.util.Map<Integer, String> getImageSize()
    {
        return imageSize;
    }

    public void  setImageSize(java.util.Map<Integer, String> imageSize)
    {
        this.imageSize = imageSize;
    }

    public AppInitRsp()
    {
        setAdvInfo(advInfo);
        setFlag(flag);
        setOption(option);
        setImageSize(imageSize);
    }

    public AppInitRsp(java.util.ArrayList<com.duowan.show.AdvInfo> advInfo, int flag, java.util.Map<Integer, String> option, java.util.Map<Integer, String> imageSize)
    {
        setAdvInfo(advInfo);
        setFlag(flag);
        setOption(option);
        setImageSize(imageSize);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        AppInitRsp t = (AppInitRsp) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(advInfo, t.advInfo) && 
            com.duowan.taf.jce.JceUtil.equals(flag, t.flag) && 
            com.duowan.taf.jce.JceUtil.equals(option, t.option) && 
            com.duowan.taf.jce.JceUtil.equals(imageSize, t.imageSize) );
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
        if (null != advInfo)
        {
            _os.write(advInfo, 0);
        }
        _os.write(flag, 1);
        if (null != option)
        {
            _os.write(option, 2);
        }
        if (null != imageSize)
        {
            _os.write(imageSize, 3);
        }
    }

    static java.util.ArrayList<com.duowan.show.AdvInfo> cache_advInfo;
    static java.util.Map<Integer, String> cache_option;
    static java.util.Map<Integer, String> cache_imageSize;

    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        if(null == cache_advInfo)
        {
            cache_advInfo = new java.util.ArrayList<com.duowan.show.AdvInfo>();
            com.duowan.show.AdvInfo __var_4 = new com.duowan.show.AdvInfo();
            ((java.util.ArrayList<com.duowan.show.AdvInfo>)cache_advInfo).add(__var_4);
        }
        setAdvInfo((java.util.ArrayList<com.duowan.show.AdvInfo>) _is.read(cache_advInfo, 0, false));

        setFlag((int) _is.read(flag, 1, false));

        if(null == cache_option)
        {
            cache_option = new java.util.HashMap<Integer, String>();
            Integer __var_5 = 0;
            String __var_6 = "";
            cache_option.put(__var_5, __var_6);
        }
        setOption((java.util.Map<Integer, String>) _is.read(cache_option, 2, false));

        if(null == cache_imageSize)
        {
            cache_imageSize = new java.util.HashMap<Integer, String>();
            Integer __var_7 = 0;
            String __var_8 = "";
            cache_imageSize.put(__var_7, __var_8);
        }
        setImageSize((java.util.Map<Integer, String>) _is.read(cache_imageSize, 3, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(advInfo, "advInfo");
        _ds.display(flag, "flag");
        _ds.display(option, "option");
        _ds.display(imageSize, "imageSize");
    }

}

