package kr.co.nicevan.genotcbarcode.model;

import com.google.gson.annotations.SerializedName;

public class ReqDelCardA1 {

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
        public String a2 = "";

    }



}