package softpatrol.drinkapp.api;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Getter extends AsyncTask<String, Void, ResponsePair> {

    private Analyzer analyzer;
    private ArrayList<Pair<String, String>> headers;

    private List<NameValuePair> urlParameters;


    public Getter(Analyzer analyzer, ArrayList<Pair<String, String>> headers) {
        this.analyzer = analyzer;
        this.headers = headers;
    }


    public Getter(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Getter(Analyzer analyzer, List<NameValuePair> nvps) {
        this.analyzer = analyzer;
        this.urlParameters = nvps;
    }

    public Getter(Analyzer analyzer,NameValuePair nvp) {
        this.analyzer = analyzer;
        this.urlParameters = new ArrayList<>();
        this.urlParameters.add(nvp);
    }

    public Getter(Analyzer analyzer,NameValuePair nvp,ArrayList<Pair<String, String>> headers) {
        this.analyzer = analyzer;
        this.headers = headers;
        this.urlParameters = new ArrayList<>();
        this.urlParameters.add(nvp);
    }


    @Override
    protected ResponsePair doInBackground(String... params) {
        String src = params[0];

        String result;

        // Client-side HTTP transport library
        HttpClient httpClient = new DefaultHttpClient();

        // using POST method
        HttpGet httpGetRequest = new HttpGet(src);
        if(headers != null) for(Pair<String, String> header : headers) httpGetRequest.addHeader(header.first , header.second);

        try {
            //TODO: WTF happened to multipartEntityBuilder?

            if (urlParameters != null) {
                String url = httpGetRequest.getURI().toASCIIString();

                for (int i = 0; i < urlParameters.size(); i++) {
                    if (i == 0) {
                        url += "?";
                    }
                    url += urlParameters.get(i).toString();
                    if (i < urlParameters.size()-1) {
                        url += "&";
                    }
                }
                httpGetRequest.setURI(URI.create(url));
            }
            //if(postParameters != null) httpPostRequest.setEntity(new UrlEncodedFormEntity(postParameters));

            // Execute POST request to the given URL
            HttpResponse httpResponse = httpClient.execute(httpGetRequest);

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