package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    /*private FirebaseAuth mAuth;*/
    private EditText full_name, email_address, password, confirm_password;
    private Button register_button, redirect_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

       /* mAuth = FirebaseAuth.getInstance();*/

        register_button = (Button) findViewById(R.id.register_button);
        register_button.setOnClickListener(this);

        redirect_button = (Button) findViewById(R.id.redirect_button);
        redirect_button.setOnClickListener(this);

        full_name = (EditText) findViewById(R.id.full_name);
        email_address = (EditText) findViewById(R.id.email_address);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                registerUser();
                break;

            case R.id.redirect_button:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    private void registerUser() {
        String fullName = full_name.getText().toString().trim();
        String emailAddress = email_address.getText().toString().trim();
        String passwordEnter = password.getText().toString().trim();
        String confirmPassword = confirm_password.getText().toString().trim();

        if(fullName.isEmpty()) {
            full_name.setError("All fields are required.");
            full_name.requestFocus();
            return;
        }

        if(emailAddress.isEmpty()) {
            email_address.setError("All fields are required.");
            email_address.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            email_address.setError("Please enter valid email.");
            email_address.requestFocus();
            return;
        }

        if(passwordEnter.isEmpty()) {
            password.setError("All fields are required.");
            password.requestFocus();
            return;
        }

        if(passwordEnter.length()<8) {
            password.setError("Password cannot be less than 8 characters.");
            password.requestFocus();
            return;
        }

        if(confirmPassword.isEmpty()) {
            confirm_password.setError("All fields are required.");
            confirm_password.requestFocus();
            return;
        }

        if(!confirmPassword.equals(passwordEnter)) {
            confirm_password.setError("Passwords do not match.");
            confirm_password.requestFocus();
            return;
        }

        /*mAuth.createUserWithEmailAndPassword(emailAddress, passwordEnter)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            User user = new User(fullName, emailAddress, passwordEnter);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "User has been registered.", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_LONG).show();
                        }

                    }
                });*/
    }
}



