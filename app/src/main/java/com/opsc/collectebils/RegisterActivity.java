package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    // Establishes a Firebase connection variable
    private FirebaseAuth mAuth;
    private EditText fullName, emailAddress, password, confirmPassword;
    private Button registerButton, redirectButton;
    // Creates the variable which will store the salt method
    private String salt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initializes the Firebase connection variable
        mAuth = FirebaseAuth.getInstance();

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        redirectButton = (Button) findViewById(R.id.redirectButton);
        redirectButton.setOnClickListener(this);
        fullName = (EditText) findViewById(R.id.fullName);
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);

        // Line 59 - 63
        // Title: Java – Create a Secure Password Hash
        // Author: Lokesh Gupta
        // Date: 25 Jan 2022
        // Resource Link: https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/#:~:text=A%20secure%20password%20hash%20is,prove%20effective%20for%20password%20security.
        // Stores the salt generating method in the variable
        try {
            salt = getSalt();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v)
    {
        // Establishes a switch case to allow the function of 2 buttons
        switch (v.getId())
        {
            // Initializes a case for the register button and its method
            case R.id.registerButton:
                registerUser();
                break;

            // Initializes a case the button that will link to the login page
            case R.id.redirectButton:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                break;
        }
    }

    // Line 90 - 137
    // Title: Java – Create a Secure Password Hash
    // Author: Lokesh Gupta
    // Date: 25 Jan 2022
    // Resource Link: https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/#:~:text=A%20secure%20password%20hash%20is,prove%20effective%20for%20password%20security.
    // Creates and initializes the sequence that will randomize the hashing
    public String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }

    // The method that takes in the initialized variable (emailAddress)
    // It then hashes it using the salt method and a series of loops
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

    // The method that takes in the initialized variable (password)
    // It then hashes it using the salt method and a series of loops
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

    // The method that will create a user in Firebase
    private void registerUser()
    {
        String fullNameEnter = fullName.getText().toString().trim();
        String emailAddressEnter = emailAddress.getText().toString().trim();
        String passwordEnter = password.getText().toString().trim();
        String confirmPasswordEnter = confirmPassword.getText().toString().trim();

        // Line 149 - 196
        // A series of if statements that will validate the user's input
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

        // 2 strings that will store the hashed password and email address
        String hashedPassword = getSHA1SecurePassword(password, salt);
        String hashedEmail = getSHA1SecureEmail(emailAddress, salt);

        // Creates the new user in Firebase using the input specified
        mAuth.createUserWithEmailAndPassword(emailAddressEnter, passwordEnter)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    // Method that checks if the operation is successful
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        // If statement that stores user information to the Firebase realtime database
                        // Only if previous operation is successful
                        if(task.isSuccessful()) {
                            User user = new User(fullNameEnter, hashedEmail, hashedPassword);

                            // Operation that adds the information to the realtime database
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {

                                    // If statement that specifies what happens if the above operation is successful or not
                                    if(task.isSuccessful())
                                    {
                                        LayoutInflater inflater = getLayoutInflater();
                                        View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                                        TextView textView6 = customToastLayout.findViewById(R.id.name);
                                        textView6.setText("User has been registered.");

                                        Toast mToast = new Toast(RegisterActivity.this);
                                        mToast.setDuration(Toast.LENGTH_SHORT);
                                        mToast.setView(customToastLayout);
                                        mToast.show();
                                        //Toast.makeText(RegisterActivity.this, "User has been registered.", Toast.LENGTH_LONG).show();

                                        // Line 235 - 238
                                        // Title: How to clear back stack android activity
                                        // Author: Grepper
                                        // Date: 26 Feb 2021
                                        // Resource Link: https://www.codegrepper.com/code-examples/java/how+to+clear+back+stack+android+activity
                                        // Takes the user to their home page and prevents the previous page from being accessed
                                        Intent i = new Intent(RegisterActivity.this, MyCollectionsActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        RegisterActivity.this.finish();
                                    }

                                    else
                                    {
                                        LayoutInflater inflater = getLayoutInflater();
                                        View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                                        TextView textView6 = customToastLayout.findViewById(R.id.name);
                                        textView6.setText("Registration failed.");

                                        Toast mToast = new Toast(RegisterActivity.this);
                                        mToast.setDuration(Toast.LENGTH_SHORT);
                                        mToast.setView(customToastLayout);
                                        mToast.show();
                                        //Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }

                        else
                        {
                            LayoutInflater inflater = getLayoutInflater();
                            View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                            TextView textView6 = customToastLayout.findViewById(R.id.name);
                            textView6.setText("Registration failed.");

                            Toast mToast = new Toast(RegisterActivity.this);
                            mToast.setDuration(Toast.LENGTH_SHORT);
                            mToast.setView(customToastLayout);
                            mToast.show();
                            //Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
}



