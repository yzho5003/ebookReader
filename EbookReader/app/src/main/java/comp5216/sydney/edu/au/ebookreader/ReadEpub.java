package comp5216.sydney.edu.au.ebookreader;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;


public class ReadEpub extends AppCompatActivity {

    //static values
    private static final int TRANSLATE = 1;

    //UI varibles
    TextView textview;
    String line;
    //Tab
    TabHost host;
    LinearLayout l1;
    ListView listView;
    ListView listView2;


    //textselection
    CharSequence selectedText;
    String[] separated;
    private Map<String, String> map;
    static String testdatastr;

    //local db
    private TestAdapter mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_epub);

        //TextView
        textview = (TextView)findViewById(R.id.textview);

        //Tab objects
        host = (TabHost) findViewById(R.id.tabHost);
        l1 = (LinearLayout) findViewById(R.id.ghaib);
        listView = (ListView) findViewById(R.id.list);
        listView2 = (ListView) findViewById(R.id.list2);
        //hide tab first
        host.setup();
        l1.bringToFront();
        l1.setVisibility(View.GONE);

        //tab init
        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Translation");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Translation");
        host.addTab(spec);
        //Tab 2
        spec = host.newTabSpec("Wikipedia");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Wikipedia");
        host.addTab(spec);


        //set up local db
        mDbHelper = new TestAdapter(ReadEpub.this);
        mDbHelper.createDatabase();

        //Read the book
        openEpub();

        //TextView onActionMode
        textview.setCustomSelectionActionModeCallback(new Callback() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Remove the "select all" option
                menu.add(0, TRANSLATE, 0, "Translate").setIcon(R.mipmap.ic_launcher); //choose any icon
                // Remove the other options
                menu.removeItem(android.R.id.selectAll);
                menu.removeItem(android.R.id.cut);
                menu.removeItem(android.R.id.copy);

                return true;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Called when action mode is first created. The menu supplied
                // will be used to generate action buttons for the action mode

                // Here is an example MenuItem
                Toast.makeText(getApplicationContext(), "hellpp", Toast.LENGTH_SHORT).show();

                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case TRANSLATE:

                        //step 0: show tap
                        l1.setVisibility(View.VISIBLE);

                        //step 1: get the selected string
                        int min = 0;
                        int max = textview.getText().length();
                        if (textview.isFocused()) {
                            final int selStart = textview.getSelectionStart();
                            final int selEnd = textview.getSelectionEnd();

                            min = Math.max(0, Math.min(selStart, selEnd));
                            max = Math.max(0, Math.max(selStart, selEnd));
                        }

                        selectedText = textview.getText().subSequence(min, max); //this is your desired string

                        //step 3: search for translation
                        int count=0;
                        mDbHelper.open();
                        String CurrentString = selectedText.toString();
                        separated = CurrentString.split("[,'\". \n\r\t]");

                        new MyAsyncTask().execute("");
                        mode.finish();

                        return true;
                }
                return false;
            }

            class MyAsyncTask extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... strings) {
                    map = new HashMap<String, String>(); // Is "map" k variable mein poora array hai
                    for (int i = separated.length-1; i>=0 ; i--) {
                        Cursor testdata = mDbHelper.getTestData(separated[i].trim());
                        if (testdata.moveToFirst()) {
                            String upperString = separated[i].substring(0, 1).toUpperCase() + separated[i].substring(1); //for capitilaizing first letter
                            map.put(upperString, testdata.getString(0));                                                //of word
                            testdatastr = testdata.getString(0);
                        } else {
                            runOnUiThread(new Runnable(){

                                @Override
                                public void run(){
                                    Toast.makeText(ReadEpub.this,"Internet Issue",Toast.LENGTH_LONG).show();
                                    //update ui here
                                    // display toast here
                                }
                            });
                        }
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    showtranslation(map);
                    mDbHelper.close();
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Called when an action mode is about to be exited and
                // destroyed
            }

        });

        textview.setOnClickListener(new View.OnClickListener()
        {   @Override
            public void onClick(View view)
            {
                l1.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void showtranslation(Map<String, String> map) {
        MyAdapter adapter = new MyAdapter(map);
        listView.setAdapter(adapter);
    }


    //private void readItemsFromFile() {
        /*//retrieve the app's private folder.
        //this folder cannot be accessed by other apps
        File filesDir = getFilesDir();
        //prepare a file to read the data
        File todoFile = new File(filesDir,"sample_article.txt");
        //if file does not exist, create an empty list
        FileReader fr = null;
        try {
            fr = new FileReader(todoFile);
            BufferedReader br = new BufferedReader(fr);
            while((line = br.readLine()) != null)
            {
                textview.append(line);
                textview.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fr) {
                try {
                    fr.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }*/

    private void openEpub() {

        TextView textview = (TextView) findViewById(R.id.textview);

        Book book=null;
        try {
            AssetManager assetManager = getAssets();
            //InputStream epubInputStream = assetManager.open("CinderSilly_Empowered_Princess_2014.epub");
            InputStream epubInputStream = assetManager.open("The_Tenant_of_Wildfell_Hall-Anne_Bronte.epub");
            book = (new EpubReader()).readEpub(epubInputStream);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //String baseUrl = Environment.getExternalStorageDirectory().getPath() + "/";
        String data=null;
        try {
            data = new String(book.getContents().get(2).getData());
            //data.replaceAll("<img.*?>.*?</img>", "");
            Spanned result = Html.fromHtml(data);
            textview.append(result);


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //webview.loadDataWithBaseURL(baseUrl, data, "text/html", "UTF-8", null);

    }
}

