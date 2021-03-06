package comp5216.sydney.edu.au.ebookreader;

/**
 * Created by jason on 13/10/16.
 * Used for update the translation tab
 */

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    private final ArrayList mData;
    private Typeface charter;

    public MyAdapter(Map<String, String> map, Typeface wordFont) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
        charter = wordFont;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_translation, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, String> item = getItem(position);

        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(android.R.id.text1)).setText(item.getKey());
        ((TextView) result.findViewById(android.R.id.text1)).setTypeface(charter);
        ((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue());

        return result;
    }
}