package com.example.kaixiang;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class BoxSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_selection);

        Button btnKilowatt = findViewById(R.id.btn_kilowatt);
        Button btnCS1 = findViewById(R.id.btn_cs1);
        Button btnGallery = findViewById(R.id.btn_gallery);

        // 设置按钮图标
        btnKilowatt.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.kilowatt_case_small, 0, 0);
        btnCS1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cs1_case_small, 0, 0);
        btnGallery.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.gallery_case_small, 0, 0);

        // 设置按钮文字和图标间距
        int padding = getResources().getDimensionPixelSize(R.dimen.button_padding);
        btnKilowatt.setCompoundDrawablePadding(padding);
        btnCS1.setCompoundDrawablePadding(padding);
        btnGallery.setCompoundDrawablePadding(padding);

        btnKilowatt.setOnClickListener(v -> openCase("kilowatt"));
        btnCS1.setOnClickListener(v -> openCase("cs1"));
        btnGallery.setOnClickListener(v -> openCase("gallery"));
    }

    private void openCase(String caseType) {
        Intent intent = new Intent(this, CaseOpeningActivity.class);
        intent.putExtra("case_type", caseType);
        startActivity(intent);
    }
}