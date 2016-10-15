package comp5216.sydney.edu.au.ebookreader;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;


public class ReadEpub extends AppCompatActivity {

    //custom fonts
    Typeface charter;
    Typeface charterBold;
    Typeface pingfang;
    Typeface roboto;
    Typeface robotoMedium;

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

    //wikipedia
    String CurrentString;

    ArrayList<String> Title = new ArrayList<String>();
    ArrayList<Bitmap> Image = new ArrayList<Bitmap>();
    ArrayList<String> Description = new ArrayList<String>();
    ArrayList<String> Link = new ArrayList<String>();

    //vocab library
    static Map<String, String> vocab;
    SharedPreferences pref;
    SharedPreferences preferences;

    //textselection
    CharSequence selectedText;
    String[] separated;
    private Map<String, String> map;
    private HashMap<String, stringHashMap> mappedFiles;
    static String testdatastr;

    //local db
    private TestAdapter mDbHelper;

    //book info
    static int pos=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_read_epub);

        //Custom fonts
        charter = Typeface.createFromAsset(getAssets(), "Charter Regular.ttf");
        charterBold = Typeface.createFromAsset(getAssets(), "Charter Bold.ttf");
        pingfang = Typeface.createFromAsset(getAssets(), "PingFang.ttc");
        roboto = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");

        //TextView
        textview = (TextView)findViewById(R.id.textview);
        textview.setTypeface(charter);

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

        //init vocab library
        vocab = new HashMap<String, String>();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        pref = this.getSharedPreferences(CustomSwipeAdapter.fileName[Books.pos]+"451words", this.MODE_PRIVATE);

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
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case TRANSLATE:

                        //step 0: show tab
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
                        CurrentString = selectedText.toString();
                        separated = CurrentString.split("[,'\". \n\r\t]");
                        new Thread(new Task()).start();
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
                            Log.d("translate:", "MicroSoft");
                            Translate.setClientId("bwefo3820f3uboejnw03");
                            Translate.setClientSecret("xUamXe4g5uVOAem/vXVb+hfdhdxc7FDbrgTjjucTrIw=");
                            String translatedText;
                            try {
                                translatedText = Translate.execute(separated[i].trim(), Language.AUTO_DETECT, Language.CHINESE_SIMPLIFIED);
                                String upperString = separated[i].substring(0, 1).toUpperCase() + separated[i].substring(1); //for capitilaizing first letter
                                map.put(upperString, translatedText);
                                //Log.w("text", translatedText);
                            } catch (Exception e) {
                                translatedText = e.toString();
                                //Log.w("text", translatedText);
                            }
                        }
