package dibrahex.games.deibra.zvoid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ScrollingActivity extends AppCompatActivity {
    public JSONObject jsonObject;
    public String query;
    public String url;
    public String pic_url;
    public Bitmap bmp = null;
    public static int j=0;
    public static int max=5;
    public static ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TableLayout ll = (TableLayout) findViewById(R.id.tableLayout2);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        progress = (ProgressBar)findViewById(R.id.pbHeaderProgress);




        fab.setBackgroundResource(R.drawable.searchicon);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                max=5;
                j=0;
                if (progress != null) {
                    progress.setVisibility(View.VISIBLE);
                }
                TableLayout ll = (TableLayout) findViewById(R.id.tableLayout2);

                int count = ll.getChildCount();
                if(count>1) {
                    ll.removeViews(0, (count-1));
                }
                EditText q = (EditText) findViewById(R.id.searchbox);
                query = q.getText().toString();
                if(q.length()<1){
                    Snackbar.make(view, "Enter text to search!! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=9d125f270ce02fcb7b1ebf033e981ea9&in_gallery=&per_page=200&page=1&format=json&nojsoncallback=1&tags="+query;
                Snackbar.make(view, "Searching Flicker for: "+query, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                //TODO your background code
                                try{
                             jsonObject = getJSONObjectFromURL(url);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        editUi();
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });


        NestedScrollView scroller = (NestedScrollView) findViewById(R.id.myScroll);

        if (scroller != null) {

            scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (scrollY > oldScrollY) {
                        Log.i("TAG", "Scroll DOWN");
                    }
                    if (scrollY < oldScrollY) {
                        Log.i("TAG", "Scroll UP");
                    }

                    if (scrollY == 0) {
                        Log.i("TAG", "TOP SCROLL");
                    }

                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i("TAG", "BOTTOM SCROLL");
                        if(max<=100) {
                            max += 3;
                            try {
                                editUi();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }else{
                            if (progress != null) {
                                progress.setVisibility(View.INVISIBLE);
                            }
                        }

                    }
                }
            });
        }


    }

    public void editUi() throws MalformedURLException {
        TableLayout ll = (TableLayout) findViewById(R.id.tableLayout2);

        for(;j<max;j++) {
            TableRow row = new TableRow(this);
            for (int i = 0; i < 2; i++) {
                ImageButton[] btnGreen = new ImageButton[2];

                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);


                JSONObject photos = null;
                JSONArray photo =null;
                String secret="";
                String picID="";
                String farm="";
                String serverID="";


                try {
                    photos = jsonObject.getJSONObject("photos");
                    photo = photos.getJSONArray("photo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (photo.length() > 0) {
                    JSONObject first = null;
                    try {
                        first = photo.getJSONObject(2*j+i);
                        secret = first.getString("secret");
                        picID = first.getString("id");
                        serverID = first.getString("server");
                        farm = first.getString("farm");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                pic_url="https://farm"+farm+".staticflickr.com/"+serverID+"/"+picID+"_"+secret+".jpg";
                Log.d("url  : "+(2*j+i), pic_url);

                btnGreen[i] = new ImageButton(this);
                btnGreen[i].setId((2*j+i));
                btnGreen[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    showImage(v);
                    }
                });

                btnGreen[i].setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
                btnGreen[i].setBackgroundResource(0);
                btnGreen[i].setAdjustViewBounds(false);
                btnGreen[i].setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                new DownloadImageTask(btnGreen[i])
                        .execute(pic_url);
                // btnGreen[i].setImageBitmap(bmp);
                row.addView(btnGreen[i]);
            }

            ll.addView(row, j);
        }

    }



    public void showImage(View v){
        int i = v.getId();
      //  Toast.makeText(this,i+"", Toast.LENGTH_SHORT).show();

        JSONObject photos = null;
        JSONArray photo =null;
        String secret="";
        String picID="";
        String farm="";
        String serverID="";
        String title="";

        try {
            photos = jsonObject.getJSONObject("photos");
            photo = photos.getJSONArray("photo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (photo.length() > 0) {
            JSONObject first = null;
            try {
                first = photo.getJSONObject(i);
                secret = first.getString("secret");
                picID = first.getString("id");
                serverID = first.getString("server");
                farm = first.getString("farm");
                title=first.getString("title");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        String pic_url2="https://farm"+farm+".staticflickr.com/"+serverID+"/"+picID+"_"+secret+".jpg";
        Intent myIntent = new Intent(ScrollingActivity.this, imageActivity.class);
        myIntent.putExtra("pic_url2", pic_url2); //Optional parameters
        myIntent.putExtra("picID", picID); //Optional parameters
        myIntent.putExtra("serverID", serverID); //Optional parameters
        myIntent.putExtra("farm", farm); //Optional parameters
        myIntent.putExtra("title", title); //Optional parameters

        ScrollingActivity.this.startActivity(myIntent);

    }


    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {

        HttpURLConnection urlConnection = null;

        URL url = new URL(urlString);

        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);

        urlConnection.setDoOutput(true);

        urlConnection.connect();

        BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));

        char[] buffer = new char[1024];

        String jsonString = new String();

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();

        jsonString = sb.toString();

        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


