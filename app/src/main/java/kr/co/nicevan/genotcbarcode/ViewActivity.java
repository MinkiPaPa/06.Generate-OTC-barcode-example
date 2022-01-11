package kr.co.nicevan.genotcbarcode;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ViewActivity extends AppCompatActivity {

    private ArrayList<Dictionary> mArrayList;
    private CustomAdapter mAdapter;
    private int count = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        String getString = getIntent().getStringExtra("String-keyword");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(getString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        mArrayList = new ArrayList<>();

        mAdapter = new CustomAdapter( mArrayList);
        mRecyclerView.setAdapter(mAdapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        JSONObject jsonObject = null;
        String wdate = null;
        String wname = null;
        String wamt = null;
        String wbal = null;

        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                wdate = jsonObject.getString("TRAN_DT");
                wname = jsonObject.getString("MERC_NM");
                wamt = jsonObject.getString("TRAN_AMT");
                wbal = jsonObject.getString("BALENCE");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Dictionary data = new Dictionary(wdate, wname, wamt, wbal);
            //mArrayList.add(0, dict); //RecyclerView의 첫 줄에 삽입
            mArrayList.add(data); // RecyclerView의 마지막 줄에 삽입
        }

        Dictionary data = new Dictionary(" BAL", "ANCE",wamt, wbal);
        mArrayList.add(data);

        mAdapter.notifyDataSetChanged();

        Button buttonInsert = (Button)findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

}