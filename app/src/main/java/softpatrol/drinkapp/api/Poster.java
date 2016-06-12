package softpatrol.drinkapp.api;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import softpatrol.drinkapp.util.Debug;

public class Poster extends AsyncTask<String, Void, ResponsePair> {

    private Analyzer analyzer;
    private ArrayList<Pair<String, String>> headers;
    private JSONObject postParameters;

    public Poster(Analyzer analyzer, JSONObject postParameters, ArrayList<Pair<String, String>> headers) {
        this.analyzer = analyzer;
        this.postParameters = postParameters;
        this.headers = headers;
    }

    public Poster(Analyzer analyzer, JSONObject postParameters) {
        this.analyzer = analyzer;
        this.postParameters = postParameters;
    }

    public Poster(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    protected ResponsePair doInBackground(String... params) {
        String src = params[0];

        String result;

        // Client-side HTTP transport library
        HttpClient httpClient = new DefaultHttpClient();

        // using POST method
        HttpPost httpPostRequest = new HttpPost(src);
        if(headers != null) for(Pair<String, String> header : headers) httpPostRequest.addHeader(header.first , header.second);

        try {
            //TODO: WTF happened to multipartEntityBuilder?

            //if(postParameters != null) httpPostRequest.setEntity(new StringEntity(postParameters.toString(), "UTF8"));
            if(postParameters != null) Log.d("NEXTWORK: ", postParameters.toString());
            if(postParameters != null) httpPostRequest.setEntity(new StringEntity(postParameters.toString()));
            //if(postParameters != null) httpPostRequest.setEntity(new UrlEncodedFormEntity(postParameters));


            // Execute POST request to the given URL
            HttpResponse httpResponse = httpClient.execute(httpPostRequest);

            int status = httpResponse.getStatusLine().getStatusCode();
            Log.d("DBG", "Poster: status:" + status);
            if(status != 200 && status != 400)
                return null;

            // receive response as inputStream
            InputStream inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

            return new ResponsePair(status,result);
        } catch (Exception e) {
            return null;
        }

        // return result;
    }


    @Override
    protected void onPostExecute(ResponsePair result) {
        if (result == null)
            return;
        try{
            Log.d("NETWORK: ", result.getResult());
            if (result.getStatus() == 200)
                analyzer.analyzeData(new JSONObject(result.getResult()).getJSONObject("result"));
            else if (result.getStatus() == 400)
                analyzer.analyzeError(new JSONObject(result.getResult()).getJSONObject("message"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}

