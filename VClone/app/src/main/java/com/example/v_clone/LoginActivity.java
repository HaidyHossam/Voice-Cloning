package com.example.v_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private String sharedPrefFile = "com.example.android.onlineShoppingPref";
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        DatabaseHelper DB = new DatabaseHelper(this);
        TextView signup = (TextView) findViewById(R.id.signup2);
        TextView forgotpass = (TextView) findViewById(R.id.forgetpass);
        Button login = (Button) findViewById(R.id.loginBtn);
        final TextInputLayout floatingUsernameLabel = (TextInputLayout) findViewById(R.id.UsernametextInputLayout);
        final TextInputLayout floatingPassLabel = (TextInputLayout) findViewById(R.id.PasstextInputLayout);
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        CheckBox remember = (CheckBox) findViewById(R.id.rememberme);

        SharedPreferences mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        Boolean Logged_in = mPreferences.getBoolean("remember", false);
        if (Logged_in == true) {
            username.setText(mPreferences.getString("username", ""));
            password.setText(mPreferences.getString("password", ""));
        }
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, activity_forget_password.class);
                startActivity(i);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameValue = username.getText().toString();
                String passwordValue = password.getText().toString();
                floatingUsernameLabel.setErrorEnabled(false);
                floatingPassLabel.setErrorEnabled(false);
                Boolean checkPass = DB.checkPassword(usernameValue, passwordValue);
                if (checkPass == true) {
                    if (remember.isChecked()) {
                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putString("username", usernameValue);
                        editor.putString("password", passwordValue);
                        editor.putBoolean("remember", true);
                        editor.apply();
                    }
                    Cursor cursor = DB.checkUsername(username.getText().toString());
                    Intent i = new Intent(LoginActivity.this, PreMainActivity.class);
                    i.putExtra("username", cursor.getString(0));
                    startActivity(i);
                } else {
                    floatingPassLabel.setError("Wrong username or password");
                    floatingPassLabel.setErrorEnabled(true);
                    floatingUsernameLabel.setError("  ");
                    floatingUsernameLabel.setErrorEnabled(true);
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }
}