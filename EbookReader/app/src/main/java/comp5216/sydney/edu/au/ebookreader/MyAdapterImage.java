package comp5216.sydney.edu.au.ebookreader;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapterImage extends BaseAdapter {
    private ArrayList mData;



    public MyAdapterImage(HashMap<String, stringHashMap> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, stringHashMap> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result;

        Map.Entry<String, stringHashMap> item = getItem(position);
        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_adapter_image_item, parent, false);




        } else {
            result = convertView;
        }
        // TODO replace findViewById by ViewHolder
        TextView programTitle =(TextView) result.findViewById(android.R.id.text1);

        TextView programDes = (TextView) result.findViewById(android.R.id.text2);
        ImageView imageView = (ImageView) result.findViewById(R.id.image1);
        imageView.setImageBitmap(item.getValue().Img);
        //  new DownloadImageTask().execute(item.getValue().Img);

        programTitle.setText(item.getKey());
        programDes.setText(item.getValue().Des);



       //imageView.setImageResource(R.mipmap.ic_launcher); //set image here




        return result;
    }





}