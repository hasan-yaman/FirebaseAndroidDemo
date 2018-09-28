package yaman.hasan.hasandemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsernameActivity extends AppCompatActivity {

    String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        final EditText usernameEditText = findViewById(R.id.username);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userUid = currentUser.getUid();

        Button goButton = findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                DatabaseReference userReference = firebaseDatabase.getReference(Constans.FIREBASE_USERS).child(userUid);
                userReference.child("username").setValue(username);

                DatabaseReference usernameReference = firebaseDatabase.getReference(Constans.FIREBASE_USERNAMES);
                usernameReference.child(username).setValue(userUid);

                // Kullanıcı usernameini belirledikten sonra onlineusers a kaydet.

                DatabaseReference onlineUserReference = firebaseDatabase.getReference(Constans.FIREBASE_ONLINEUSERS);
                onlineUserReference.child(username).setValue(userUid);

                goGroupActivity(username);
            }
        });

    }

    public void goGroupActivity(String username) {
        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra(Constans.USERNAME, username);
        intent.putExtra(Constans.USERUID, userUid);
        startActivity(intent);
    }

}

