package com.example.kaixiang;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaseOpeningActivity extends AppCompatActivity {

    private Button btnOpenCase;
    private ImageView caseImage;
    private TextView tvTotalSpent;
    private TextView tvTotalValue;
    private TextView tvProfitLoss;
    private TextView tvGoldCount;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isOpening = false;

    private int openCount = 0;
    private final List<SkinItem> inventory = new ArrayList<>();
    private float totalSpent = 400.0f;
    private double totalValue = 0.0;
    private int goldCount = 0;
    private List<SkinItem> itemPool;
    private List<List<SkinItem>> groupedItems;
    private String caseType;

    public static class SkinItem implements Parcelable {
        String name;
        Rarity rarity;
        Wear wear;
        boolean isStatTrak;
        double basePrice;

        public SkinItem(String name, Rarity rarity, double basePrice) {
            this.name = name;
            this.rarity = rarity;
            this.basePrice = basePrice;
        }

        protected SkinItem(Parcel in) {
            name = in.readString();
            rarity = Rarity.valueOf(in.readString());
            wear = Wear.valueOf(in.readString());
            isStatTrak = in.readByte() != 0;
            basePrice = in.readDouble();
        }

        public static final Creator<SkinItem> CREATOR = new Creator<SkinItem>() {
            @Override
            public SkinItem createFromParcel(Parcel in) {
                return new SkinItem(in);
            }

            @Override
            public SkinItem[] newArray(int size) {
                return new SkinItem[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(rarity.name());
            dest.writeString(wear.name());
            dest.writeByte((byte) (isStatTrak ? 1 : 0));
            dest.writeDouble(basePrice);
        }

        public double getActualPrice() {
            double price = basePrice;

            if (wear != null) {
                switch (wear) {
                    case MINIMAL_WEAR:
                        price *= 0.9;
                        break;
                    case FIELD_TESTED:
                        price *= 0.75;
                        break;
                    case WELL_WORN:
                        price *= 0.6;
                        break;
                    case BATTLE_SCARRED:
                        price *= 0.55;
                        break;
                }
            }

            if (isStatTrak) {
                if (rarity == Rarity.GOLD) {
                    price = Math.max(0, price - 100);
                } else {
                    price *= 2;
                }
            }

            return price;
        }

        public String getDisplayText() {
            String displayText = (isStatTrak ? "★ StatTrak™ " : "") + name;

            if (wear != null) {
                displayText += " | " + wear.displayName;
            }

            if (rarity != null) {
                switch (rarity) {
                    case MIL_SPEC:
                        displayText = "[军规] " + displayText;
                        break;
                    case RESTRICTED:
                        displayText = "[受限] " + displayText;
                        break;
                    case CLASSIFIED:
                        displayText = "[保密] " + displayText;
                        break;
                    case COVERT:
                        displayText = "[隐秘] " + displayText;
                        break;
                    case GOLD:
                        displayText = "[★金★] " + displayText;
                        break;
                }
            }

            return displayText;
        }
    }

    enum Rarity {
        MIL_SPEC("军规级", 79.92),
        RESTRICTED("受限", 15.98),
        CLASSIFIED("保密", 3.20),
        COVERT("隐秘", 0.64),
        GOLD("金", 0.26);

        final String displayName;
        final double probability;

        Rarity(String displayName, double probability) {
            this.displayName = displayName;
            this.probability = probability;
        }
    }

    enum Wear {
        FACTORY_NEW("崭新出厂", 7),
        MINIMAL_WEAR("略有磨损", 8),
        FIELD_TESTED("久经沙场", 23),
        WELL_WORN("破损不堪", 7),
        BATTLE_SCARRED("战痕累累", 55);

        final String displayName;
        final double probability;

        Wear(String displayName, double probability) {
            this.displayName = displayName;
            this.probability = probability;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_opening);

        btnOpenCase = findViewById(R.id.btn_open_case);
        caseImage = findViewById(R.id.caseImage);
        tvTotalSpent = findViewById(R.id.tv_total_spent);
        tvTotalValue = findViewById(R.id.tv_total_value);
        tvProfitLoss = findViewById(R.id.tv_profit_loss);
        tvGoldCount = findViewById(R.id.tv_gold_count);

        caseType = getIntent().getStringExtra("case_type");
        if (caseType == null) caseType = "kilowatt";

        initializeItemPool();

        switch (caseType) {
            case "kilowatt":
                caseImage.setImageResource(R.drawable.kilowatt_case);
                break;
            case "cs1":
                caseImage.setImageResource(R.drawable.cs1_case);
                break;
            case "gallery":
                caseImage.setImageResource(R.drawable.gallery_case);
                break;
        }

        DecimalFormat df = new DecimalFormat("¥#,##0.00");
        tvTotalSpent.setText("总投入: " + df.format(totalSpent));
        tvTotalValue.setText("总获得: " + df.format(totalValue));
        tvProfitLoss.setText("盈  亏: " + df.format(totalValue - totalSpent));
        tvGoldCount.setText("金色物品: " + goldCount + "件");

        btnOpenCase.setOnClickListener(v -> {
            if (!isOpening) {
                startCaseOpeningAnimation();
                simulateCaseOpening();
            }
        });
    }

    private void initializeItemPool() {
        itemPool = new ArrayList<>();

        switch (caseType) {
            case "kilowatt":
                itemPool.add(new SkinItem("MAC-10 | 灯箱", Rarity.MIL_SPEC, 3.74));
                itemPool.add(new SkinItem("SSG 08 | 灾难", Rarity.MIL_SPEC, 2.0));
                itemPool.add(new SkinItem("Tec-9 | 渣渣", Rarity.MIL_SPEC, 2.2));
                itemPool.add(new SkinItem("UMP-45 | 机动化", Rarity.MIL_SPEC, 1.9));
                itemPool.add(new SkinItem("FN57 | 混合体", Rarity.RESTRICTED, 16.57));
                itemPool.add(new SkinItem("M4A4 | 蚀刻领主", Rarity.RESTRICTED, 17.1));
                itemPool.add(new SkinItem("MP7 | 笑一个", Rarity.RESTRICTED, 16.28));
                itemPool.add(new SkinItem("格洛克18型 | 崩络克-18", Rarity.RESTRICTED, 15.99));
                itemPool.add(new SkinItem("M4A1-S | 黑莲花", Rarity.CLASSIFIED, 143.54));
                itemPool.add(new SkinItem("USP-S | 破颚者", Rarity.CLASSIFIED, 163.0));
                itemPool.add(new SkinItem("宙斯电击枪 | 奥林匹斯", Rarity.CLASSIFIED, 120.0));
                itemPool.add(new SkinItem("AK47 | 传承", Rarity.COVERT, 1422.0));
                itemPool.add(new SkinItem("AWP | 镀铬大炮", Rarity.COVERT, 826.5));
                itemPool.add(new SkinItem("廓尔喀刀 | 渐变之色", Rarity.GOLD, 3942.0));
                itemPool.add(new SkinItem("廓尔喀刀 | 屠夫", Rarity.GOLD, 2720.0));
                itemPool.add(new SkinItem("廓尔喀刀 | 夜色", Rarity.GOLD, 1520.0));
                itemPool.add(new SkinItem("廓尔喀刀 | 森林DDPAT", Rarity.GOLD, 1740.0));
                break;

            case "cs1":
                itemPool.add(new SkinItem("沙漠之鹰 | 沙漠精英", Rarity.MIL_SPEC, 3.5));
                itemPool.add(new SkinItem("P250 | 沙尘暴", Rarity.MIL_SPEC, 1.8));
                itemPool.add(new SkinItem("MP9 | 沙漠绿洲", Rarity.MIL_SPEC, 2.1));
                itemPool.add(new SkinItem("SG 553 | 沙漠风暴", Rarity.MIL_SPEC, 1.7));
                itemPool.add(new SkinItem("M4A4 | 沙漠精英", Rarity.RESTRICTED, 18.2));
                itemPool.add(new SkinItem("AK-47 | 沙漠骑士", Rarity.RESTRICTED, 19.5));
                itemPool.add(new SkinItem("AWP | 沙漠之鹰", Rarity.RESTRICTED, 17.8));
                itemPool.add(new SkinItem("USP-S | 沙漠战术", Rarity.RESTRICTED, 16.3));
                itemPool.add(new SkinItem("沙漠之鹰 | 黄金猎鹰", Rarity.CLASSIFIED, 155.0));
                itemPool.add(new SkinItem("AK-47 | 黄金沙漠", Rarity.CLASSIFIED, 178.0));
                itemPool.add(new SkinItem("AWP | 黄金巨龙", Rarity.CLASSIFIED, 210.0));
                itemPool.add(new SkinItem("M4A1-S | 沙漠幽灵", Rarity.COVERT, 1350.0));
                itemPool.add(new SkinItem("爪子刀 | 沙漠风暴", Rarity.COVERT, 950.0));
                itemPool.add(new SkinItem("蝴蝶刀 | 黄金沙尘", Rarity.GOLD, 3650.0));
                itemPool.add(new SkinItem("刺刀 | 沙漠玫瑰", Rarity.GOLD, 2850.0));
                itemPool.add(new SkinItem("折叠刀 | 沙漠之星", Rarity.GOLD, 1950.0));
                break;

            case "gallery":
                itemPool.add(new SkinItem("格洛克18 | 艺术画廊", Rarity.MIL_SPEC, 4.2));
                itemPool.add(new SkinItem("P90 | 抽象艺术", Rarity.MIL_SPEC, 3.8));
                itemPool.add(new SkinItem("法玛斯 | 油画印象", Rarity.MIL_SPEC, 2.5));
                itemPool.add(new SkinItem("UMP-45 | 涂鸦艺术", Rarity.MIL_SPEC, 3.1));
                itemPool.add(new SkinItem("M4A4 | 蒙德里安", Rarity.RESTRICTED, 22.5));
                itemPool.add(new SkinItem("AK-47 | 梵高星空", Rarity.RESTRICTED, 24.8));
                itemPool.add(new SkinItem("AWP | 达芬奇密码", Rarity.RESTRICTED, 20.1));
                itemPool.add(new SkinItem("沙漠之鹰 | 毕加索", Rarity.RESTRICTED, 18.9));
                itemPool.add(new SkinItem("M4A1-S | 莫奈花园", Rarity.CLASSIFIED, 165.0));
                itemPool.add(new SkinItem("AWP | 文艺复兴", Rarity.CLASSIFIED, 195.0));
                itemPool.add(new SkinItem("USP-S | 印象日出", Rarity.CLASSIFIED, 135.0));
                itemPool.add(new SkinItem("AK-47 | 蒙娜丽莎", Rarity.COVERT, 1550.0));
                itemPool.add(new SkinItem("蝴蝶刀 | 星空之画", Rarity.COVERT, 1100.0));
                itemPool.add(new SkinItem("爪子刀 | 画廊珍品", Rarity.GOLD, 4200.0));
                itemPool.add(new SkinItem("折叠刀 | 艺术大师", Rarity.GOLD, 3150.0));
                itemPool.add(new SkinItem("刺刀 | 名画收藏", Rarity.GOLD, 2350.0));
                break;
        }

        groupedItems = new ArrayList<>();
        for (Rarity rarity : Rarity.values()) {
            List<SkinItem> group = new ArrayList<>();
            for (SkinItem item : itemPool) {
                if (item.rarity == rarity) {
                    group.add(item);
                }
            }
            groupedItems.add(group);
        }
    }

    private void startCaseOpeningAnimation() {
        isOpening = true;
        btnOpenCase.setEnabled(false);

        ObjectAnimator shakeX = ObjectAnimator.ofFloat(caseImage, "translationX", 0, 30, -30, 20, -20, 10, -10, 0);
        shakeX.setDuration(500);
        ObjectAnimator shakeY = ObjectAnimator.ofFloat(caseImage, "translationY", 0, 30, -30, 20, -20, 10, -10, 0);
        shakeY.setDuration(500);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(caseImage, "alpha", 1f, 0.3f);
        fadeOut.setDuration(300);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(caseImage, "alpha", 0.3f, 1f);
        fadeIn.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        AnimatorSet shakeSet1 = new AnimatorSet();
        shakeSet1.playTogether(shakeX, shakeY);

        AnimatorSet fadeSet = new AnimatorSet();
        fadeSet.playSequentially(fadeOut, fadeIn);

        AnimatorSet shakeSet2 = new AnimatorSet();
        shakeSet2.playTogether(shakeX.clone(), shakeY.clone());

        animatorSet.playSequentially(shakeSet1, fadeSet, shakeSet2);
        animatorSet.start();
    }

    private void simulateCaseOpening() {
        final double COST_PER_OPEN = 20.0;

        if (openCount >= 20) {
            handler.post(() -> {
                Intent intent = new Intent(CaseOpeningActivity.this, SummnyActivity.class);
                intent.putExtra("total_spent", totalSpent);
                intent.putExtra("total_value", totalValue);
                intent.putExtra("gold_count", goldCount);
                intent.putParcelableArrayListExtra("inventory", new ArrayList<>(inventory));
                startActivity(intent);
                finish();
            });
            return;
        }

        Random random = new Random();
        totalSpent += COST_PER_OPEN;
        openCount++;

        double rarityRoll = random.nextDouble() * 100;
        double cumulative = 0;
        Rarity selectedRarity = Rarity.MIL_SPEC;

        for (Rarity rarity : Rarity.values()) {
            cumulative += rarity.probability;
            if (rarityRoll <= cumulative) {
                selectedRarity = rarity;
                break;
            }
        }

        List<SkinItem> rarityGroup = groupedItems.get(selectedRarity.ordinal());
        if (rarityGroup.isEmpty()) {
            rarityGroup = groupedItems.get(0);
        }

        SkinItem item = rarityGroup.get(random.nextInt(rarityGroup.size()));
        SkinItem newItem = new SkinItem(item.name, item.rarity, item.basePrice);

        double wearRoll = random.nextDouble() * 100;
        cumulative = 0;
        for (Wear wear : Wear.values()) {
            cumulative += wear.probability;
            if (wearRoll <= cumulative) {
                newItem.wear = wear;
                break;
            }
        }

        newItem.isStatTrak = random.nextDouble() < 0.1;

        double itemValue = newItem.getActualPrice();
        totalValue += itemValue;
        if (newItem.rarity == Rarity.GOLD) {
            goldCount++;
        }
        inventory.add(newItem);

        handler.post(() -> {
            DecimalFormat df = new DecimalFormat("¥#,##0.00");
            double profit = totalValue - totalSpent;

            tvTotalSpent.setText("总投入: " + df.format(totalSpent));
            tvTotalValue.setText("总获得: " + df.format(totalValue));
            String profitText = "盈  亏: " + (profit >= 0 ? "+" : "") + df.format(profit);
            tvProfitLoss.setText(profitText);
            tvProfitLoss.setTextColor(profit >= 0 ? Color.GREEN : Color.RED);
            tvGoldCount.setText("金色物品: " + goldCount + "件");

            Intent resultIntent = new Intent(CaseOpeningActivity.this, OpeningResultActivity.class);
            resultIntent.putExtra("item_name", newItem.getDisplayText());
            resultIntent.putExtra("item_value", itemValue);
            resultIntent.putExtra("rarity", newItem.rarity.name());
            startActivity(resultIntent);

            btnOpenCase.setEnabled(true);
            isOpening = false;
        });
    }
}