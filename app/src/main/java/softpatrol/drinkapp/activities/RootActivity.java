package softpatrol.drinkapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import softpatrol.drinkapp.api.Analyzer;
import softpatrol.drinkapp.api.Definitions;
import softpatrol.drinkapp.api.Getter;
import softpatrol.drinkapp.api.Poster;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.database.models.account.Account;
import softpatrol.drinkapp.util.Debug;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class RootActivity extends BaseActivity {
    BaseActivity parent;
    public final static int MAIN_ACTIVITY = 1;

    public static int displayWidth = 0;
    public static int displayHeight = 0;
    public static final String ACCOUNT_DIR="acc";

    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/SimpleAndroidOCR/";
    public static final String LANGUAGE = "eng";

    public void changeToMainActivity(boolean b){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("firstTime", b);
        startActivityForResult(intent, MAIN_ACTIVITY);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Debug.debugMessage(this, "onResult...");
        switch (requestCode) {
            case MAIN_ACTIVITY:
                Debug.debugMessage(this, "- fragment_tag...");
                if (resultCode == RESULT_OK){

                }
                else if(resultCode == RESULT_CANCELED){
                    Debug.debugMessage(this, "- logged out");

                }
                else{

                }
                break;
            default:
                Log.d("DBG", "THE END LOGIN ACTIVITY");
                break;
        }
    }

    static int PERMISSION_CHECK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.ACTIVITY_NAME = "Root";
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        displayWidth = display.getWidth();
        displayHeight = display.getHeight();
        parent = this;

        //TODO: When commenting out the reset look below for next TODO!!!!
        DatabaseHandler db = DatabaseHandler.getInstance(this);
        //db.onUpgrade(db.getWritableDatabase(), 1, 2);

        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Debug.debugMessage(this, "ERROR: Creation of directory " + path + " on sdcard failed");
                } else {
                    Debug.debugMessage(this, "Created directory " + path + " on sdcard");
                }
            }

        }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization
        if (!(new File(DATA_PATH + "tessdata/" + LANGUAGE + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + LANGUAGE + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + LANGUAGE + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Debug.debugMessage(this, "Copied " + LANGUAGE + " traineddata");
            } catch (IOException e) {
                Debug.debugMessage(this, "Was unable to copy " + LANGUAGE + " traineddata " + e.toString());
            }
        }



        ArrayList<String> permissionRequests = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionRequests.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionRequests.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionRequests.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissionRequests.add(Manifest.permission.INTERNET);
        }
        if(permissionRequests.size() != 0) {
            String[] permissionRequestArray = new String[permissionRequests.size()];
            for(int i = 0;i<permissionRequests.size();i++) permissionRequestArray[i] = permissionRequests.get(i);
            ActivityCompat.requestPermissions(this, permissionRequestArray, PERMISSION_CHECK);
        }
        else {
            Long currentAccountId = DatabaseHandler.getCurrentAccount(this);
            //First time user
            if(currentAccountId == -1) { //TODO: Remove true when database stops resetting
//                MultipartEntityBuilder multiPartEntityBuilder = MultipartEntityBuilder.create();
//                multiPartEntityBuilder.setCharset(Charset.forName("UTF-8"));
                Debug.debugMessage(parent, "attempting to get account");
                new Poster(new AccountCreation(this)).execute(Definitions.CREATE_MOBILE);
            }
            else {
                Debug.debugMessage(parent, db.getAccount(DatabaseHandler.getCurrentAccount(parent)).toString());
                changeToMainActivity(true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(this != null) {
                        changeToMainActivity(true);
                    }
                } else {
                    Toast.makeText(this, "PRESS OK YOU RETARD", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    private class AccountCreation extends Analyzer {
        public AccountCreation(Activity activity) { super(activity); }
        @Override
        public void analyzeData(JSONObject result) throws JSONException {
            Debug.debugMessage(parent, "attempting to get account");
            JSONObject account = result.getJSONObject("data").getJSONObject("account");
            Account acc = new Account(account);
            DatabaseHandler db = DatabaseHandler.getInstance(parent);
            db.addAccount(acc);
            DatabaseHandler.setCurrentAccount(db.getAccount(acc.getAlias()).getId(), parent);
            Debug.debugMessage(parent, db.getAccount(DatabaseHandler.getCurrentAccount(parent)).toString());
            changeToMainActivity(true);
        }
    }
}
