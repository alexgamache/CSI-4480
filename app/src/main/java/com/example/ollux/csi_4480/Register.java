package com.example.ollux.csi_4480;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private TextView loginText;
    private EditText FullName;
    private EditText Email;
    private EditText Password;
    private Button Submit;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore fStore;
    private String userID;
    private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginText = (TextView) findViewById(R.id.login);
        FullName = (EditText) findViewById(R.id.name);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        Submit = (Button) findViewById(R.id.submit);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Register.this, Login.class);
                Register.this.startActivity(myIntent);
            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sEmail = Email.getText().toString();
                final String sPassword = Password.getText().toString();
                final String fullName = FullName.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                fAuth.createUserWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fname", fullName);
                            user.put("email", sEmail);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else {
                            Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

    }
}