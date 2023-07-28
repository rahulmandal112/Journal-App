package com.devonngames.myjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.devonngames.myjournal.model.Journal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class JournalListActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StorageReference storageReference;
    private List<Journal> journalList;

    private RecyclerView recyclerView;
    private  JournalRecyclerAdapter journalRecyclerAdapter;

    private CollectionReference collectionReference =db.collection("Journal");
    private TextView noPostsTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        //Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        noPostsTV = findViewById(R.id.list_no_postTV);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Post ArrayList
        journalList = new ArrayList<>();


    }
    //Adding the Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_home:
                //Showing all Journals
                if(user != null && firebaseAuth != null)
                {
                    startActivity(new Intent(JournalListActivity.this,AllJournalListActivity.class));
                }
                break;

            case  R.id.action_add:
                //Going to Add Journal Activity
                if(user != null && firebaseAuth != null)
                {
                    startActivity(new Intent(JournalListActivity.this,AddJournalActivity.class));
                }
                break;
            case  R.id.action_signout:
                //Signing out the user
                if(user != null && firebaseAuth != null)
                {
                    firebaseAuth.signOut();
                    startActivity(new Intent(JournalListActivity.this,MainActivity.class));
                }
                break;
            case R.id.action_search:
                //Going to search Activity
                if(user!=null && firebaseAuth !=null)
                {
                    startActivity(new Intent(JournalListActivity.this,SearchActivity.class));
                }
                break;

        }
        return  super.onOptionsItemSelected(item);

    }
    //Getting All Posts

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.whereEqualTo("userId",JournalUser.getInstance().getUserId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty())
                {
                    for(QueryDocumentSnapshot journals:queryDocumentSnapshots)
                    {
                        Journal journal =journals.toObject(Journal.class);
                        journalList.add(journal);
                    }
                    //RecyclerView
                    //Create Adapter for RecyclerView
                    journalRecyclerAdapter  =new JournalRecyclerAdapter(JournalListActivity.this,journalList);
                    recyclerView.setAdapter(journalRecyclerAdapter);
                    journalRecyclerAdapter.notifyDataSetChanged();


                }
                else
                {
                    noPostsTV.setVisibility(View.VISIBLE);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //ANy Failure
                Toast.makeText(JournalListActivity.this, "Oops! Something went wrong "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}