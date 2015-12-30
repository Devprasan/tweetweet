package com.example.user.tweetweet;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private static String twitterAPIKEY = "P7qDsVmeTLR183aYCJXmR1b42";
    private static String twitterAPISECRET = "DBL41tHzX9eZmtqn51HlTmbdECkJGEuJu1UaTzcHEL3Aee9YU9";
    static String RESULT_TYPE = "recent";
    static int COUNT = 15;
    static String tag="%23";
    static final String twitterAPIurl = "https://api.twitter.com/1.1/search/tweets.json";
    static String search = "wwe";
    static String twitterToken = null;
    static String jsonTokenStream = null;
    static String jsonFeed = null;
    TextView twitterText;
    private Myadapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        twitterText = (TextView) findViewById(R.id.text);

        new loadTwitterToken().execute();
    }

    protected class loadTwitterToken extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {

            try {
                DefaultHttpClient httpclient = new DefaultHttpClient(
                        new BasicHttpParams());
                HttpPost httppost = new HttpPost(
                        "https://api.twitter.com/oauth2/token");

                String apiString = twitterAPIKEY + ":" + twitterAPISECRET;
                String authorization = "Basic "
                        + Base64.encodeToString(apiString.getBytes(),
                        Base64.NO_WRAP);

                httppost.setHeader("Authorization", authorization);
                httppost.setHeader("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.setEntity(new StringEntity(
                        "grant_type=client_credentials"));

                InputStream inputStream = null;
                org.apache.http.HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                jsonTokenStream = sb.toString();
                return 1;

            } catch (Exception e) {
                Log.e("loadTwitterToken",
                        "doInBackground Error:" + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            try {
                JSONObject root = new JSONObject(jsonTokenStream);
                twitterToken = root.getString("access_token");
                final Handler handler = new Handler();
                Runnable refresh = new Runnable() {
                    @Override
                    public void run() {
                        new loadTwitterFeed().execute(search);
                        handler.postDelayed(this, 2000);


                    }
                };
                handler.postDelayed(refresh, 20);
            } catch (Exception e) {
                Log.e("loadTwitterToken", "onPost Error:" + e.getMessage());
            }

        }
    }

    protected class loadTwitterFeed extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
             BufferedReader reader = null;
            String tweeterURL = twitterAPIurl + "?q="+tag+ params[0] + "&result_type=" + RESULT_TYPE + "&count=" + COUNT;

            try {
                DefaultHttpClient httpclient = new DefaultHttpClient(
                        new BasicHttpParams());
                HttpGet httpget = new HttpGet(tweeterURL);
                httpget.setHeader("Authorization", "Bearer " + twitterToken);
                httpget.setHeader("Content-type", "application/json");

                InputStream inputStream = null;
                org.apache.http.HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                reader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"), 8);


                StringBuilder sb = new StringBuilder();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                return sb.toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            StringBuffer sb = new StringBuffer();

            try {
                JSONObject resultObject = new JSONObject(result);
                org.json.JSONArray tweetArray = resultObject.getJSONArray("statuses");
                List<ModelTweet> modelTweets = new ArrayList<>();
                for (int i = 0; i < tweetArray.length(); i++) {
                    ModelTweet modelTweet = new ModelTweet();
                    JSONObject tweetObject = tweetArray.getJSONObject(i);
                    String tweettttt = (tweetObject.getString("text") + ": " + "\n");
                    modelTweet.setText(tweettttt);
                    JSONObject jsonObject = tweetObject.getJSONObject("user");
                    String name = jsonObject.getString("name");
                    String screen_name = jsonObject.getString("screen_name");
                    modelTweet.setName(name);
                    modelTweet.setScreen_name(screen_name);
                    modelTweets.add(modelTweet);
                }
                ListView listView = (ListView) findViewById(R.id.list);
                arrayAdapter = new Myadapter(getApplicationContext(), R.layout.listrow, modelTweets);
                listView.setAdapter(arrayAdapter);
                //arrayAdapter.notifyDataSetChanged();
                Log.d("this", result);
            } catch (Exception e) {
                Log.e("Tweet", "Error retrieving JSON stream" + e.getMessage());
                jsonFeed = sb.toString();
                twitterText.setText(search);
                e.printStackTrace();
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Enter search");

        android.support.v7.widget.SearchView.OnQueryTextListener queryTextListener =new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.startsWith("#")){
                    search=query.substring(3);
                }
                else {
                    search=query;
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
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
        if (id == R.id.menu_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
