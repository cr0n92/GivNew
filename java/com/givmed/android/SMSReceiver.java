package com.givmed.android;

/**
 * Created by agroikos on 29/12/2015.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;



/**
 * Created by Ravi on 09/07/15.
 */
public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = SMSReceiver.class.getSimpleName();
    private boolean old_user = false;

    @Override
    public void onReceive(Context context, Intent intent) {



        SmsMessage smsMessage;

        if(Build.VERSION.SDK_INT>=19) { //KITKAT

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
        if (!senderAddress.toLowerCase().contains(Config.SMS_ORIGIN.toLowerCase())) {
            return;
        }

        // verification code from sms
        String verificationCode = getVerificationCode(message);

        Log.e(TAG, "OTP received: " + verificationCode);

        Intent httpIntent = new Intent(context, VerifyService.class);
        httpIntent.putExtra("otp", verificationCode);
        if (old_user)
            httpIntent.putExtra("oldUser", "yes");
        else
            httpIntent.putExtra("oldUser", "no");


        context.startService(httpIntent);


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
        int index = message.indexOf(Config.OTP_DELIMITER);

        String arr[] = message.split(" ", 2);

        String firstWord = arr[0];

        if (firstWord.equals("Χαιρόμαστε"))
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