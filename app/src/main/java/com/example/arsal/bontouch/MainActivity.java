package com.example.arsal.bontouch;

import android.content.Context;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> dictionary;
    TextView textView;
    TextView textView2;

    private class GetDictionaryTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder sb = new StringBuilder();
            dictionary = new ArrayList<>();

            TextView textView = findViewById(R.id.text_view);
            textView.setText("Downloading");

            try {
                url = new URL("http://runeberg.org/words/ss100.txt");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Reader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while((line = ((BufferedReader) reader).readLine()) != null){
                    dictionary.add(line);
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
            textView.setText("Download done");
        }
    }

    private class SearchDictionaryTask extends AsyncTask<String,Void,String>{

        int numWordsFound = 0;

        @Override
        protected String doInBackground(String... strings) {
            String searchWord = strings[0];
            ArrayList<String> substrings = findAllSubstrings(searchWord);
            Set<String> result = getAllSubstringOccurrences(substrings);
            List<String> sortedList = new ArrayList(result);
            Collections.sort(sortedList);

            numWordsFound = result.size();

            StringBuilder sb = new StringBuilder();
            for(String str: sortedList){
                sb.append(str+"\n");
            }

            return sb.toString();
        }

        protected void onPostExecute(String result){
            TextView textView = findViewById(R.id.text_view);
            textView.setText("Search done, found "+numWordsFound+" words");

            TextView textView2 = findViewById(R.id.textView2);
            textView2.setText(result);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text_view);
        textView2 = findViewById(R.id.textView2);

        new GetDictionaryTask().execute();
    }

    public void search(View view){

        // Get the widget reference from XML layout
        ConstraintLayout layout = findViewById(R.id.main_layout);
        EditText editText = findViewById(R.id.editText);

        // Set a click listener for CoordinatorLayout
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the input method manager
                InputMethodManager inputMethodManager = (InputMethodManager)
                        view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                // Hide the soft keyboard
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        });

        textView.setText("Searching");
        textView2.setText("");


        if(dictionary.size() == 0){
            textView.setText("empty");
            return;
        }

        String searchWord = editText.getText().toString();
        if(searchWord.length()==0){
            textView.setText("Query was empty");
            return;
        }

        new SearchDictionaryTask().execute(searchWord);
    }

    // TODO
    private Set<String> getAllSubstringOccurrences(ArrayList<String> substrings) {
        Set<String> result = new HashSet<>();

        for(String substring: substrings){
            for(String dictWord:dictionary){
                if(dictWord.contains(substring)){
                    result.add(dictWord);
                }
            }
        }

        return result;
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
