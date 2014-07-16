package com.hp.saas.agm.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.io.*;

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

    public void saveToFile(String filename, byte[]bytes) {
        try {
            FileOutputStream fs = context.openFileOutput(filename, Context.MODE_PRIVATE);
            BufferedOutputStream bos = new BufferedOutputStream(fs);
            bos.write(bytes);
            bos.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getFromFile(String filename) {
        InputStream result = null;
        try {
            result = context.openFileInput(filename);
        }catch(IOException e) {
            e.printStackTrace();
        }
        return result;
    }



    public void setObjectPreference(String filename, Object value) {
        try {
            FileOutputStream fs = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream os  =  new ObjectOutputStream(fs);
            os.writeObject(value);
            os.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Object getObjectPreference(String filename) {
        try {
            FileInputStream fs = context.openFileInput(filename);
            ObjectInputStream os = new ObjectInputStream(fs);
            return os.readObject();
        }catch(IOException e) {
            e.printStackTrace();
            return null;

        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }




}
