package com.example.athletae;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    private RecyclerView notif_list_view;
    private List<Notification> notif_list;
    private List<String> blog_id_list;

    private NotificationAdapter notificationAdapter;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    public NotificationFragment(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        blog_id_list = new ArrayList<>();
        notif_list = new ArrayList<>();
        notif_list_view = view.findViewById(R.id.notif_list_view);

        notificationAdapter = new NotificationAdapter(notif_list);
        notif_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        notif_list_view.setAdapter(notificationAdapter);

        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        final String[] blogPostId = new String[1];
        firebaseFirestore = FirebaseFirestore.getInstance();
        Date date = new Date();
        //Notification ex = new Notification("Andrei", "Andrei o sa vina la evenimentul tau!", date);
        //Notification ex2 = new Notification("Qwqery", "Alexandrul o sa vina la evenimentul tau!", date);
        //Notification ex3 = new Notification("asdfg", "Georgian o sa vina la evenimentul tau!", date);

        //notif_list.add(ex);
        //notif_list.add(ex2);
        //notif_list.add(ex3);
        //retrieve data from firestore

        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if ( e == null ){
                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                        if(doc.getType() == DocumentChange.Type.ADDED){

                            Post blogpost = doc.getDocument().toObject(Post.class);
                            if(currentUserId.equals(blogpost.getUser_id())) {
                                blog_id_list.add(doc.getDocument().getId());
                            }
                        }

                    }

                    for (int i=0; i<blog_id_list.size(); i++){
                        firebaseFirestore.collection("Posts/" + blog_id_list.get(i) + "/Notifications").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if ( e == null ){
                                    if (!queryDocumentSnapshots.isEmpty()){
                                        for (QueryDocumentSnapshot not : queryDocumentSnapshots){
                                            Notification notification = not.toObject(Notification.class);
                                            notif_list.add(notification);
                                            notificationAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        });


                    }
                }
            }
        });


        return view;
    }
}

 /*
    private void readNotifications(){

        if(firebaseAuth.getCurrentUser()  != null) {
            // .orderBy("timestamp", Query.Direction.DESCENDING);
            CollectionReference postsCollectionRef = firebaseFirestore.collection("Posts");

            Query query = postsCollectionRef.whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid()).orderBy("timestamp", Query.Direction.DESCENDING);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document: task.getResult()){
                            BlogPost post = document.toObject(BlogPost.class);
                            blogList.add(post);
                        }

                        for (BlogPost blogPost : blogList){

                            firebaseFirestore.collection("Posts/" + blogPost.user_id + "/Notifications").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    if ( e == null ){
                                        if (!queryDocumentSnapshots.isEmpty()){

                                            for (QueryDocumentSnapshot not : queryDocumentSnapshots){
                                                Notification notification = not.toObject(Notification.class);
                                                notificationList.add(notification);

                                                notificationAdapter.notifyDataSetChanged();
                                            }

                                        }else{
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }*/
