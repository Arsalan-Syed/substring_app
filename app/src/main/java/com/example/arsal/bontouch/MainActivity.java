package com.example.arsal.bontouch;

import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Arsalan Syed on 13th October 2018
 *
 * Basic app that allows you to search for all words containing
 * a certain input word. It searches through a dictionary that is
 * downloaded once the app starts. It downloads the dictionary and
 * searches through it in separate threads to avoid slowing down
 * the main UI thread.
 */

public class MainActivity extends AppCompatActivity {

    // Used for storing the entire dictionary once downloaded
    private ArrayList<String> dictionary;

    // Displays messages about query results
    TextView debugMessage;

    // Will contain all words from our search query
    TextView foundWordsText;

    //Asynchronous task for downloading the dictionary
    private class GetDictionaryTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            dictionary = new ArrayList<>();

            debugMessage.setText(R.string.download);

            try {
                url = new URL("http://runeberg.org/words/ss100.txt");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Reader reader = new BufferedReader(new InputStreamReader(in));

                // Read the result of the GET request and add words to dictionary
                String line;
                while((line = ((BufferedReader) reader).readLine()) != null){
                    dictionary.add(line);
                }
            } catch (IOException e) {
                debugMessage.setText(R.string.fail_download);
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            if(dictionary.size()==0){
                debugMessage.setText(R.string.fail_download);
            } else {
                debugMessage.setText(R.string.dictionary_downloaded);
            }
        }

    }

    //Asynchronous task for performing a search query
    private class SearchDictionaryTask extends AsyncTask<String,Void,String>{

        int numWordsFound = 0;

        @Override
        protected String doInBackground(String... strings) {
            String searchWord = strings[0];

            //Get all words from dictionary containing searchWord
            Set<String> result = findWords(searchWord);

            //Sort our result
            List<String> sortedList = new ArrayList(result);
            Collections.sort(sortedList);

            numWordsFound = result.size();

            StringBuilder sb = new StringBuilder();
            for(String str: sortedList){
                sb.append(str).append("\n");
            }

            return sb.toString();
        }

        protected void onPostExecute(String result){
            debugMessage.setText(String.format("Search done, found %d words", numWordsFound));
            foundWordsText.setText(result);
        }
    }

    /**
     * Called when the app is intialized
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debugMessage = findViewById(R.id.text_view);
        foundWordsText = findViewById(R.id.textView2);

        new GetDictionaryTask().execute();
    }

    /**
     * Called when the button is clicked
     * @param view
     */
    public void onSubmit(View view){

        // Get the widget reference from XML layout
        ConstraintLayout layout = findViewById(R.id.main_layout);
        EditText editText = findViewById(R.id.editText);

        debugMessage.setText(R.string.searching);
        foundWordsText.setText("");

        // Can't do a search
        if(dictionary.size() == 0){
            debugMessage.setText(R.string.dictionary_empty);
            return;
        }

        //Extract the query parameter
        String searchWord = editText.getText().toString();

        if(searchWord.length()==0){
            debugMessage.setText(R.string.empty_query);
            return;
        }

        new SearchDictionaryTask().execute(searchWord);
    }

    /**
     * Finds all words in the dictionary that contain the input string
     */
    private Set<String> findWords(String word){
        Set<String> result = new HashSet<>();

        for(String dictWord:dictionary){
            if(dictWord.contains(word)){
                result.add(dictWord);
            }
        }

        return result;
    }
}
