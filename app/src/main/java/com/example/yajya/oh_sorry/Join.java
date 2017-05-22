package com.example.yajya.oh_sorry;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Join extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
    }

    public void btnSignUp(View view) {
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
        finish();
    }
}
