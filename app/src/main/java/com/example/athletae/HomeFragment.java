package com.example.athletae;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView blog_list_view;
    private List<Post> blog_list;
    String currentUserCity;

    private FirebaseFirestore firebaseFirestore;
    private PostAdapter postAdapter;

    private DocumentSnapshot lastVisible;

    private Boolean isFirstPageFirstLoaded = true;

    FirebaseAuth firebaseAuth;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        isFirstPageFirstLoaded = true;
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        blog_list = new ArrayList<>();
        blog_list_view = view.findViewById(R.id.blog_list_view);

        firebaseAuth = FirebaseAuth.getInstance();

        postAdapter = new PostAdapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blog_list_view.setAdapter(postAdapter);

        if(firebaseAuth.getCurrentUser()  != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                    if(reachedBottom){
                        loadMorePost();
                    }
                }
            });





            if ( currentUserCity == null ){
                firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot postTaken = task.getResult();
                        assert postTaken != null;
                        currentUserCity = (String) postTaken.get("city");
                        Query firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("city", currentUserCity).orderBy("timestamp", Query.Direction.DESCENDING).limit(4);

                        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                if (e == null) {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        if (isFirstPageFirstLoaded) {
                                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                        }
                                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                                String blogPostId = doc.getDocument().getId();

                                                Post post = doc.getDocument().toObject(Post.class).withId(blogPostId);

                                                if (isFirstPageFirstLoaded) {
                                                    blog_list.add(post);
                                                } else {
                                                    blog_list.add(0, post);
                                                }

                                                postAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        isFirstPageFirstLoaded = false;

                                    }
                                }
                            }
                        });
                    }
                    }
                });

            } else {
                Query firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("city", currentUserCity).orderBy("timestamp", Query.Direction.DESCENDING).limit(4);

                firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e == null) {
                            if (!queryDocumentSnapshots.isEmpty()) {


                                if(isFirstPageFirstLoaded) {
                                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                }
                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        String blogPostId = doc.getDocument().getId();

                                        Post post = doc.getDocument().toObject(Post.class).withId(blogPostId);

                                        if(isFirstPageFirstLoaded){
                                            blog_list.add(post);
                                        }else{
                                            blog_list.add(0, post);
                                        }

                                        postAdapter.notifyDataSetChanged();

                                    }
                                }

                                isFirstPageFirstLoaded = false;

                            }
                        }
                    }
                });
            }
        }
        // Inflate the layout for this fragment
        return view;
    }

    public void loadMorePost() {
        if(firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("Posts")
                    .whereEqualTo("city", currentUserCity)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(4);

            nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e == null) {
                        if (queryDocumentSnapshots != null) {
                            if (queryDocumentSnapshots.size() > 0) {
                                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        String blogPostId = doc.getDocument().getId();
                                        Post post = doc.getDocument().toObject(Post.class).withId(blogPostId);
                                        blog_list.add(post);

                                        postAdapter.notifyDataSetChanged();

                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }


}
