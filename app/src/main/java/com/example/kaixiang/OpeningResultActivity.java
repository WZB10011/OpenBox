package com.example.kaixiang;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OpeningResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_result);

        String itemName = getIntent().getStringExtra("item_name");
        double itemValue = getIntent().getDoubleExtra("item_value", 0);
        String rarity = getIntent().getStringExtra("rarity");

        TextView tvItemName = findViewById(R.id.tv_item_name);
        TextView tvItemValue = findViewById(R.id.tv_item_value);

        tvItemName.setText(itemName);
        tvItemValue.setText(String.format("价值: ¥%.2f", itemValue));

        switch (rarity) {
            case "GOLD":
                tvItemName.setTextColor(Color.parseColor("#FFD700"));
                break;
            case "COVERT":
                tvItemName.setTextColor(Color.parseColor("#EB4B4B"));
                break;
            case "CLASSIFIED":
                tvItemName.setTextColor(Color.parseColor("#D32CE6"));
                break;
            case "RESTRICTED":
                tvItemName.setTextColor(Color.parseColor("#8847FF"));
                break;
            default:
                tvItemName.setTextColor(Color.parseColor("#5E98D9"));
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}