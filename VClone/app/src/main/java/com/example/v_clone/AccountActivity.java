package com.example.v_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

public class AccountActivity extends AppCompatActivity {
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        String Username = getIntent().getExtras().getString("username"," ");
        DatabaseHelper DB = new DatabaseHelper(this);
        Cursor cursor = DB.getUser(Username);

        EditText username = (EditText) findViewById(R.id.usernameAccount);
        EditText email = (EditText) findViewById(R.id.emailAccount);
        EditText name = (EditText) findViewById(R.id.nameAccount);
        EditText password = (EditText) findViewById(R.id.passwordAccount);
        EditText newPassword = (EditText) findViewById(R.id.newPassword);
        Button save = (Button) findViewById(R.id.save);
        Button changePassword = (Button) findViewById(R.id.changePass);
        FloatingActionButton editName = (FloatingActionButton) findViewById(R.id.editName);
        FloatingActionButton editEmail = (FloatingActionButton) findViewById(R.id.editEmail);

        final TextInputLayout EmailLayout = (TextInputLayout) findViewById(R.id.EmailInputLayoutAcc);
        final TextInputLayout PasswordLayout = (TextInputLayout) findViewById(R.id.currentPasswordInputLayout);
        final TextInputLayout newPasswordLayout = (TextInputLayout) findViewById(R.id.newPasswordInputLayout);

        username.setText(Username);
        email.setText(cursor.getString(3));
        name.setText(cursor.getString(0));

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }
        });
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }
        });

        email.addTextChangedListener(new TextWatcher() {
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
                String email = ((EditText) findViewById(R.id.emailAccount)).getText().toString();
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!name.getText().toString().equals("")&&!email.getText().toString().equals("")){
                    boolean InvalidEmail = false;
                    if(isEmailValid(email.getText().toString()) == false)
                        InvalidEmail = true;

                    if (InvalidEmail == true) {
                        EmailLayout.setError("Invalid email address");
                        EmailLayout.setErrorEnabled(true);
                    }
                    else {
                        DB.updateInfo(email.getText().toString(), name.getText().toString(), Username);
                        Toast.makeText(getApplicationContext(),"Info updated successfully",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"No Name or Email entered",Toast.LENGTH_SHORT).show();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals("")){
                    PasswordLayout.setError("Please enter password");
                    PasswordLayout.setErrorEnabled(true);
                }
                else
                    PasswordLayout.setErrorEnabled(false);
                if(newPassword.getText().toString().equals("")){
                    newPasswordLayout.setError("Please enter new password");
                    newPasswordLayout.setErrorEnabled(true);
                }
                else{
                    newPasswordLayout.setErrorEnabled(false);
                    if(password.getText().toString().equals("")){
                        PasswordLayout.setError("Please enter password");
                        PasswordLayout.setErrorEnabled(true);
                    }
                    else if(DB.checkPassword(Username,password.getText().toString()) == false){
                        PasswordLayout.setError("Incorrect password");
                        PasswordLayout.setErrorEnabled(true);
                    }
                    else{
                        PasswordLayout.setErrorEnabled(false);
                        DB.updatePassword(Username,newPassword.getText().toString());
                        Toast.makeText(getApplicationContext(),"Password changed",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }
}