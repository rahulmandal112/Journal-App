package com.devonngames.myjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchActivity extends AppCompatActivity {

    private EditText usernameET;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Search Journal");

        usernameET=findViewById(R.id.ETusername);
        btnSearch=findViewById(R.id.buttonSearch);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(usernameET.getText().toString().trim()))
                {
                    String username = usernameET.getText().toString().trim();
                    Intent i =new Intent(SearchActivity.this,SearchJournalListActivity.class);
                    i.putExtra("username",username);
                    startActivity(i);
                }
            }
        });

    }
}