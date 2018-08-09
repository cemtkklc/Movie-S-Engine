package mobile.cem.moviesengine;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class SearchResults extends ListActivity {
    JSONArray movies;
    ArrayList<HashMap<String,String>> movieInfo;
    private ProgressDialog progressDialog;
    private static final String TAG_TITLE = "original_title";
    private static final String TAG_DATE = "release_date";
    private static final String TAG_VOTE = "vote_average";
    private static final String TAG_ID = "id";
    public static final String ID = "id";

    private static final String URL_API = "http://api.themoviedb.org/3/search/movie?api_key=";
    private static final String API_KEY = "7c6b6a28edae1eee9814fee611ef3ab9";
    public String movieName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list);
        ListView listView = getListView();
        movieInfo = new ArrayList<HashMap<String, String>>();
        Intent intent = getIntent();
        movieName = intent.getStringExtra(MainActivity.MovieName);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idofMovie = ((TextView) findViewById(R.id.id)).getText().toString();
                Intent intent1 = new Intent(getApplicationContext(),SuggestedMovies.class);
                intent1.putExtra(ID,idofMovie);
                startActivity(intent1);

            }
        });


        new GetMovies().execute();

    }
    private class GetMovies extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SearchResults.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please Wait..");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(URL_API);
            stringBuilder.append(API_KEY);
            String movieName_nospace = movieName.replaceAll("\\s+","%20");
            stringBuilder.append("&query=" + "\"" + movieName_nospace + "\"");
            //String urlString = URL_API + API_KEY + "&query=" + movieName;
            InputStream inInputStream = null;
            try{
                URL url = new URL(stringBuilder.toString());
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.addRequestProperty("Accept","application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                int responsecode = httpURLConnection.getResponseCode();
                Log.d("Code error!","The response code is:" + responsecode + " " + httpURLConnection.getResponseMessage());
                inInputStream = httpURLConnection.getInputStream();
                String jsonString = getString(inInputStream);
                if(jsonString != null){
                    JSONObject jsonObject = new JSONObject(jsonString);
                    movies = jsonObject.getJSONArray("results");
                    for (int i = 0; i<movies.length();i++){
                        JSONObject jSon = movies.getJSONObject(i);
                        String title = jSon.getString(TAG_TITLE);
                        String date = jSon.getString(TAG_DATE);
                        int id = jSon.getInt(TAG_ID);
                        double vote_average = jSon.getDouble(TAG_VOTE);
                        String idS = Integer.toString(id);
                        String vote_averages = Double.toString(vote_average);
                        HashMap<String,String>  movieHash  = new HashMap<String,String>();
                        movieHash.put(TAG_ID,idS);
                        movieHash.put(TAG_TITLE,title);
                        movieHash.put(TAG_DATE,date);
                        movieHash.put(TAG_VOTE,vote_averages);
                        movieInfo.add(movieHash);

                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }



            return null;
        }

        public String getString (InputStream inpInputStream) throws IOException,UnsupportedEncodingException {

                Reader reader = null;
                reader = new InputStreamReader(inpInputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(reader);
                return bufferedReader.readLine();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }


            ListAdapter listAdapter = new SimpleAdapter(SearchResults.this,movieInfo,R.layout.items_list, new String[]{TAG_TITLE,TAG_DATE,TAG_VOTE,TAG_ID},new int[]{R.id.title,R.id.release_date,R.id.vote,R.id.id});
            setListAdapter(listAdapter);
        }
    }



}
