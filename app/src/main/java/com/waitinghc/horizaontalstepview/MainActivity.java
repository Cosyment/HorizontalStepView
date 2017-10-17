package com.waitinghc.horizaontalstepview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private HorizontalStepView mStepView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStepView = (HorizontalStepView) findViewById(R.id.stepView);

        List<HorizontalStepView.Item> items = new ArrayList<>();
        for (int i = 0, size = 5; i < size; i++) {
            items.add(new HorizontalStepView.Item("测试 "+i, "09-08 "+i));
        }

        mStepView.setItems(items);
        mStepView.setCurrentItem(3.5f);
    }
}
