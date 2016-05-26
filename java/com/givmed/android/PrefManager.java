package com.givmed.android;

/**
 * Created by agroikos on 29/12/2015.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;



//SharedPreference: Store private primitive data in key-value pairs. (To store small entries/data)
//Internal Storage: Store private data on the device memory. (To store large datasets)

public class PrefManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "GivMed";

    // All Shared Preferences Keys
    private static final String KEY_IS_WAITING_FOR_SMS = "IsWaitingForSms";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_SEX = "sex";
    private static final String KEY_BIRTH = "birth";
    private static final String KEY_NEED_DATE = "needDate";
    private static final String KEY_PHAR_DATE = "pharDate";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_NOTIFICATION_PERMIT = "notification_permit";
    private static final String KEY_COUNTDOWN = "countdown";
    private static final String KEY_UPDATE_DONATIONS = "update_donations";
    private static final String KEY_OLD_MONTH = "old_month";
    private static final String KEY_REG_DONE = "reg_done";
    private static final String KEY_OLD_USER = "old_user";
    private static final String KEY_NEXT_SPLASH = "next_splash";
    private static final String KEY_DATA_LOADED = "data_loaded";

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String SMS_ORIGIN = "givmed";

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    public static final int MY_PERMISSIONS_REQUEST_SMS = 2;


    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";




    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsWaitingForSms(boolean isWaiting) {
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        editor.commit();
    }

    public boolean isWaitingForSms() {
        return pref.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    public void setMobileNumber(String mobileNumber) {
        editor.putString(KEY_MOBILE_NUMBER, mobileNumber);
        editor.commit();
    }

    public String getMobileNumber() {
        return pref.getString(KEY_MOBILE_NUMBER, null);
    }

    public void setNextSplash(String splash) {
        editor.putString(KEY_NEXT_SPLASH, splash);
        editor.commit();
    }

    public String getNextSplash() {
        return pref.getString(KEY_NEXT_SPLASH, "Tutorial");
    }

    public void setCountdown(String countdown) {
        editor.putString(KEY_COUNTDOWN, countdown);
        editor.commit();
    }

    public String getCountdown() {
        return pref.getString(KEY_COUNTDOWN, "first");
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public void setToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public boolean getNotificiationPermission() {
        return pref.getBoolean(KEY_NOTIFICATION_PERMIT, true); //to default einai na epitrepontai
    }

    public void setNotificationPermission(Boolean permit) {
        editor.putBoolean(KEY_NOTIFICATION_PERMIT, permit);
        editor.commit();
    }

    public int getOldMonth() {
        return pref.getInt(KEY_OLD_MONTH, -1);
    }

    public void setUsername(String username) {
        editor.putString(KEY_NAME, username);
        editor.commit();
    }

    public String getUsername() {
        return pref.getString(KEY_NAME, "");
    }

    public void setEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public void setRegDone(boolean flag) {
        editor.putBoolean(KEY_REG_DONE, flag);
        editor.commit();
    }

    public boolean getOldUser() {
        return pref.getBoolean(KEY_OLD_USER, false);
    }

    public void setOldUser(boolean flag) {
        editor.putBoolean(KEY_OLD_USER, flag);
        editor.commit();
    }

    public boolean getRegDone() {
        return pref.getBoolean(KEY_REG_DONE, false);
    }

    public void setSex(String sex) {
        editor.putString(KEY_SEX, sex);
        editor.commit();
    }

    public String getSex() {
        return pref.getString(KEY_SEX, "F");
    }

    public void setNeedDate(String date) {
        editor.putString(KEY_NEED_DATE, date);
        editor.commit();
    }

    public String getNeedDate() {
        return pref.getString(KEY_NEED_DATE, "1992-08-07");
    }

    public void setPharDate(String date) {
        editor.putString(KEY_PHAR_DATE, date);
        editor.commit();
    }

    public String getPharDate() {
        return pref.getString(KEY_PHAR_DATE, "1992-08-07");
    }

    public void setBirthDate(String birthDate) {
        editor.putString(KEY_BIRTH, birthDate);
        editor.commit();
    }

    public String getBirthDate() {
        return pref.getString(KEY_BIRTH, "");
    }

    public void setAddress(String address) {
        editor.putString(KEY_ADDRESS, address);
        editor.commit();
    }

    public String getAddress() {
        return pref.getString(KEY_ADDRESS, "");
    }

    public void setHasLoadedData(boolean hasHe) {
        editor.putBoolean(KEY_DATA_LOADED, hasHe);
        editor.commit();
    }

    public boolean getHasLoadedData() {
        return pref.getBoolean(KEY_DATA_LOADED, false);
    }

    public void setOldMonth(int month) {
        editor.putInt(KEY_OLD_MONTH, month);
        editor.commit();
    }

    public void createLogin() {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }
//
//    public void createLogin(String name, String email, String mobile) {
//        editor.putString(KEY_NAME, name);
//        editor.putString(KEY_EMAIL, email);
//        editor.putString(KEY_MOBILE, mobile);
//        editor.putBoolean(KEY_IS_LOGGED_IN, true);
//        editor.commit();
//    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}
