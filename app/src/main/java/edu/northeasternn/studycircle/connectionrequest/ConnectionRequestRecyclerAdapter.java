package edu.northeasternn.studycircle.connectionrequest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import edu.northeasternn.studycircle.R;
import edu.northeasternn.studycircle.model.Connection;
import edu.northeasternn.studycircle.model.Friend;
import edu.northeasternn.studycircle.model.User;
import edu.northeasternn.studycircle.util.ConnectionStatus;
import edu.northeasternn.studycircle.util.NewConnection;

public class ConnectionRequestRecyclerAdapter extends RecyclerView.Adapter<ConnectionRequestRecyclerAdapter.ConnectioRequestViewHolder>{
    private List<Connection> requests;

    private User currentUser;

    private View parentView;

    private FirebaseFirestore db;

    private FirebaseUser currUser;

    private static final String TAG = "ConnectionListRecyclerAdapter";
    public ConnectionRequestRecyclerAdapter(List<Connection>  requests,View parentView) {
        this.requests = requests;
        this.parentView = parentView;
        db = FirebaseFirestore.getInstance();
        fetchCurrentUserInfo();
    }

    @Override
    public ConnectioRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.connection_request, parent, false);
        return new ConnectioRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ConnectioRequestViewHolder holder, int position) {

        Connection connection = new Connection(requests.get(position));
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        holder.newRequestFullName.setText(connection.getSender_FN());
        holder.newRequestEmail.setText(connection.getSender_email());
        holder.userAvatar.setImageResource(R.drawable.account_foreground);
        Friend forSender = new Friend(connection.getReceiver_FN(),connection.getReceiver_email().toUpperCase());
        Friend forReceiver= new Friend(connection.getSender_FN(),connection.getSender_email().toUpperCase());
        DocumentReference senderRef = db.collection("userConnection").document(connection.getSender_email().toUpperCase()).collection("friends").document(forSender.getEmail());
        DocumentReference receiverRef = db.collection("userConnection").document(connection.getReceiver_email().toUpperCase()).collection("friends").document(forReceiver.getEmail());

        holder.acceptBtn.setOnClickListener(view -> {
            receiverRef.set(forReceiver)
                    .addOnSuccessListener(v->{
                        Snackbar.make(parentView, "Network Updated", Snackbar.LENGTH_LONG).show();})
                    .addOnFailureListener(v->{
                        Snackbar.make(parentView, "Failed to Accept request.Network Not Updated", Snackbar.LENGTH_LONG).show();
                    });
            senderRef.set(forSender).addOnSuccessListener(v->{})
                    .addOnFailureListener(v->{
                        Snackbar.make(parentView, "Failed to Update Sender", Snackbar.LENGTH_LONG).show();
                    });
            deleleConnectionForSenderReceiver(connection);

        });

        holder.rejectBtn.setOnClickListener(view->{
            deleleConnectionForSenderReceiver(connection);
        });
    }

    private void deleleConnectionForSenderReceiver(Connection connection) {
        String docid = String.format("%s%s%s",connection.getSender_email().toUpperCase(),"-",connection.getReceiver_email().toUpperCase());
        DocumentReference receiverRef = db.collection("connections").document(currUser.getEmail().toUpperCase()).collection("connectionReceived").document(docid);
        DocumentReference senderRef =  db.collection("connections").document(connection.getSender_email().toUpperCase()).collection("connectionSent").document(docid);

        senderRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Sender Connection DocumentSnapshot successfully deleted!");
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
                        Log.d(TAG, "Receiver Connection DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }


    @Override
    public int getItemCount() {
        return requests.size();
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

    public class ConnectioRequestViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView newRequestFullName;
        public final TextView newRequestEmail;
        public final ImageView userAvatar;

        public  final Button acceptBtn,rejectBtn;

        public ConnectioRequestViewHolder(View view) {
            super(view);
            mView = view;

            newRequestFullName = view.findViewById(R.id.userconnectionname);
            newRequestEmail = view.findViewById(R.id.userconnectionemail);
            userAvatar = view.findViewById(R.id.connectionrequestavatar);
            acceptBtn = view.findViewById(R.id.requestacceptbtn);
            rejectBtn = view.findViewById(R.id.requestrejectbtn);

        }

        @Override
        public String toString() {
            return super.toString() + " '" +  newRequestEmail.getText() + "'";
        }
    }
}