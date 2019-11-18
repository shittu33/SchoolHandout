package com.example.abumuhsin.udusmini_library.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.abumuhsin.udusmini_library.firebaseStuff.model.RegisteredStudent;
import com.example.abumuhsin.udusmini_library.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements OnClickListener {
    private static final String LOGIN_DEBUG = "LoginDebug";
    public static final int SIGNUP_REQUEST_CODE = 434;
    public static final int STUDENT_LOGGED_IN_CODE = 3456;
    public static final int SIGN_OUT_CODE = 289;
    private EditText e_username, e_password;
    private TextView txt;
    private Button login;
    private FirebaseAuth firebaseAuth;
    private String Uid;
    private FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.udus_login_activity);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            new AlertDialog.Builder(this).setMessage("You are currently signed in, do you want to sign out?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            firebaseAuth.signOut();
//                            setResult(SIGN_OUT_CODE);
                        }
                    })
                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setResult(STUDENT_LOGGED_IN_CODE);
                            finish();
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    }

    private void init() {
        init_Views();
        init_Firebase();
    }

    private void init_Views() {
        e_username = findViewById(R.id.username);
        e_password = findViewById(R.id.password);
        login = findViewById(R.id.btn);
        txt = findViewById(R.id.txt);
        login.setOnClickListener(this);
        txt.setOnClickListener(this);
    }

    private void init_Firebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
    private String adm_no;

    private String password;

    public void getTextFromFields() {
        adm_no = getFormText(e_username);
        password = getFormText(e_password);
    }

    private String getFormText(EditText editText) {
        return editText.getText().toString();
    }

    public boolean is_formFilled(String txt) {
        return !TextUtils.isEmpty(txt);
    }

    public boolean is_AllformFilled() {
        getTextFromFields();
        return is_formFilled(adm_no) && is_formFilled(adm_no) && is_formFilled(password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                if (is_AllformFilled()) {
                    final ProgressDialog dialog = ProgressDialog.show(this, "", "Authenticating....");
                    databaseReference.child("registered student").child(adm_no).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            RegisteredStudent registeredStudent = dataSnapshot.getValue(RegisteredStudent.class);
                            if (registeredStudent != null) {
                                String user_email = registeredStudent.getEmail();
                                firebaseAuth.signInWithEmailAndPassword(user_email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        dialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "user logged in", Toast.LENGTH_SHORT).show();
                                        setResult(STUDENT_LOGGED_IN_CODE);
                                        finish();
                                    }
                                });
                            } else {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, "no email was found for this user", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(this, "One of the fields are empty", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.txt:
                SignUp();
                break;
        }
    }

    private void SignUp() {
        Intent intent = null;
        try {
            intent = new Intent(this, Sign_Up_Activity.class);
            Log.i(LOGIN_DEBUG, "let's go!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivityForResult(intent, SIGNUP_REQUEST_CODE);
        Log.i(LOGIN_DEBUG, "let's go!");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNUP_REQUEST_CODE) {
            if (resultCode == Sign_Up_Activity.USER_SIGNEDUP_CODE) {
                setResult(STUDENT_LOGGED_IN_CODE);
                finish();
            }
        }
    }
}
