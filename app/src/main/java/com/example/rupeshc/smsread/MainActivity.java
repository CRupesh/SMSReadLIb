package com.example.rupeshc.smsread;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final String OTP_REGEX = "[0-9]{1,6}";
    public static final int PERMISSIONS_CODE = 1;
    TextView textView;
    Button btnDeleteSMS;

    AES encrptDcrpt;
    String otp="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.iText);
        btnDeleteSMS = (Button) findViewById(R.id.btnDeleteSMS);

        textView.setText("" +
                "Sender: \n\n" +
                "MessAge: \n\n"+
                "OTP: "
        );

        askForPermission();

        Util util = new Util();
        Util.SmsReceiver.bindListener(new Util.SmsListener() {
            @Override
            public void messageReceived(String messageText, String sender) {
                try {
                    messageText = encrptDcrpt.decrypt("pass@321",messageText);//encrptDcrpt.decrypt(messageText,"pass@321");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.e("Message",messageText);
                // If your OTP is six digits number, you may use the below code
//                Pattern pattern = Pattern.compile(OTP_REGEX);
//                Matcher matcher = pattern.matcher(messageText);
//                while (matcher.find())
//                {otp = matcher.group();}

                otp = decryptOTP(messageText);

                textView.setText("" +
                        "Sender: "+ sender +"\n\n" +
                        "MessAge: "+ messageText +"\n\n"+
                        "OTP: "+otp
                );
            }
        });
//        SmsReceiver.bindListener(new SmsListener() {
//            @Override
//            public void messageReceived(String messageText, String sender) {
//
//
//            }
//        });


    }

    private void askForPermission() {
        // Here, thisActivity is the current activity
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_SMS},
                PERMISSIONS_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_SMS)) {

                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "permission granted", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
    public String decryptOTP(String Msg){
        String [] splited = Msg.split("\\s+");
        String strOTP="";
        for (int i=0; i<splited.length; i++){
            if (splited[i].contains("&#")){
                strOTP = encrptDcrpt.decrypt("pass@321",splited[i].split("#")[1]);
            }
        }
        return strOTP;
    }

    public void deleteSMS(Context context, String message, String number) {
        try {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = context.getContentResolver().query(uriSms,
                    new String[] { "_id", "thread_id", "address",
                            "person", "date", "body" }, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);

                    if (message.equals(body) && address.equals(number)) {
                        Log.e("","Deleting SMS with id: " + threadId);
                        context.getContentResolver().delete(
                                Uri.parse("content://sms/" + id), null, null);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e("","Could not delete SMS from inbox: " + e.getMessage());
        }
    }
}
