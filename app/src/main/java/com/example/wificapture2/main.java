package com.example.wificapture2;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import  android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import java.lang.String;
public class main extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    String st= null;
    TextView txt;
    Button bt;
    public void onStart(){
        super.onStart();
        intializer();
    }
    public void intializer(){
        setContentView(R.layout.layout);
        txt=(TextView)findViewById(R.id.textView3);
        //txt.setText("Wifi");
        //txt.setTextSize(30);
        bt=(Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getBaseContext(),"Clicking!",Toast.LENGTH_LONG).show();
                Intent intent= new Intent(main.this,MainActivity.class);
                //Bundle bdle=new Bundle();
                String id="thissss";
                intent.putExtra("EXTRA_MESSAGE",id.toString());
                //bdle.putString("Key","value");
                //intent.putExtras(bdle);
                startActivity(intent);
            }
        });

    }


}