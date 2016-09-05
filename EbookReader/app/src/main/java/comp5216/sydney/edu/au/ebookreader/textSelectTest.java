package comp5216.sydney.edu.au.ebookreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
<<<<<<< Updated upstream
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
=======
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
>>>>>>> Stashed changes
import android.widget.TextView;
import android.widget.Toast;

<<<<<<< Updated upstream
=======
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

>>>>>>> Stashed changes
public class textSelectTest extends AppCompatActivity {

    private static final int TRANSLATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_select_test);
        

        final TextView mTextView = (TextView)findViewById(R.id.textViewTest);

//        mTextView.setCustomSelectionActionModeCallback(new Callback() {
//
//            public boolean onCreateActionMode(ActionMode mode, Menu menu){
//                menu.add(0,TRANSLATE,0,"Translate");
//
//                //Remove the other options
//                menu.removeItem(android.R.id.selectAll);
//                menu.removeItem(android.R.id.cut);
//                menu.removeItem(android.R.id.copy);
//                return true;
//            }
//
//
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu){
//                return true;
//            }
//
//
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item){
//                switch(item.getItemId()){
//                    case TRANSLATE:
//                        int min = 0;
//                        int max = mTextView.getText().length();
//                        if(mTextView.isFocused()){
//                            final int selStart = mTextView.getSelectionStart();
//                            final int selEnd = mTextView.getSelectionEnd();
//
//                            min = Math.max(0,Math.min(selStart,selEnd));
//                            min = Math.max(0,Math.max(selStart,selEnd));
//                        }
//
//                        //get the selected String
//                        final CharSequence selectedText = mTextView.getText().subSequence(min,max);
//
//                        //Use toast to show the string
//                        Toast.makeText(getApplicationContext(), selectedText, Toast.LENGTH_SHORT).show();
//
//                        mode.finish();
//                }
//
//                return false;
//            }
//
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//                // Called when an action mode is about to be exited and
//                // destroyed
//            }
//
//        });

        mTextView.setCustomSelectionActionModeCallback(new Callback() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Remove the "select all" option
                menu.removeItem(android.R.id.selectAll);
                // Remove the "cut" option
                menu.removeItem(android.R.id.cut);
                // Remove the "copy all" option
                menu.removeItem(android.R.id.copy);
                return true;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Called when action mode is first created. The menu supplied
                // will be used to generate action buttons for the action mode

                // Here is an example MenuItem
                menu.add(0, TRANSLATE, 0, "Translate");
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case TRANSLATE:
                        int min = 0;
                        int max = mTextView.getText().length();
                        if (mTextView.isFocused()) {
                            final int selStart = mTextView.getSelectionStart();
                            final int selEnd = mTextView.getSelectionEnd();

                            min = Math.max(0, Math.min(selStart, selEnd));
                            max = Math.max(0, Math.max(selStart, selEnd));
                        }
                        // Perform your definition lookup with the selected text
                        final CharSequence selectedText = mTextView.getText().subSequence(min, max);
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

    protected void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = getApplicationContext().getAssets().open("file.db");

        // Path to the just created empty db
        String outFileName = "/data/data/com.yourpackagename/databases/" + "file.db";

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }


}
