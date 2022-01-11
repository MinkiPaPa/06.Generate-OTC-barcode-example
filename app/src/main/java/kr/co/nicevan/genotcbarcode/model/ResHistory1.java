package kr.co.nicevan.genotcbarcode.model;

import com.google.gson.annotations.SerializedName;

public class ResHistory1 {

    public String RESULT_CODE = "";
    public String RESULT_MSG = "";
    public String header = "";
    public String Body = "";

    @SerializedName("REPEAT_DATA")
    public Repeat_Data repeat_data = new Repeat_Data();

    public class Repeat_Data {

        public String TRAN_DT = "";
        public String MERC_NM = "";
        public String TRAN_KD = "";
        public String TRAN_AMT = "";
        public String BALANCE = "";
    }
}
