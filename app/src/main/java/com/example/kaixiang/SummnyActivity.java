package com.example.kaixiang;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SummnyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summny);

        float totalSpent = getIntent().getFloatExtra("total_spent", 0);
        double totalValue = getIntent().getDoubleExtra("total_value", 0);
        int goldCount = getIntent().getIntExtra("gold_count", 0);
        ArrayList<CaseOpeningActivity.SkinItem> inventory =
                (ArrayList<CaseOpeningActivity.SkinItem>) getIntent().getSerializableExtra("inventory");

        DecimalFormat df = new DecimalFormat("¥#,##0.00");
        double profit = totalValue - totalSpent;

        TextView tvTotalSpent = findViewById(R.id.tv_total_spent);
        TextView tvTotalValue = findViewById(R.id.tv_total_value);
        TextView tvProfitLoss = findViewById(R.id.tv_profit_loss);
        TextView tvGoldCount = findViewById(R.id.tv_gold_count);
        TextView tvWarning = findViewById(R.id.tv_warning);
        LinearLayout layoutInventory = findViewById(R.id.layout_inventory);

        tvTotalSpent.setText("总投入: " + df.format(totalSpent));
        tvTotalValue.setText("总获得: " + df.format(totalValue));
        tvProfitLoss.setText("盈  亏: " + (profit >= 0 ? "+" : "") + df.format(profit));
        tvProfitLoss.setTextColor(profit >= 0 ? Color.GREEN : Color.RED);
        tvGoldCount.setText("金色物品: " + goldCount + "件");
        tvWarning.setText("珍爱生命，远离开箱");

        // 显示所有开箱物品
        for (CaseOpeningActivity.SkinItem item : inventory) {
            TextView itemView = new TextView(this);
            itemView.setText(item.getDisplayText() + " | 价值: " + df.format(item.getActualPrice()));
            itemView.setTextSize(14);
            itemView.setPadding(8, 8, 8, 8);

            switch (item.rarity) {
                case GOLD:
                    itemView.setTextColor(Color.parseColor("#FFD700"));
                    break;
                case COVERT:
                    itemView.setTextColor(Color.parseColor("#EB4B4B"));
                    break;
                case CLASSIFIED:
                    itemView.setTextColor(Color.parseColor("#D32CE6"));
                    break;
                case RESTRICTED:
                    itemView.setTextColor(Color.parseColor("#8847FF"));
                    break;
                default:
                    itemView.setTextColor(Color.parseColor("#5E98D9"));
            }

            layoutInventory.addView(itemView);
        }

        Button btnBack = findViewById(R.id.btn_back_to_selection);
        btnBack.setOnClickListener(v -> {
            finishAffinity();
            startActivity(new Intent(this, BoxSelectionActivity.class));
        });
    }
}