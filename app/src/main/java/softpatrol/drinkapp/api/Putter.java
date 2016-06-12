package softpatrol.drinkapp.api;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Putter extends AsyncTask<String, Void, ResponsePair> {

    private Analyzer analyzer;
    private MultipartEntityBuilder multipartEntityBuilder;

    public Putter(Analyzer analyzer, MultipartEntityBuilder multipartEntityBuilder) {
        this.analyzer = analyzer;
        this.multipartEntityBuilder = multipartEntityBuilder;
    }


    @Override
    protected ResponsePair doInBackground(String... params) {
        String src = params[0];

        String result;

        // Client-side HTTP transport library
        HttpClient httpClient = new DefaultHttpClient();

        // using PUT method
        HttpPut httpPostRequest = new HttpPut(src);
        try {

            httpPostRequest.setEntity(multipartEntityBuilder.build());

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
            if (result.getStatus() == 200)
                analyzer.analyzeData(new JSONObject(result.getResult()).getJSONObject("result"));
            else if (result.getStatus() == 400)
                analyzer.analyzeError(new JSONObject(result.getResult()).getJSONObject("error"));
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

