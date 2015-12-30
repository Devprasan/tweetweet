package com.example.user.tweetweet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 12/29/2015.
 */
public class Myadapter extends ArrayAdapter {
    private Context context;
    private int resource;
    private List<ModelTweet> modelTweets;
    private LayoutInflater inflater;

    public Myadapter(Context context, int resource, List<ModelTweet> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.modelTweets = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.name);
        TextView textView1 = (TextView) convertView.findViewById(R.id.screen_name);
        TextView textView2 = (TextView) convertView.findViewById(R.id.tweet);

        textView2.setText(modelTweets.get(position).getText());
        textView.setText(modelTweets.get(position).getName());
        textView1.setText(modelTweets.get(position).getScreen_name());


        return convertView;


    }
}
