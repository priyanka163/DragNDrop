package com.example.next.dragndrop2;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.graphics.Typeface;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

    private TextView option1, option2, option3, choice1, choice2, choice3;
    public CharSequence dragData;

    TextView dropTarget;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get both sets of text views

        //views to drag
        option1 = (TextView)findViewById(R.id.option_1);
        option2 = (TextView)findViewById(R.id.option_2);
        option3 = (TextView)findViewById(R.id.option_3);

        //views to drop onto
        choice1 = (TextView)findViewById(R.id.choice_1);
        choice2 = (TextView)findViewById(R.id.choice_2);
        choice3 = (TextView)findViewById(R.id.choice_3);

        //set touch listeners
        option1.setOnTouchListener(new ChoiceTouchListener());
        option2.setOnTouchListener(new ChoiceTouchListener());
        option3.setOnTouchListener(new ChoiceTouchListener());

        //set drag listeners
        choice1.setOnDragListener(new ChoiceDragListener());
        choice2.setOnDragListener(new ChoiceDragListener());
        choice3.setOnDragListener(new ChoiceDragListener());


        String s=readJson();
        parse(s);

    }

    /**
     * ChoiceTouchListener will handle touch events on draggable views
     *
     */
    private final class ChoiceTouchListener implements OnTouchListener {
        @SuppressLint("NewApi")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            /*
             * Drag details: we only need default behavior
             * - clip data could be set to pass data as part of drag
             * - shadow can be tailored
             */
                ClipData data = ClipData.newPlainText("", "");
                DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                //start dragging the item touched
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * DragListener will handle dragged views being dropped on the drop area
     * - only the drop action will have processing added to it as we are not
     * - amending the default behavior for other parts of the drag process
     *
     */
    @SuppressLint("NewApi")
    public class ChoiceDragListener implements OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DROP:

                    //handle the dragged view being dropped over a drop view
                    View view = (View) event.getLocalState();
                    //view dragged item is being dropped on
                     dropTarget = (TextView) v;
                    //view being dragged and dropped
                    TextView dropped = (TextView) view;
                    //checking whether first character of dropTarget equals first character of dropped
                    if(dropTarget.getText().toString().charAt(0) == dropped.getText().toString().charAt(0))
                    {
                        //stop displaying the view where it was before it was dragged
                        view.setVisibility(View.INVISIBLE);
                        //update the text in the target view to reflect the data being dropped
                        dropTarget.setText(dropTarget.getText().toString() + dropped.getText().toString());
                        //make it bold to highlight the fact that an item has been dropped
                        dropTarget.setTypeface(Typeface.DEFAULT_BOLD);
                        //if an item has already been dropped here, there will be a tag
                        Object tag = dropTarget.getTag();
                        //if there is already an item here, set it back visible in its original place
                        if(tag!=null)
                        {
                            //the tag is the view id already dropped here
                            int existingID = (Integer)tag;
                            //set the original view visible again
                            findViewById(existingID).setVisibility(View.VISIBLE);
                        }
                        //set the tag in the target view being dropped on - to the ID of the view being dropped
                        dropTarget.setTag(dropped.getId());
                        //remove setOnDragListener by setting OnDragListener to null, so that no further drag & dropping on this TextView can be done
                        dropTarget.setOnDragListener(null);
                    }
                    else
                        //displays message if first character of dropTarget is not equal to first character of dropped
                        Toast.makeText(MainActivity.this, dropTarget.getText().toString() + "is not " + dropped.getText().toString(), Toast.LENGTH_LONG).show();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //no action necessary
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    public void reset(View view)
    {
        option1.setVisibility(TextView.VISIBLE);
        option2.setVisibility(TextView.VISIBLE);
        option3.setVisibility(TextView.VISIBLE);

        choice1.setText("A for ");
        choice2.setText("c for ");
        choice3.setText("B for ");

        choice1.setTag(null);
        choice2.setTag(null);
        choice3.setTag(null);

        choice1.setTypeface(Typeface.DEFAULT);
        choice2.setTypeface(Typeface.DEFAULT);
        choice3.setTypeface(Typeface.DEFAULT);

        choice1.setOnDragListener(new ChoiceDragListener());
        choice2.setOnDragListener(new ChoiceDragListener());
        choice3.setOnDragListener(new ChoiceDragListener());
    }

    public void onclicc(View v){
        option3.setVisibility(TextView.VISIBLE);
       // choice3.setText("B for ");
       // choice3.setTag(null);
       // choice3.setTypeface(Typeface.DEFAULT);
        choice3.setOnDragListener(new ChoiceDragListener());

    }



    private  String readJson (){
        BufferedReader bufferedReader;
        try {
            InputStream inputStream=getAssets().open("data.json");
            int size=inputStream.available();
            byte[] buffer= new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void parse(String returndata){
        Log.i("daat", "parse: "+returndata);

       // arraylist = new ArrayList<>();
        JSONObject object= null;
        try {
            object = new JSONObject(returndata);


          JSONObject  secobj = object.getJSONObject("question");
           JSONObject  ansobj=object.getJSONObject("answer");
            String fruit1=  secobj.getString("fruita");
            Log.i("1", "parse: "+fruit1);
            String fruit2=  secobj.getString("fruitb");
            Log.i("2", "parse: "+fruit2);

            String fruit3 = secobj.getString("fruitc");
            Log.i("3", "parse: "+fruit3);


           // arraylist.add(fruit1);
           // arraylist.add(fruit2);
            //arraylist.add(fruit3);

            //return arraylist;
            option1.setText(fruit1);
            option2.setText(fruit2);
            option3.setText(fruit3);
            String a=ansobj.getString("a");
            String b=ansobj.getString("b");
            String c=ansobj.getString("c");
            //  arrayListsec.add(a);
            // arrayListsec.add(b);
            // arrayListsec.add(c);
            choice1.setText(a);
            choice2.setText(b);
            choice3.setText(c);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void  jsonParse(String returndata){
       // arrayListsec=new ArrayList<>();
        JSONObject obj= null;
        try {
            obj = new JSONObject(returndata);
            JSONObject jsonObject=obj.getJSONObject("answer");
            String a=jsonObject.getString("a");
            String b=jsonObject.getString("b");
            String c=jsonObject.getString("c");
          //  arrayListsec.add(a);
           // arrayListsec.add(b);
           // arrayListsec.add(c);
            choice1.setText(a);
            choice2.setText(b);
            choice3.setText(c);
        } catch (JSONException e) {
            e.printStackTrace();
        }





        //return arrayListsec;
    }

    public CharSequence getDragData() {

        return dragData;
    }
}