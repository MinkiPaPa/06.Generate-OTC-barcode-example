package kr.co.nicevan.genotcbarcode.util;

import android.content.Context;

public class SharedManager {

    private SharedPreferenceControl pref = null;

    public SharedManager(Context context){
        pref = SharedPreferenceControl.getInstance(context);
    }

    public String getServerIP() {
        String result = pref.getValue(PreferenceKeys.serverIP.name(), "");
        return result;
    }

    public void setServerIP(String serverIP) {
        pref.setValue(PreferenceKeys.serverIP.name(), serverIP);
    }

    public String getServerPort() {
        String result = pref.getValue(PreferenceKeys.serverPort.name(), "");
        return result;
    }

    public void setServerPort(String serverPort) {
        pref.setValue(PreferenceKeys.serverPort.name(), serverPort);
    }

    public boolean getIsCert() {
        boolean result = pref.getValue(PreferenceKeys.isCert.name(), false);
        return result;
    }

    public void setIsCert(boolean isCert) {
        pref.setValue(PreferenceKeys.isCert.name(), isCert);
    }

    public String getServerCert() {
        String result = pref.getValue(PreferenceKeys.serverCert.name(), "");
        return result;
    }

    public void setServerCert(String serverCert) {
        pref.setValue(PreferenceKeys.serverCert.name(), serverCert);
    }

    public String getCardId() {
        String result = pref.getValue(PreferenceKeys.cardId.name(), "");
        return result;
    }

    public void setCardId(String cardId) {
        pref.setValue(PreferenceKeys.cardId.name(), cardId);
    }

    public String getSessionKey() {
        String result = pref.getValue(PreferenceKeys.sessionKey.name(), "");
        return result;
    }

    public void setSessionKey(String sessionKey) {
        pref.setValue(PreferenceKeys.sessionKey.name(), sessionKey);
    }

}