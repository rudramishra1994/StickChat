package edu.northeasternn.studycircle.chatartifact;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.Query;

import java.util.List;

import edu.northeasternn.studycircle.R;
import edu.northeasternn.studycircle.model.Message;

public class MessageAdapter extends  RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    private final String TAG = "MessageAdapter";
    Context context;
    String userId;

    List<Message> messageList;
    private RequestOptions requestOptions = new RequestOptions();
    private final int MESSAGE_IN_VIEW_TYPE  = 1;
    private final int MESSAGE_OUT_VIEW_TYPE = 2;

    public MessageAdapter(List<Message> messageList,String userId, Context context) {
        Log.i(TAG, "MessageAdapter: created");
        this.context = context;
        this.userId = userId;
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {

        if (messageList.get(position).getMessageUserId().equals(userId))
            return MESSAGE_OUT_VIEW_TYPE;
            return MESSAGE_IN_VIEW_TYPE;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(viewType==MESSAGE_IN_VIEW_TYPE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.incoming_message, parent, false);
        }
        else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.outgoing_message, parent, false);
        }
        return new MessageHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Message model = messageList.get(position);
        holder.mUsername.setText(model.getMessageUser());
        holder.mText.setText(model.getMessageText());
        holder.mTime.setText(DateFormat.format("dd MMM  (h:mm a)", model.getMessageTime()));
    }
    @Override
    public int getItemCount() {
        return messageList.size();
    }


    public class MessageHolder extends RecyclerView.ViewHolder {
        TextView mText;
        TextView mUsername;
        TextView mTime;
        public MessageHolder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.message_text);
            mUsername = itemView.findViewById(R.id.message_user);
            mTime = itemView.findViewById(R.id.message_time);
        }
    }
}