// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `show.jce'
// **********************************************************************

package com.duowan.show;

public final class AddTopicReq extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "show.AddTopicReq";
    }

    public String fullClassName()
    {
        return "com.duowan.show.AddTopicReq";
    }

    public String content = "";

    public com.duowan.show.PicInfo image = null;

    public java.util.ArrayList<Integer> tagIdList = null;

    public String getContent()
    {
        return content;
    }

    public void  setContent(String content)
    {
        this.content = content;
    }

    public com.duowan.show.PicInfo getImage()
    {
        return image;
    }

    public void  setImage(com.duowan.show.PicInfo image)
    {
        this.image = image;
    }

    public java.util.ArrayList<Integer> getTagIdList()
    {
        return tagIdList;
    }

    public void  setTagIdList(java.util.ArrayList<Integer> tagIdList)
    {
        this.tagIdList = tagIdList;
    }

    public AddTopicReq()
    {
        setContent(content);
        setImage(image);
        setTagIdList(tagIdList);
    }

    public AddTopicReq(String content, com.duowan.show.PicInfo image, java.util.ArrayList<Integer> tagIdList)
    {
        setContent(content);
        setImage(image);
        setTagIdList(tagIdList);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        AddTopicReq t = (AddTopicReq) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(content, t.content) && 
            com.duowan.taf.jce.JceUtil.equals(image, t.image) && 
            com.duowan.taf.jce.JceUtil.equals(tagIdList, t.tagIdList) );
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
        if (null != content)
        {
            _os.write(content, 0);
        }
        if (null != image)
        {
            _os.write(image, 1);
        }
        if (null != tagIdList)
        {
            _os.write(tagIdList, 2);
        }
    }

    static com.duowan.show.PicInfo cache_image;
    static java.util.ArrayList<Integer> cache_tagIdList;

    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setContent( _is.readString(0, false));

        if(null == cache_image)
        {
            cache_image = new com.duowan.show.PicInfo();
        }
        setImage((com.duowan.show.PicInfo) _is.read(cache_image, 1, false));

        if(null == cache_tagIdList)
        {
            cache_tagIdList = new java.util.ArrayList<Integer>();
            Integer __var_13 = 0;
            ((java.util.ArrayList<Integer>)cache_tagIdList).add(__var_13);
        }
        setTagIdList((java.util.ArrayList<Integer>) _is.read(cache_tagIdList, 2, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(content, "content");
        _ds.display(image, "image");
        _ds.display(tagIdList, "tagIdList");
    }

}

