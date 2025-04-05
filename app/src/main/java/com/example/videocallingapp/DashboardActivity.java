package com.example.videocallingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;




public class DashboardActivity extends AppCompatActivity {

    EditText codeBox;
//    Button joinBtn, shareBtn, demoBtn;
Button joinBtn, shareBtn, demoBtn, createBtn;

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        codeBox=findViewById(R.id.codeBox);
        joinBtn=findViewById(R.id.joinBtn);
        shareBtn=findViewById(R.id.shareBtn);
       createBtn = findViewById(R.id.createBtn);

         URL serverURL;
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

         try {
             serverURL= new URL("https://meet.jit.si");
             JitsiMeetConferenceOptions defaultOptions=
                     new JitsiMeetConferenceOptions.Builder()
                             .setServerURL(serverURL)
                             .setFeatureFlag("welcomepage.enabled",false)
                             .setFeatureFlag("invite.enabled", false)
                             .build();
             JitsiMeet.setDefaultConferenceOptions(defaultOptions);
         } catch (MalformedURLException e) {
             e.printStackTrace();
         }
// Create button
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomCode = "Room_" + System.currentTimeMillis(); // Generate unique room code
                joinMeeting(roomCode);
            }
        });

        // JOIN button (uses codeBox input)
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("JITSI_CALL", "Join button clicked");
                String roomCode = codeBox.getText().toString().trim();
                if (!roomCode.isEmpty()) {
                    joinMeeting(roomCode);
                }

                else {
                    Toast.makeText(DashboardActivity.this, "Enter room code", Toast.LENGTH_SHORT).show();
                }
            }
        });



        shareBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String string=codeBox.getText().toString();
                 Intent intent=new Intent();
                 intent.setAction(intent.ACTION_SEND);
                 intent.putExtra(Intent.EXTRA_TEXT,string);
                 intent.setType("text/plain");
                 startActivity(intent);
             }
         });

         demoBtn=(Button) findViewById(R.id.demoBtn);
         demoBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FirebaseAuth.getInstance().signOut();
                 startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
             }
         });
    }
    private void joinMeeting(String roomCode) {
        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                .setRoom(roomCode)
                .setFeatureFlag("welcomepage.enabled", false)
                .build();
        JitsiMeetActivity.launch(DashboardActivity.this, options);
    }

}