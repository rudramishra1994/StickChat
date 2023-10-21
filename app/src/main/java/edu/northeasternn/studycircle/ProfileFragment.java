package edu.northeasternn.studycircle;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.northeasternn.studycircle.model.User;
import edu.northeasternn.studycircle.util.ProfilePagerAdapter;
import edu.northeasternn.studycircle.util.ZoomOutPageTransformer;


public class ProfileFragment extends Fragment {

    private TextView userName, fullName;
    private TabLayout profileFragmentToolbar;

    private ViewPager2 profileFragmentPager;

    private View view;

    private User currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile,
                container, false);
        this.userName = view.findViewById(R.id.userName);
        this.fullName = view.findViewById(R.id.fullName);
        profileFragmentToolbar = view.findViewById(R.id.connectionsToolbar);

        setUpTabLayout();
        setUpViewPager();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toUpperCase();
        String username = email;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dr = db.collection("users").document(username);

        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists() && documentSnapshot != null) {
                    currentUser = documentSnapshot.toObject(User.class);
                    String userFullName = currentUser.getFirstName() + " " + currentUser.getLastName();
                    fullName.setText(userFullName);
                }
            }
        });
        this.userName.setText(username);
        return view;
    }

    private void setUpTabLayout() {
        TabLayout.Tab  myconnections = profileFragmentToolbar.newTab();
        TabLayout.Tab mynetwork = profileFragmentToolbar.newTab();
        TabLayout.Tab connectionrequest = profileFragmentToolbar.newTab();

        profileFragmentToolbar.addTab(mynetwork);
        profileFragmentToolbar.addTab(myconnections);
        profileFragmentToolbar.addTab(connectionrequest);
    }

    private void setUpViewPager() {

        profileFragmentPager = view.findViewById(R.id.myProfilePager);
        final ProfilePagerAdapter myAdapter = new ProfilePagerAdapter(this);
        profileFragmentPager.setAdapter(myAdapter);
        profileFragmentPager.setPageTransformer(new ZoomOutPageTransformer());
        int [] tabIcons = {R.drawable.network_foreground,R.drawable.newconnection_foreground,R.drawable.newrequest_foreground};
        int [] tabLabels = {R.string.myconnection,R.string.newconnection,R.string.connectionRequest};
        new TabLayoutMediator(profileFragmentToolbar, profileFragmentPager, (tab, position) -> {
            tab.setIcon(tabIcons[position]);
            tab.setText(tabLabels[position]);
        }).attach();
        profileFragmentPager.setOffscreenPageLimit(2);
        profileFragmentPager.setOffscreenPageLimit(2);
        profileFragmentToolbar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                profileFragmentPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}