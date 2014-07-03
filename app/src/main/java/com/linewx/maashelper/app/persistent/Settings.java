package com.linewx.maashelper.app.persistent;


import android.content.Context;
import android.content.SharedPreferences;
import com.hp.alm.ali.utils.SpUtil;

/**
 * Created by lugan on 6/24/2014.
 */
public class Settings {


    private String user;
    private String password;
    private String teamId;
    private String teamName;
    private String releaseId;
    private String releaseName;
    private SharedPreferences sp;

    private static Settings settings = new Settings();

    private Settings() {

    }

    public static Settings getInstance() {
        return settings;
    }

    public void init(Context context) {
        sp = SpUtil.getInstance().getSharePerference(context);
        user = sp.getString("user", null);
        password = sp.getString("password", null);
        teamId = sp.getString("teamId", null);
        teamName = sp.getString("teamName", null);
        releaseId = sp.getString("releaseId", null);
        releaseName = sp.getString("releaseName", null);
    }

    public void setUser(String theUser) {
        user = theUser;
        SpUtil.getInstance().setStringSharedPerference(sp, "user", user);
    }

    public void setPassword(String thePassword) {
        password = thePassword;
        SpUtil.getInstance().setStringSharedPerference(sp, "password", password);
    }

    public void setReleaseId(String theReleaseId) {
        releaseId = theReleaseId;
        SpUtil.getInstance().setStringSharedPerference(sp, "releaseId", releaseId);
    }


    public void setReleaseName(String theReleaseName) {
        releaseName = theReleaseName;
        SpUtil.getInstance().setStringSharedPerference(sp, "releaseName", releaseName);
    }



    public void setTeamId(String theTeamId) {
        teamId = theTeamId;
        SpUtil.getInstance().setStringSharedPerference(sp, "teamId", teamId);
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }
}
