package comp5216.sydney.edu.au.ebookreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by jason on 14/10/16.
 */

public class CustomSwipeAdapter extends PagerAdapter {

    static ImageView i1;

    String pdfName;
    private int[] images = {R.drawable.a1,R.drawable.a2,R.drawable.a1,R.drawable.a2};

    static Bitmap[] bmp ;


    static String [] stringPath;
    static String [] fileName = {"Book 1", "Book 2"};
    static String [] textPath;

    private Context ctx;
    private LayoutInflater layoutInflater;

    public CustomSwipeAdapter(Context ctx) {
        this.ctx =ctx;
//        bmp=Picgraber();
    }

    @Override
    public int getCount() {
//        return bmp.length;
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //Picgraber();
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.viewpager_single,container,false);

        i1 = (ImageView)item_view.findViewById(R.id.bookCover);
        TextView t1 = (TextView)item_view.findViewById(R.id.bookName);

        // i1.setImageBitmap(bmp[position]);
        //SET BOOK COVER
        i1.setImageResource(R.drawable.a1);
        //i1.setTag(stringPath[position]);

        //SET BOOK NAME
//        t1.setText(fileName[position]);
        t1.setText("Book Name");

        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }

    public Bitmap[] Picgraber(){
        ArrayList<Bitmap> list = new ArrayList<Bitmap>();
        ArrayList<String> PathHash = new ArrayList<String>();
        ArrayList<String> FileNameHash = new ArrayList<String>();
        ArrayList<String> textpath = new ArrayList<String>();
        File file= new File("/sdcard/com.yzho5003/");
        File[] folders=file.listFiles();
        for(File ff:folders){
            File it=new File(file.getPath()+File.separator+ff.getName()+File.separator+"image"+File.separator+ff.getName()+".png");
            // Log.w("folder with path",it.getPath());
            list.add(BitmapFactory.decodeFile(it.getPath()));
            PathHash.add(it.getPath());
            FileNameHash.add(it.getName().replace("_pdf.png",""));
            textpath.add(file.getPath()+File.separator+ff.getName()+File.separator+"text"+File.separator);
            Log.w("text path",file.getPath()+File.separator+ff.getName()+File.separator+"text"+File.separator);
        }
        stringPath =  PathHash.toArray(new String[PathHash.size()]);
        fileName=FileNameHash.toArray(new String[FileNameHash.size()]);
        textPath=textpath.toArray(new String[textpath.size()]);
        Bitmap[] Array =  list.toArray(new Bitmap[list.size()]);
        return Array;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setImages(){
        bmp=Picgraber();
        this.notifyDataSetChanged();
    }


}