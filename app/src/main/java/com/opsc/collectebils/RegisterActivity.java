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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener
{

    private FirebaseAuth mAuth;
    private EditText fullName, emailAddress, password, confirmPassword;
    private Button registerButton, redirectButton;
    private String salt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        redirectButton = (Button) findViewById(R.id.redirectButton);
        redirectButton.setOnClickListener(this);
        fullName = (EditText) findViewById(R.id.fullName);
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);

        try {
            salt = getSalt();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.registerButton:
                registerUser();
                break;

            case R.id.redirectButton:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                break;
        }
    }

    public String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }

    public String getSHA1SecureEmail(EditText emailAddress, String salt) {
        String secureEmail = emailAddress.getText().toString().trim();
        String genEmail = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(salt.getBytes());
            byte [] bytes = md.digest(secureEmail.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            genEmail = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return genEmail;
    }

    public String getSHA1SecurePassword(EditText password, String salt) {
        String securePassword = password.getText().toString().trim();
        String genPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(salt.getBytes());
            byte [] bytes = md.digest(securePassword.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            genPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return genPassword;
    }

    private void registerUser()
    {
        String fullNameEnter = fullName.getText().toString().trim();
        String emailAddressEnter = emailAddress.getText().toString().trim();
        String passwordEnter = password.getText().toString().trim();
        String confirmPasswordEnter = confirmPassword.getText().toString().trim();

        if(fullNameEnter.isEmpty())
        {
            fullName.setError("All fields are required.");
            fullName.requestFocus();
            return;
        }

        if(emailAddressEnter.isEmpty())
        {
            emailAddress.setError("All fields are required.");
            emailAddress.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddressEnter).matches())
        {
            emailAddress.setError("Please enter valid email.");
            emailAddress.requestFocus();
            return;
        }

        if(passwordEnter.isEmpty())
        {
            password.setError("All fields are required.");
            password.requestFocus();
            return;
        }

        if(passwordEnter.length()<8)
        {
            password.setError("Password cannot be less than 8 characters.");
            password.requestFocus();
            return;
        }

        if(confirmPasswordEnter.isEmpty())
        {
            confirmPassword.setError("All fields are required.");
            confirmPassword.requestFocus();
            return;
        }

        if(!confirmPasswordEnter.equals(passwordEnter))
        {
            confirmPassword.setError("Passwords do not match.");
            confirmPassword.requestFocus();
            return;
        }

        String hashedPassword = getSHA1SecurePassword(password, salt);
        String hashedEmail = getSHA1SecureEmail(emailAddress, salt);

        mAuth.createUserWithEmailAndPassword(emailAddressEnter, passwordEnter)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful()) {
                            User user = new User(fullNameEnter, hashedEmail, hashedPassword);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {

                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "User has been registered.", Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(RegisterActivity.this, MyCollectionsActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        RegisterActivity.this.finish();
                                    }

                                    else
                                    {
                                        Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }

                        else
                        {
                            Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
}



