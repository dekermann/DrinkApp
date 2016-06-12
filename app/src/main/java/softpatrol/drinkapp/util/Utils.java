package softpatrol.drinkapp.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ignored){}
    }
    public static float toDp(Context context, int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    public static double CalculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        /*
            Haversine formula:
            A = sin²(Δlat/2) + cos(lat1).cos(lat2).sin²(Δlong/2)
            C = 2.atan2(√a, √(1−a))
            D = R.c
            R = radius of earth, 6371 km.
            All angles are in radians
            */

        double deltaLatitude = Math.toRadians(Math.abs(latitude1 - latitude2));
        double deltaLongitude = Math.toRadians(Math.abs(longitude1 - longitude2));
        double latitude1Rad = Math.toRadians(latitude1);
        double latitude2Rad = Math.toRadians(latitude2);

        double a = Math.pow(Math.sin(deltaLatitude / 2), 2) +
                (Math.cos(latitude1Rad) * Math.cos(latitude2Rad) * Math.pow(Math.sin(deltaLongitude / 2), 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c * 1000; //Distance in meters

    }

    public static boolean IsNullOrEmpty(String text) {
        return text == null || text.length() == 0;
    }

    private static String parseMillis(long millis){
        long time = millis/1000;
//        Log.d("DBG", "Converted to seconds: " + time);
        if (time<60){
            return time + " second"+ (time==1 ? "" : "s");
        }
        time = time/60;
//        Log.d("DBG", "Converted to minutes: " + time);
        if (time<60){
            return time + " m";
        }
        time = time/60;
//        Log.d("DBG", "Converted to hours: " + time);
        if (time<24){
            return time + " hour"+ (time==1 ? "" : "s");
        }
        time = time/24;
//        Log.d("DBG", "Converted to days: " + time);
        if (time<30){
            return time + " day"+ (time==1 ? "" : "s");
        }
        time = time/30;
//        Log.d("DBG", "Converted to months: " + time);
        if (time<12){
            return time + " month"+ (time==1 ? "" : "s");
        }
        else{
            return time/12 + " year"+ (time==1 ? "" : "s");
        }
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515; //Miles
        if (unit == 'K') {
            //Kilometers
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            //Nautical Miles
            dist = dist * 0.8684;
        }
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }



    public static boolean makeFolder() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/drinkapp");
        return folder.exists() || folder.mkdir();
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    public static int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    public static void setFontForAllTextViewsInHierarchy(ViewGroup aViewGroup, Typeface aFont) {
        for (int i=0; i<aViewGroup.getChildCount(); i++) {
            View _v = aViewGroup.getChildAt(i);
            if (_v instanceof TextView) {
                ((TextView) _v).setTypeface(aFont);
            } else if (_v instanceof ViewGroup) {
                setFontForAllTextViewsInHierarchy((ViewGroup) _v, aFont);
            }
        }
    }
    public static String concatenate(ArrayList<Long> values) {
        String concatenatedValues = "";
        for(Long s : values) concatenatedValues += s + ",";
        return concatenatedValues;
    }
    public static ArrayList<Long> split(String concatenatedValues) {
        ArrayList<Long> retValues = new ArrayList<>();
        String[] strings = concatenatedValues.split(",");
        try {
            for (String s : strings) retValues.add(Long.parseLong(s));
        } catch (Exception ignored) {}
        return retValues;
    }
}