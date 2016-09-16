package comp5216.sydney.edu.au.ebookreader;

import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;



public class ReadEpub extends AppCompatActivity {

    TextView textview;
    String line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_epub);
        textview = (TextView)findViewById(R.id.textview);

        textview.setMovementMethod(ScrollingMovementMethod.getInstance());

    }

    public void onReadClick(View view) {
        textview.setText("");
        openEpub();
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

