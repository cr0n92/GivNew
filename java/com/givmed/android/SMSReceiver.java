package com.givmed.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;


public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = SMSReceiver.class.getSimpleName();
    private boolean old_user = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage smsMessage;
        final Context contexto = context;

        if(Build.VERSION.SDK_INT >= 19) { //KITKAT
            SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            smsMessage = msgs[0];
        }
        else {
            final Bundle bundle = intent.getExtras();
            Object pdus[] = (Object[]) bundle.get("pdus");

            smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
        }

        String senderAddress = smsMessage.getDisplayOriginatingAddress();
        String message = smsMessage.getDisplayMessageBody();

        Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);

        // if the SMS is not from our gateway, ignore the message
        if (!senderAddress.toLowerCase().contains(PrefManager.SMS_ORIGIN.toLowerCase())) {
            return;
        }

        // verification code from sms
        String verificationCode = getVerificationCode(message);

        Log.e(TAG, "OTP received: " + verificationCode);

        final Intent httpIntent = new Intent(context, VerifyService.class);
        httpIntent.putExtra("otp", verificationCode);
        if (old_user)
            httpIntent.putExtra("oldUser", "yes");
        else
            httpIntent.putExtra("oldUser", "no");

        new CountDownTimer(6000, 5000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                contexto.startService(httpIntent);
            }
        }.start();
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(PrefManager.OTP_DELIMITER);

        if (index == 55)
            old_user = true;

        if (index != -1) {
            int start = index + 1;
            int length = 4;
            code = message.substring(start, start + length);
            Log.e(TAG, "vrhke OTP: " + code);

            return code;
        }

        return code;
    }
}