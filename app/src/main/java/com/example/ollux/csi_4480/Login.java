package com.example.ollux.csi_4480;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private TextView registerText;
    private EditText mEmail, mPassword;
    private Button mlogin;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mlogin = findViewById(R.id.register);
        fAuth = FirebaseAuth.getInstance();
        registerText = (TextView) findViewById(R.id.login);
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sEmail = mEmail.getText().toString();
                final String sPassword = mPassword.getText().toString();
                fAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Login.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else {
                            Toast.makeText(Login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}