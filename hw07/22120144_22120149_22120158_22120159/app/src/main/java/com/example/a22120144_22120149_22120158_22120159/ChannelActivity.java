package com.example.a22120144_22120149_22120158_22120159;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChannelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        Intent intent1 = getIntent();
        String nameApp = intent1.getStringExtra("news_source");
        TextView textView = findViewById(R.id.channel_title);
        if(nameApp != null) {
            textView.setText("CHANNELS IN " + nameApp);
        }
        ListView channelListView = findViewById(R.id.channel_list_view);
        String[] channels = {"THỂ THAO", "DU LỊCH", "GIÁO DỤC", "KINH TẾ", "PHÁP LUẬT"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, channels);
        channelListView.setAdapter(adapter);

        channelListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ChannelActivity.this, ItemActivity.class);
            intent.putExtra("channel_title", nameApp);
            intent.putExtra("channel_name", channels[position]);
            startActivity(intent);
        });
    }
}