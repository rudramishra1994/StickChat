package edu.northeasternn.studycircle;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.northeasternn.studycircle.connectionartifact.ConnectionListRecyclerAdapter;
import edu.northeasternn.studycircle.connectionrequest.ConnectionRequestRecyclerAdapter;
import edu.northeasternn.studycircle.model.Connection;
import edu.northeasternn.studycircle.model.User;
import edu.northeasternn.studycircle.util.ConnectionStatus;
import edu.northeasternn.studycircle.util.NewConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PendingConnectionRequest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingConnectionRequest extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private ProgressBar progressBar;

    private  static final String TAG = "PendingConnectionRequest";

    private List<Connection> requests;

    private RecyclerView pendingReqRecyclerView;

    private ConnectionRequestRecyclerAdapter adapter;

    private FirebaseFirestore db;

    private TextView emptyRecylerViewText;

    public PendingConnectionRequest() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PendingConnectionRequest.
     */
    // TODO: Rename and change types and number of parameters
    public static PendingConnectionRequest newInstance(String param1, String param2) {
        PendingConnectionRequest fragment = new PendingConnectionRequest();
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
        view = inflater.inflate(R.layout.fragment_pending_connection_request, container, false);
        db = FirebaseFirestore.getInstance();
        requests = new ArrayList<>();
        pendingReqRecyclerView = view.findViewById(R.id.pendingrequestview);
        emptyRecylerViewText = view.findViewById(R.id.pendingrequestblanktext);
        pendingReqRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(pendingReqRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        pendingReqRecyclerView.addItemDecoration(dividerItemDecoration);
        progressBar = view.findViewById(R.id.pendingreqprogessbar);
        fetchPendingConnectionRequest();
        return view;
    }

    private void fetchPendingConnectionRequest() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toUpperCase();
        CollectionReference receiverRef = db.collection("connections").document(email).collection("connectionReceived");

        Query query = receiverRef;
        receiverRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().isEmpty()){
                                emptyRecylerViewText.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                requests = new ArrayList<>();
                            }
                            else {
                                requests = new ArrayList<>();
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    Connection user = documentSnapshot.toObject(Connection.class);
                                    requests.add(user);
                                }
                                if(!requests.isEmpty()){
                                    emptyRecylerViewText.setVisibility(View.INVISIBLE);

                                }else {
                                    emptyRecylerViewText.setVisibility(View.INVISIBLE);
                                }
                                progressBar.setVisibility(View.GONE);


                            }
                            Collections.sort(requests);

                            adapter = new ConnectionRequestRecyclerAdapter(requests,view);
                            adapter.notifyDataSetChanged();
                            pendingReqRecyclerView.setAdapter(adapter);
                        }
                    }
                });
            }
        });

    }
}