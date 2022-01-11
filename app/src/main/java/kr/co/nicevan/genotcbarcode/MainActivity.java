package kr.co.nicevan.genotcbarcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.gglimsoft.glink.e2e.security.E2EGWClientAnd;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.co.nicevan.genotcbarcode.model.ReqIssueOTCA1;
import kr.co.nicevan.genotcbarcode.model.ReqIssueOTCA2;
import kr.co.nicevan.genotcbarcode.model.ResIssueOTC;
import kr.co.nicevan.genotcbarcode.model.ResIssueOTC1;
import kr.co.nicevan.genotcbarcode.model.ResIssueOTC2;
import kr.co.nicevan.genotcbarcode.model.ReqCharseA1;
import kr.co.nicevan.genotcbarcode.model.ReqCharseA2;
import kr.co.nicevan.genotcbarcode.model.ResCharse;
import kr.co.nicevan.genotcbarcode.model.ResCharse1;
import kr.co.nicevan.genotcbarcode.model.ResCharse2;
import kr.co.nicevan.genotcbarcode.model.ReqHistoryA1;
import kr.co.nicevan.genotcbarcode.model.ReqHistoryA2;
import kr.co.nicevan.genotcbarcode.model.ResHistory;
import kr.co.nicevan.genotcbarcode.model.ResHistory1;
import kr.co.nicevan.genotcbarcode.model.ResHistory2;
import kr.co.nicevan.genotcbarcode.model.ResSession;
import kr.co.nicevan.genotcbarcode.ViewActivity;
import kr.co.nicevan.genotcbarcode.util.RandomUtil;
import kr.co.nicevan.genotcbarcode.util.SHA256;
import kr.co.nicevan.genotcbarcode.util.SharedManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {

    Button btnSession, btnGenerate, btnSetting, btnChase, btnHistory;
    ImageView imageBarcode;
    TextView textapprono;
    EditText editAmount;
    private DecimalFormat decimalFormat = new DecimalFormat("##,###,###,##");
    private String result="";

    SharedManager mSharedManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedManager = new SharedManager(getApplicationContext());

        textapprono = (TextView) findViewById(R.id.textApprono);
        editAmount = (EditText) findViewById(R.id.editAmount);
        editAmount.addTextChangedListener(watcher);

        btnGenerate = findViewById(R.id.BtnGenerate);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sessionKey = null;
                String encSessionKey = null;
                String encData = null;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

                ReqIssueOTCA2 reqIssueOTCA2 = new ReqIssueOTCA2();

                reqIssueOTCA2.PAY_COMP_CODE = "1000";
                reqIssueOTCA2.SYSTEM_CD = "1000";
                reqIssueOTCA2.TEXT_ID = "0200";
                reqIssueOTCA2.TRANS_ID = "N008";
                reqIssueOTCA2.TRANS_DT = sdf.format(new Date());
                reqIssueOTCA2.TRANSACTION_ID = GlobalVars.getTransId();
                reqIssueOTCA2.CARD_ID = mSharedManager.getCardId();
                reqIssueOTCA2.UUID = "ABCDEFGHIJKLMN01234567890";

                String tempJson = new Gson().toJson(reqIssueOTCA2);

                E2EGWClientAnd e2EGWClient = new E2EGWClientAnd();

                try {

                    encData = e2EGWClient.E2EGWC_EncBlockData(mSharedManager.getSessionKey(), tempJson.getBytes());

                } catch (Exception e) {
                    Log.e("niceckt", e.getLocalizedMessage());
                }

                ReqIssueOTCA1 reqIssueOTCA1 = new ReqIssueOTCA1();

                reqIssueOTCA1.body.a1.SESSION_ID = generateSessionId(reqIssueOTCA2);
                reqIssueOTCA1.body.a1.MAC_VALUE = generateMACValue(reqIssueOTCA2);

                reqIssueOTCA1.body.a2 = encData;

                String json = new Gson().toJson(reqIssueOTCA1);
                Log.d("niceckt", json);

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(String.format("https://%s:%s/ioc/offlineOtc_N008.do", mSharedManager.getServerIP(), mSharedManager.getServerPort()))
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();

                new Thread() {
                    @Override
                    public void run() {
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();

                            String res = response.body().string();

 //                           byte[] resBytes = e2EGWClient.E2EGWC_DecBlockData(mSharedManager.getSessionKey(), res);

 //                           ResIssueOTC resIssueOTC = new Gson().fromJson(new String(resBytes), ResIssueOTC.class);
                            ResIssueOTC1 resIssueOTC1 = new Gson().fromJson(new String(res), ResIssueOTC1.class);

                            if (resIssueOTC1 != null) {
                                if (resIssueOTC1.RESULT_CODE.equals("0000")) {
                                    String decString = resIssueOTC1.Body;
                                    byte[] resBytes = e2EGWClient.E2EGWC_DecBlockData(mSharedManager.getSessionKey(), decString);
                                    String resString = new String(resBytes);
                                    ResIssueOTC2 resIssueOTC2 = new Gson().fromJson(resString, ResIssueOTC2.class);

                                    onShowBarcode(resIssueOTC2.OTC_NUM);
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

        btnChase = findViewById(R.id.BtnCharse);
        btnChase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sessionKey = null;
                String encSessionKey = null;
                String encData = null;
                String result2 = null;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

                ReqCharseA2 reqCharseA2 = new ReqCharseA2();

                reqCharseA2.PAY_COMP_CODE = "1000";
                reqCharseA2.SYSTEM_CD = "1000";
                reqCharseA2.TEXT_ID = "0200";
                reqCharseA2.TRANS_ID = "N006";
                reqCharseA2.TRANS_DT = sdf.format(new Date());
                reqCharseA2.TRANSACTION_ID = GlobalVars.getTransId();
                reqCharseA2.CARD_ID = mSharedManager.getCardId();
                reqCharseA2.UUID = "ABCDEFGHIJKLMN01234567890";
                reqCharseA2.OS_TYPE = "1";
                result2 = result.replaceAll(",","");
                reqCharseA2.TRANTS_AMT = result2;

                String tempJson = new Gson().toJson(reqCharseA2);

                E2EGWClientAnd e2EGWClient = new E2EGWClientAnd();

                try {

                    encData = e2EGWClient.E2EGWC_EncBlockData(mSharedManager.getSessionKey(), tempJson.getBytes());

                } catch (Exception e) {
                    Log.e("niceckt", e.getLocalizedMessage());
                }

                ReqCharseA1 reqCharseA1 = new ReqCharseA1();

                reqCharseA1.body.a1.SESSION_ID = charseSessionId(reqCharseA2);
                reqCharseA1.body.a1.MAC_VALUE = charseMACValue(reqCharseA2);

                reqCharseA1.body.a2 = encData;

                String json = new Gson().toJson(reqCharseA1);
                Log.d("niceckt", json);

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(String.format("https://%s:%s/ioc/charge_N006.do", mSharedManager.getServerIP(), mSharedManager.getServerPort()))
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();

                new Thread() {
                    @Override
                    public void run() {
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();

                            String res = response.body().string();

                            //                           byte[] resBytes = e2EGWClient.E2EGWC_DecBlockData(mSharedManager.getSessionKey(), res);

                            //                           ResIssueOTC resIssueOTC = new Gson().fromJson(new String(resBytes), ResIssueOTC.class);
                            ResCharse1 resCharse1 = new Gson().fromJson(new String(res), ResCharse1.class);

                            if (resCharse1 != null) {
                                if (resCharse1.RESULT_CODE.equals("0000") || resCharse1.RESULT_CODE.equals("0500")) {

                                    String decString = resCharse1.Body;
                                    byte[] resBytes = e2EGWClient.E2EGWC_DecBlockData(mSharedManager.getSessionKey(), decString);
                                    String resString = new String(resBytes);
                                   ResCharse2 resCharse2 = new Gson().fromJson(resString, ResCharse2.class);
//                                   textapprono.setText(resCharse2.TRANTS_S_NO);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textapprono.setText(" 충전 성공 ");
//                                            textapprono.setText(resCharse2.TRANTS_S_NO);
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

        btnHistory = findViewById(R.id.BtnHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sessionKey = null;
                String encSessionKey = null;
                String encData = null;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");

                ReqHistoryA2 reqHistoryA2 = new ReqHistoryA2();

                reqHistoryA2.PAY_COMP_CODE = "1000";
                reqHistoryA2.SYSTEM_CD = "1000";
                reqHistoryA2.TEXT_ID = "0200";
                reqHistoryA2.TRANS_ID = "N009";
                reqHistoryA2.TRANS_DT = sdf.format(new Date());
                reqHistoryA2.TRANSACTION_ID = GlobalVars.getTransId();
                reqHistoryA2.CARD_ID = mSharedManager.getCardId();
                reqHistoryA2.UUID = "ABCDEFGHIJKLMN01234567890";
                reqHistoryA2.START_DATE = ymd.format(new Date());
                reqHistoryA2.END_DATE = ymd.format(new Date());

                String tempJson = new Gson().toJson(reqHistoryA2);

                E2EGWClientAnd e2EGWClient = new E2EGWClientAnd();

                try {

                    encData = e2EGWClient.E2EGWC_EncBlockData(mSharedManager.getSessionKey(), tempJson.getBytes());

                } catch (Exception e) {
                    Log.e("niceckt", e.getLocalizedMessage());
                }

                ReqHistoryA1 reqHistoryA1 = new ReqHistoryA1();

                reqHistoryA1.body.a1.SESSION_ID = historySessionId(reqHistoryA2);
                reqHistoryA1.body.a1.MAC_VALUE = historyMACValue(reqHistoryA2);

                reqHistoryA1.body.a2 = encData;

                String json = new Gson().toJson(reqHistoryA1);
                Log.d("niceckt", json);

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(String.format("https://%s:%s/ioc/historySelect_N009.do", mSharedManager.getServerIP(), mSharedManager.getServerPort()))
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();

                new Thread() {
                    @Override
                    public void run() {
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();

                            String res = response.body().string();

                            //                           byte[] resBytes = e2EGWClient.E2EGWC_DecBlockData(mSharedManager.getSessionKey(), res);

                            //                           ResIssueOTC resIssueOTC = new Gson().fromJson(new String(resBytes), ResIssueOTC.class);
                            ResHistory1 resHistory1 = new Gson().fromJson(new String(res), ResHistory1.class);

                            if (resHistory1 != null) {
                                if ((resHistory1.RESULT_CODE.equals("0000")) || (resHistory1.RESULT_CODE.equals("0500"))) {
//                                    String decString = resHistory1.Body;
//                                    byte[] resBytes = e2EGWClient.E2EGWC_DecBlockData(mSharedManager.getSessionKey(), decString);
//                                    String resString = new String(resBytes);
//                                    ResHistory2 resHistory2 = new Gson().fromJson(resString, ResHistory2.class);

                                    String result = res.substring(res.lastIndexOf("["),res.lastIndexOf("]") + 1);
                                    JSONArray jsonArray = new JSONArray(result);
                                    Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                                    intent.putExtra("String-keyword",result );
                                    startActivity(intent);

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


        btnSetting = findViewById(R.id.BtnSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        imageBarcode = findViewById(R.id.ImageBarcode);

        boolean isCert = mSharedManager.getIsCert();

        if (!isCert) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        }
    }

    private void onShowBarcode(String otc) {

        Bitmap bitmap = null;

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.encodeBitmap(otc, BarcodeFormat.CODE_128, 5000, 2000);
        } catch (Exception e) {
            Log.e("niceckt", e.getMessage());
        }

        if (bitmap != null) {
            imageBarcode.setImageBitmap(bitmap);
        }

    }

    private String generateSessionId(ReqIssueOTCA2 reqIssueOTC) {

        String result = "";

        result = reqIssueOTC.SYSTEM_CD + reqIssueOTC.UUID + reqIssueOTC.TRANSACTION_ID;

        result = SHA256.sha256Hash(result);

        return result;

    }

    private String generateMACValue(ReqIssueOTCA2 reqIssueOTC) {

        String result;

        result = reqIssueOTC.TRANSACTION_ID + reqIssueOTC.SYSTEM_CD + "2187";

        result = SHA256.sha256Hash(result);

        return result;

    }


    private String charseSessionId(ReqCharseA2 reqCharse) {

        String result = "";

        result = reqCharse.SYSTEM_CD + reqCharse.UUID + reqCharse.TRANSACTION_ID;

        result = SHA256.sha256Hash(result);

        return result;

    }

    private String charseMACValue(ReqCharseA2 reqCharse) {

        String result;

        result = reqCharse.TRANSACTION_ID + reqCharse.SYSTEM_CD + "2187";

        result = SHA256.sha256Hash(result);

        return result;

    }

    private String historySessionId(ReqHistoryA2 reqHistory) {

        String result = "";

        result = reqHistory.SYSTEM_CD + reqHistory.UUID + reqHistory.TRANSACTION_ID;

        result = SHA256.sha256Hash(result);

        return result;

    }

    private String historyMACValue(ReqHistoryA2 reqHistory) {

        String result;

        result = reqHistory.TRANSACTION_ID + reqHistory.SYSTEM_CD + "2187";

        result = SHA256.sha256Hash(result);

        return result;

    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)){
                result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                editAmount.setText(result);
                editAmount.setSelection(result.length());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}