package com.devonngames.myjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText passwordCreateET;
    private  EditText emailCreateET;
    private  EditText usernameCreateET;
    private Button createBtn;

    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firebase connection
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference collectionReference =db.collection("Users");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Firebase auth rewire Google Account on the device

        passwordCreateET = findViewById(R.id.password_create);
        emailCreateET = findViewById(R.id.email_create);
        createBtn  = findViewById(R.id.email_sign_up_btn);
        usernameCreateET = findViewById(R.id.username_create);

        //Authentication
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser =firebaseAuth.getCurrentUser();
                if(currentUser != null)
                {
                    //User already logged in
                }
                else
                {
                    //no user yet
                }
            }
        };
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(emailCreateET.getText().toString()) && !TextUtils.isEmpty(passwordCreateET.getText().toString()))
                {
                    String email = emailCreateET.getText().toString().trim();
                    String password = passwordCreateET.getText().toString().trim();
                    String username = usernameCreateET.getText().toString().trim();

                    CreateUserEmailAccount(email,password,username);

                }
                else
                {
                    Toast.makeText(SignUpActivity.this,"Empty Fields",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CreateUserEmailAccount(String email, String password ,final  String username) {
        if(!TextUtils.isEmpty(emailCreateET.getText().toString()) && !TextUtils.isEmpty(passwordCreateET.getText().toString()))
        {
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        //we take user to next activity
                        currentUser =firebaseAuth.getCurrentUser();
                        assert currentUser !=null;
                        final  String currentUserId= currentUser.getUid();

                        //Create a userMap so we can create a user in the User Collection in Firebase
                        Map<String,String> userObj = new HashMap<>();
                        userObj.put("userId",currentUserId);
                        userObj.put("username",username);

                        //Adding users to firestore
                        collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(Objects.requireNonNull(task.getResult()).exists())
                                        {
                                            String name =task.getResult().getString("username");
                                            //If yhe user is registered successfully
                                            //then move to the journal activity
                                            //Getting use of GLobal Journal user
                                            JournalUser journalUser = JournalUser.getInstance();
                                            journalUser.setUserId(currentUserId);
                                            journalUser.setUsername(name);




                                            Intent i= new Intent(SignUpActivity.this,AddJournalActivity.class);
                                            i.putExtra("username",name);
                                            i.putExtra("usreId",currentUserId);
                                            startActivity(i);

                                        }
                                        else
                                        {

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Display Failed message
                                        Toast.makeText(SignUpActivity.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });



                    }

                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}