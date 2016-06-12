package softpatrol.drinkapp.api;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Deleter extends AsyncTask<String, Void, ResponsePair> {

    private Analyzer analyzer;

    public Deleter(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public void setAnalyzer(Analyzer analyzer){
        this.analyzer = analyzer;
    }

    @Override
    protected ResponsePair doInBackground(String... params) {

        String src = params[0];

        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("DELETE");
            connection.connect();
            int status = connection.getResponseCode();
            Log.d("DBG", "Getter: status: " + status + " URL: " + src);
            if(status != 200 && status != 400)
                return null;

            BufferedReader input;
            if(status == 200) input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            else input = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                sb.append(line);
                Log.d("DBG", "Getter: got: " + line);
            }
            return new ResponsePair(status,sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(ResponsePair result) {
        if (result == null)
            return;
        try{
            if (result.getStatus() == 200) {
                analyzer.analyzeData(new JSONObject(result.getResult()).getJSONObject("result"));
            }

            else if (result.getStatus() == 400) {
                analyzer.analyzeError(new JSONObject(result.getResult()).getJSONObject("error"));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}