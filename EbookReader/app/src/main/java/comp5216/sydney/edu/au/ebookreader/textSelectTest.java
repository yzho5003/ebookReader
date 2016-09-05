package comp5216.sydney.edu.au.ebookreader;

import android.icu.text.BreakIterator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import java.util.Locale;

public class textSelectTest extends AppCompatActivity {

    private static final int TRANSLATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_select_test);
        

        final TextView mTextView = (TextView)findViewById(R.id.textViewTest);

        //solution 2, using BreakIterator
        String definition = "Char\u00ADter was de\u00ADsigned by Matthew Carter in 1987 as a body text font that would hold up well on low-res\u00ADo\u00ADlu\u00ADtion out\u00ADput de\u00ADvices of the day—fax ma\u00ADchines and 300 dpi laser printers.\n" +
                "While 300 dpi print\u00ADing is long gone, most of to\u00ADday’s com\u00ADputer dis\u00ADplays op\u00ADer\u00ADate far be\u00ADlow 300 dpi. Char\u00ADter, un\u00ADsur\u00ADpris\u00ADingly, holds up ad\u00ADmirably well as a screen font. (Matthew would go on to de\u00ADsign the quin\u00ADtes\u00ADsen\u00ADtial screen fonts Ver\u00ADdana and Geor\u00ADgia for Microsoft.)\n" +
                "In 1992, Bit\u00ADstream con\u00ADtributed Post\u00ADScript ver\u00ADsions of the ba\u00ADsic Char\u00ADter fam\u00ADily to the X Con\u00ADsor\u00ADtium. These orig\u00ADi\u00ADnal files are still avail\u00ADable and can be freely down\u00ADloaded and mod\u00ADi\u00ADfied. But they can be hard to find. So I tracked them down and con\u00ADverted them into a new pack\u00ADage of OTFs, TTFs, and webfonts.\n" +
                "Why? Be\u00ADcause Char\u00ADter is a great font, and eas\u00ADily one of the best free fonts avail\u00ADable. So I’m do\u00ADing what I can to put more Char\u00ADter into the world. If your project de\u00ADmands a font with\u00ADout li\u00ADcens\u00ADing re\u00ADstric\u00ADtions, con\u00ADsider it.\n".trim();
        TextView definitionView = (TextView) findViewById(R.id.textViewTest);

        definitionView.setMovementMethod(LinkMovementMethod.getInstance());

        //set text to the TextView with predefined String
        definitionView.setText(definition, BufferType.SPANNABLE);

        //Spannable object use the same String as the TextView
        Spannable spans = (Spannable)definitionView.getText();


        BreakIterator iterator = BreakIterator.getWordInstance(Locale.US);
        iterator.setText(definition);

        int start = iterator.first();
        for(int end=iterator.next(); end!=BreakIterator.DONE; start=end, end=iterator.next()){
            String possibleWord = definition.substring(start, end);
            if(Character.isLetterOrDigit(possibleWord.charAt(0))){
                ClickableSpan clickSpan = getClickableSpan(possibleWord);
                spans.setSpan(clickSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        //solution 1, using setCustomSelectionActionModeCallback
//        mTextView.setCustomSelectionActionModeCallback(new Callback() {
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                // Remove the "select all" option
//                menu.removeItem(android.R.id.selectAll);
//                // Remove the "cut" option
//                menu.removeItem(android.R.id.cut);
//                // Remove the "copy all" option
//                menu.removeItem(android.R.id.copy);
//                return true;
//            }
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                // Called when action mode is first created. The menu supplied
//                // will be used to generate action buttons for the action mode
//
//                // Here is an example MenuItem
//                menu.add(0, TRANSLATE, 0, "Translate");
//                return true;
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                switch (item.getItemId()) {
//                    case TRANSLATE:
//                        int min = 0;
//                        int max = mTextView.getText().length();
//                        if (mTextView.isFocused()) {
//                            final int selStart = mTextView.getSelectionStart();
//                            final int selEnd = mTextView.getSelectionEnd();
//
//                            min = Math.max(0, Math.min(selStart, selEnd));
//                            max = Math.max(0, Math.max(selStart, selEnd));
//                        }
//                        // Perform your definition lookup with the selected text
//                        final CharSequence selectedText = mTextView.getText().subSequence(min, max);
//                        // Finish and close the ActionMode
//                        Toast.makeText(getApplicationContext(), selectedText, Toast.LENGTH_SHORT).show();
//                        mode.finish();
//                        return true;
//                    default:
//                        break;
//                }
//                return false;
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//                // Called when an action mode is about to be exited and
//                // destroyed
//            }
//
//        });
    }

    private ClickableSpan getClickableSpan(final String word){
        return new ClickableSpan() {
            final String mWord;
            {
                mWord = word;
            }

            @Override
            public void onClick(View widget){
                Log.d("tapped on:", mWord);
                Toast.makeText(widget.getContext(), mWord, Toast.LENGTH_SHORT).show();
            }

            public void updateDrawState(TextPaint ds){
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(0xff161616);
            }
        };
    }
}
