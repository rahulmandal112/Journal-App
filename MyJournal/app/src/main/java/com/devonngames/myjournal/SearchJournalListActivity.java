package com.devonngames.myjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.devonngames.myjournal.model.Journal;
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

public class SearchJournalListActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StorageReference storageReference;
    private List<Journal> journalList;
    private FirebaseAuth firebaseAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private  JournalRecyclerAdapter journalRecyclerAdapter;
    private CollectionReference collectionReference =db.collection("Journal");
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_journal_list);

        recyclerView = findViewById(R.id.searchRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        //Post ArrayList
        journalList = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        setTitle(username+"'s Journal");

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {

            case  R.id.action_signout:
                //Signing out the user
                if(user != null && firebaseAuth != null)
                {
                    firebaseAuth.signOut();
                    startActivity(new Intent(SearchJournalListActivity.this,MainActivity.class));
                }
                break;
            case R.id.action_search:
                //Going to search Activity
                if(user!=null && firebaseAuth !=null)
                {
                    startActivity(new Intent(SearchJournalListActivity.this,SearchActivity.class));
                }
            case R.id.action_home:
                //Going to myJournal activity
                if(user!=null && firebaseAuth !=null)
                {
                    startActivity(new Intent(SearchJournalListActivity.this,JournalListActivity.class));
                }

        }
        return  super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.whereEqualTo("userName",username).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                    journalRecyclerAdapter  =new JournalRecyclerAdapter(SearchJournalListActivity.this,journalList);
                    recyclerView.setAdapter(journalRecyclerAdapter);
                    journalRecyclerAdapter.notifyDataSetChanged();


                }
            }
        });
    }
}