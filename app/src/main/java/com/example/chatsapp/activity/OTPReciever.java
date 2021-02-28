package com.example.chatsapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.chaos.view.PinView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OTPReciever extends BroadcastReceiver {

    private static PinView pinView;

    public void setPinView(PinView pinView) {
        OTPReciever.pinView = pinView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage sms : smsMessages) {
            String message = sms.getMessageBody();
            Log.d("DUCKHANH", message);
            Pattern pattern = Pattern.compile("(\\d\\d\\d\\d\\d\\d)");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String OTP = message.substring(matcher.start(), matcher.end());
                Log.d("DUCKHANH", OTP);
                pinView.setText(OTP);
            }

        }
    }
}
