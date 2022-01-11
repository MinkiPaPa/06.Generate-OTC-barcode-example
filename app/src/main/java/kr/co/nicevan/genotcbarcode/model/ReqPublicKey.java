package kr.co.nicevan.genotcbarcode.model;

import com.google.gson.annotations.SerializedName;

public class ReqPublicKey {

    public String header = "";
    @SerializedName("Body")
    public Body body = new Body();

    public class Body {

        @SerializedName("A1")
        public A1 a1 = new A1();
        @SerializedName("A2")
        public A2 a2 = new A2();

        public class A1 {

            public String MAC_VALUE = "";

        }

        public class A2 {

            public String PAY_COMP_CODE = "";
            public String SYSTEM_CD = "";
            public String TEXT_ID = "";
            public String TRANS_ID = "";
            public String TRANS_DT = "";
            public String TRANSACTION_ID = "";
            public String UUID = "";

        }

    }

}