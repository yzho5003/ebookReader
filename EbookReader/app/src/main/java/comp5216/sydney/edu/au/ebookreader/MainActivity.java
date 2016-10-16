package comp5216.sydney.edu.au.ebookreader;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by jason on 6/9/16.
 */

public class MainActivity extends AppCompatActivity {
    TextView text;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView)findViewById(R.id.textview);
        imageView = (ImageView) findViewById(R.id.image1);
        if(hasPermissions()) {
            //Toast.makeText(this,"YES",Toast.LENGTH_SHORT).show();
        }

        else {
            //Toast.makeText(this,"NO",Toast.LENGTH_SHORT).show();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {
                            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET
                    },1);
                }
            }else {
                //Toast.makeText(this,"NO",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean hasPermissions() {
        int res = 0;
        String[] permissions = new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET};

        for (String perms: permissions) {
            res = checkCallingOrSelfPermission(perms);
            if(!(res == PackageManager.PERMISSION_GRANTED)) {
                return  false;
            }
        }
        return  true;
    }

    public void readEpub(View view){
        Intent readEpub = new Intent(MainActivity.this, ReadEpub.class);

        if(readEpub != null){
            startActivity(readEpub);
        }
    }

    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case 1:
                    Uri uri = data.getData();
                    String Fpath = uri.getPath();
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    String path = getDataColumn(this, contentUri, null, null);
                    File file = new File(path);
                    Book book = null;
                    try{
                        InputStream is = new InputStream() {
                            @Override
                            public int read() throws IOException {
                                return 0;
                            }
                        };
                        FileInputStream fis = new FileInputStream(file);
                        book = (new EpubReader()).readEpub(fis);
                    }
                    catch (FileNotFoundException e) {
                        Toast.makeText(this,"File not found",Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException e) {
                        Toast.makeText(this, "IOException",Toast.LENGTH_SHORT).show();
                    }
                    try{
                        String title = book.getTitle();
                        String contents = new String(book.getContents().get(2).getData());
                        Spanned result = Html.fromHtml(contents, Html.FROM_HTML_MODE_COMPACT);
                        Log.d("title", title);
                        Log.d("contents", result.toString());
                        text.append(result);

                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}
