package com.example.itunesremake;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    EditText email_input;
    EditText password_input;
    Button login;
    TextView sign_up_link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email_input = findViewById(R.id.email_input_sign_in);
        password_input = findViewById(R.id.password_input_sign_in);
        login = findViewById(R.id.sign_in_button);
        sign_up_link = findViewById(R.id.go_to_sign_up);

        login.setOnClickListener(this);
        sign_up_link.setOnClickListener(this);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.sign_in_button:
                loginUser();
            break;

            case R.id.go_to_sign_up:
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            break;
        }
    }

    private void loginUser()
    {
        String email = email_input.getText().toString().trim();
        String password = password_input.getText().toString().trim();

        if(email.isEmpty())
        {
            email_input.setError("Email is required");
            email_input.requestFocus();
            return;
        }
        else if(password.isEmpty())
        {
            password_input.setError("Password is required");
            email_input.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            email_input.setError("Please enter a valid email address");
            email_input.requestFocus();
            return;
        }

        if(password.length() < 4)
        {
            password_input.setError("Minim length for password is 4");
            password_input.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Intent home = new Intent(SignInActivity.this, HomeActivity.class);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(home);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
