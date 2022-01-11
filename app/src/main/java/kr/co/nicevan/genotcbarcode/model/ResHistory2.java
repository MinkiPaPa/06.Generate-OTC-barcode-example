package kr.co.nicevan.genotcbarcode.model;

import com.google.gson.annotations.SerializedName;

public class ResHistory2 {

    public String PAY_COMP_CODE = "";
    public String SYSTEM_CD = "";
    public String TEXT_ID = "";
    public String TRANS_ID = "";
    public String TRANS_DT = "";
    public String TRANSACTION_ID = "";
    public String Card_ID = "";
    @SerializedName("Repeat_Data")
    public Repeat_Data repeat_data = new Repeat_Data();

    public static class Repeat_Data {

        public String EXCHANGE_DATE = "";
        public String EXCHANGE_NAME = "";
        public String EXCHANGE_KIND = "";
        public String USE_AMOUNT = "";
        public String BALANCE = "";
    }
}