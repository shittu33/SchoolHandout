package com.example.abumuhsin.udusmini_library.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.RegisteredStudent;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.model.Student;
import com.example.abumuhsin.udusmini_library.FirebaseStuff.util.FirebaseLoginOperation;
import com.example.abumuhsin.udusmini_library.R;
import com.example.abumuhsin.udusmini_library.utils.UdusDepartmentAndFacultyProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class Sign_Up_Activity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 6666;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ImageView student_img;
    private Button browse_btn;
    private Uri filePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initViews();
        initSpinnerAdapters();
        initFirebase();
    }

    private void initSpinnerAdapters() {
        ArrayAdapter<String> level_adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item
                , new String[]{"Select your level", "100 Level", "200 Level", "300 Level", "400 Level", "500 Level", "600 Level"});
        final ArrayAdapter<String> faculty_adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item
                , UdusDepartmentAndFacultyProvider.getFaculties());
        spin_level.setAdapter(level_adapter);
        spin_faculty.setAdapter(faculty_adapter);
        spin_faculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_faculty = faculty_adapter.getItem(i);
                ArrayAdapter<String> dept_adapter = new ArrayAdapter<>(Sign_Up_Activity.this
                        , R.layout.support_simple_spinner_dropdown_item
                        , UdusDepartmentAndFacultyProvider.getFactDepartments(selected_faculty));
                spin_dept.setAdapter(dept_adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private EditText edt_adm_no;
    private EditText edt_email;
    private EditText edt_password;
    private EditText edt_password2;
    private EditText edt_surname;
    private EditText edt_full_name;
    private EditText edt_phone_no;
    private Spinner spin_level;
    private Spinner spin_faculty;
    private Spinner spin_dept;

    private void initViews() {
        edt_adm_no = findViewById(R.id.adm_no);
        edt_email = findViewById(R.id.e_email);
        edt_password = findViewById(R.id.e_password);
        edt_password2 = findViewById(R.id.e_password2);
        edt_surname = findViewById(R.id.surname);
        edt_full_name = findViewById(R.id.e_full_name);
        edt_phone_no = findViewById(R.id.e_phone);
        spin_dept = findViewById(R.id.spin_dept);
        spin_level = findViewById(R.id.spinner_level);
        spin_faculty = findViewById(R.id.spinner_faculty);
        student_img = findViewById(R.id.student_img);
        browse_btn = findViewById(R.id.browse_img_btn);
        browse_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_up_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public final static int USER_SIGNEDUP_CODE = 12344;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_up:
                if (is_AllformFilled()) {
                    if (!is_emailValid(email)) {
                        Toast.makeText(this, "Email not valid", Toast.LENGTH_SHORT).show();
                    } else if (!is_password_matches(password, password2)) {
                        Toast.makeText(this, "password doesn't matches", Toast.LENGTH_SHORT).show();
                    } else if (!is_PasswordValid(password)) {
                        Toast.makeText(this, "password length must be greater than up to 6", Toast.LENGTH_LONG).show();
                    } else {
                        //Register
                        final ProgressDialog dialog = ProgressDialog.show(this, "", "Registering Student....");
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                if (currentUser != null) {
                                    RegisterStudentWithDatabase(new RegisteredStudent(adm_no, email, surname, full_name, currentUser.getUid(), department, faculty, level, phone_no));
                                }
                                dialog.dismiss();
                                setResult(USER_SIGNEDUP_CODE);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(Sign_Up_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Please fill the required fields", Toast.LENGTH_SHORT).show();
                }
//                Toast.makeText(this, "i will sign you up", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SaveStudentInfo(Student student) {
        databaseReference.child("Students Info").child(student.getStudent_uid()).setValue(student);
    }

    private void RegisterStudentWithDatabase(final RegisteredStudent registeredStudent) {
        if (filePath != null) {
            FirebaseLoginOperation.uploadImage(filePath, adm_no, new FirebaseLoginOperation.OnStudentImageUpload() {
                @Override
                public void onStudentImageUploaded(final Uri uri, String student_adm_no) {
                    registeredStudent.setStudent_imge_path(uri.toString());
                    databaseReference.child("registered student").child(adm_no).setValue(registeredStudent).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Student student = new Student(adm_no, email, full_name, registeredStudent.getStudent_uid()
                                    , department, faculty, level, uri.toString());
                            if (is_formFilled(phone_no)) {
                                student.setPhone_no(phone_no);
                            }
                            if (is_formFilled(surname)) {
                                student.setSurname(surname);
                            }
                            SaveStudentInfo(student);
                        }
                    });
                }

                @Override
                public void onStudentImageUploadFailed(Object object) {
                    if (object instanceof Exception) {
                        Toast.makeText(Sign_Up_Activity.this, ((Exception) object).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "you need to upload a picture", Toast.LENGTH_SHORT).show();
        }
    }

    private String adm_no;
    private String email;
    private String password;
    private String password2;
    private String surname;
    private String full_name;
    private String department;
    private String phone_no;
    private String faculty;
    private String level;

    public void getTextFromFields() {
        adm_no = getFormText(edt_adm_no);
        email = getFormText(edt_email);
        password = getFormText(edt_password);
        password2 = getFormText(edt_password2);
        surname = getFormText(edt_surname);
        full_name = getFormText(edt_full_name);
        department = getFormText(spin_dept);
        phone_no = getFormText(edt_phone_no);
        faculty = getFormText(spin_faculty);
        level = getFormText(spin_level);
    }

    private String getFormText(View view) {
        if (view instanceof EditText) {
            return ((EditText) view).getText().toString().trim();
        } else if (view instanceof Spinner) {
            return ((Spinner) view).getSelectedItem().toString().trim();
        }
        return "";
    }

    public boolean is_formFilled(String txt) {
        return !TextUtils.isEmpty(txt);
    }

    public boolean is_SpinSelected(String txt) {
        return !TextUtils.isEmpty(txt) && !txt.equals("Select your Department") && !txt.equals("Select your faculty") && !txt.equals("Select your level");
    }

    public boolean is_password_matches(String password, String password2) {
        return password.equals(password2);
    }

    public boolean is_emailValid(String email) {
        return email.contains("@");
    }

    public boolean is_PasswordValid(String password) {
        return password.length() >= 6;
    }

    public boolean is_AllformFilled() {
        getTextFromFields();
        return is_formFilled(adm_no) && is_formFilled(email) && is_formFilled(password) && is_formFilled(password2)
                && is_formFilled(full_name) && is_SpinSelected(department) && is_SpinSelected(faculty) && is_SpinSelected(level);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                FileUtils.getDownSizedBitmapFromPath(filePath.getPath(),200,200);
                student_img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
