package edu.northeasternn.studycircle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.northeasternn.studycircle.groupArtifacts.GroupCard;
import edu.northeasternn.studycircle.groupArtifacts.GroupCardAdapter;
import edu.northeasternn.studycircle.model.Group;
import edu.northeasternn.studycircle.model.User;


public class SearchGroupFragment extends Fragment {

    private Button searchBotton;
    private EditText zipcode, subject,groupName;
    private FirebaseFirestore db;

    private Map<String, Group> groups;
    private final int PERMISSION_ID = 123;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String locationZip;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private GroupCardAdapter recyclerViewAdapter;
    private List<GroupCard> groupCards;

    private RadioGroup searchByFilter;

    private RadioButton zipSelected;

    private RadioButton currLocationSelected;
    private View fragmentView;

    private User currentUser;



    public SearchGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search_group,
                container, false);
        fragmentView = view;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            setCurrentLocationZip();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
        }

        searchBotton = view.findViewById(R.id.groupSearchButton);
        subject =  view.findViewById(R.id.searchSubject);
        groupName = view.findViewById(R.id.groupTitle);
        zipcode =  view.findViewById(R.id.searchZipCode);
        searchByFilter = view.findViewById(R.id.searchOptionGroup);
        zipSelected = view.findViewById(R.id.zipRadio);
        currLocationSelected = view.findViewById(R.id.locationRadio);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        searchBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchGroups(view);
            }
        });

        db = FirebaseFirestore.getInstance();

        return view;
    }

    @SuppressLint("MissingPermission")
    private void setCurrentLocationZip() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {


                            try {
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                locationZip =  String.valueOf(addressList.get(0).getPostalCode());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
        );
    }

    private void searchGroups(View view) {


        CollectionReference dbStudyGroup = db.collection("Groups");
        Query query = dbStudyGroup;

        if (zipSelected.isChecked()) {
            if (zipcode.getText().length() == 0) {
                Snackbar.make(SearchGroupFragment.this.fragmentView, "Zip code cannot be empty.Zip should be 5 character long number", Snackbar.LENGTH_LONG).show();
                return;
            }
            try {
                Integer.valueOf(zipcode.getText().toString());
                if (zipcode.getText().length() != 5) {
                    Snackbar.make(SearchGroupFragment.this.fragmentView, "Invalid Zip Code.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                locationZip = zipcode.getText().toString();
            }
            catch (Exception e) {
                Snackbar.make(SearchGroupFragment.this.fragmentView, "Invalid Zip Code", Snackbar.LENGTH_LONG).show();
                return;
            }

        }
        else  {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                setCurrentLocationZip();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
            }
            if (locationZip == null) {
                Snackbar.make(SearchGroupFragment.this.fragmentView, "Current Location cannot be fetched", Snackbar.LENGTH_LONG).show();
                return;
            }



        }
        if(groupName.getText().toString()!=null && !groupName.getText().toString().trim().isEmpty()){
            query = dbStudyGroup.whereEqualTo("title",groupName.getText().toString());
        }
        if(subject.getText().toString()!=null && !subject.getText().toString().trim().isEmpty()){
            query = query.whereEqualTo("subject",subject.getText().toString());
        }
        query = query.whereEqualTo("location", locationZip);
        Query finalQuery = query;
        db.collection("Groups").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                finalQuery.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        groups = new HashMap<String, Group>() {
                        };
                        groupCards = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            groups.put(document.getId(),document.toObject(Group.class));
                        }
                        if (groups.size() == 0){
                            Snackbar.make(SearchGroupFragment.this.fragmentView, "No such group found", Snackbar.LENGTH_LONG).show();
                        }
                        createRecyclerView();

                    }
                });

            }
        });
    }

    /**
     * A method to create a recycler view.
     */
    void createRecyclerView(){
        layoutManager = new GridLayoutManager(getActivity(),2);
        //Pass groupcards to the adapter
        for(String groupId : groups.keySet()){
            Group group = groups.get(groupId);
            GroupCard groupCard = new GroupCard(group.getTitle(), group.getSubject(), group.getLocation(), groupId,group.getDays(),group.getDescription());
            groupCards.add(groupCard);
        }
        recyclerViewAdapter = new GroupCardAdapter(groupCards);


        //TODO : Fetch groupId so that user can join them when one accesses it

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }
}