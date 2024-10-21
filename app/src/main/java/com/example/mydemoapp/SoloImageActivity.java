    package com.example.mydemoapp;

    import android.content.Intent;
    import android.os.Bundle;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;
    import android.widget.Button;

    import androidx.appcompat.app.AppCompatActivity;

    import com.bumptech.glide.Glide;

    public class SoloImageActivity extends AppCompatActivity {
        private ImageView soloImageView;
        private TextView tvTitle;
        private Button backBtn, nextBtn, previousBtn, setBackgroundBtn;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.solo_image); // Create the XML layout

            soloImageView = findViewById(R.id.imgView_solo_image);
            tvTitle = findViewById(R.id.txtView_solo_image_title);
            backBtn = findViewById(R.id.btn_solo_back);
            nextBtn = findViewById(R.id.btn_solo_next);
            previousBtn = findViewById(R.id.btn_solo_previous);

            // Get the data from the intent
            Intent intent = getIntent();
            String imageUrl = intent.getStringExtra("com.example.mydemoapp.IMAGE_URL");
            int imageId = intent.getIntExtra("com.example.mydemoapp.IMAGE_RESOURCE_ID", -1);
            String title = "The image id: " + Integer.toString(imageId);
            tvTitle.setText(title);

            // Load the image using Glide
            try {
                if (imageUrl != null) {
                    Glide.with(this).load(imageUrl).into(soloImageView);
                } else if (imageId != -1) {
                    Glide.with(this).load(imageId).into(soloImageView);
                }
                else{
                    //for debugging
                    //for now, the image URL will always be null (because the current sender is ImageItem)
                    Toast.makeText(SoloImageActivity.this, "The image ID received is "+imageId+"\nThe image URL received is null", Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(SoloImageActivity.this, "The image ID received is "+imageId+" The image URL received is "+imageUrl, Toast.LENGTH_LONG).show();
            }


            //when pressing "Back" button, go back to the main screen showing thumbnails (small images)
            //no need to start an intent or activity; this will return to the previous activity on the stack
            backBtn.setOnClickListener(view -> finish());



        }
    }

