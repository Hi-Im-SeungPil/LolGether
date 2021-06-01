package org.jeonfeel.withlol2.DTO;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetLoLId extends AsyncTask<String,Void, JSONObject> {

    JSONObject json_userId;

    @Override
    protected void onPreExecute(){super.onPreExecute();}

    @Override
    protected JSONObject doInBackground(String... strings) {

        try{
            URL url = new URL(strings[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            StringBuffer builder = new StringBuffer();

            String inputString = null;
            while((inputString = bufferedReader.readLine()) != null){
                builder.append(inputString);
            }

            String s = builder.toString();
            json_userId = new JSONObject(s);

            conn.disconnect();
            bufferedReader.close();
            inputStream.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json_userId;
    }
    @Override
    protected void onPostExecute(JSONObject result){super.onPostExecute(result);}

    @Override
    protected  void onCancelled(){super.onCancelled();}
}