//                        else {
//                            runOnUiThread(new Runnable(){
//
//                                @Override
//                                public void run(){
//                                    Toast.makeText(ReadEpub.this,"Internet Issue",Toast.LENGTH_LONG).show();
//                                    //update ui here
//                                    // display toast here
//                                }
//                            });
//                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    vocab= new HashMap<String, String>();
                    pref = getSharedPreferences(CustomSwipeAdapter.fileName[Books.pos]+"451words", 0);
                    for( Map.Entry entry : pref.getAll().entrySet() ) {
                        vocab.put(entry.getKey().toString(),entry.getValue().toString());

                    }
                    vocab.putAll(map);

                    SharedPreferences.Editor editor = pref.edit();
                    for (String ss : vocab.keySet()) {
                        editor.putString(ss, vocab.get(ss));
                    }
                    editor.commit();

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
        MyAdapter adapter = new MyAdapter(map, charterBold);
        listView.setAdapter(adapter);
    }

    public void showwiki(HashMap<String, stringHashMap> map2) {
        MyAdapterImage adapter2 = new MyAdapterImage(map2);
        listView2.setAdapter(adapter2);
    }


    class RequestTask extends AsyncTask<String[], Void, String[]> {

        @Override
        protected String[] doInBackground(String[]... passing) {
            Link.clear();
            Title.clear();
            Description.clear();
            Image.clear();

            String toSearch = CurrentString;
            //   toSearch = "different";

            String wikiDataUrl = "https://en.wikipedia.org/w/api.php?action=opensearch&search=" + toSearch + "&prop=pageimage&limit=1&namespace=0&format=json";
            String wikiImageUrl = "https://en.wikipedia.org/w/api.php?action=query&titles=" + toSearch + "&prop=pageimages&format=json&pithumbsize=100";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(wikiDataUrl);
                HttpResponse response = httpclient.execute(httpget);

                HttpClient httpclientImg = new DefaultHttpClient();
                HttpGet httpgetImg = new HttpGet(wikiImageUrl);
                HttpResponse responseImg = httpclientImg.execute(httpgetImg);

                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity server_response = response.getEntity();
                    String data = EntityUtils.toString(server_response);
                    JSONObject last = new JSONObject("{" + '"' + "data" + '"' + ":" + data + "}");
                    JSONArray ja = last.getJSONArray("data");
                    for (int j = 1; j < ja.length(); j++) {
                        String string = ja.getString(j);
                        if (j == 1) {
                            Title.add(string.substring(2, string.length() - 2));
                        } else if (j == 2) {
                            Description.add(string.substring(2, string.length() - 2));
                        } else if (j == 3) {
                            Link.add(string.substring(2, string.length() - 2));
                        }

                        //Log.d("TAG",string.substring(2,string.length()-2)); // do whatever you want with "string"
                    }
                    if (responseImg.getStatusLine().getStatusCode() == 200) {
                        HttpEntity server_responseImg = responseImg.getEntity();
                        String dataImg = EntityUtils.toString(server_responseImg);
                        JSONObject lastImg = new JSONObject(dataImg);
                        JSONObject jo1 = lastImg.getJSONObject("query");
                        String result = null;
                        if (jo1.has("pages")) {
                            JSONObject jo2 = jo1.getJSONObject("pages");
                            Iterator<String> keys = jo2.keys();
                            if (keys.hasNext()) {
                                String key = (String) keys.next(); // First key in your json object
                                JSONObject jo3 = jo2.getJSONObject(key);
                                if (jo3.has("thumbnail")) {
                                    JSONObject jo4 = jo3.getJSONObject("thumbnail");
                                    // Log.w("ahahah",jo4.getString("source"));
                                    result = jo4.getString("source");
                                } else {
                                    result = "https://www.megx.net/net.megx.esa/img/no_photo.png";
                                }
                            }// yeh itrator ka hai iska koi else nae hai... no confusion

                        } else {
                            result = "https://www.megx.net/net.megx.esa/img/no_photo.png";
                        }


                        Bitmap responseString = null;

                        URL newurl = null;
                        try {
                            newurl = new URL(result);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        try {
                            responseString = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                            Image.add(responseString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("title", Title.get(0).toString());
            Log.d("description", Description.get(0).toString());
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            mappedFiles = new HashMap<String, stringHashMap>();
            for (int hm = 0; hm < Title.size(); hm++) {
                mappedFiles.put(Title.get(hm), new stringHashMap(Title.get(hm), Description.get(hm), Image.get(hm)));
            }
            showwiki(mappedFiles);
            Log.w("Title", String.valueOf(Title.size()));
            Log.w("Images", String.valueOf(Image.size()));
            Log.w("Description", String.valueOf(Description.size()));
            Log.w("Link", String.valueOf(Link.size()));
            //Do anything with response..
        }
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
        String data=null;
        try {
            data = new String(book.getContents().get(2).getData());
            Spanned result = Html.fromHtml(data, Html.FROM_HTML_MODE_COMPACT);
            textview.append(result);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Task implements Runnable {
        @Override
        public void run() {
            new RequestTask().execute(separated);
        }
    }
}

