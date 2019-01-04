package am.amitm29.com.home;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        int pos = getIntent().getIntExtra("url", 0);

        String s1 = "https://github.com/amitm29";
        String s2 = "https://www.youtube.com/techtv";
        String s3 = "http://instagram.com/_u/tech_tv13";
        String s4 = "https://www.facebook.com/techtv13";

        WebView wb1 = findViewById(R.id.wb1);
        String arr[] = new String[4];
        arr[0] = s1;
        arr[1] = s2;
        arr[2] = s3;
        arr[3] = s4;


        wb1.loadUrl(arr[pos]);
    }
}

