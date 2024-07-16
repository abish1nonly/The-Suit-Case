package com.abish.thesuitcase;
import android.text.TextUtils;
import android.widget.EditText;

public class PasswordValidation {

    public static boolean validatePassword(EditText passwordEditText, EditText reEnterPasswordEditText) {
        String password = passwordEditText.getText().toString().trim();
        String reEnterPassword = reEnterPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter a password");
            return false;
        }

        if (TextUtils.isEmpty(reEnterPassword)) {
            reEnterPasswordEditText.setError("Please re-enter the password");
            return false;
        }

        if (!password.equals(reEnterPassword)) {
            reEnterPasswordEditText.setError("Passwords do not match");
            return false;
        }

        return true;
    }
}
