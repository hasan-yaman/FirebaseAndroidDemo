package yaman.hasan.hasandemo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hasanyaman on 4.09.2018.
 */

public class VideoAdapter extends BaseAdapter {

    private Activity activity;
    private List<Video> videos;

    public VideoAdapter(Activity activity, List<Video> videos) {
        this.activity = activity;
        this.videos = videos;
    }


    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Object getItem(int i) {
        return videos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.video_row, viewGroup, false);
        }

        TextView videoTitle = view.findViewById(R.id.videoTitle);
        videoTitle.setText(videos.get(i).getTitle());

        ImageView videoThumbnail = view.findViewById(R.id.videoThumbnail);

        Picasso.get().load(videos.get(i).getThumbnailURL()).into(videoThumbnail);


        return view;
    }
}
