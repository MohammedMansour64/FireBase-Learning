package com.mohammedev.firebaselearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.firebaselearning.data.MyData;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users");
//                MyData myData = new MyData("Mohammed Mansour" , "17" , "High School Student" , );
//                databaseReference1.push().setValue(myData);
//            }
//        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , MainActivity3.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , MainActivity2.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryGetName();
            }
        });

        //getting data from data base. how did it know what data base i am connected in? this application is connected to firebase and it will know automatically that you have made
        //a real time data base and will deliver this data to this project.
        firebaseDatabase = FirebaseDatabase.getInstance();
        // get a Reference from my database.
        DatabaseReference databaseReference = firebaseDatabase.getReference();


        getMultiData();
    }

    public void getMultiData(){
        //this method is for retrieving multiple data from a data base.
        //first line is getting a reference from the database and we should override addListenerValueEvent to retrieve data.
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // making a for loop to get all the data. this is a for each.
                // must save data in a new variable and it is in our example snapshot1. now snapshot1 has the children of (snapshot).
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    // then we want to get a specific data like name or age.. what we will do ?
                    // store it in a new myData object so we can after that we can now use the getters and setters.
                    //MyData myData = snapshot1.getValue(MyData.class);
                    //then we use it.
                    //Log.v("myData" , myData.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // do something here
            }
        });
    }

    private void queryGetName(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        // query is a firebase class that gets a single value from giving it a reference value of a "Node"
        // then it returns another "Node" from your choosing in the same object . like i did in line 96.
        // then everything else is familiar
        Query query = databaseReference.orderByChild("name").equalTo("Hamni");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        MyData myData = dataSnapshot.getValue(MyData.class);
                        Log.v("myDataQuery" , myData.getJob() + " " + myData.getAge());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}