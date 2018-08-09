package mobile.cem.moviesengine;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.net.URL;
import java.util.HashMap;


public class SuggestedMovies extends Activity {
    HashMap<String,String> hashMap;
    JSONArray jsonArray;
    Button button;
    int[]ids;
    int i = 0;
    TextView textView;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView tv5;
    TextView tv7;
    TextView tv9;
    TextView ltv;
    ProgressBar progressBar;
    ImageView imageView;
    private ProgressDialog progressDialog;
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "original_title";
    private static final String TAG_DATE ="release_date";
    private static final String TAG_IMAGE_PATH ="poster_path";
    private static final String TAG_VOTE = "vote_average";
    private static final String TAG_OVERVIEW = "overview";
    private static final String TAG_STATUS = "status";
    public static final String url = "http://api.themoviedb.org/3/movie/";
    public static final String API = "7c6b6a28edae1eee9814fee611ef3ab9";
    public String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggested_list_movies);
        button = (Button) findViewById(R.id.button2);
        textView = (TextView) findViewById(R.id.textView4);
        textView1 = (TextView) findViewById(R.id.textView6);
        textView2 = (TextView) findViewById(R.id.textView8);
        textView3 = (TextView) findViewById(R.id.textView10);
        textView4 = (TextView) findViewById(R.id.textView11);
        textView4.setMovementMethod(new ScrollingMovementMethod());
        ltv = (TextView) findViewById(R.id.textView12);
        tv5 = (TextView) findViewById(R.id.textView5);
        tv7 = (TextView) findViewById(R.id.textView7);
        tv9 = (TextView) findViewById(R.id.textView9);
        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        setInvisibleUI();
        Intent intent = getIntent();
        id = intent.getStringExtra(SearchResults.ID);
        new GetSimilarMovies().execute();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibleUI();
                if(i<ids.length){
                    String s_id = Integer.toString(ids[i]);
                    new GetSingleMovie().execute(s_id);
                    i++;
                }else{
                    i = 0;

                }


            }
        });


    }
    class GetSimilarMovies extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SuggestedMovies.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
           try {
               StringBuilder stringBuilder = new StringBuilder();
               stringBuilder.append("http://api.themoviedb.org/3/movie/");
               stringBuilder.append(id + "/similar?api_key=");
               stringBuilder.append(API);
               URL url = new URL(stringBuilder.toString());
               InputStream inInputStream = null;
               HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
               httpURLConnection.setRequestMethod("GET");
               httpURLConnection.setReadTimeout(10000);
               httpURLConnection.setConnectTimeout(15000);
               httpURLConnection.addRequestProperty("Accept", "application/json");
               httpURLConnection.setDoInput(true);
               httpURLConnection.connect();
               inInputStream = httpURLConnection.getInputStream();
               String jsonString = getString(inInputStream);
               if(jsonString != null){
                   JSONObject jsonObject = new JSONObject(jsonString);
                   jsonArray = jsonObject.getJSONArray("results");
                   ids = new int[jsonArray.length()];
                   for(int i = 0;i <jsonArray.length();i++){
                       JSONObject jSon = jsonArray.getJSONObject(i);
                       int id = jSon.getInt(TAG_ID);
                       ids[i] = id;

                   }


               }

           }catch (Exception e){

           }finally {

           }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }
    class GetSingleMovie extends AsyncTask<String,Void,HashMap<String,String>>{

        @Override
        protected HashMap<String, String> doInBackground(String... params) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://api.themoviedb.org/3/movie/" +params[0]);
            stringBuilder.append("?api_key=7c6b6a28edae1eee9814fee611ef3ab9");
            InputStream inpInputStream;
            try {
                URL url = new URL(stringBuilder.toString());
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                inpInputStream = httpURLConnection.getInputStream();
                String jsonArray = getString(inpInputStream);
                JSONObject jsonObject = new JSONObject(jsonArray);
                String title = jsonObject.getString(TAG_TITLE);
                String releasedate = jsonObject.getString(TAG_DATE);
                String imagepath = jsonObject.getString(TAG_IMAGE_PATH);
                String overview = jsonObject.getString(TAG_OVERVIEW);
                Double vote_double = jsonObject.getDouble(TAG_VOTE);
                String vote = Double.toString(vote_double);
                String status = jsonObject.getString(TAG_STATUS);
                String image_url = "https://image.tmdb.org/t/p/w185" + imagepath;
                hashMap = new HashMap<String,String>();
                hashMap.put(TAG_TITLE,title);
                hashMap.put(TAG_IMAGE_PATH,image_url);
                hashMap.put(TAG_DATE,releasedate);
                hashMap.put(TAG_IMAGE_PATH,image_url);
                hashMap.put(TAG_STATUS,status);
                hashMap.put(TAG_OVERVIEW,overview);
                hashMap.put(TAG_VOTE,vote);
                return  hashMap;
            }catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> stringStringHashMap) {
            super.onPostExecute(stringStringHashMap);
            textView.setText(stringStringHashMap.get(TAG_TITLE));
            textView1.setText(stringStringHashMap.get(TAG_DATE));
            textView2.setText(stringStringHashMap.get(TAG_VOTE));
            textView3.setText(stringStringHashMap.get(TAG_STATUS));
            textView4.setText(stringStringHashMap.get(TAG_OVERVIEW));
            new LoadBitmap(imageView).execute(stringStringHashMap.get(TAG_IMAGE_PATH));
        }
    }
    public void setInvisibleUI(){
        textView.setVisibility(View.INVISIBLE);
        textView1.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        textView3.setVisibility(View.INVISIBLE);
        textView4.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        tv5.setVisibility(View.INVISIBLE);
        tv7.setVisibility(View.INVISIBLE);
        tv9.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

    }
    public void setVisibleUI(){
        textView.setVisibility(View.VISIBLE);
        textView1.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);
        textView3.setVisibility(View.VISIBLE);
        textView4.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        tv5.setVisibility(View.VISIBLE);
        tv7.setVisibility(View.VISIBLE);
        tv9.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        ltv.setVisibility(View.INVISIBLE);
    }
    public String getString (InputStream inpInputStream) throws IOException,UnsupportedEncodingException {

        Reader reader = null;
        reader = new InputStreamReader(inpInputStream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        return bufferedReader.readLine();

    }
    class LoadBitmap extends AsyncTask<String,Void,Bitmap>{
        ImageView imageView;
        public LoadBitmap(ImageView imageView){
            this.imageView = imageView;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageurl = params[0];
            Bitmap bitmap =null;
            try {
                InputStream inpInputStream = new URL(imageurl).openStream();
                bitmap = BitmapFactory.decodeStream(inpInputStream);

            }catch (Exception e){
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressBar.setEnabled(false);
            progressBar.setVisibility(View.GONE);
            imageView.setImageBitmap(bitmap);
        }
    }


}
