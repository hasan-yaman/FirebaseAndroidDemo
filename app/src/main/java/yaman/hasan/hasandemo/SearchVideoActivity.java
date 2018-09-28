package yaman.hasan.hasandemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchVideoActivity extends AppCompatActivity {

    private ListView listView;

    private String currentGroupID;
    private String groupName;
    private boolean isOwner;
    private String currentUserUid;
    private List<User> selectedUsers;

    List<Video> videos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_video);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            groupName = bundle.getString(Constans.GROUP_NAME);
            isOwner = bundle.getBoolean(Constans.ISOWNER);
            currentUserUid = bundle.getString(Constans.USERUID);

            selectedUsers = (List<User>)bundle.getSerializable(Constans.SELECTED_USERS);
        }

        listView = findViewById(R.id.listView);

        final EditText keywordEditText = findViewById(R.id.keywordEditText);

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword  = keywordEditText.getText().toString();

                try {
                    videos = new YoutubeSearchUtils(keyword).execute().get();

                    listView.setAdapter(new VideoAdapter(SearchVideoActivity.this, videos));

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String videoID = videos.get(position).getId();
                String videoTitle = videos.get(position).getTitle();

                DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_GROUP);
                DatabaseReference pushRef = groupReference.push();

                currentGroupID = pushRef.getKey();

                Group group = new Group(currentGroupID,groupName,currentUserUid,selectedUsers,true,
                        videoID,videoTitle);
                pushRef.setValue(group);

                // selectedUsersların hepsine groupid i ekle

                DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_USERS);

                for(User u : selectedUsers) {
                    usersReference.child(u.getUid()).child(Constans.GROUPID).setValue(currentGroupID);
                }

                goPlayVideoActivity(videoID, videoTitle);
            }
        });
    }

    public void goPlayVideoActivity(String videoID, String videoTitle) {
        Intent intent = new Intent(this, PlayVideoActivity.class);
        intent.putExtra(Constans.GROUPID,currentGroupID);
        intent.putExtra(Constans.ISOWNER, true);
        intent.putExtra(Constans.USERUID,currentUserUid);
        intent.putExtra(Constans.VIDEOID,videoID);
        intent.putExtra(Constans.VIDEOTITLE, videoTitle);
        startActivity(intent);
    }
}
