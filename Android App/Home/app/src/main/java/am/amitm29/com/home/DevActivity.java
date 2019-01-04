package am.amitm29.com.home;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class DevActivity extends AppCompatActivity {

    LinearLayout emailLL, youtubeLL, facebookLL, instagramLL, githubLL;
    Button contactMe;
    TextView devName;

    public static String FACEBOOK_URL = "https://www.facebook.com/techtv13";
    public static String FACEBOOK_PAGE_ID = "techtv13";

    String email = "dev.amitm29@gmail.com";
    String subject = "HOME AUTOMATION APP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);

        emailLL = findViewById(R.id.email);
        youtubeLL = findViewById(R.id.youtube);
        facebookLL = findViewById(R.id.facebook);
        instagramLL = findViewById(R.id.instagram);
        githubLL = findViewById(R.id.github);
        contactMe = findViewById(R.id.contact_me);
        devName = findViewById(R.id.devName);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("About Developer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", email, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                final PackageManager pm = getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                ResolveInfo best = null;
                for(final ResolveInfo info : matches)
                    if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                        best = info;
                if (best != null)
                    emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);

                startActivity(emailIntent);
            }
        });

        youtubeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.youtube.com/techtv";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(i);
                }
                else
                {
                    startActivity(new Intent(DevActivity.this, WebActivity.class).putExtra("url", 1));
                }
            }
        });

        facebookLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL();
                facebookIntent.setData(Uri.parse(facebookUrl));
                try {
                    startActivity(facebookIntent);
                }
                catch (ActivityNotFoundException e) {
                    startActivity(new Intent(DevActivity.this, WebActivity.class).putExtra("url", 3));
                }
            }
        });

        instagramLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://instagram.com/_u/tech_tv13");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(DevActivity.this, WebActivity.class).putExtra("url", 2));
                }
            }
        });

        githubLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DevActivity.this, WebActivity.class).putExtra("url", 0));
            }
        });

        contactMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", email, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                final PackageManager pm = getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                ResolveInfo best = null;
                for(final ResolveInfo info : matches)
                    if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                        best = info;
                if (best != null)
                    emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);

                startActivity(emailIntent);
            }
        });


        devName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://play.google.com/store/apps/developer?id=Amit+Mohan")));
            }
        });

    }

    //method to get the right URL to use in the intent
    public String getFacebookPageURL() {
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            default :
                //in case of tapping the back button
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
