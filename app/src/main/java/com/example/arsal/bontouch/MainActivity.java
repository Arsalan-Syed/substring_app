package com.example.arsal.bontouch;

import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private class GetDictionaryTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder sb = new StringBuilder();

            try {
                url = new URL("http://runeberg.org/words/ss100.txt");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                Reader reader = new BufferedReader(new InputStreamReader(in));
                int c = 0;
                while((c=reader.read())!= -1) {
                    sb.append((char) c);
                }
            } catch (IOException e) {
                e.printStackTrace(); //TODO change to give error in app not console
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }
            return sb.toString();
        }

        protected void onPostExecute(String result) {
            TextView textView = findViewById(R.id.text_view);
            textView.setText(result);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GetDictionaryTask().execute();
    }




    /*
    Optimization: check if a is in a dict word,
    then check if ab is in that same word, then abc,...
     */

    public void search(View view){
        EditText editText = findViewById(R.id.editText);
        String searchWord = editText.getText().toString();
        ArrayList<String> substrings = findAllSubstrings(searchWord);

        StringBuilder sb = new StringBuilder();
        for(String str: substrings){
            sb.append(str+"\n");
        }

        TextView textView = findViewById(R.id.text_view);
        textView.setText(sb.toString());
    }

    public static ArrayList<String> findAllSubstrings(String s){
        ArrayList<String> substrings = new ArrayList<>();
        int length = s.length();

        for(int i=length-1;i>=0;i--){
            int numWords = length-i;
            int start = 0;
            int end = i;

            for(int j=0;j<numWords;j++){
                substrings.add(s.substring(start,end+1));
                start += 1;
                end += 1;
            }
        }

        return substrings;
    }

}
