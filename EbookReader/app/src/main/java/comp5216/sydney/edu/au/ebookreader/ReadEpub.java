package comp5216.sydney.edu.au.ebookreader;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;


public class ReadEpub extends AppCompatActivity {

    private static final int TRANSLATE = 1;

    TextView textview;
    String line;

    //local db
    private TestAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_epub);

        //set up local db
        mDbHelper = new TestAdapter(ReadEpub.this);
        mDbHelper.createDatabase();

        //TextView
        textview = (TextView)findViewById(R.id.textview);
        //Read the book
        openEpub();

        //TextView onActionMode
        textview.setCustomSelectionActionModeCallback(new Callback() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Remove the "select all" option
                menu.clear();
                return true;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Called when action mode is first created. The menu supplied
                // will be used to generate action buttons for the action mode

                // Here is an example MenuItem
                mode.setTitle("Test");
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case TRANSLATE:
                        int min = 0;
                        int max = textview.getText().length();
                        if (textview.isFocused()) {
                            final int selStart = textview.getSelectionStart();
                            final int selEnd = textview.getSelectionEnd();

                            min = Math.max(0, Math.min(selStart, selEnd));
                            max = Math.max(0, Math.max(selStart, selEnd));
                        }
                        // Perform your definition lookup with the selected text
                        final CharSequence selectedText = textview.getText().subSequence(min, max);
                        // Finish and close the ActionMode
                        Toast.makeText(getApplicationContext(), selectedText, Toast.LENGTH_SHORT).show();
                        mode.finish();
                        return true;
                    default:
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Called when an action mode is about to be exited and
                // destroyed
            }

        });

    }

//    public void onReadClick(View view) {
//        textview.setText("");
//        openEpub();
//    }

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

