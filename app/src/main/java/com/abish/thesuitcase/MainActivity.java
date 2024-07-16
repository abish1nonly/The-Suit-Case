package com.abish.thesuitcase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginButton;
    private Button createAccountButton;
    private EditText password, emailAddress, userEmail;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference loginData = db.collection("Users").document("Login details");

    public static final String KEY_EMAIL = "email";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login_button);
        createAccountButton = findViewById(R.id.create_acct_button_login);
        emailAddress = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.login_progress);

        createAccountButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view) {
        if(view == loginButton) {
            Log.i("signIn", "we've clicked sign in");

            String email = emailAddress.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            signIn(email, userPassword);
        }
        else if(view == createAccountButton){
            Log.i("CreateAccount", "we've clicked create account");
            Intent intent = new Intent(MainActivity.this, CreateAccount.class);
            startActivity(intent);
        }
    }

    private void signIn(String email, String userPassword) {
        mAuth.signInWithEmailAndPassword(email, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("LoginAuth", "signInWithCustomToken:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserDataToFirestore();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            Log.w("LoginAuth", "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void saveUserDataToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String email = emailAddress.getText().toString().trim();

        Map<String, Object> data = new HashMap<>();
        data.put(KEY_EMAIL, email);

        db.collection("Users")
                .document(email)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(),"User data saved to Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Error saving user data to Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}