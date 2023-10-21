package edu.northeasternn.studycircle;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.northeasternn.studycircle.chatartifact.MessageAdapter;
import edu.northeasternn.studycircle.model.Message;
import edu.northeasternn.studycircle.model.User;


public class ChatFragment extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore database;
    Query query;
    private MessageAdapter adapter;
    private EditText input;


    private TextView emptyTextView;
    private String userId;

    private String title;
    private String userName;
    private String gId;


    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);
        ImageButton btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        input = findViewById(R.id.input);
        emptyTextView = findViewById(R.id.noChatHistoryText);
        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        auth = FirebaseAuth.getInstance();
        if(savedInstanceState!=null){
            this.gId = savedInstanceState.getString("gId");
            this.title = savedInstanceState.getString("title");
        }
        messageList = new ArrayList<>();

        user = auth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        this.gId = ""; // or other values
        if(b != null) {
            this.gId = b.getString("groupId");
            this.title = b.getString("title");
            this.setTitle(title);
        }

        System.out.println("Group Id: " + this.gId);

        if(user==null){
            startActivity(new Intent(this, login.class));
            finish();
            return;
        }

        System.out.println(user.getUid());
        userId = user.getEmail().toUpperCase();
        String n = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userName = n.toUpperCase();
        database = FirebaseFirestore.getInstance();

        CollectionReference productsRef = database.collection("messages");
        query = productsRef.whereEqualTo("groupId", gId);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    // Handle error
                    //...
                    return;
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                    List<Message> messageList = new ArrayList<>();
                    for(QueryDocumentSnapshot item : queryDocumentSnapshots){
                        messageList.add(item.toObject(Message.class));
                    }
                    if(messageList.size()>0){
                        emptyTextView.setVisibility(View.INVISIBLE);
                    }else{
                        emptyTextView.setVisibility(View.VISIBLE);
                    }
                    Collections.sort(messageList);
                    adapter = new MessageAdapter(messageList, userId, ChatFragment.this);
                    recyclerView.setAdapter(adapter);
                }
            }
        });


//        query = database.collection("messages").orderBy("messageTime");
//        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
//                    pgBar.setVisibility(View.GONE);
//                }
//            }
//        });
//        adapter = new MessageAdapter(query, userId, MainActivityChatFragment.this);
//        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnSend){
            String message = input.getText().toString();
            if(TextUtils.isEmpty(message)){
                return;
            }

            DocumentReference cf = database.collection("users").document(userName);
            cf.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    database.collection("messages").add(new Message(user.getFullName(),userName, message, gId,new Date().getTime()));
                }});


            input.setText("");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        savedInstanceState.putString("title",title);
        savedInstanceState.putString("gId", this.gId);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}

