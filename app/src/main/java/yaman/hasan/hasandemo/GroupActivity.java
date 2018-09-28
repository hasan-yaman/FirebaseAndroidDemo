package yaman.hasan.hasandemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GroupActivity extends AppCompatActivity {


    String currentUsername;
    String currentUserUid;

    String message;
    String groupID;

    String videoID;
    String videoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUsername = bundle.getString(Constans.USERNAME);
            currentUserUid = bundle.getString(Constans.USERUID);
        }


        Button createGroupButton = findViewById(R.id.createGroup);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GroupActivity.this, CreateGroupActivity.class);
                intent.putExtra(Constans.USERNAME, currentUsername);
                intent.putExtra(Constans.USERUID, currentUserUid);
                startActivity(intent);
            }
        });

        Button logoutButton = findViewById(R.id.userLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // useri onlineusers dan sil.

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference onlineUserReference = firebaseDatabase.getReference(Constans.FIREBASE_ONLINEUSERS);
                Log.i("Info", "currentUsername 2 -> " + currentUsername);
                onlineUserReference.child(currentUsername).setValue(null);

                FirebaseAuth.getInstance().signOut();

                goMainActivity();

            }
        });

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_USERS)
                .child(currentUserUid);
        userReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("Info", "onChildAdded");

                if (dataSnapshot.getKey().equals(Constans.GROUPID)) {
                    // Bu durumda kullanıcı bir gruba eklendi.
                    groupID = dataSnapshot.getValue().toString();


                    // Önce bu grubun sahibinin kullanıcı idsini bulalım.


                    DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_GROUP)
                            .child(groupID);

                    Query query = groupReference.orderByKey().equalTo("ownerUserID");

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String ownerUserID = "";

                            for (DataSnapshot children : dataSnapshot.getChildren()) {
                                ownerUserID = children.getValue().toString();
                            }

                            // Daha sonra bulduğumuz id den ownerin kullanıcı adını bulun.

                            DatabaseReference usernamesReference = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_USERNAMES);
                            Query usernamesQuery = usernamesReference.orderByValue().equalTo(ownerUserID);

                            usernamesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    String ownerUsername = "";

                                    for (DataSnapshot children : dataSnapshot.getChildren()) {
                                        ownerUsername = children.getKey();
                                    }

                                    message = ownerUsername + " isimli kullanıcı sizi ";

                                    // Daha sonra grubun ismini bulalım.

                                    Query groupNameQuery = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_GROUP)
                                            .child(groupID).orderByKey().equalTo("groupName");
                                    groupNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String groupName = "";

                                            //Log.i("Info",dataSnapshot.getKey());
                                            //Log.i("Info",dataSnapshot.getValue().toString());

                                            for (DataSnapshot children : dataSnapshot.getChildren()) {
                                                Log.i("Info","k " + children.getKey());
                                                Log.i("Info","v " + children.getValue());
                                                groupName = children.getValue().toString();
                                            }


                                            message += groupName + " grubuna ekledi.";

                                            Log.i("Info", "message -> " + message);

                                            buildAlertDialog();

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    Query videoQuery = groupReference.orderByKey().equalTo("videoID");
                    videoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            videoID = "";

                            for (DataSnapshot children : dataSnapshot.getChildren()) {
                                videoID = children.getValue().toString();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Query videoTitleQuery = groupReference.orderByKey().equalTo("videoTitle");
                    videoTitleQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot children : dataSnapshot.getChildren()) {
                                videoTitle = children.getValue().toString();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i("Info", "onChildChanged");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void buildAlertDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Hazır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Bu durumda kullanıcı hazır.

                        // Kullanıcıyı PlayVideoActivity e yönlendir ve orada kullanıcıyı hazır yap.

                        goPlayVideoActivity();


                    }
                })
                .setNegativeButton("Çık", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Bu durumda kullanıcıyı gruptan çıkar.

                        final DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_GROUP).
                                child(groupID).child(Constans.FIREBASE_USERS);
                        Query query = usersReference.orderByKey();

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for(DataSnapshot children : dataSnapshot.getChildren()) {

                                    String currentIndex = children.getKey();
                                    String uid = children.child("uid").getValue().toString();

                                    if(uid.equals(currentUserUid)) {
                                        // find current user
                                        usersReference.child(currentIndex).setValue(null);
                                    }
                                }

                                // Kullanıcıyı gruptan çıkınca aynı zamanda usersdanda kullanıcıda bulunan groupidsini sil

                                FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_USERS)
                                        .child(currentUserUid).child(Constans.GROUPID).setValue(null);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .show();
    }

    public void goPlayVideoActivity() {
        Intent intent = new Intent(this, PlayVideoActivity.class);
        intent.putExtra(Constans.GROUPID,groupID);
        intent.putExtra(Constans.ISOWNER, false);
        intent.putExtra(Constans.USERUID,currentUserUid);
        intent.putExtra(Constans.VIDEOID, videoID);
        intent.putExtra(Constans.VIDEOTITLE, videoTitle);
        startActivity(intent);
    }
}
