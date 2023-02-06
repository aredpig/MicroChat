package com.example.mealshared;

import com.example.mealshared.Models.Post;
import com.example.mealshared.Models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * This is a helper class that make use of firestore to manage data
 * @author
 * Isaac Zhang
 */
public class DatabaseHelper {

    private static DatabaseHelper instance = null;
    private final FirebaseFirestore db;
    private static final String TAG = "DataBaseHelper";
    public static final String COLLECTION_USERS_PATH = "Users";
    public static final String COLLECTION_POSTS_PATH = "Posts";
    private boolean isEmpty;

    /**
     * This is the constructor method that fulfills singleton class design
     */
    private DatabaseHelper() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
        db.collection("AVL");
        db.collection("Users");
        db.collection("Posts");
    }

    /**
     * This get the instance of DatabaseHelper class
     * @return
     *  Return DatabaseHelper instance
     */
    public static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    // Add methods
    /**
     * This stores User class to firestore database
     * @return
     *  Return DocumentReference instance of that User
     */
    public DocumentReference AddUser(User user) {
        DocumentReference userReference = db.collection(COLLECTION_USERS_PATH).document(user.getEmail());
        userReference.set(user);
        return userReference;
    }

    /**
     * This stores Post class to firestore database
     * @return
     *  Return DocumentReference instance of that Post
     */
    public DocumentReference AddPost(Post post) {
        DocumentReference userReference = db.collection("Posts").document(String.valueOf(post.getTimeline()));
        userReference.set(post);
        return userReference;
    }
    /**
     * This stores AVL class to firestore database
     * @return
     *  Return DocumentReference instance of that AVL
     */

    public DocumentReference AddAVL(AVLTree tree){
        DocumentReference userReference = db.collection("AVL").document("AVLtree");
//        System.out.println(tree);
        userReference.set(tree);
        return userReference;
    }
    //Add methods over

    //Get methods
    /**
     * This method get the User class by its Email
     * @return
     *  Return a User object
     */
    public void getUser(String Email, final Consumer<User> consumer) {
        DocumentReference docRef = db.collection(COLLECTION_USERS_PATH).document(Email);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            User tempUser = documentSnapshot.toObject(User.class);
            consumer.accept(tempUser);
        });
    }

    /**
     * This method get the User class by its Email
     * @return
     *  Return all User objects by a list
     */
    public void getUsers(final Consumer<List<User>> consumer) {
        db.collection(COLLECTION_USERS_PATH)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<User> users = new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                                users.add(document.toObject(User.class));
                            }
                            consumer.accept(users);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * This method get the User class by its Email
     * @return
     *  Return all Post objects by a list
     */
    public void getPosts(final Consumer<List<Post>> consumer) {
        db.collection("Posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Post> posts = new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                                posts.add(document.toObject(Post.class));
                            }
                            consumer.accept(posts);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    /**
     * This method get the AVL class
     * @return
     *  Return a AVL class object
     */
    public void getTree(final Consumer<AVLTree> consumer){
        DocumentReference docRef = db.collection("AVL").document("AVLtree");
        docRef.get().addOnSuccessListener(documentSnapshot -> {
//            System.out.println(documentSnapshot.getData().size());
//            System.out.println(documentSnapshot.getData());
            AVLTree tempTree = documentSnapshot.toObject(AVLTree.class);
            consumer.accept(tempTree);
        });
    }
    //Get methods over

    //Update methods
    /**
     * This method update AVL tree in database
     */
    public DocumentReference UpdateTree(AVLTree tree){
        DeleteTree();
        return AddAVL(tree);
    }

    /**
     * This method update one node of AVL tree in database
     */
    public void UpdateTreeNode(User user){
        DocumentReference docRef = db.collection("AVL").document("AVLtree");

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            AVLTree tempTree = documentSnapshot.toObject(AVLTree.class);
            tempTree.findNode(user.getUid()).user = user;
            db.collection("AVL").document("AVLtree")
                    .set(tempTree).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        });
    }

    /**
     * This update Users class to firestore database
     */
    public void UpdateUsersData(User user) {
        String collection = COLLECTION_USERS_PATH;
        db.collection(collection).document(user.getEmail())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    /**
     * This update Post class to firestore database
     */
    public void UpdatePostData(Post post) {
        String collection = COLLECTION_POSTS_PATH;
        db.collection(collection).document(post.getTimeline())
                .set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    //Update methods over


    /**
     * This delete class that is stored in firestore database
     */
    public void DeleteTree() {
        db.collection("AVL").document("AVLtree").delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Tree successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }



    /**
     * This method checks if a user exist in database
     * @return
     *  Return boolean
     */
    public void userExist(String Email, final Consumer<Boolean> callback) {
        String collection = COLLECTION_USERS_PATH;
        DocumentReference docIdRef = db.collection(collection).document(Email);
        docIdRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    Log.d(TAG, "Document exists!");
                    isEmpty = true;
                } else {
                    Log.d(TAG, "Document does not exist!");
                    isEmpty = false;
                }
            } else {
                Log.d(TAG, "Failed with: ", task.getException());
            }
            callback.accept(isEmpty);
        });
    }

}
