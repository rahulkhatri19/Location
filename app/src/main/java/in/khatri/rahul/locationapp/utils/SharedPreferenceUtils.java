package in.khatri.rahul.locationapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtils {
    private Context mContext;
    SharedPreferences pref;
    public SharedPreferenceUtils(Context context) {
        this.mContext = context;
        try {
            pref = this.mContext.getSharedPreferences("Location", Context.MODE_PRIVATE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setLoginFlag(boolean flag){
        SharedPreferences.Editor editor= pref.edit();
        editor.putBoolean("loginFlag", flag);
        editor.commit();
    }
    public boolean getLoginFlag(){
        if (pref.contains("loginFlag"))
            return pref.getBoolean("loginFlag", false);
        else return false;
    }
    public void setPhone(String flag){
        SharedPreferences.Editor editor= pref.edit();
        editor.putString("phone", flag);
        editor.commit();
    }
    public String getPhone(){
        if (pref.contains("phone"))
            return pref.getString("phone", "");
        else return "null";
    }
    public void setLogin(String flag){
        SharedPreferences.Editor editor= pref.edit();
        editor.putString("log", flag);
        editor.commit();
    }
    public String getLogin(){
        if (pref.contains("log"))
            return pref.getString("log", "");
        else return "null";
    }
    public void setName(String flag){
        SharedPreferences.Editor editor= pref.edit();
        editor.putString("name", flag);
        editor.commit();
    }
    public String getName(){
        if (pref.contains("name"))
            return pref.getString("name", "");
        else return "null";
    }
}
