package pe.edu.utp.unihelppro.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class SharedPrefsUtils {
    private static SharedPrefsUtils privateInstance;
    private SharedPreferences sharedPreferences;


    public static void init(Application application) {
        if(privateInstance == null) {
            privateInstance = new SharedPrefsUtils(application);
        }
    }

    private SharedPrefsUtils(Application application) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
    }
    public static SharedPrefsUtils getInstance() {
        return privateInstance;
    }

    public String getStringPreference(String key) {
        String value = "";
        if (sharedPreferences != null) {
            value = sharedPreferences.getString(key, "");
        }
        return value;
    }

    public boolean setStringPreference(String key, String value) {
        if (sharedPreferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    public float getFloatPreference(String key, float defaultValue) {
        float value = defaultValue;
        if (sharedPreferences != null) {
            value = sharedPreferences.getFloat(key, defaultValue);
        }
        return value;
    }

    public boolean setFloatPreference(String key, float value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(key, value);
            return editor.commit();
        }
        return false;
    }

    public long getLongPreference(String key, long defaultValue) {
        long value = defaultValue;
        if (sharedPreferences != null) {
            value = sharedPreferences.getLong(key, defaultValue);
        }
        return value;
    }

    public boolean setLongPreference(String key, long value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(key, value);
            return editor.commit();
        }
        return false;
    }

    public int getIntegerPreference(String key, int defaultValue) {
        int value = defaultValue;
        if (sharedPreferences != null) {
            value = sharedPreferences.getInt(key, defaultValue);
        }
        return value;
    }

    public boolean setIntegerPreference(String key, int value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            return editor.commit();
        }
        return false;
    }

    public boolean getBooleanPreference(String key, boolean defaultValue) {
        boolean value = defaultValue;
        if (sharedPreferences != null) {
            value = sharedPreferences.getBoolean(key, defaultValue);
        }
        return value;
    }

    public boolean setBooleanPreference(String key, boolean value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        }
        return false;
    }
}
