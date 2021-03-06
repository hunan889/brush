// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class GetCommentListRsp extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "gamenews.GetCommentListRsp";
    }

    public String fullClassName()
    {
        return "com.duowan.gamenews.GetCommentListRsp";
    }

    public java.util.ArrayList<com.duowan.gamenews.Comment> commentList = null;

    public String attachInfo = "";

    public boolean hasMore = true;

    public int count = 0;

    public java.util.ArrayList<com.duowan.gamenews.Comment> getCommentList()
    {
        return commentList;
    }

    public void  setCommentList(java.util.ArrayList<com.duowan.gamenews.Comment> commentList)
    {
        this.commentList = commentList;
    }

    public String getAttachInfo()
    {
        return attachInfo;
    }

    public void  setAttachInfo(String attachInfo)
    {
        this.attachInfo = attachInfo;
    }

    public boolean getHasMore()
    {
        return hasMore;
    }

    public void  setHasMore(boolean hasMore)
    {
        this.hasMore = hasMore;
    }

    public int getCount()
    {
        return count;
    }

    public void  setCount(int count)
    {
        this.count = count;
    }

    public GetCommentListRsp()
    {
        setCommentList(commentList);
        setAttachInfo(attachInfo);
        setHasMore(hasMore);
        setCount(count);
    }

    public GetCommentListRsp(java.util.ArrayList<com.duowan.gamenews.Comment> commentList, String attachInfo, boolean hasMore, int count)
    {
        setCommentList(commentList);
        setAttachInfo(attachInfo);
        setHasMore(hasMore);
        setCount(count);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        GetCommentListRsp t = (GetCommentListRsp) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(commentList, t.commentList) && 
            com.duowan.taf.jce.JceUtil.equals(attachInfo, t.attachInfo) && 
            com.duowan.taf.jce.JceUtil.equals(hasMore, t.hasMore) && 
            com.duowan.taf.jce.JceUtil.equals(count, t.count) );
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
        if (null != commentList)
        {
            _os.write(commentList, 0);
        }
        if (null != attachInfo)
        {
            _os.write(attachInfo, 1);
        }
        _os.write(hasMore, 2);
        _os.write(count, 3);
    }

    static java.util.ArrayList<com.duowan.gamenews.Comment> cache_commentList;

    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        if(null == cache_commentList)
        {
            cache_commentList = new java.util.ArrayList<com.duowan.gamenews.Comment>();
            com.duowan.gamenews.Comment __var_46 = new com.duowan.gamenews.Comment();
            ((java.util.ArrayList<com.duowan.gamenews.Comment>)cache_commentList).add(__var_46);
        }
        setCommentList((java.util.ArrayList<com.duowan.gamenews.Comment>) _is.read(cache_commentList, 0, false));

        setAttachInfo( _is.readString(1, false));

        setHasMore((boolean) _is.read(hasMore, 2, false));

        setCount((int) _is.read(count, 3, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(commentList, "commentList");
        _ds.display(attachInfo, "attachInfo");
        _ds.display(hasMore, "hasMore");
        _ds.display(count, "count");
    }

}

