package edu.northeasternn.studycircle;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import edu.northeasternn.studycircle.model.Connection;
import edu.northeasternn.studycircle.model.Sample;
import edu.northeasternn.studycircle.model.User;
import edu.northeasternn.studycircle.util.ConnectionStatus;
import edu.northeasternn.studycircle.util.HomePagerAdapter;
import edu.northeasternn.studycircle.util.ZoomOutPageTransformer;

public class MainActivity extends AppCompatActivity {


    private ViewPager2 homeScreenViewPager;

    private static  final String TAG = "MainActivity";
    private TabLayout homeScreenTabs;

    private User currUser;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeScreenTabs = findViewById(R.id.homeScreenToolbar);
        db = FirebaseFirestore.getInstance();
        fetchCurrentUserInfo();

        int [] tabIcons = {R.drawable.home_icon_foreground,R.drawable.groups_icon_foreground,R.drawable.profile_icon_foreground};
        int [] tabLabels = {R.string.home,R.string.groups,R.string.profile};
        TabLayout.Tab homeTab = homeScreenTabs.newTab();
        homeTab.setIcon(R.drawable.home_icon_foreground);
        homeTab.setText(R.string.home);

        TabLayout.Tab groupsTab = homeScreenTabs.newTab();
        groupsTab.setIcon(R.drawable.groups_icon_foreground);
        groupsTab.setText(R.string.groups);

        TabLayout.Tab profileTab = homeScreenTabs.newTab();
        profileTab.setIcon(R.drawable.profile_icon_foreground);
        profileTab.setText(R.string.profile);


        homeScreenTabs.addTab(homeTab);
        homeScreenTabs.addTab(groupsTab);
        homeScreenTabs.addTab(profileTab);
        homeScreenTabs.setTabGravity(TabLayout.GRAVITY_FILL);

        homeScreenViewPager = findViewById(R.id.homeScreenPager);
        final HomePagerAdapter homePagerAdapter = new HomePagerAdapter(this);
        homeScreenViewPager.setAdapter(homePagerAdapter);
        homeScreenViewPager.setPageTransformer(new ZoomOutPageTransformer());
        new TabLayoutMediator(homeScreenTabs, homeScreenViewPager, (tab, position) -> {
            tab.setIcon(tabIcons[position]);
            tab.setText(tabLabels[position]);
        }).attach();
        homeScreenViewPager.setOffscreenPageLimit(2);
        homeScreenTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                homeScreenViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        addSampleToFireStore();
//        getSampleToFireStore();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menusentitem: logoutFromTheApp();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainactivity_menu,menu);
        return true;
    }


    private void logoutFromTheApp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        logoutFromTheApp();
    }
    private void fetchCurrentUserInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currUser = document.toObject(User.class);
                    } else {
                        Log.d(TAG, "No User Info found");
                    }
                } else {
                    Log.d(TAG, "Failed to connect to database ", task.getException());
                }
            }
        });
    }



    private void addSampleToFireStore(){
        DocumentReference df = db.collection("sample").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        List<Connection> sent = new ArrayList<>();
        List<Connection> received = new ArrayList<>();
        Sample sample = new Sample("fsgsdgf",sent,received);
        df.set(sample).addOnSuccessListener(v->{}).addOnFailureListener(v->{});
    }

    private void getSampleToFireStore(){
        DocumentReference df = db.collection("sample").document(FirebaseAuth.getInstance().getCurrentUser().getEmail().toUpperCase());
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Sample sample = document.toObject(Sample.class);
                        Log.d(TAG,sample.toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }






}