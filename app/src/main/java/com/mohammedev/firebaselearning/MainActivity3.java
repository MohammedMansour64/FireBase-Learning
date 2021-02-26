package com.mohammedev.firebaselearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.firebaselearning.data.MyData;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

public class MainActivity3 extends AppCompatActivity  {
    private static final String TAG = "FireBase Authentication";
    private TextView nameTxt , ageTxt , jobTxt , emailTxt , register;
    private EditText emailEdt , passwordEdt;
    private ImageView photoImg;
    private Button signBtn;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


//                    //this means that it will take the reference of the child in the database. if not then will create a new one.
//                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
//                    MyData myData = new MyData(name.getText().toString() , age.getText().toString() , job.getText().toString());
//                    //here we will push the new data into the database specifically into the child we mentioned above.
//                    databaseReference.push().setValue(myData);

        nameTxt = findViewById(R.id.name_txt);
        ageTxt = findViewById(R.id.age_txt);
        jobTxt = findViewById(R.id.job_txt);
        emailTxt = findViewById(R.id.email_txt);
        register = findViewById(R.id.register_txt);

        emailEdt = findViewById(R.id.email_edt);
        passwordEdt = findViewById(R.id.password_edt);

        photoImg = findViewById(R.id.photo_img);

        signBtn = findViewById(R.id.sign_btn);
        mProgressBar = findViewById(R.id.progressBar);


        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity3.this , RegisterActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();


    }


    private void SignIn(){
        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(emailEdt.getText().toString().trim(), passwordEdt.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(user.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    MyData myData = snapshot.getValue(MyData.class);

                                    nameTxt.setText(myData.getName());
                                    ageTxt.setText(myData.getAge());
                                    jobTxt.setText(myData.getJob());
                                    emailTxt.setText(myData.getEmail());
                                    Picasso.with(MainActivity3.this).load(myData.getImageUrl()).into(photoImg);
                                    mProgressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity3.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            mProgressBar.setVisibility(View.GONE);

                        }

                        // ...
                    }
                });
    }
}