package com.example.athletae;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public List<Post> post_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public PostAdapter(List<Post> post_list){

        this.post_list = post_list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String postId = post_list.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        final String[] currentUsername = new String[1];

        String desc_data = post_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String image_url = post_list.get(position).getImage_url();
        String thumbUri = post_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUri);

        final String user_id = post_list.get(position).getUser_id();
        //User Data will be retrieved here...
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.setUserData(userName, userImage);
                }
            }
        });

        firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    currentUsername[0] = task.getResult().getString("name");

                }
            }
        });

        try {
            long millisecond = post_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        firebaseFirestore.collection("Posts/" + postId + "/People").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if ( e == null ){
                    if (!queryDocumentSnapshots.isEmpty()){
                        int count = queryDocumentSnapshots.size();
                        holder.updatePeopleCount(count);
                    }else{
                        holder.updatePeopleCount(0);
                    }
                }
            }
        });

        // get People
        firebaseFirestore.collection("Posts/" + postId + "/People").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            if(e == null){
                if(documentSnapshot.exists()){
                    holder.postBeThereBtn.setImageDrawable(context.getDrawable(R.mipmap.action_be_there_accent));
                } else {
                    holder.postBeThereBtn.setImageDrawable(context.getDrawable(R.mipmap.action_be_there));
                }
            }
            }
        });
        //Be there feature
        holder.postBeThereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts/" + postId + "/People").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String, Object> peopleMap = new HashMap<>();
                            peopleMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + postId + "/People").document(currentUserId).set(peopleMap);
                        } else {
                            firebaseFirestore.collection("Posts/" + postId + "/People").document(currentUserId).delete();
                        }
                    }
                });

                firebaseFirestore.collection("Posts/" + postId + "/Notifications").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()) {
                            Map<String, Object> notifMap = new HashMap<>();

                            notifMap.put("timestamp", FieldValue.serverTimestamp());
                            notifMap.put("user_id", currentUserId);
                            notifMap.put("username", currentUsername[0] + " said he will be there.");

                            firebaseFirestore.collection("Posts/" + postId + "/Notifications").document(currentUserId).set(notifMap);
                        } else {
                            firebaseFirestore.collection("Posts/" + postId + "/Notifications").document(currentUserId).delete();
                        }
                    }
                });
            }
        });

        // delete feature
        holder.deletePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage("You want to delete this post?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseFirestore.collection("Posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot postTaken = task.getResult();
                                        assert postTaken != null;
                                        if (currentUserId.equals(postTaken.get("user_id"))) {
                                            if (task.getResult().exists()) {
                                                firebaseFirestore.collection("Posts").document(postId).delete();
                                                post_list.remove(position);
                                                notifyDataSetChanged();
                                            } else {
                                                Toast.makeText(context, "You are not allowed to do that!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(context, "You are not allowed to do that!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return post_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView;
        private ImageView postImageView;
        private TextView postDate;

        private TextView postUsername;
        private CircleImageView postUserImage;

        private ImageView postBeThereBtn;
        private ImageView deletePostBtn;

        private TextView postPeopleCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

             mView = itemView;

             postBeThereBtn = mView.findViewById(R.id.blog_be_there_btn);
             deletePostBtn = mView.findViewById(R.id.post_delete_btn);
        }

        public void setDescText(String descText){
            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);
        }

        public void setBlogImage(String downloadUri, String thumbUri){
            postImageView = mView.findViewById(R.id.blog_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.rectangle);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
                    ).into(postImageView);
        }

        public void setTime(String date) {
            postDate = mView.findViewById(R.id.blog_date);
            postDate.setText(date);
        }

        public void setUserData(String name, String image){
            postUserImage = mView.findViewById(R.id.blog_user_image);
            postUsername = mView.findViewById(R.id.blog_user_name);

            postUsername.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.ellipse);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(postUserImage);


        }

        public void updatePeopleCount(int count){
            postPeopleCount = mView.findViewById(R.id.blog_people_count);
            postPeopleCount.setText(count + " people are coming :D");
        }
    }
}
