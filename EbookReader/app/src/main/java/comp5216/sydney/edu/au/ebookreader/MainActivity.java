package comp5216.sydney.edu.au.ebookreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void textSelectionOnClick(View view){
        Intent textSelectTest = new Intent(MainActivity.this, TextSelectTest.class);

        if(textSelectTest != null){
            startActivity(textSelectTest);
        }
    }
}
