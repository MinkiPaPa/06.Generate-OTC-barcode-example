package kr.co.nicevan.genotcbarcode;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gglimsoft.glink.e2e.security.E2EGWClientAnd;
import com.gglimsoft.glink.gw.client.security.E2EGWClient;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.co.nicevan.genotcbarcode.model.ReqDelCardA1;
import kr.co.nicevan.genotcbarcode.model.ReqDelCardA2;
import kr.co.nicevan.genotcbarcode.model.ReqIssueOTCA1;
import kr.co.nicevan.genotcbarcode.model.ReqIssueOTCA2;
import kr.co.nicevan.genotcbarcode.model.ReqPublicKey;
import kr.co.nicevan.genotcbarcode.model.ReqRegCardA1;
import kr.co.nicevan.genotcbarcode.model.ReqRegCardA2;
import kr.co.nicevan.genotcbarcode.model.ReqSessionKeyA1;
import kr.co.nicevan.genotcbarcode.model.ReqSessionKeyA2;
import kr.co.nicevan.genotcbarcode.model.ResDelCard;
import kr.co.nicevan.genotcbarcode.model.ResIssueOTC;
import kr.co.nicevan.genotcbarcode.model.ResPublicKey;
import kr.co.nicevan.genotcbarcode.model.ResRegCard;
import kr.co.nicevan.genotcbarcode.model.ResRegCard1;
import kr.co.nicevan.genotcbarcode.model.ResRegCard2;
import kr.co.nicevan.genotcbarcode.model.ResSession;
import kr.co.nicevan.genotcbarcode.util.RandomUtil;
import kr.co.nicevan.genotcbarcode.util.SHA256;
import kr.co.nicevan.genotcbarcode.util.SharedManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingActivity extends AppCompatActivity {

    EditText editIP, editPort;
    Button btnDownload, btnSession, btnRegCard, btnDelCard, btnClose, btnCardview ;
    TextView textCertInfo, textMessage;
    String finalCertKey;

    SharedManager mSharedManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mSharedManager = new SharedManager(getApplicationContext());

        editIP = findViewById(R.id.EditIP);
        editIP.setText(mSharedManager.getServerIP());

        if (editIP.getText().toString().equals("")) {
            editIP.setText("ambc.nicevan.co.kr");
        }

        editPort = findViewById(R.id.EditPort);
        editPort.setText(mSharedManager.getServerPort());

        if (editPort.getText().toString().equals("")) {
            editPort.setText("3443");
        }

        btnDownload = findViewById(R.id.BtnDownLoad);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    mSharedManager.setServerIP(editIP.getText().toString());
                    mSharedManager.setServerPort(editPort.getText().toString());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

                    ReqPublicKey reqPublicKey = new ReqPublicKey();

                    reqPublicKey.header = "";

                    reqPublicKey.body.a2.PAY_COMP_CODE = "1000";
                    reqPublicKey.body.a2.SYSTEM_CD = "1000";
                    reqPublicKey.body.a2.TEXT_ID = "0200";
                    reqPublicKey.body.a2.TRANS_ID = "N002";
                    reqPublicKey.body.a2.TRANS_DT = sdf.format(new Date());
                    reqPublicKey.body.a2.TRANSACTION_ID = RandomUtil.randomString(12);
                    reqPublicKey.body.a2.UUID = "ABCDEFGHIJKLMN01234567890";

                    reqPublicKey.body.a1.MAC_VALUE = generateReqPublicKeyMACValue(reqPublicKey);

                    String json = new Gson().toJson(reqPublicKey);
                    Log.d("niceckt", json);

                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url(String.format("https://%s:%s/ioc/pubKey_N002.do", editIP.getText().toString(), editPort.getText().toString()))
                            .post(RequestBody.create(MediaType.parse("application/json"), json))
                            .build();

                    new Thread() {
                        @Override
                        public void run() {
                            Response response = null;
                            try {
                                response = client.newCall(request).execute();

                                if (response.body() != null) {

                                    String body = response.body().string();

                                    ResPublicKey res = new Gson().fromJson(body, ResPublicKey.class);

                                    if (res != null) {
                                        if (res.RESULT_CODE.equals("0000")) {
                                            mSharedManager.setIsCert(true);
                                            mSharedManager.setServerCert(res.body.PUBLIC_KEY);
                                            finalCertKey = res.body.PUBLIC_KEY;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    textCertInfo.setText(res.body.PUBLIC_KEY);
                                                }
                                            });

                                        }
                                    }

                                }

                                Log.d("niceckt", "Response : " + response.toString());

                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textMessage.setText(e.getLocalizedMessage());
                                    }
                                });

                            }

                        }
                    }.start();

                } catch (Exception e) {
                    textMessage.setText(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        });

        btnRegCard = findViewById(R.id.BtnRegCard);
        btnRegCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sessionKey = null;
                String encSessionKey = null;
                String encData = null;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

                ReqRegCardA2 reqRegCardA2 = new ReqRegCardA2();

                reqRegCardA2.PAY_COMP_CODE = "1000";
                reqRegCardA2.SYSTEM_CD = "1000";
                reqRegCardA2.TEXT_ID = "0200";
                reqRegCardA2.TRANS_ID = "N004";
                reqRegCardA2.TRANS_DT = sdf.format(new Date());
                reqRegCardA2.TRANSACTION_ID = GlobalVars.getTransId();
                reqRegCardA2.MOBILE_PHONE = "01012345678";
                reqRegCardA2.DEVICE_MODEL_NAME = "ABCDEFGHIJKL";
                reqRegCardA2.DEVICE_ID = "A0123456789";
                reqRegCardA2.UUID = "ABCDEFGHIJKLMN01234567890";

                String tempJson = new Gson().toJson(reqRegCardA2);

                E2EGWClientAnd e2EGWClient = new E2EGWClientAnd();

                try {

//                    sessionKey = e2EGWClient.E2EGWC_MakeSessionKey();

//                    encSessionKey = e2EGWClient.E2EGWC_GetnEncSessionKeyAndroid(mSharedManager.getServerCert(), sessionKey);

                    encData = e2EGWClient.E2EGWC_EncBlockData(mSharedManager.getSessionKey(), tempJson.getBytes());
                    sessionKey = mSharedManager.getSessionKey();
                } catch (Exception e) {
                    Log.e("niceckt", e.getLocalizedMessage());
                }

                ReqRegCardA1 reqRegCardA1 = new ReqRegCardA1();

                reqRegCardA1.body.a1.SESSION_ID = generateReqRegCardSessionId(reqRegCardA2);
                reqRegCardA1.body.a1.MAC_VALUE = generateReqRegCardMACValue(reqRegCardA2);

                reqRegCardA1.body.a2 = encData;

                String json = new Gson().toJson(reqRegCardA1);
                Log.d("niceckt", json);

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(String.format("https://%s:%s/ioc/regCard_N004.do", editIP.getText().toString(), editPort.getText().toString()))
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();

                new Thread() {
                    @Override
                    public void run() {
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();

                            String res = response.body().string();

//                            byte[] resBytes = e2EGWClient.E2EGWC_DecBlockData(finalSessionKey, res);
//
//                            String resString = new String(resBytes);

                            ResRegCard1 resRegCard1 = new Gson().fromJson(res, ResRegCard1.class);

                            if (resRegCard1 != null) {
                                if (resRegCard1.RESULT_CODE.equals("0000")) {
                                    String decString = resRegCard1.Body;
                                    byte[] resBytes = e2EGWClient.E2EGWC_DecBlockData(mSharedManager.getSessionKey(), decString);
                                    String resString = new String(resBytes);
                                    ResRegCard2 resRegCard2 = new Gson().fromJson(resString, ResRegCard2.class);
                                    mSharedManager.setCardId(resRegCard2.CARD_ID);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textMessage.setText("카드 등록 성공 : " + resRegCard2.CARD_ID);
                                        }
                                    });
                                } else {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textMessage.setText(resRegCard1.RESULT_CODE + " : " + resRegCard1.RESULT_MSG);
                                        }
                                    });

                                }
                            }

                            Log.d("niceckt", "Response : " + response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }.start();

            }
        });

        btnSession = findViewById(R.id.BtnSession);
        btnSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sessionKey = null;
                String encSessionKey = null;
                String encData = null;

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

                ReqSessionKeyA1 reqSessionKeyA1 = new ReqSessionKeyA1();

                reqSessionKeyA1.body.a2.PAY_COMP_CODE = "1000";
                reqSessionKeyA1.body.a2.SYSTEM_CD = "1000";
                reqSessionKeyA1.body.a2.TEXT_ID = "0200";
                reqSessionKeyA1.body.a2.TRANS_ID = "N003";
                reqSessionKeyA1.body.a2.TRANS_DT = sdf.format(new Date());
                GlobalVars.setTransId(RandomUtil.randomString(12));
                reqSessionKeyA1.body.a2.TRANSACTION_ID = GlobalVars.getTransId();
                reqSessionKeyA1.body.a2.MOBILE_PHONE = "01012345678";
                reqSessionKeyA1.body.a2.UUID = "ABCDEFGHIJKLMN01234567890";
                reqSessionKeyA1.body.a2.OS_VER = "4.4.2";
                reqSessionKeyA1.body.a2.OS_TYPE = "1";
                reqSessionKeyA1.body.a2.PARTNER_MEMBER_ID = "";
                reqSessionKeyA1.body.a2.CORP_NUMBER = "2208115770";

                String result = "";

                result = reqSessionKeyA1.body.a2.SYSTEM_CD + reqSessionKeyA1.body.a2.UUID + reqSessionKeyA1.body.a2.TRANSACTION_ID;

                reqSessionKeyA1.body.a1.SESSION_ID = SHA256.sha256Hash(result);

                String result2 = "";

                result2 = reqSessionKeyA1.body.a2.TRANSACTION_ID + reqSessionKeyA1.body.a2.SYSTEM_CD + "2187";

                reqSessionKeyA1.body.a1.MAC_VALUE = SHA256.sha256Hash(result2);


                E2EGWClientAnd e2EGWClient = new E2EGWClientAnd();

                try {

                    reqSessionKeyA1.body.a2.SESSION_KEY = e2EGWClient.E2EGWC_MakeSessionKey();
                    sessionKey = reqSessionKeyA1.body.a2.SESSION_KEY;
                    mSharedManager.setSessionKey(reqSessionKeyA1.body.a2.SESSION_KEY);

                    reqSessionKeyA1.body.a2.UUID = e2EGWClient.E2EGWC_RSAEncDataAndroid(mSharedManager.getServerCert(), reqSessionKeyA1.body.a2.UUID.getBytes());
                    reqSessionKeyA1.body.a2.SESSION_KEY = e2EGWClient.E2EGWC_RSAEncDataAndroid(mSharedManager.getServerCert(), mSharedManager.getSessionKey().getBytes());

                } catch (Exception e) {
                    Log.e("niceckt", e.getLocalizedMessage());
                }

                String json = new Gson().toJson(reqSessionKeyA1);

                Log.d("niceckt", json);

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(String.format("https://%s:%s/ioc/sessKey_N003.do", editIP.getText().toString(), editPort.getText().toString()))
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();

                new Thread() {
                    @Override
                    public void run() {
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();

                            String res = response.body().string();

//                            byte[] resBytes = e2EGWClient.E2EGWC_DecBlockData(finalSessionKey, res);
//
//                            String resString = new String(resBytes);

                            ResSession resSession = new Gson().fromJson(res, ResSession.class);

                            if (resSession != null) {
                                if (resSession.RESULT_CODE.equals("0000")) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textMessage.setText("세션키 요청 성공");
                                        }
                                    });
                                } else {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textMessage.setText(resSession.RESULT_CODE + " : " + resSession.RESULT_MSG);
                                        }
                                    });

                                }
                            }

                            Log.d("niceckt", "Response : " + response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }.start();

            }
        });

        btnDelCard = findViewById(R.id.BtnDelCard);
        btnDelCard.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {

                String cardId = mSharedManager.getCardId();

                if (cardId == null || cardId.equals("")) {
                    Toast.makeText(SettingActivity.this, "등록된 카드가 존재하지 않습니다.", Toast.LENGTH_LONG);
                    return;
                }

                String sessionKey = null;
                String encSessionKey = null;
                String encData = null;

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

                ReqDelCardA2 reqDelCardA2 = new ReqDelCardA2();

                reqDelCardA2.PAY_COMP_CODE = "1000";
                reqDelCardA2.SYSTEM_CD = "1000";
                reqDelCardA2.TEXT_ID = "0200";
                reqDelCardA2.TRANS_ID = "N005";
                reqDelCardA2.TRANS_DT = sdf.format(new Date());
                reqDelCardA2.TRANSACTION_ID = GlobalVars.getTransId();;
                reqDelCardA2.CARD_ID = cardId;
                reqDelCardA2.DEL_TP = "1";
                reqDelCardA2.UUID = "ABCDEFGHIJKLMN01234567890";
                reqDelCardA2.OS_TYPE = "1";

                String tempJson = new Gson().toJson(reqDelCardA2);

                E2EGWClientAnd e2EGWClient = new E2EGWClientAnd();

                try {
                    sessionKey = mSharedManager.getSessionKey();
                    encData = e2EGWClient.E2EGWC_EncBlockData(mSharedManager.getSessionKey(), tempJson.getBytes());

                } catch (Exception e) {
                    Log.e("niceckt", e.getLocalizedMessage());
                }

                final String finalSessionKey = sessionKey;

                ReqDelCardA1 reqDelCardA1 = new ReqDelCardA1();

                reqDelCardA1.body.a1.SESSION_ID = generateReqDelCardSessionId(reqDelCardA2);
                reqDelCardA1.body.a1.MAC_VALUE = generateReqDelCardMACValue(reqDelCardA2);

                reqDelCardA1.body.a2 = encData;

                String json = new Gson().toJson(reqDelCardA1);
                Log.d("niceckt", json);

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(String.format("https://%s:%s/ioc/delCard_N005.do", editIP.getText().toString(), editPort.getText().toString()))
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();

                new Thread() {
                    @Override
                    public void run() {
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();

                            String res = response.body().string();

//                            byte[] resBytes = e2EGWClient.E2EGWC_DecBlockData(finalSessionKey, res);

                            ResDelCard resDelCard = new Gson().fromJson(res, ResDelCard.class);

                            if (resDelCard != null) {

                                if (resDelCard.RESULT_CODE.equals("0000")) {
                                    mSharedManager.setCardId("");
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textMessage.setText("카드 삭제 완료");
                                    }
                                });
                            }

                            Log.d("niceckt", "Response : " + response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }.start();

            }
        });

        btnClose = findViewById(R.id.BtnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textCertInfo = findViewById(R.id.TextCertInfo);
        textMessage = findViewById(R.id.TextMessage);
    }

    private String generateReqPublicKeyMACValue(ReqPublicKey reqPublicKey) {

        String result = "";

        result = reqPublicKey.body.a2.TRANSACTION_ID + reqPublicKey.body.a2.SYSTEM_CD + "2187";

        result = SHA256.sha256Hash(result);

        return result;

    }

    private String generateReqRegCardSessionId(ReqRegCardA2 reqRegCard) {

        String result = "";

        result = reqRegCard.SYSTEM_CD + reqRegCard.UUID + reqRegCard.TRANSACTION_ID;

        result = SHA256.sha256Hash(result);

        return result;

    }

    private String generateReqRegCardMACValue(ReqRegCardA2 reqRegCard) {

        String result = "";

        result = reqRegCard.TRANSACTION_ID + reqRegCard.SYSTEM_CD + "2187";

        result = SHA256.sha256Hash(result);

        return result;

    }

    private String generateReqDelCardSessionId(ReqDelCardA2 reqDelCard) {

        String result = "";

        result = reqDelCard.SYSTEM_CD + reqDelCard.UUID + reqDelCard.TRANSACTION_ID;

        result = SHA256.sha256Hash(result);

        return result;

    }

    private String generateReqDelCardMACValue(ReqDelCardA2 reqDelCard) {

        String result = "";

        result = reqDelCard.TRANSACTION_ID + reqDelCard.SYSTEM_CD + "2187";

        result = SHA256.sha256Hash(result);

        return result;

    }

    private String generateReqSessionSessionId(ReqSessionKeyA2 reqSession) {

        String result = "";

        result = reqSession.SYSTEM_CD + reqSession.UUID + reqSession.TRANSACTION_ID;

        result = SHA256.sha256Hash(result);

        return result;

    }

    private String generateReqSessionMACValue(ReqSessionKeyA2 reqSession) {

        String result = "";

        result = reqSession.TRANSACTION_ID + reqSession.SYSTEM_CD + "2187";

        result = SHA256.sha256Hash(result);

        return result;

    }

}