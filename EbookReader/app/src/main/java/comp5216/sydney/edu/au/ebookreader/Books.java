package comp5216.sydney.edu.au.ebookreader;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by jason on 14/10/16.
 */

public class Books extends AppCompatActivity {

    //UI variables
    ViewPager viewPager;
    CustomSwipeAdapter adapter;
    Button button;
    static String path;

    //swipe geature
    float x1, x2;
    float y1, y2;
    static final int MIN_DISTANCE = 200;

    //book info
    static int pos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        path = null;

        //init viewpager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new CustomSwipeAdapter(this);
        viewPager.setAdapter(adapter);
        button = (Button) findViewById(R.id.button);

        //animation
        viewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                pos = position;
            }
        });

        //for viewpager
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    // when user first touches the screen we get x and y coordinate
                    case MotionEvent.ACTION_DOWN: {
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        x2 = event.getX();
                        y2 = event.getY();


                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE)
                        {
                            //if left to right sweep event on screen
                            if (x1 < x2) {

                            }
                            // if right to left sweep event on screen
                            if (x1 > x2) {

                            }
                        }
                        else
                        {
                            // if UP to Down sweep event on screen
                            if (y1 < y2-100) {

                            }

                            //if Down to UP sweep event on screen
                            if (y1 > y2 + 100) {
                                downtoupswipe();
                            }
                            if(x1==x2 && y1==y2){
                                pos =viewPager.getCurrentItem();
//                                readTextFile(CustomSwipeAdapter.textPath[pos]);
                                launchPdfToText();
                            }
                        }


                        break;
                    }
                }
                return false;
            }
        });
    }

    public void downtoupswipe() {
        Intent intent = new Intent(Books.this, ReadingDetails.class);
        intent.putExtra("bookName", CustomSwipeAdapter.fileName[pos] );
        startActivity(intent);
        overridePendingTransition( R.anim.slide_in_bottom,R.anim.slide);
    }

    public void launchPdfToText(){
        Intent i = new Intent(Books.this,ReadEpub.class);
        startActivity(i);
    }

    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    path = getDataColumn(this, contentUri, null, null);
                    Intent intent = new Intent(Books.this,ReadEpub.class);
                    startActivity(intent);
            }
        }
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
