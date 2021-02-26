package com.mohammedev.firebaselearning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohammedev.firebaselearning.Adapters.Adapter;
import com.mohammedev.firebaselearning.data.MyData;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 5;
    private static final String TAG = "photoUrl";
    Uri photoUri;
    ImageView imageEdt;
    EditText nameEdt, ageEdt, jobEdt;
    Button addBtn;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;

    DatabaseReference databaseReference;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        nameEdt = findViewById(R.id.name_edt);
        ageEdt = findViewById(R.id.age_edt);
        jobEdt = findViewById(R.id.job_edt);
        imageEdt = findViewById(R.id.image_edt);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


        imageEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        addBtn = findViewById(R.id.btn);

        recyclerView = findViewById(R.id.list);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameEdt.getText().toString().trim().equals("") | ageEdt.getText().toString().trim().equals("") | jobEdt.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity2.this, "one of the data is missing", Toast.LENGTH_SHORT).show();

                } else {
                    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    //MyData myData = new MyData("hey" , "43" , "minecrtat" , "https://pix6.agoda.net/hotelImages/361/3612581/3612581_18010316150060733791.jpg");
                    //databaseReference.push().setValue(myData);
                    fileUploader();




                }

            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        fetch();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK ){


            photoUri = data.getData();

            Picasso.with(this).load(photoUri).into(imageEdt);

        }

    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent , PICK_IMAGE_REQUEST);
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTxt, ageTxt, jobTxt;
        private ImageView photoImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTxt = itemView.findViewById(R.id.list_name);
            ageTxt = itemView.findViewById(R.id.list_age);
            jobTxt = itemView.findViewById(R.id.list_job);
            photoImg = itemView.findViewById(R.id.list_photo);
        }


    }

    // these two are must. idk why but you need them to show the View.
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }

    private void fetch() {
        // explained in MainActivity
        Query query = FirebaseDatabase.getInstance().getReference().child("Users");

        // before explaining the class, i will explain generics "<>".
        // generics is just like a parameter input. what does it do is change the variables below it to the one given in the new object.
        // For Instance, imagine that we have a class named "First" and we have a variable a inside it.
        // once we put a generic beside the class name like that: (public class First<T>),
        // it becomes a generic class the "T" inside generic brackets is a changeable variable type,
        // when we create an object of that class in other classes
        // we should put generics in front of class name so it becomes like (First<Integer> first = new First<Integer>();)
        // what does that do is it changes the variable inside the class into the given type in the generics.

        // this Class controls how FireBaseUI populates <(translate it) the RecyclerView with data from Real Time database.
        // this means that you can not use normal recyclerView to get data from the realtime database.
        // so they created this class to ease up the process.
        // as we can see, the class needs a "Model" generic and then we create an object from it and build it and setting the query.
        // there are going to be a lot of comments soryy.
        // so the query needs something called snapshot to retrieve its data from the database.
        // and in this object we need to set a query. so we pass it our query that we have made above and create a new Snapshot.
        FirebaseRecyclerOptions<MyData> options = new FirebaseRecyclerOptions.Builder<MyData>().setQuery(query, new SnapshotParser<MyData>() {
            @NonNull
            @Override
            public MyData parseSnapshot(@NonNull DataSnapshot snapshot) {
                // and then we return our data we need from query to call it in the adapter.
                return new MyData(
                        snapshot.child("name").getValue().toString(),
                        snapshot.child("age").getValue().toString(),
                        snapshot.child("job").getValue().toString(),
                        snapshot.child("imageUrl").getValue().toString());


            }
         // cannot forget that!
        }).build();

        // i see it this way... these two are going to be married and have a child with data and view. this child is then called Ultimate recyclerView.
        adapter = new FirebaseRecyclerAdapter<MyData, ViewHolder>(options) {
            // FirebaseRecyclerAdapter nonsense explanation from mr.shaliakwt.
            // in generics, MyData data gets populated with "options" object.
            // then MyData with its new Data goes into viewHolder which then is displayed by the recyclerView.
            //yep that's all.


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // typical stuff -_-
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout, parent, false);

                // the above viewHolder ^
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull MyData myData) {
                // typical stuff -_- 2
                viewHolder.nameTxt.setText(myData.getName());
                viewHolder.ageTxt.setText(myData.getAge());
                viewHolder.jobTxt.setText(myData.getJob());
                Picasso.with(MainActivity2.this).load(myData.getImageUrl()).into(viewHolder.photoImg);
            }
        };
        //typical stuff -_- 3
        recyclerView.setAdapter(adapter);
    }

   private String getExtension(Uri uri){

       ContentResolver cr = getContentResolver();
       MimeTypeMap mime = MimeTypeMap.getSingleton();
       return mime.getExtensionFromMimeType(cr.getType(uri));
   }



   private void fileUploader(){
       if (photoUri != null)
       {
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
                       Uri downloadUri = task.getResult();
                       Log.v(TAG, downloadUri.toString());

                       MyData upload = new MyData(
                               nameEdt.getText().toString().trim(),
                               ageEdt.getText().toString().trim(),
                               jobEdt.getText().toString().trim(),
                               downloadUri.toString());

                       databaseReference.push().setValue(upload);

                       nameEdt.setText("");
                       ageEdt.setText("");
                       jobEdt.setText("");
                       imageEdt.setImageResource(R.mipmap.ic_launcher_round);
                       photoUri = null;

                       Toast.makeText(MainActivity2.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                   } else
                   {
                       Toast.makeText(MainActivity2.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                   }
               }
           });
       } else {
           Toast.makeText(this, "No Photo Selected", Toast.LENGTH_SHORT).show();
       }
   }
}