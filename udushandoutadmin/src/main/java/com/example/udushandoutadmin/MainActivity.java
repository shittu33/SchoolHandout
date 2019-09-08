package com.example.udushandoutadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.udushandoutadmin.FirebaseUtils.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUtils.get(this);
        firebaseAuth = FirebaseUtils.firebaseAuth;
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser!=null){
            TextView msg_txt = findViewById(R.id.txt);
            msg_txt.setText("Welcome back!!!");
            Toast.makeText(this.getBaseContext(), "you are welcome", Toast.LENGTH_SHORT).show();
        }else {
            FirebaseUtils.SignIn();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    TextView msg_txt = findViewById(R.id.txt);
                    msg_txt.setText("Welcome back!!!");
                    Toast.makeText(this.getBaseContext(), "you are welcome", Toast.LENGTH_SHORT).show();
                }
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

}
