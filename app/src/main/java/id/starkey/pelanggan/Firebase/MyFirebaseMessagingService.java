package id.starkey.pelanggan.Firebase;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import id.starkey.pelanggan.Kunci.TrxKunci.TrxKunciActivity;
import id.starkey.pelanggan.Kunci.WaitingKunci.WaitingKunciActivity;
import id.starkey.pelanggan.LiveRatting.LiveRattingActivity;
import id.starkey.pelanggan.MainActivity;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.Stempel.TrxStempel.TrxStempelActivity;
import id.starkey.pelanggan.Stempel.WaitingStempel.WaitingStempelActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dani on 3/19/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "fcmmessage";
    private JSONObject jsonObjectNotif;
    private String sTitleNotif, sBodyNotif;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }


            String statusMessage = remoteMessage.getData().get("statusMessage");
            Log.d("statusMessageFb", statusMessage);
            if (statusMessage.equals("transaction_accepted")){
                //open activity
                Intent intentTrx = new Intent(MyFirebaseMessagingService.this, TrxKunciActivity.class);
                String detailMitra = remoteMessage.getData().get("transaction");
                intentTrx.putExtra("message", detailMitra);
                //intentTrx.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentTrx.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentTrx);
                WaitingKunciActivity.getInstance().finishAffinity();
            } else if (statusMessage.equals("transaction_cancelled")){
                Intent intentMain = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                //intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentMain);
                //WaitingKunciActivity.getInstance().finish();
                //Toast.makeText(this, "Mitra telah membatalkan pesanan, harap order kembali", Toast.LENGTH_SHORT).show();
                MainActivity.isBatalByMitra = true;
                TrxKunciActivity.mitraState = 9;
                TrxKunciActivity.getInstance().finish();
            } else if (statusMessage.equals("transaction_failed_mitra_not_found")){//tidak ada mitra yang ambil orderan kunci
                WaitingKunciActivity.getInstance().finish();

            } else if (statusMessage.equals("stempel_transaction_failed_mitra_not_found")){// tidak ada mitra yang ambil orderan stempel
                WaitingStempelActivity.getInstance().finish();
            } else if (statusMessage.equals("stempel_transaction_accepted")){ // request diterima mitra stempel
                Intent intentTrxStempel = new Intent(MyFirebaseMessagingService.this, TrxStempelActivity.class);
                String detailMitra = remoteMessage.getData().get("transaction");
                intentTrxStempel.putExtra("messageStempel", detailMitra);
                intentTrxStempel.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentTrxStempel);
                WaitingStempelActivity.getInstance().finishAffinity();

            } else if (statusMessage.equals("stempel_transaction_cancelled")){//trx stempel cancelled by mitra
                Intent toMain = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                toMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toMain);
                TrxStempelActivity.mitraState = 9;
                TrxStempelActivity.getInstance().finish();

            } else if (statusMessage.equals("stempel_transaction_finish")){//request trx stempel finish
                //Intent toMain = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                Intent toMain = new Intent(MyFirebaseMessagingService.this, LiveRattingActivity.class);
                String payloadstemp = remoteMessage.getData().get("transaction");
                Log.d("payloadstempelselesai", payloadstemp);
                toMain.putExtra("payloadtransaksiselesai", payloadstemp);
                toMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toMain);
                TrxStempelActivity.getInstance().finish();

            } else {//request trx kunci finish
                //Intent intentMain = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                //intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Intent intentMain = new Intent(MyFirebaseMessagingService.this, LiveRattingActivity.class);
                String payload = remoteMessage.getData().get("transaction");
                Log.d("payloadkunciselesai", payload);
                intentMain.putExtra("payloadtransaksiselesai", payload);
                intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentMain);
                //WaitingKunciActivity.getInstance().finish();
                TrxKunciActivity.getInstance().finish();
            }


        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        //notifyUser(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());
        try {
            String stringify = remoteMessage.getData().get("notification");
            jsonObjectNotif = new JSONObject(stringify);
            sTitleNotif = jsonObjectNotif.getString("title");
            sBodyNotif = jsonObjectNotif.getString("body");
            //Log.d("bodynya", sBodyNotif);
        } catch (JSONException ex){

        }
        sendNotification(remoteMessage.getFrom(), sBodyNotif);
    }

    private void sendNotification (String from, String notification){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra("keyID",idorder);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                // .setLargeIcon(image)
                //.setSmallIcon(R.mipmap.ic_launcher)
                .setSmallIcon(R.mipmap.ic_starkey_customer_curved)
                .setContentTitle("Starkey")
                .setContentText(notification)
                //     .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public void notifyUser(String from, String notification){
        MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
        myNotificationManager.showNotification(from, notification, new Intent(getApplicationContext(), MainActivity.class));
    }
}
