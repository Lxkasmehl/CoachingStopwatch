package io.github.lxkasmehl.coachingstopwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LapAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private List<String> mLaps;

    public LapAdapter(Context context, List<String> laps) {
        super(context, 0, laps);
        mContext = context;
        mLaps = laps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        }

        TextView lapTextView = view.findViewById(R.id.lap_textview);
        lapTextView.setText(mLaps.get(position));

        return view;
    }
}
