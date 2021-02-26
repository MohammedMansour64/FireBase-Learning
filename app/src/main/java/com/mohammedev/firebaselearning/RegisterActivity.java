package com.mohammedev.firebaselearning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohammedev.firebaselearning.data.MyData;
import com.squareup.picasso.Picasso;

public class RegisterActivity extends AppCompatActivity {

    private static final int GET_IMAGE = 1;
    private static final String TAG = "Firebase Storage";
    Uri photoUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    ImageView photoImg;

    EditText nameEdt , ageEdt , jobEdt , emailEdt , passwordEdt;

    Button registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        photoImg = findViewById(R.id.photo_img);

        nameEdt = findViewById(R.id.name_edt);
        ageEdt = findViewById(R.id.age_edt);
        jobEdt = findViewById(R.id.job_edt);
        emailEdt = findViewById(R.id.email_edt);
        passwordEdt = findViewById(R.id.password_edt);

        registerBtn = findViewById(R.id.register_btn);




        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toStorage();
                signUp();
            }
        });

        photoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });

    }
    private void getImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent , GET_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_IMAGE && resultCode == RESULT_OK){
            photoUri = data.getData();
            Picasso.with(this).load(photoUri).into(photoImg);
        }
    }

    private String getExtension(Uri uri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void toStorage(){
        if (photoUri == null |
                nameEdt.getText().toString().trim().isEmpty() |
                ageEdt.getText().toString().trim().isEmpty() |
                jobEdt.getText().toString().trim().isEmpty() |
                emailEdt.getText().toString().trim().isEmpty() |
                passwordEdt.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "one of the data is empty", Toast.LENGTH_SHORT).show();
        } else {
            storageReference = FirebaseStorage.getInstance().getReference().child("images").child(System.currentTimeMillis() + "." + getExtension(photoUri));
            storageReference.putFile(photoUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        pause();
                        Uri downloadUri = task.getResult();
                        Log.v(TAG, downloadUri.toString());

                        MyData upload = new MyData(
                                nameEdt.getText().toString().trim() ,
                                ageEdt.getText().toString().trim() ,
                                jobEdt.getText().toString().trim() ,
                                downloadUri.toString() ,
                                emailEdt.getText().toString().trim());

                                                                                                        //here we made another child in databaseReference named getUid.
                                                                                                        // Uid is a random code created for the Authentication account
                                                                                                        // we need to have the child of "Users" named same as Uid
                                                                                                        // so then we can retrieve data using the similar random code
                                                                                                        // between the authentication and the realtime database.
                                                                                                        // go to MainActivity3 to see how it was done.
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        databaseReference.setValue(upload);



                        Toast.makeText(RegisterActivity.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        Toast.makeText(RegisterActivity.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    nameEdt.setText("");
                    ageEdt.setText("");
                    jobEdt.setText("");
                    emailEdt.setText("");
                    passwordEdt.setText("");
                    photoImg.setImageResource(R.drawable.ic_baseline_crop_original_24);
                    photoUri = null;

                    start();
                }
            });

        }
    }

    private void signUp() {
        String email = emailEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();

        if (email.isEmpty() | password.isEmpty()){
            Toast.makeText(this, "Email or Password is empty", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdt.setError("this email is not valid.");
            emailEdt.requestFocus();
        }
        else if (password.length() < 6){
            passwordEdt.setError("this password is weak!");
            passwordEdt.requestFocus();

        }else{
            mAuth.createUserWithEmailAndPassword(email , password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(RegisterActivity.this, "Authenticated Successfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // ...
                        }
                    });
        }

    }

    private void pause(){
        nameEdt.setEnabled(false);
        ageEdt.setEnabled(false);
        jobEdt.setEnabled(false);
        emailEdt.setEnabled(false);
        passwordEdt.setEnabled(false);
        photoImg.setEnabled(false);
        registerBtn.setEnabled(false);

    }
    private void start(){
        nameEdt.setEnabled(true);
        ageEdt.setEnabled(true);
        jobEdt.setEnabled(true);
        emailEdt.setEnabled(true);
        passwordEdt.setEnabled(true);
        photoImg.setEnabled(true);
        registerBtn.setEnabled(true);
    }
}