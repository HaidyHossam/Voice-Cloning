package com.example.v_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        DatabaseHelper DB = new DatabaseHelper(this);
        Button signup = (Button) findViewById(R.id.signupbtn);
        final TextInputLayout UsernameLayout = (TextInputLayout) findViewById(R.id.usernameInputlayout);
        final TextInputLayout EmailLayout = (TextInputLayout) findViewById(R.id.EmailInputLayout);
        EditText usernameText = (EditText) findViewById(R.id.usernameSignup);
        EditText emailText = (EditText) findViewById(R.id.email);

        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    EmailLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                EmailLayout.setErrorEnabled(false);
                if (email.length() == 0)
                    EmailLayout.setErrorEnabled(false);
                else {
                    boolean InvalidEmail = false;
                    if (isEmailValid(email) == false)
                        InvalidEmail = true;
                    if (InvalidEmail == true) {
                        EmailLayout.setError("Invalid email address");
                        EmailLayout.setErrorEnabled(true);
                    }
                }
            }
        });
        usernameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    UsernameLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String username = ((EditText) findViewById(R.id.usernameSignup)).getText().toString();
                UsernameLayout.setErrorEnabled(false);
                if (username.length() == 0)
                    UsernameLayout.setErrorEnabled(false);
                else {
                    Cursor cursor = DB.checkUsername(username);
                    boolean usernameExists = false;
                    if (cursor != null && cursor.getCount() > 0)
                        usernameExists = true;
                    if (usernameExists == true) {
                        UsernameLayout.setError("Username already exists");
                        UsernameLayout.setErrorEnabled(true);
                    }
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText) findViewById(R.id.nameSignup)).getText().toString();
                String username = ((EditText) findViewById(R.id.usernameSignup)).getText().toString();
                String pass = ((EditText) findViewById(R.id.passwordSignup)).getText().toString();
                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                Cursor cursor = DB.checkUsername(username);
                boolean usernameExists = false;
                if (cursor != null && cursor.getCount() > 0)
                    usernameExists = true;
                if (usernameExists == true) {
                    Toast.makeText(getApplicationContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                } else {
                    DB.addUser(name, username, pass,email);

                    Intent i = new Intent(SignUpActivity.this, PreMainActivity.class);
                    i.putExtra("username", username);
                    startActivity(i);
                }
            }
        });
    }
}