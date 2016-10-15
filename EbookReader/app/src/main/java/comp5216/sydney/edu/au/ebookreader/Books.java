package comp5216.sydney.edu.au.ebookreader;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;

public class Books extends AppCompatActivity {

    //UI variables
    ViewPager viewPager;
    CustomSwipeAdapter adapter;

    //swipe geature
    float x1, x2;
    float y1, y2;
    static final int MIN_DISTANCE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        //init viewpager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new CustomSwipeAdapter(this);
        viewPager.setAdapter(adapter);

        //animation
        viewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
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
                            //sensitivity zada ki hai +100 karke//
                            if (y1 > y2 + 100) {
                                downtoupswipe();
                            }
                            if(x1==x2 && y1==y2){
//                                pos =viewPager.getCurrentItem();
//                                readTextFile(CustomSwipeAdapter.textPath[pos]);
//                                launchPdfToText();
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
//        Intent intent = new Intent(Books.this, ReadingDetails.class);
//        intent.putExtra("bookName", CustomSwipeAdapter.fileName[pos] );
//        startActivity(intent);
//        overridePendingTransition( R.anim.slide_in_bottom,R.anim.slide);
    }


}
