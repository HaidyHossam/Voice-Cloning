package com.example.v_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class activity_forget_password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        String correctCode = "1234";
        DatabaseHelper DB = new DatabaseHelper(this);
        EditText newPassword = (EditText) findViewById(R.id.newPassword);
        EditText confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        EditText confirmationCode = (EditText) findViewById(R.id.confirmtionCode);
        EditText EmailAddress = (EditText) findViewById(R.id.EmailAddress);
        final TextInputLayout passwordLayout = (TextInputLayout) findViewById(R.id.passlayout);
        final TextInputLayout questionLayout = (TextInputLayout) findViewById(R.id.questionLayout);
        final TextInputLayout codeLayout = (TextInputLayout) findViewById(R.id.confirmationCodeLayout);
        Button change = (Button) findViewById(R.id.change);
        passwordLayout.setErrorEnabled(false);
        questionLayout.setErrorEnabled(false);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = getIntent().getExtras().getString("username");
                String newPass = newPassword.getText().toString();
                String confirmPass = confirmPassword.getText().toString();
                String Email = EmailAddress.getText().toString();
                String Code = confirmationCode.getText().toString();
                questionLayout.setErrorEnabled(false);
                if (Code.equals(correctCode)) {
                    if (confirmPass.equals(newPass)) {
                        passwordLayout.setErrorEnabled(false);
                        DB.updatePassword(username, newPass);

                        Intent i = new Intent(com.example.v_clone.activity_forget_password.this, MainActivity.class);
                        startActivity(i);

                    } else {
                        passwordLayout.setErrorEnabled(true);
                        passwordLayout.setError("Password doesn't match!");
                    }
                }
                else{
                    passwordLayout.setErrorEnabled(true);
                    passwordLayout.setError("Please Recheck Confirmation Code Sent");
                }
            }
        });
    }
}
