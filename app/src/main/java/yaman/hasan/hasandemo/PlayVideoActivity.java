package yaman.hasan.hasandemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.ui.PlayerUIController;


public class PlayVideoActivity extends AppCompatActivity{

    YouTubePlayerView youtubePlayerView;
    YouTubePlayer mYouTubePlayer;
    TextView readyTextView;

    String groupID;
    String currentUserUid;
    boolean isOwner;
    String videoID;
    String videoTitle;

    boolean allUsersReady = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            groupID = bundle.getString(Constans.GROUPID);
            isOwner = bundle.getBoolean(Constans.ISOWNER);
            currentUserUid = bundle.getString(Constans.USERUID);
            videoID = bundle.getString(Constans.VIDEOID);
            videoTitle = bundle.getString(Constans.VIDEOTITLE);
        }

        TextView videoTitleTextView = findViewById(R.id.videoTitle);
        videoTitleTextView.setText(videoTitle);

        readyTextView = findViewById(R.id.readyTextView);

        youtubePlayerView = findViewById(R.id.youtubePlayerView);

        final DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_GROUP).
                child(groupID).child(Constans.FIREBASE_USERS);

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int falseCounter = 0;

                for (DataSnapshot children : dataSnapshot.getChildren()) {

                    Boolean ready = (Boolean) children.child("ready").getValue();
                    if (!ready) {
                        falseCounter++;
                    }
                }

                if (falseCounter == 0) {
                    allUsersReady = true;
                    readyTextView.setVisibility(View.GONE);
                    playYoutubeVideo();
                } else {
                    allUsersReady = false;
                    readyTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (!isOwner) {
            // Kullanıcı grubun sahibi değilse GroupActivityden gelmiştir
            // Kullanıcıyı hazır yap.


            Query query = usersReference.orderByKey();

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot children : dataSnapshot.getChildren()) {

                        String currentIndex = children.getKey();
                        String uid = children.child("uid").getValue().toString();

                        // readyi true yap.

                        if (uid.equals(currentUserUid)) {
                            // find current user
                            usersReference.child(currentIndex).child("ready").setValue(true);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        }

        // Kullanıcı grubun sahibi değilse gruptaki status değişimlerini dinlemeli.
        DatabaseReference groupsReference = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_GROUP)
                .child(groupID);
        groupsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.getKey().equals(Constans.STATUS)) {
                    // TODO add null control to make sure
                    if(dataSnapshot.getValue().toString().equals(Constans.PLAYING)) {
                        // Bu durumda videoyu oynat.
                        if(mYouTubePlayer != null) {
                            mYouTubePlayer.play();
                        }
                    } else if(dataSnapshot.getValue().toString().equals(Constans.PAUSED)){
                        // Bu durumda videoyu durdur.
                        if(mYouTubePlayer != null) {
                            mYouTubePlayer.pause();
                        }
                    }
                }

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

    public void playYoutubeVideo() {

        Log.i("Info", "playYoutubeVideo");



        youtubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(@NonNull YouTubePlayer youTubePlayer) {

                mYouTubePlayer = youTubePlayer;

                mYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        super.onReady();
                        if (allUsersReady) {
                            Log.i("Info", "playYoutubeVideo-start");
                            mYouTubePlayer.loadVideo(videoID, 0);
                        }
                    }

                    @Override
                    public void onStateChange(@NonNull PlayerConstants.PlayerState state) {
                        super.onStateChange(state);

                        if (state == PlayerConstants.PlayerState.PLAYING) {
                            Log.i("Info", "Playing");
                            if (isOwner) {
                                // Kullanıcı grubun sahibiyse grubun statusunu değiştir.
                                DatabaseReference groupsReference = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_GROUP)
                                        .child(groupID);
                                groupsReference.child(Constans.STATUS).setValue(Constans.PLAYING);
                            }
                        } else if (state == PlayerConstants.PlayerState.PAUSED) {
                            Log.i("Info", "Paused");
                            if (isOwner) {
                                // Kullanıcı grubun sahibiyse grubun statusunu değiştir.
                                DatabaseReference groupsReference = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_GROUP)
                                        .child(groupID);
                                groupsReference.child(Constans.STATUS).setValue(Constans.PAUSED);
                            }
                        } else if (state == PlayerConstants.PlayerState.ENDED) {
                            Log.i("Info", "Ended");
                        }

                    }
                });
            }
        }, true);
    }


}
