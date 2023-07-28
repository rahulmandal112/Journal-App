package com.devonngames.myjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devonngames.myjournal.model.Journal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.Date;
import java.util.Objects;

public class AddJournalActivity extends AppCompatActivity {

    private static final int GALLERY_CODE = 1;

    private Button saveBtn;
    private ProgressBar progressBar;
    private ImageView addPhotoBtn;
    private EditText titleEditText;
    private  EditText thoughtEditText;
    private TextView currentUserTextView;
    private  ImageView imageView;

    //userId and username
    private  String currentUSerId;
    private  String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //connection to FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Journal");
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.post_progress_bar);
        titleEditText =findViewById(R.id.post_title_ET);
        thoughtEditText = findViewById(R.id.post_description_ET);
        currentUserTextView =findViewById(R.id.post_username_textView);
        imageView = findViewById(R.id.post_imageView);
        saveBtn = findViewById(R.id.post_save_journal_button);
        addPhotoBtn =findViewById(R.id.postCameraButton);

        progressBar.setVisibility(View.INVISIBLE);
        if(JournalUser.getInstance()!=null)
        {
            currentUSerId = JournalUser.getInstance().getUserId();
            currentUserName =JournalUser.getInstance().getUsername();
            currentUserTextView.setText(currentUserName);
        }
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user!=null)
                {

                }
                else
                {

                }
            }
        };
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveJournal();
            }
        });
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting image from gallery
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent,GALLERY_CODE);

            }
        });
    }

    private void SaveJournal() {
        final  String title = titleEditText.getText().toString().trim();
        final  String thoughts= thoughtEditText.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri !=null)
        {
            //the saving path of the images in storage firebase:
            final StorageReference filepath = storageReference.child("journal_images").child("my_image_"+ Timestamp.now().getSeconds());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl= uri.toString();
                            //Creating objects of Journal
                            //Lets create the model class
                            Journal journal =new Journal();
                            journal.setTitle(title);
                            journal.setThoughts(thoughts);
                            journal.setImageUrl(imageUrl);
                            journal.setTimeAdded(new Timestamp((new Date())));
                            journal.setUserName(currentUserName);
                            journal.setUserId(currentUSerId);
                            // Invoking Collection Reference
                            collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(AddJournalActivity.this,JournalListActivity.class));
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });



                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
        else
        {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode ==RESULT_OK)
        {
            if(data != null)
            {
                imageUri =data.getData();//Getting the actual image path
                imageView.setImageURI(imageUri);//showing the image
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user =firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}