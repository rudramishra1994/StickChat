package edu.northeasternn.studycircle;

import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.northeasternn.studycircle.connectionartifact.ConnectionListRecyclerAdapter;
import edu.northeasternn.studycircle.model.Friend;
import edu.northeasternn.studycircle.model.User;
import edu.northeasternn.studycircle.userconnectionartifact.UserConnectionRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private RecyclerView connectionListRecyclerView;

    private ConnectionListRecyclerAdapter adapter;

    private ProgressBar progressBar;

    private List<User> userList;

    private TextView recyclerviewEmptyText;
    private FirebaseFirestore db ;

    private List<String> friendList;

    private User currentUser;

    public AllUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllUserFragment newInstance(String param1, String param2) {
        AllUserFragment fragment = new AllUserFragment();
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
        view = inflater.inflate(R.layout.fragment_all_user, container, false);
        userList = new CopyOnWriteArrayList<>();
        friendList = new CopyOnWriteArrayList<>();
        recyclerviewEmptyText = view.findViewById(R.id.alluserrecyclerviewemptytext);
        connectionListRecyclerView = view.findViewById(R.id.alluserrecyclerview);
        connectionListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(connectionListRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        progressBar = view.findViewById(R.id.alluserreqprogessbar);
        progressBar.setVisibility(View.VISIBLE);
        connectionListRecyclerView.addItemDecoration(dividerItemDecoration);
        setupRecyclerViewDataListner();
        return view;
    }

    private void setupRecyclerViewDataListner() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toUpperCase();
        db = FirebaseFirestore.getInstance();
        friendList.add(email);
        CollectionReference df = db
                .collection("users");
        Query q2 = df;

        CollectionReference cf = db.collection("userConnection").document(email).collection("friends");
        Query query2 = cf;

        cf.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                query2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().isEmpty()){

                            }
                            else {
                                friendList= new CopyOnWriteArrayList<>();
                                friendList.add(email);
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    Friend friend = documentSnapshot.toObject(Friend.class);
                                    friendList.add(friend.getEmail().toUpperCase());
                                }
                            }

                            df.whereNotIn("userName",friendList).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        if(task.getResult().isEmpty()){
                                            progressBar.setVisibility(View.INVISIBLE);
                                            recyclerviewEmptyText.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            userList = new CopyOnWriteArrayList<>();
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                User user = documentSnapshot.toObject(User.class);
                                                userList.add(user);
                                            }
                                            if(userList.isEmpty()){
                                                recyclerviewEmptyText.setVisibility(View.VISIBLE);
                                            }else {
                                                recyclerviewEmptyText.setVisibility(View.INVISIBLE);
                                            }
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Collections.sort(userList);
                                            adapter = new ConnectionListRecyclerAdapter(userList,currentUser,view);
                                            adapter.notifyDataSetChanged();
                                            connectionListRecyclerView.setAdapter(adapter);

                                        }
                                    }
                                }
                            });

                        }
                    }
                });
            }
        });
        df.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                q2.whereNotIn("userName",friendList).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().isEmpty()){
                            progressBar.setVisibility(View.INVISIBLE);
                            recyclerviewEmptyText.setVisibility(View.VISIBLE);
                        }
                        else {
                            userList = new CopyOnWriteArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                User user = documentSnapshot.toObject(User.class);
                                userList.add(user);
                            }
                            query2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        if(task.getResult().isEmpty()){

                                        }
                                        else {
                                            friendList= new CopyOnWriteArrayList<>();
                                            friendList.add(email);
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                Friend friend = documentSnapshot.toObject(Friend.class);
                                                friendList.add(friend.getEmail().toUpperCase());
                                            }
                                        }

                                    }

                                    if(userList.isEmpty()){
                                        recyclerviewEmptyText.setVisibility(View.VISIBLE);
                                    }else {
                                        recyclerviewEmptyText.setVisibility(View.INVISIBLE);
                                    }
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Collections.sort(userList);
                                    removeFriendsFromUserList();
                                    adapter = new ConnectionListRecyclerAdapter(userList,currentUser,view);
                                    adapter.notifyDataSetChanged();
                                    connectionListRecyclerView.setAdapter(adapter);
                                }
                            });



                        }

                    }
                });
            }
        });



//        df.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
//                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            if(task.getResult().isEmpty()){
//                                progressBar.setVisibility(View.INVISIBLE);
//                                recyclerviewEmptyText.setVisibility(View.VISIBLE);
//                            }
//                            else {
//                                userList = new ArrayList<>();
//                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                                    User user = documentSnapshot.toObject(User.class);
//                                    userList.add(user);
//                                }
//                                if(userList.isEmpty()){
//                                    recyclerviewEmptyText.setVisibility(View.VISIBLE);
//                                }else {
//                                    recyclerviewEmptyText.setVisibility(View.INVISIBLE);
//                                }
//                                progressBar.setVisibility(View.INVISIBLE);
//                                Collections.sort(userList);
//                                adapter = new ConnectionListRecyclerAdapter(userList,currentUser,view);
//                                adapter.notifyDataSetChanged();
//                                connectionListRecyclerView.setAdapter(adapter);
//
//                            }
//                        }
//                    }
//                });
//            }
//        });
    }

    private void removeFriendsFromUserList() {
        for(String s: friendList){
            for(User user : userList){
                if(s.equalsIgnoreCase(user.getUserName())){
                    userList.remove(user);
                }
            }
        }
    }
}