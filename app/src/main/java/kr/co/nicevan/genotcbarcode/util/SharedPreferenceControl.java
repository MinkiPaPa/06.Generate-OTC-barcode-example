package kr.co.nicevan.genotcbarcode.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceControl {

    private static final String SHARED_TAG = "GenOTCBarcode";
    private SharedPreferences spref = null;
    private Editor editor = null;
    private static SharedPreferenceControl mSpref = null;
    private final String DEFVALUE = "0";

    public SharedPreferenceControl(Context context )
    {
        this.spref = context.getSharedPreferences( SHARED_TAG, Context.MODE_PRIVATE );
        this.editor = this.spref.edit();
    }

    public static SharedPreferenceControl getInstance( Context context )
    {
        if( mSpref == null )
        {
            mSpref = new SharedPreferenceControl( context );
        }
        return mSpref;
    }

    //get Method : Defined Defalut Value - Start
    public String getValue(String key, String defValue)
    {
        if(this.spref == null)
        {
            return defValue;
        }

        return this.spref.getString(key, defValue);
    }

    public int getValue(String key, int defValue)
    {
        if(this.spref == null)
        {
            return defValue;
        }

        return this.spref.getInt( key, defValue );

    }

    public long getValue(String key, long defValue)
    {
        if(this.spref == null)
        {
            return defValue;
        }

        return this.spref.getLong( key, defValue );

    }

    public boolean getValue(String key, boolean defValue)
    {
        if(this.spref == null)
        {
            return defValue;
        }
        return this.spref.getBoolean( key, defValue );
    }

    public float getValue(String key, float defValue)
    {
        if(this.spref == null)
        {
            return defValue;
        }
        return this.spref.getFloat( key, defValue );
    }
    //get Method : Defined Defalut Value - End

    //set Method - Start
    public void setValue(String key, String value)
    {
        if(this.editor == null)
        {
            return;
        }

        editor.putString(key, value);
        commit();
    }

    public void setValue(String key, int value)
    {
        if(this.editor == null)
        {
            return;
        }

        editor.putInt( key, value );
        commit();
    }

    public void setValue(String key, boolean value )
    {
        if(this.editor == null)
        {
            return;
        }

        editor.putBoolean( key, value );
        commit();
    }

    public void setValue(String key, long value )
    {
        if(this.editor == null)
        {
            return;
        }

        editor.putLong( key, value );
        commit();
    }

    public void setValue(String key, float value )
    {
        if(this.editor == null)
        {
            return;
        }

        editor.putFloat( key, value );
        commit();
    }

    public void onRemove( String key )
    {
        if(this.editor == null)
        {
            return;
        }

        editor.remove(key);
        commit();
    }
    //set Method - End

    private void commit()
    {
        if(this.editor == null)
        {
            return;
        }
        this.editor.commit();
    }

}
