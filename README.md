# SMSReadLIb
Reading the SMS (useful for reading OTP)
Steps to read SMS Code

1. add compile 'com.github.CRupesh:SMSReadLIb:0.1.1' in app build.gradle

2. Permissions
    <uses-permission android:name="android.permission.RECEIVE_SMS" />
    <uses-permission android:name="android.permission.READ_SMS" />
    <uses-permission android:name="android.permission.SEND_SMS" />

3. Subscribe SMSUtil.SmsReceiver BroadcastReceiver in either Manifest or in code

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mybroadcast, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mybroadcast);
    }


4. dont forget to unsubscribe the receiver if application gone background

5. finally ,

	SMSUtil.SmsReceiver.bindListener(new SMSUtil.SmsListener() {
            @Override
            public void messageReceived(String s, String s1) {
                text.setText("SMS Sender: "+s1+"\n\n SMS Text:"+s);
            }
        });
