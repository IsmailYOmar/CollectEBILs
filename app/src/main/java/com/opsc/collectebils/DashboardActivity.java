//status?, H. and Llorente, J., 2016. How can I use Shared Preferences to save ToggleButton/Switch status?. [online] Stack Overflow. Available at: <https://stackoverflow.com/questions/38735812/how-can-i-use-shared-preferences-to-save-togglebutton-switch-status> [Accessed 29 June 2022].
//Tutlane.com. 2022. Android Custom Toast with Examples - Tutlane. [online] Available at: <https://www.tutlane.com/tutorial/android/android-custom-toast-with-examples#:~:text=Create%20a%20Custom%20Toast%20in,code%20like%20as%20shown%20below> [Accessed 29 June 2022].


package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseUser user;
    private DatabaseReference ref;
    private String userId;
    private Button logoutButton;
    private Button collectionStatistics;
    Spinner dropDown;
    // initializes UI switch
    Switch twoFactorToggle;
    // initializes UI dialog
    Dialog authentication;
    public  BottomNavigationView bottomNavigationView;
    // initializes GoogleSignInClient to variable
    //private GoogleSignInClient signInClient;
    // initializes int used in Google sign in process
    //private final static int RC_SIGN_IN = 123;
    // initializes Firebase Authentication to variable
    //private FirebaseAuth mAuth;
    // initializes int
    public int toggle;

    /*@Override
    public void onStart() {
        super.onStart();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // constructor for new UI dialog
        authentication = new Dialog(this);

        dropDown = findViewById(R.id.darkMode);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Spinner_items,
                R.layout.dropdown_item
        );
        //style dropdown menu
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        dropDown.setAdapter(adapter);

        // save current position dropdown menu
        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);
        int spinnerValue = sharedPref.getInt("spinnerChoice",-1);
        if(spinnerValue != -1)
            dropDown.setSelection(spinnerValue);

        //change theme depending on position in dropdown menu
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (dropDown.getSelectedItem().equals("On")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    //when on selected set them to dark mode
                }
                else if(dropDown.getSelectedItem().equals("Off")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    //when on selected set them to light mode
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    //else use phone theme
                }

                //save selected dropdown position to SharedPreferences
                int Choice = dropDown.getSelectedItemPosition();
                SharedPreferences sharedPref = getSharedPreferences("FileName",0);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putInt("spinnerChoice",Choice);
                prefEditor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // defines variable as an instance object for Firebase
        //mAuth = FirebaseAuth.getInstance();

        // calls the createRequest method
        //createRequest();

        // links the variable to its UI counter part
        twoFactorToggle = (Switch) findViewById(R.id.twoFactorToggle);

        //SharedPreferences sharedPrefs = getSharedPreferences("twoFactor",0);

        //toggle = sharedPrefs.getInt("twoFactorToggle", 0);

        /*if (toggle == 1) {
            twoFactorToggle.setChecked(true);
        } else if (toggle == 0) {
            twoFactorToggle.setChecked(false);
        }*/

        // method to allow interaction between backend and UI switch
        twoFactorToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                // takes design from layout resource file
                authentication.setContentView(R.layout.authentication_window);
                // sets the background of the design to transparent
                authentication.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Button closeBtn;
                Button googleAuth;
                Button otherAuth;

                closeBtn = (Button) authentication.findViewById(R.id.closeBtn);
                googleAuth = (Button) authentication.findViewById(R.id.googleAuth);
                otherAuth = (Button) authentication.findViewById(R.id.otherAuth);

                // condition of boolean if true
                if (b == true) {
                    // sets int to 1
                    toggle = 1;
                    // condition if int is 1
                    if (toggle == 1) {
                        // sets UI switch to true
                        twoFactorToggle.setChecked(true);
                        // displays UI dialog
                        authentication.show();
                        otherAuth.setOnClickListener(new View.OnClickListener() {
                            @Override
                            // displays a custom toast message if clicked
                            public void onClick(View view) {
                                LayoutInflater inflater = getLayoutInflater();
                                View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                                TextView textView6 = customToastLayout.findViewById(R.id.name);
                                textView6.setText("Not available at this moment. Coming soon");

                                Toast mToast = new Toast(DashboardActivity.this);
                                mToast.setDuration(Toast.LENGTH_SHORT);
                                mToast.setView(customToastLayout);
                                mToast.show();
                            }
                        });
                        closeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            // closes UI dialog and sets UI switch to false
                            public void onClick(View view) {
                                authentication.dismiss();
                                twoFactorToggle.setChecked(false);
                                /*SharedPreferences sharedPref = getSharedPreferences("FileName",0);
                                SharedPreferences.Editor prefEditor = sharedPref.edit();
                                prefEditor.putInt("twoFactorToggle",0);
                                prefEditor.commit();*/
                            }
                        });
                        googleAuth.setOnClickListener(new View.OnClickListener() {
                            // displays a custom toast message if clicked
                            @Override
                            public void onClick(View view) {

                                LayoutInflater inflater = getLayoutInflater();
                                View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                                TextView textView6 = customToastLayout.findViewById(R.id.name);
                                textView6.setText("Not available at this moment. Coming soon");

                                Toast mToast = new Toast(DashboardActivity.this);
                                mToast.setDuration(Toast.LENGTH_SHORT);
                                mToast.setView(customToastLayout);
                                mToast.show();

                                // closes UI dialog
                                /*authentication.dismiss();
                                // call the SignIn method
                                SignIn();
                                SharedPreferences sharedPref = getSharedPreferences("twoFactor",0);
                                SharedPreferences.Editor prefEditor = sharedPref.edit();
                                prefEditor.putInt("twoFactorToggle",1);
                                prefEditor.commit();*/
                            }
                        });
                    }
                }

                /*if (b) {
                    authentication.show();
                    otherAuth.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(DashboardActivity.this, "Not available.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            authentication.dismiss();
                            twoFactorToggle.setChecked(false);
                            SharedPreferences sharedPref = getSharedPreferences("FileName",0);
                            SharedPreferences.Editor prefEditor = sharedPref.edit();
                            prefEditor.putInt("twoFactorToggle",0);
                            prefEditor.commit();
                        }
                    });
                    googleAuth.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            authentication.dismiss();
                            SignIn();
                            SharedPreferences sharedPref = getSharedPreferences("twoFactor",0);
                            SharedPreferences.Editor prefEditor = sharedPref.edit();
                            prefEditor.putInt("twoFactorToggle",1);
                            prefEditor.commit();
                        }
                    });
                }*/


            }
        });

        logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);

        // get current user and create firebase instance
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users");
        userId = user.getUid();

        final TextView full_name_view = (TextView) findViewById(R.id.full_name_view);
        //final TextView email_address_view = (TextView) findViewById(R.id.email_address_view);

        collectionStatistics = (Button) findViewById(R.id.collection_statistics);
        collectionStatistics.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openCollectionStatisticsActivity();
            }
        });

        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener()
        {// get users Full Name from FireBase and Display in  TextView
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null) {
                    String fullName = userProfile.fullName;
                    String emailAddress = userProfile.emailAddress;

                    full_name_view.setText(fullName);
                    //email_address_view.setText(emailAddress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {//nav menu styling and intents to change activity
                    case R.id.myCollections:
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        startActivity(new Intent(getApplicationContext(), MyCollectionsActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.explore:
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
                        startActivity(new Intent(getApplicationContext(), ExploreActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.marketplace:
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);
                        startActivity(new Intent(getApplicationContext(), MarketplaceActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.dashboard:
                        bottomNavigationView.getMenu().getItem(3).setChecked(true);
                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return true;
            }
        });
    }

    // requests, builds, and initializes a Google request token and client
    // https://medium.com/geekculture/how-to-integrate-firebase-authentication-for-google-sign-in-functionality-e955d7e549bf
    /*private void createRequest() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);
    }

    // initializes a Google sign in intent and starts the process
    // https://medium.com/geekculture/how-to-integrate-firebase-authentication-for-google-sign-in-functionality-e955d7e549bf
    private void SignIn() {
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    // starts and integrates Google sign in
    // https://medium.com/geekculture/how-to-integrate-firebase-authentication-for-google-sign-in-functionality-e955d7e549bf
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // pairs Firebase Authentication services with Google sign in services
    // https://medium.com/geekculture/how-to-integrate-firebase-authentication-for-google-sign-in-functionality-e955d7e549bf
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            startActivity(intent);
                        }
                        else {

                        }
                    }
                });
    }*/

    public void openCollectionStatisticsActivity()
    { // Open activity to display users collection statistics
        Intent intent = new Intent(this, CollectionStatisticsActivity.class);
        startActivity(intent);
    }
    @Override
    public void onResume()
    {//override page animation on page resume
        super.onResume();
        overridePendingTransition(0, 0);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.logoutButton:
                LogoutUser();
                //GoogleLogout();
                break;
        }
    }

    private void LogoutUser()
    {//log out user from Firebase
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    // logs out a Google user
    //https://medium.com/geekculture/how-to-integrate-firebase-authentication-for-google-sign-in-functionality-e955d7e549bf
    /*private void GoogleLogout() {
        signInClient.signOut();
    }*/
}