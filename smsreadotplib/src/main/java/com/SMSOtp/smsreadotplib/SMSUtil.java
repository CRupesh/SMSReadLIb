package com.SMSOtp.smsreadotplib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by Rupesh C on 15-11-2017.
 */

public class SMSUtil {

    public SMSUtil() {
    }

    public static class SmsReceiver extends BroadcastReceiver {

        public static SmsListener mListener;

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle data  = intent.getExtras();
            Object[] pdus = (Object[]) data.get("pdus");
            String messageBody = "";
            String sender = "";
            for(int i=0;i<pdus.length;i++){
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                sender = smsMessage.getDisplayOriginatingAddress();
                //Check the sender to filter messages which we require to read
//                if (sender.equals("+918320437295"))
//                {
//                    messageBody = messageBody + smsMessage.getMessageBody();
//                }

                messageBody = messageBody + smsMessage.getMessageBody();
            }
            mListener.messageReceived(messageBody,sender);
        }

        public static void bindListener(SmsListener listener) {
            mListener = listener;
        }
    }

    public static interface SmsListener {

        public void messageReceived(String messageText, String sender);
    }
}
