package edu.northeasternn.studycircle.userconnectionartifact;

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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import edu.northeasternn.studycircle.R;
import edu.northeasternn.studycircle.model.Friend;
import edu.northeasternn.studycircle.model.User;


public class UserConnectionRecyclerViewAdapter extends RecyclerView.Adapter<UserConnectionRecyclerViewAdapter.UserConnectionViewHolder>{

    private List<Friend> userList;


    private  static final String TAG = "UserConnectionRecyclerViewAdapter";

    private View parentView;

    private FirebaseUser currUser;

    private FirebaseFirestore db;

    public UserConnectionRecyclerViewAdapter(List<Friend>  userList,View parentView) {
        this.userList = userList;
        this.parentView = parentView;
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public UserConnectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.connectioncard, parent, false);
        return new UserConnectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserConnectionViewHolder holder, int position) {

        Friend user = userList.get(position);
        holder.friendFullName.setText(user.getFullName());
        holder.userAvatar.setImageResource(R.drawable.account_foreground);
        DocumentReference docRef = db.collection("userConnection").document(currUser.getEmail().toUpperCase()).collection("friends").document(user.getEmail().toUpperCase());

        DocumentReference docRef2 = db.collection("userConnection").document(user.getEmail().toUpperCase()).collection("friends").document(currUser.getEmail().toUpperCase());
        holder.unfriendbtn.setOnClickListener(v->{
            docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Friend deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
            docRef2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Friend deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
        });
//        holder.messagebtn.setOnClickListener(v->{
//
//        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserConnectionViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView friendFullName;
        public final ImageView userAvatar;

        public  final Button unfriendbtn;

        public UserConnectionViewHolder(View view) {
            super(view);
            mView = view;

            friendFullName = view.findViewById(R.id.friendusername);
            userAvatar = view.findViewById(R.id.friendavatar);
            unfriendbtn = view.findViewById(R.id.unfriendbtn);
            //messagebtn = view.findViewById(R.id.messagebtn);

        }

        @Override
        public String toString() {
            return super.toString() + " '" +  friendFullName.getText() + "'";
        }
    }
}
