package com.example.videocallingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.ConnectivityManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import java.net.MalformedURLException;
import java.net.URL;
import android.content.Context;

import timber.log.Timber;


public class DashboardActivity extends AppCompatActivity {


//    Button joinBtn, shareBtn, demoBtn;
Button  shareBtn, demoBtn, createBtn;

    public boolean isNetworkUnavailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return true;

            NetworkCapabilities capabilities =
                    connectivityManager.getNetworkCapabilities(network);
            return !(capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)));
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (isNetworkUnavailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        EditText codeBox = findViewById(R.id.codeBox);


        shareBtn=findViewById(R.id.shareBtn);
       createBtn = findViewById(R.id.createBtn);

         URL serverURL;
        if (isNetworkUnavailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

         try {
             serverURL= new URL("https://meet.ffmuc.net/");
             JitsiMeetConferenceOptions defaultOptions= new JitsiMeetConferenceOptions.Builder()
                             .setServerURL(serverURL)

                             .setFeatureFlag("welcomePage.enabled",false)
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
                String roomCode = codeBox.getText().toString().trim();

                if (roomCode.isEmpty()) {
                    // No input → create new room
                    roomCode = "Room" + System.currentTimeMillis();
                    codeBox.setText(roomCode); // Show generated code
                    Toast.makeText(DashboardActivity.this, "New room created: " + roomCode, Toast.LENGTH_SHORT).show();
                } else {
                    // Input exists → join as participant or host
                    Toast.makeText(DashboardActivity.this, "Joining room: " + roomCode, Toast.LENGTH_SHORT).show();
                }

                joinMeeting(roomCode);
            }
        });





        shareBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String string=codeBox.getText().toString();

                 Intent intent = new Intent(Intent.ACTION_SEND);
                 intent.putExtra(Intent.EXTRA_TEXT,string);
                 intent.setType("text/plain");
                 startActivity(intent);
             }
         });

         demoBtn=findViewById(R.id.demoBtn);
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
                .setFeatureFlag("welcomePage.enabled", false)
                .build();
        JitsiMeetActivity.launch(DashboardActivity.this, options);
    }

}