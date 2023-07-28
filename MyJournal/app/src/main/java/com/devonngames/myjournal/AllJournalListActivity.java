package com.devonngames.myjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

public class AllJournalListActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StorageReference storageReference;
    private List<Journal> journalList;
    private FirebaseAuth firebaseAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private  JournalRecyclerAdapter journalRecyclerAdapter;
    private CollectionReference collectionReference =db.collection("Journal");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_journal_list);
        setTitle("All Journal's");

        recyclerView = findViewById(R.id.allJournalRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        //Post ArrayList
        journalList = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                    journalRecyclerAdapter  =new JournalRecyclerAdapter(AllJournalListActivity.this,journalList);
                    recyclerView.setAdapter(journalRecyclerAdapter);
                    journalRecyclerAdapter.notifyDataSetChanged();


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AllJournalListActivity.this, "Oops! Something went wrong "+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}