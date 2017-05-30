package com.example.yajya.oh_sorry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Join extends AppCompatActivity {
    EditText id;
    EditText password;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        id = (EditText)findViewById(R.id.ID);
        password = (EditText)findViewById(R.id.PASSWORD);
        name = (EditText)findViewById(R.id.NAME);
    }

    public void btnSignUp(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table = database.getReference("AppDB/User");

        final String Id = id.getText().toString();
        Query query = table.orderByChild("Id").equalTo(Id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    Toast.makeText(getApplicationContext(),"아이디가 중복됩니다.",Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference user = table.push();
                    user.child("Id").setValue(Id);
                    user.child("Password").setValue(password.getText().toString());
                    user.child("Name").setValue(name.getText().toString());

                    Intent intent = new Intent(getApplicationContext(), LogIn.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
