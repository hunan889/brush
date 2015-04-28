// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.5.5
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class RaceInfo extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "gamenews.RaceInfo";
    }

    public String fullClassName()
    {
        return "com.duowan.gamenews.RaceInfo";
    }

    public String id = "";

    public int liveTime = 0;

    public String sportType = "";

    public String leagueName = "";

    public int raceFlag = 0;

    public java.util.ArrayList<com.duowan.gamenews.Team> teamList = null;

    public java.util.ArrayList<String> liveSourceList = null;

    public String getId()
    {
        return id;
    }

    public void  setId(String id)
    {
        this.id = id;
    }

    public int getLiveTime()
    {
        return liveTime;
    }

    public void  setLiveTime(int liveTime)
    {
        this.liveTime = liveTime;
    }

    public String getSportType()
    {
        return sportType;
    }

    public void  setSportType(String sportType)
    {
        this.sportType = sportType;
    }

    public String getLeagueName()
    {
        return leagueName;
    }

    public void  setLeagueName(String leagueName)
    {
        this.leagueName = leagueName;
    }

    public int getRaceFlag()
    {
        return raceFlag;
    }

    public void  setRaceFlag(int raceFlag)
    {
        this.raceFlag = raceFlag;
    }

    public java.util.ArrayList<com.duowan.gamenews.Team> getTeamList()
    {
        return teamList;
    }

    public void  setTeamList(java.util.ArrayList<com.duowan.gamenews.Team> teamList)
    {
        this.teamList = teamList;
    }

    public java.util.ArrayList<String> getLiveSourceList()
    {
        return liveSourceList;
    }

    public void  setLiveSourceList(java.util.ArrayList<String> liveSourceList)
    {
        this.liveSourceList = liveSourceList;
    }

    public RaceInfo()
    {
        setId(id);
        setLiveTime(liveTime);
        setSportType(sportType);
        setLeagueName(leagueName);
        setRaceFlag(raceFlag);
        setTeamList(teamList);
        setLiveSourceList(liveSourceList);
    }

    public RaceInfo(String id, int liveTime, String sportType, String leagueName, int raceFlag, java.util.ArrayList<com.duowan.gamenews.Team> teamList, java.util.ArrayList<String> liveSourceList)
    {
        setId(id);
        setLiveTime(liveTime);
        setSportType(sportType);
        setLeagueName(leagueName);
        setRaceFlag(raceFlag);
        setTeamList(teamList);
        setLiveSourceList(liveSourceList);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        RaceInfo t = (RaceInfo) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(id, t.id) && 
            com.duowan.taf.jce.JceUtil.equals(liveTime, t.liveTime) && 
            com.duowan.taf.jce.JceUtil.equals(sportType, t.sportType) && 
            com.duowan.taf.jce.JceUtil.equals(leagueName, t.leagueName) && 
            com.duowan.taf.jce.JceUtil.equals(raceFlag, t.raceFlag) && 
            com.duowan.taf.jce.JceUtil.equals(teamList, t.teamList) && 
            com.duowan.taf.jce.JceUtil.equals(liveSourceList, t.liveSourceList) );
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
        if (null != id)
        {
            _os.write(id, 0);
        }
        _os.write(liveTime, 1);
        if (null != sportType)
        {
            _os.write(sportType, 2);
        }
        if (null != leagueName)
        {
            _os.write(leagueName, 3);
        }
        _os.write(raceFlag, 4);
        if (null != teamList)
        {
            _os.write(teamList, 5);
        }
        if (null != liveSourceList)
        {
            _os.write(liveSourceList, 6);
        }
    }

    static java.util.ArrayList<com.duowan.gamenews.Team> cache_teamList;
    static java.util.ArrayList<String> cache_liveSourceList;

    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setId( _is.readString(0, false));

        setLiveTime((int) _is.read(liveTime, 1, false));

        setSportType( _is.readString(2, false));

        setLeagueName( _is.readString(3, false));

        setRaceFlag((int) _is.read(raceFlag, 4, false));

        if(null == cache_teamList)
        {
            cache_teamList = new java.util.ArrayList<com.duowan.gamenews.Team>();
            com.duowan.gamenews.Team __var_61 = new com.duowan.gamenews.Team();
            ((java.util.ArrayList<com.duowan.gamenews.Team>)cache_teamList).add(__var_61);
        }
        setTeamList((java.util.ArrayList<com.duowan.gamenews.Team>) _is.read(cache_teamList, 5, false));

        if(null == cache_liveSourceList)
        {
            cache_liveSourceList = new java.util.ArrayList<String>();
            String __var_62 = "";
            ((java.util.ArrayList<String>)cache_liveSourceList).add(__var_62);
        }
        setLiveSourceList((java.util.ArrayList<String>) _is.read(cache_liveSourceList, 6, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(id, "id");
        _ds.display(liveTime, "liveTime");
        _ds.display(sportType, "sportType");
        _ds.display(leagueName, "leagueName");
        _ds.display(raceFlag, "raceFlag");
        _ds.display(teamList, "teamList");
        _ds.display(liveSourceList, "liveSourceList");
    }

}

