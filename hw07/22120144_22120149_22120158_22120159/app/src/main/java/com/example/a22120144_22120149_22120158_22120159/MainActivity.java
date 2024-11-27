package com.example.a22120144_22120149_22120158_22120159;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button thanhNienButton = findViewById(R.id.thanh_nien_button);
        Button vnExpressButton = findViewById(R.id.vn_express_button);
        Button danTriButton = findViewById(R.id.dan_tri_button);
        Button tuoiTreButton = findViewById(R.id.tuoi_tre_button);

        thanhNienButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ChannelActivity.class);
            intent.putExtra("news_source", "THANH NIÊN");
            startActivity(intent);
        });

        vnExpressButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ChannelActivity.class);
            intent.putExtra("news_source", "VN EXPRESS");
            startActivity(intent);
        });

        danTriButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ChannelActivity.class);
            intent.putExtra("news_source", "DÂN TRÍ");
            startActivity(intent);
        });

        tuoiTreButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ChannelActivity.class);
            intent.putExtra("news_source", "TUỔI TRẺ");
            startActivity(intent);
        });


    }
}