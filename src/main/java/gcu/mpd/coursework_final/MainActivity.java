
//
// Name                 Nathan Brown
// Student ID           S1512511
// Programme of Study   Computing
//

package gcu.mpd.coursework_final;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    ListView lvRss;
    ArrayList<String> titles;
    ArrayList<String> links;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvRss = (ListView) findViewById(R.id.lvRss);

        titles = new ArrayList<String>();

        links = new ArrayList<String>();

        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Uri uri = Uri.parse(links.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });


        new ProcessInBackground().execute();
    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        }
        catch ( IOException e) {
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer, Void,Exception> {

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        Exception exception =null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
        }


        @Override
        protected Exception doInBackground(Integer... params) {

            try {
                URL url = new URL("http://quakes.bgs.ac.uk/feeds/MhSeismology.xml");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                XmlPullParser xmlPullParser = factory.newPullParser();

                xmlPullParser.setInput(getInputStream(url), "UTF_8");

                boolean insideItem = false;

                int eventType =xmlPullParser.getEventType();

                while(eventType !=XmlPullParser.END_DOCUMENT) {
                    if (eventType ==XmlPullParser.START_TAG){
                        if (xmlPullParser.getName().equalsIgnoreCase("item")){
                            insideItem =true;
                        }
                        else if (xmlPullParser.getName().equalsIgnoreCase("title")) {
                            if(insideItem) {
                                titles.add(xmlPullParser.nextText());

                            }
                        }
                        else if(xmlPullParser.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                links.add(xmlPullParser.nextText());
                            }
                        }

                    }



                    eventType = xmlPullParser.next();
                }
            }
            catch (MalformedURLException e) {
                exception = e;
            }
            catch (XmlPullParserException e) {
                exception = e;
            }
            catch (IOException e) {
                exception = e;
            }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.listrow,R.id.text1, titles);


            lvRss.setAdapter(adapter);

            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.title_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_refresh)
            new ProcessInBackground();
        return true;
    }
}
