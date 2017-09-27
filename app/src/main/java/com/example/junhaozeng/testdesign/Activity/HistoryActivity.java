package com.example.junhaozeng.testdesign.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.junhaozeng.testdesign.R;
import com.example.junhaozeng.testdesign.Utils.CommonAdapter;
import com.example.junhaozeng.testdesign.Utils.CommonViewHolder;
import com.example.junhaozeng.testdesign.Utils.DateStepsPair;
import com.example.junhaozeng.testdesign.Utils.DbManager;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ImageView iv_back;
    private ListView lv_stats;
    private DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initDb();
        initViews();
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initData();
    }

    private void initDb() {
        dbManager = new DbManager(this);
    }

    private void initViews() {
        iv_back = (ImageView) findViewById(R.id.history_to_main_arrow);
        lv_stats = (ListView) findViewById(R.id.listview);
    }

    private void initData() {
        setEmptyView(lv_stats);
        List<DateStepsPair> dataList = dbManager.readAllRecords();
        lv_stats.setAdapter(new CommonAdapter<DateStepsPair>(this, dataList, R.layout.piece_of_record) {
            @Override
            protected void convertView(View item, DateStepsPair dateStepsPair) {
                TextView tv_date= CommonViewHolder.get(item, R.id.tv_date);
                TextView tv_step= CommonViewHolder.get(item, R.id.tv_step);
                tv_date.setText(dateStepsPair.getDate());
                tv_step.setText(String.valueOf(dateStepsPair.getSteps()));
            }
        });

    }

    protected <T extends View> T setEmptyView(ListView listView) {
        TextView emptyView = new TextView(this);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setText(getString(R.string.null_result));
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
        return (T) emptyView;
    }
}

