package yaman.hasan.hasandemo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hasanyaman on 29.08.2018.
 */

public class OnlineUserAdapter extends BaseAdapter {

    private Activity activity;
    private List<User> onlineUsers;

    public OnlineUserAdapter(Activity activity,List<User> onlineUsers) {
        this.activity = activity;
        this.onlineUsers = onlineUsers;
    }

    @Override
    public int getCount() {
        return onlineUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return onlineUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.online_user_row, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.textView);
        //textView.setText(onlineUsers.get(position).getUsername() + "  --- " + onlineUsers.get(position).getUid());
        textView.setText(onlineUsers.get(position).getUsername());

        return convertView;
    }

}
