package yaman.hasan.hasandemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    /*
        Test User
        hasannyaman@gmail.com
        123456

     */

    private String currentUsername;
    private String currentUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //FirebaseMessaging.getInstance().subscribeToTopic("msgNotification");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            currentUserUid = currentUser.getUid();
            findUsernameAndSaveUserGoLogin();
            //saveOnlineUser();
           // successfullLogin();
        }

        final EditText userEmailEditText = findViewById(R.id.userMail);
        final EditText userPasswordEditText = findViewById(R.id.userPassword);

        Button signInButton = findViewById(R.id.signIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userEmail =  userEmailEditText.getText().toString();
                String userPassword = userPasswordEditText.getText().toString();
                mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.i("Info","Kullanıcı başarıyla yaratıldı!");

                            // Yaratılan kullanıcı
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            currentUserUid = currentUser.getUid();

                            // Yaratılan kullanıcı aynı zamanda real database e yaz.
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference userReference = firebaseDatabase.getReference(Constans.FIREBASE_USERS).child(currentUserUid);
                            userReference.child("email").setValue(userEmail);

                            writeUsername(); // Kullanıcı usernameinin belirledikten sonra online user olarak kaydet.
                        } else {
                            Toast.makeText(MainActivity.this,"HATA",Toast.LENGTH_LONG).show();
                            Log.i("Info","HATA! "  + task.getException());
                        }
                    }
                });
            }
        });

        Button logInButton = findViewById(R.id.logIn);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String userEmail =  userEmailEditText.getText().toString();
                String userPassword = userPasswordEditText.getText().toString();
                mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.i("Info","Kullanıcı başarı ile giriş yaptı!");

                            // Giriş yapan kullanıcı

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            currentUserUid = currentUser.getUid();

                            findUsernameAndSaveUserGoLogin();

                            //saveOnlineUser();

                            //successfullLogin();
                        } else {
                            Toast.makeText(MainActivity.this,"HATA",Toast.LENGTH_LONG).show();
                            Log.i("Info","HATA! " + task.getException());
                        }
                    }
                });
            }
        });

    }

    public void successfullLogin() {
        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra(Constans.USERNAME,currentUsername);
        intent.putExtra(Constans.USERUID,currentUserUid);
        startActivity(intent);
    }
    public void writeUsername() {
        Intent intent = new Intent(this, UsernameActivity.class);
        startActivity(intent);
    }

    // Usernamesden currentuserın usernameni bulur.

    public void findUsernameAndSaveUserGoLogin() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference usernamesReference = firebaseDatabase.getReference(Constans.FIREBASE_USERNAMES);
        Log.i("Info","currentUserUid  -> " + currentUserUid);
        Query usernamesQuery = usernamesReference.orderByValue().equalTo(currentUserUid);

        usernamesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot children : dataSnapshot.getChildren()) {
                    currentUsername = children.getKey();
                    Log.i("Info","currentUsername method " + currentUsername);

                    saveOnlineUser(); // Make sure we have currentUsername then save

                    successfullLogin();
                    // TODO burayı interface veya listener ile hallet!?

                    // TODO Methodları forun dışına çıkar.
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Info","DatabaseError " + databaseError.getDetails());
            }
        });
    }


    // Onlineusersa username - useruid şeklinde kaydeder.
    public void saveOnlineUser() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference onlineUserReference = firebaseDatabase.getReference(Constans.FIREBASE_ONLINEUSERS);
        onlineUserReference.child(currentUsername).setValue(currentUserUid);
    }


}
