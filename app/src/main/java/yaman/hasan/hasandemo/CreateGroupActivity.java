package yaman.hasan.hasandemo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    List<User> onlineUsers;
    List<User> selectedUsers;

    ListView listView;

    String currentUsername;
    String currentUserUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        onlineUsers = new ArrayList<>();
        selectedUsers = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            currentUsername = bundle.getString(Constans.USERNAME);
            currentUserUid = bundle.getString(Constans.USERUID);
        }


        listView = findViewById(R.id.listView);

        final EditText groupNameEditText = findViewById(R.id.groupname);

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference onlineuserReference = firebaseDatabase.getReference(Constans.FIREBASE_ONLINEUSERS);
        Query usernamesQuery = onlineuserReference.orderByKey();

        usernamesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot children : dataSnapshot.getChildren()) {
                    String username = children.getKey();
                    String userUid = children.getValue().toString();


                    // TODO Burada ayrıca kullanıcının bir grupta olup olmaması kontol edilmedilir.

                    if(!currentUserUid.equals(userUid) && !userInGroup(userUid)) {
                        onlineUsers.add(new User(userUid, username,false));
                        // onlineUsers listesine current useri ekleme
                        // Böylelikle kullanıcıya aktif kullanıcı listesinde kendisini göstermeyeceğiz.
                    }
                }

                listView.setAdapter(new OnlineUserAdapter(CreateGroupActivity.this,onlineUsers));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        Button createButton = findViewById(R.id.create);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameEditText.getText().toString();
                if(groupName.trim().equals("")) {
                    Toast.makeText(CreateGroupActivity.this, "Lütfen grup ismi giriniz.",Toast.LENGTH_LONG).show();
                } else {

                    selectedUsers.add(new User(currentUserUid,currentUsername,true)); // ownerida groupdaki kullanıcılar listesine ekle

                    goSearchVideoActivity(groupName);

                }
            }
        });

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                User clickedUser = onlineUsers.get(position);
                RelativeLayout rowBackground = view.findViewById(R.id.rowBackground);

                if(selectedUsers.contains(clickedUser)) {
                    // bu durumda çıkar
                    rowBackground.setBackgroundColor(Color.WHITE);
                    selectedUsers.remove(clickedUser);
                } else {
                    // bu durumda ekle
                    rowBackground.setBackgroundColor(Color.GREEN);
                    selectedUsers.add(clickedUser);
                }
            }
        });
    }

    public void goSearchVideoActivity(String groupName) {
        Intent intent = new Intent(this, SearchVideoActivity.class);
        intent.putExtra(Constans.GROUP_NAME, groupName);
        intent.putExtra(Constans.ISOWNER, true);
        intent.putExtra(Constans.USERUID,currentUserUid);
        intent.putExtra(Constans.SELECTED_USERS,(Serializable) selectedUsers);
        startActivity(intent);
    }
    // Return true if user has a group
    // return false otherwise

    private boolean hasGroup = false;

    public boolean userInGroup(String userUid) {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_USERS)
                .child(userUid).child(Constans.GROUPID);
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                   hasGroup = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return hasGroup;
    }
}
