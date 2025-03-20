package com.example.notify;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private TextView fieldTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fieldTitle = findViewById(R.id.fieldTitle);

        String field = getIntent().getStringExtra("FIELD_NAME");
        fieldTitle.setText("Edit " + field);
    }
}
