package com.abish.thesuitcase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class CreateAccount extends AppCompatActivity implements View.OnClickListener{
    private DatabaseReference mDatabase;
    private EditText emailEditText, reEnterPasswordEditText, passwordEditText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Button createAccountButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        createAccountButton = findViewById(R.id.create_acct_button);
        progressBar = findViewById(R.id.crete_acct_progress);
        emailEditText=findViewById(R.id.email_account);
        passwordEditText=findViewById(R.id.password_account);
        loginButton = findViewById(R.id.login_page);
        reEnterPasswordEditText = findViewById(R.id.re_password);

        createAccountButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }else{
            Log.d("currentUserCheck", "not logged in");
        }

    }
    public void onClick(View view){
        if(view == createAccountButton){
            if (PasswordValidation.validatePassword(passwordEditText, reEnterPasswordEditText)) {
                String email = emailEditText.getText().toString().trim();
                String pass = passwordEditText.getText().toString().trim();
                newUser(email, pass);
            } else {
                Log.w("AuthLog", "Unmatched password");
            }
        }
        else if(view == loginButton) {
            Log.i("CreateAccount", "we've clicked create account");
            Intent intent = new Intent(CreateAccount.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void newUser(String email, String pass) {
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE); // Move inside onComplete

                        if (task.isSuccessful()) {
                            Log.d("AuthLog", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(CreateAccount.this, HomeActivity.class);
                            startActivity(intent);
                            finish(); // Finish the current activity after successful authentication
                        } else {
                            Log.w("AuthLog", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccount.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

