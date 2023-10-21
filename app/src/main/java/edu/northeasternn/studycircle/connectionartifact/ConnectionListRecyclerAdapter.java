package edu.northeasternn.studycircle.connectionartifact;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import edu.northeasternn.studycircle.R;
import edu.northeasternn.studycircle.model.Connection;
import edu.northeasternn.studycircle.model.User;
import edu.northeasternn.studycircle.util.ConnectionStatus;
import edu.northeasternn.studycircle.util.NewConnection;

public class ConnectionListRecyclerAdapter extends RecyclerView.Adapter<ConnectionListRecyclerAdapter.ConnectionListViewHolder>{

    private List<User> userList;

    private User currentUser;

    private View parentView;

    private static final String TAG = "ConnectionListRecyclerAdapter";
    public ConnectionListRecyclerAdapter(List<User>  userList,User currentUser,View parentView) {
        this.userList = userList;
        this.currentUser = currentUser;
        this.parentView = parentView;
        fetchCurrentUserInfo();
    }

    @Override
    public ConnectionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newconnectioncard, parent, false);
        return new ConnectionListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ConnectionListViewHolder holder, int position) {

        User user = userList.get(position);
        holder.newConnectionFullName.setText(user.getFullName());
        holder.newConnectionEmail.setText(user.getUserName());
        holder.userAvatar.setImageResource(R.drawable.account_foreground);
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        String connectionIdentifier = String.format("%s%s%s",currUser.getEmail().toUpperCase(),"-",user.getUserName().toUpperCase());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        holder.connectBtn.setTag(NewConnection.SEND);
        DocumentReference senderRef = db.collection("connections")
                .document(currUser.getEmail().toUpperCase()).collection("connectionSent")
                .document(connectionIdentifier);
        DocumentReference receiverRef = db.collection("connections").document(user.getUserName()).collection("connectionReceived").document(connectionIdentifier);



        CollectionReference senderConn = db.collection("connections").document(currUser.getEmail().toUpperCase()).collection("connectionSent");
        senderRef
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        if(snapshot!=null && snapshot.exists()){
                            Connection connection = snapshot.toObject(Connection.class);
                            if(connection!=null && !connection.getStatus().equals(ConnectionStatus.ACCEPTED)) {
                                holder.connectBtn.setTag(NewConnection.CANCEL);
                                holder.connectBtn.setText("Cancel Request");}
                            else {
                                holder.connectBtn.setTag(NewConnection.SEND);
                                holder.connectBtn.setText("Connect+");
                            }
                        }
                    }
                });


//        senderRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        Connection connection = documentSnapshot.toObject(Connection.class);
//                        if(connection!=null && connection.getStatus().equals(ConnectionStatus.PENDING)) {
//                            holder.connectBtn.setTag(NewConnection.CANCEL);
//                            holder.connectBtn.setText("Cancel Request");}
//                        else {
//                            holder.connectBtn.setTag(NewConnection.SEND);
//                            holder.connectBtn.setText("Connect+");
//                        }
//                    }
//                });
        holder.connectBtn.setOnClickListener(view -> {

            if(holder.connectBtn.getTag()!=null && holder.connectBtn.getTag().equals(NewConnection.SEND)){
                sendNewConnection(user,senderRef,receiverRef,holder);

            }else{
                cancelConnectionRequest(user,senderRef,receiverRef,holder);
            }



        });
    }

    private void cancelConnectionRequest(User user, DocumentReference senderRef,
                                         DocumentReference receiverRef,final ConnectionListViewHolder holder) {

        senderRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

        receiverRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        holder.connectBtn.setText("Connect+");
                        holder.connectBtn.setTag(NewConnection.SEND);
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }

    private void sendNewConnection(User user, DocumentReference senderRef, DocumentReference receiverRef,final ConnectionListViewHolder holder) {

        Connection connection = new Connection(
                currentUser.getFullName(),
                currentUser.getUserName(),
                user.getFullName(),
                user.getUserName(),
                String.valueOf(System.currentTimeMillis()),
                ConnectionStatus.PENDING
        );
        senderRef.set(connection).addOnSuccessListener(v->{
                    holder.connectBtn.setText("Cancel Request");
                    holder.connectBtn.setTag(NewConnection.CANCEL);
                    Snackbar.make(parentView, "Connection request sent", Snackbar.LENGTH_LONG).show();


                })
                .addOnFailureListener(v->{
                    Snackbar.make(parentView, "Failed to send connection request", Snackbar.LENGTH_LONG).show();
                });

        receiverRef.set(connection).addOnSuccessListener(v->{})
                .addOnFailureListener(v->{});
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void fetchCurrentUserInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toUpperCase();
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists() && documentSnapshot != null) {
                    currentUser = documentSnapshot.toObject(User.class);
                }else {
                    Log.d(TAG,"User not Found");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to connect to the database");
            }
        });
    }

    public class ConnectionListViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView newConnectionFullName;
        public final TextView newConnectionEmail;
        public final ImageView userAvatar;

        public  final MaterialButton connectBtn;

        public ConnectionListViewHolder(View view) {
            super(view);
            mView = view;

            newConnectionFullName = view.findViewById(R.id.newconnectionfullname);
            newConnectionEmail = view.findViewById(R.id.newconnectionemail);
            userAvatar = view.findViewById(R.id.newconnectionavatar);
            connectBtn = view.findViewById(R.id.connectionrequestbtn);

        }

        @Override
        public String toString() {
            return super.toString() + " '" +  newConnectionEmail.getText() + "'";
        }
    }
}