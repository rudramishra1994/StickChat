package edu.northeasternn.studycircle;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.northeasternn.studycircle.connectionrequest.ConnectionRequestRecyclerAdapter;
import edu.northeasternn.studycircle.model.Connection;
import edu.northeasternn.studycircle.model.Friend;
import edu.northeasternn.studycircle.model.User;
import edu.northeasternn.studycircle.model.UserConnection;
import edu.northeasternn.studycircle.userconnectionartifact.UserConnectionRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserConnectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserConnectionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseFirestore db ;

    private  FirebaseUser currentUser ;

    private List<Friend> friendList;

    private RecyclerView userConnectionRecyclerView;

    private UserConnectionRecyclerViewAdapter adapter;

    private View view;

    private ProgressBar progressBar;

    private TextView userConnectionEmptyText;






    public UserConnectionFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserConnectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserConnectionFragment newInstance(String param1, String param2) {
        UserConnectionFragment fragment = new UserConnectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_user_connection, container, false);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.friendList = new ArrayList<>();
        userConnectionRecyclerView = view.findViewById(R.id.userconnectionrecyclerview);
        progressBar = view.findViewById(R.id.userconnectionprogressbar);
        userConnectionEmptyText = view.findViewById(R.id.userconnectionrecyclerviewemptytext);
        userConnectionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userConnectionRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(userConnectionRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        userConnectionRecyclerView.addItemDecoration(dividerItemDecoration);

        processUserConnectionList();
        return  view;
    }


    void processUserConnectionList() {
        String email = currentUser.getEmail().toUpperCase();
        CollectionReference cf = db.collection("userConnection").document(email).collection("friends");
        DocumentReference df = db.collection("userConnection").document(email);
        progressBar.setVisibility(View.VISIBLE);
        Query query = cf;
        cf.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().isEmpty()){
                                userConnectionEmptyText.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                friendList = new ArrayList<>();
                            }
                            else {
                                friendList= new ArrayList<>();
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    Friend friend = documentSnapshot.toObject(Friend.class);
                                    friendList.add(friend);
                                }
                                if(friendList.isEmpty()){
                                    userConnectionEmptyText.setVisibility(View.VISIBLE);

                                }else {
                                    userConnectionEmptyText.setVisibility(View.INVISIBLE);
                                }
                                progressBar.setVisibility(View.INVISIBLE);



                            }
                            Collections.sort(friendList);
                            adapter = new UserConnectionRecyclerViewAdapter(friendList,view);
                            adapter.notifyDataSetChanged();
                            userConnectionRecyclerView.setAdapter(adapter);
                        }
                    }
                });
            }
        });
    }

}