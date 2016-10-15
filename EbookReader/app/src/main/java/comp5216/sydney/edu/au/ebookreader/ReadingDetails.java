package comp5216.sydney.edu.au.ebookreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

public class ReadingDetails extends AppCompatActivity {

    //vocab data
    SharedPreferences pref;
    private Map<String, String> prefData;
    static String[]  values;

    //vocab library list
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_details);

        //reading the shared preference data
        Intent intent = getIntent();
        prefData= new HashMap<String, String>();
        pref = this.getSharedPreferences(intent.getExtras().getString("bookName")+"451words", this.MODE_PRIVATE);
        pref = getSharedPreferences(CustomSwipeAdapter.fileName[Books.pos]+"451words", 0);
        for( Map.Entry entry : pref.getAll().entrySet() ) {
            prefData.put(entry.getKey().toString(),entry.getValue().toString());
        }

        //get the vocab
        int i=0;
        values = new String[prefData.size()];
        for( Map.Entry entry : pref.getAll().entrySet() ) {
            values[i]=entry.getKey().toString();
            i++;
            //Log.w("hashmap=>"+entry.getKey().toString(),entry.getValue().toString());
        }

        //display on the interface
        listView = (ListView) findViewById(R.id.listview);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);
    }

}
