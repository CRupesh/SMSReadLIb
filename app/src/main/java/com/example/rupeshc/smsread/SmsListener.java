package com.example.rupeshc.smsread;

/**
 * Created by Rupesh C on 14-11-2017.
 */

public interface SmsListener {

    public void messageReceived(String messageText, String sender);
}
