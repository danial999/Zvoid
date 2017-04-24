package dibrahex.games.deibra.zvoid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class imageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Intent i= getIntent();
        String pic_url= i.getStringExtra("pic_url2");
        String picID=i.getStringExtra("picID");
        String farm=i.getStringExtra("farm");
        String serverID=i.getStringExtra("serverID");
        String title=i.getStringExtra("title");
        ImageView img = (ImageView )findViewById(R.id.image1);
        new DownloadImageTask(img)
                .execute(pic_url);
      //  img.setImageBitmap(bmp);

        TextView txt1= (TextView)findViewById(R.id.txt1);
        txt1.setText("title: "+title);
        TextView txt2= (TextView)findViewById(R.id.txt2);
        txt2.setText("serverID: "+serverID);
        TextView txt3= (TextView)findViewById(R.id.txt3);
        txt3.setText("picID: "+picID);
        TextView txt4= (TextView)findViewById(R.id.txt4);
        txt4.setText("farmID: "+picID);
    }
}
