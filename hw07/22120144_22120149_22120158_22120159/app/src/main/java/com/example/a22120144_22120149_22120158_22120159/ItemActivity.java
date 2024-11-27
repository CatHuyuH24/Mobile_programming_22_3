package com.example.a22120144_22120149_22120158_22120159;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ItemActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        ListView itemListView = findViewById(R.id.item_list_view);
        String[] items = {"Kết quả AFF Suzuki Cup", "Tin xây dựng sân vận động", "Thành lập CLB mới", "Bóng đá Đức", "Sa thải huấn luyện viên"};
        Intent intent = getIntent();
        String channelTitle = intent.getStringExtra("channel_title");
        String channelName = intent.getStringExtra("channel_name");
        TextView textView = findViewById(R.id.item_title);
        if (channelTitle != null && channelName != null) {
            textView.setText("ITEMS IN CHANNEL " + channelTitle + " - " + channelName);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        itemListView.setAdapter(adapter);

        itemListView.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ItemActivity.this);
            dialog.setTitle(items[position]);
            dialog.setMessage("Hôm qua ngày 6/12/2018 đội tuyển Việt Nam giành vô địch World Cup\n\nDetails click button more");
            dialog.setPositiveButton("More", (dialogInterface, i) -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com"));
                startActivity(browserIntent);
            });
            dialog.setNegativeButton("Close", null);
            dialog.show();
        });
    }
}