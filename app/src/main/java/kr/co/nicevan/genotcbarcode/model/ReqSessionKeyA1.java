package kr.co.nicevan.genotcbarcode.model;

import com.google.gson.annotations.SerializedName;

public class ReqSessionKeyA1 {

    public String header = "";
    @SerializedName("Body")
    public Body body = new Body();

    public class Body {

        @SerializedName("A1")
        public A1 a1 = new A1();

        public class A1 {

            public String SESSION_ID = "";
            public String MAC_VALUE = "";

        }

        @SerializedName("A2")
 //       public String a2 = "";
        public A2 a2 = new A2();
        public class A2 {

            public String PAY_COMP_CODE = "";
            public String SYSTEM_CD = "";
            public String TEXT_ID = "";
            public String TRANS_ID = "";
            public String TRANS_DT = "";
            public String TRANSACTION_ID = "";
            public String MOBILE_PHONE = "";
            public String UUID = "";
            public String OS_VER = "";
            public String OS_TYPE = "";
            public String PARTNER_MEMBER_ID = "";
            public String CORP_NUMBER = "";
            public String SESSION_KEY = "";

        }
    }



}