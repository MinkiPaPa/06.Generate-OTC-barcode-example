package kr.co.nicevan.genotcbarcode.model;

import com.google.gson.annotations.SerializedName;

public class ResPublicKey {

    public String RESULT_CODE = "";
    public String RESULT_MSG = "";
    public String header = "";
    @SerializedName("Body")
    public Body body = new Body();

    public class Body {

        public String PAY_COMP_CODE = "";
        public String SYSTEM_CD = "";
        public String TEXT_ID = "";
        public String TRANS_ID = "";
        public String TRANS_DT = "";
        public String TRANSACTION_ID = "";
        public String PUBLIC_KEY = "";

    }

}