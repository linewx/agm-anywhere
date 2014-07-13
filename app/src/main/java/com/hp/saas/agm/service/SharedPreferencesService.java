package com.hp.saas.agm.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesService {
	private final String NAME="HP";
    private Context context;
    private SharedPreferences sharedPreferences;

    public void init(Context applicationContext) {
        this.context = applicationContext;
        this.sharedPreferences = this.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }


	public void setPreference(String key,String value){
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

    public String getPreference(String key) {
        return sharedPreferences.getString(key, null);
    }



}
