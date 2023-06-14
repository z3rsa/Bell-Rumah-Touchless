package com.example.bel_touchless;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.example.bel_touchless.db.AppDatabase;
import com.example.bel_touchless.db.Data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Home extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    NotificationManagerCompat mNotificationManager;
    private AppDatabase data;
    public static final String CHANNEL_1_ID = "Channel1";
    public static final String CHANNEL_NAME_1 = "Tamu";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        //Notification

        //Inisialisasi Variabel Bottom Navbar
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Set Home (selected)
        bottomNavigationView.setSelectedItemId(R.id.home);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.logs:
                    startActivity(new Intent(getApplicationContext(), Logs.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP ));
                    overridePendingTransition(0, 0);
                    Home.this.onResume();
                    return true;
                case R.id.home:
                    return true;
                case R.id.about:
                    startActivity(new Intent(getApplicationContext(), About.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    overridePendingTransition(0, 0);
                    Home.this.onResume();
                    return true;
            }
            return false;
        });

        final TextView hasilUltrasonik = findViewById(R.id.hasilUltrasonik);
        final TextView statusCN = findViewById(R.id.statusCN);
        final TextView statusNC = findViewById(R.id.statusNC);
        final TextView dataSuhu = findViewById(R.id.simpenSuhu);
        final TextView OLED = findViewById(R.id.hasilOLED);
        final TextView OLED1 = findViewById(R.id.hasilOLED1);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refUltrasonik = database.getReference("hasil_Ultrasonik");
        DatabaseReference refWiFi = database.getReference("status_Koneksi");
        DatabaseReference refSuhu = database.getReference("suhu_Badan");
        DatabaseReference refOLED = database.getReference("hasilOLED");
        DatabaseReference refOLED1 = database.getReference("hasilOLED1");
        dataSuhu.setVisibility(View.INVISIBLE);

        int warna = ContextCompat.getColor(this, R.color.myBlue);

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss a");
        String rilDate = dateFormat.format(currentTime.getTime());

        mNotificationManager = NotificationManagerCompat.from(this);

        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.yoru_ringtone);  //

        refUltrasonik.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    final Long hasil = snapshot.getValue(Long.class);
                    hasilUltrasonik.setText(hasil.toString());

                    String suhu = dataSuhu.getText().toString();
                    String nSuhu = "Bel Touchless";
                    Bitmap gambar = BitmapFactory.decodeResource(getResources(), R.drawable.bell_1096280_960_720);
                    String pesanKurang = "Ada Tamu! dengan suhu " + dataSuhu.getText().toString() + "\u2103";
                    String pesanLebih = "Terdapat tamu dengan suhu tinggi yaitu " + dataSuhu.getText().toString() + "\u2103";

                    //Mencegah nilai suhu tidak sesuai dengan database
                    if(hasil <= 15 && !suhu.equals(nSuhu) && Integer.parseInt(suhu) < 37) {

                        saveData(hasil.toString() + " cm", dataSuhu.getText().toString() + " derajat", rilDate);

                        NotificationChannel();

                        Intent intent = new Intent(Home.this, Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(Home.this, 0, intent, 0);

                        Notification builder = new NotificationCompat.Builder(Home.this, CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.bell_1096280_960_720)
                                .setLargeIcon(gambar)
                                .setSound(sound)
                                .setShowWhen(true)
                                .setContentTitle(HtmlCompat.fromHtml("<font color=\"" + warna + "\">" + "Bel Rumah Touchless" + "</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))
                                .setContentText(pesanKurang)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(pesanKurang))
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setChannelId(CHANNEL_1_ID)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .build();

                        int num = (int) System.currentTimeMillis();
                        mNotificationManager.notify(num, builder);
                    } else if (hasil <= 15 && !suhu.equals(nSuhu) && Integer.parseInt(suhu) > 37) {
                        saveData(hasil.toString() + " cm", dataSuhu.getText().toString() + " derajat", rilDate);

                        NotificationChannel();

                        Intent intent = new Intent(Home.this, Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(Home.this, 0, intent, 0);

                        Notification builder = new NotificationCompat.Builder(Home.this, CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.bell_1096280_960_720)
                                .setLargeIcon(gambar)
                                .setSound(sound)
                                .setShowWhen(true)
                                .setContentTitle(HtmlCompat.fromHtml("<font color=\"" + warna + "\">" + "Bel Rumah Touchless" + "</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))
                                .setContentText(pesanLebih)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(pesanLebih))
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setChannelId(CHANNEL_1_ID)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .build();

                        int num = (int) System.currentTimeMillis();
                        mNotificationManager.notify(num, builder);
                    }
                }

            }

            private void NotificationChannel(){

                int importance = NotificationManager.IMPORTANCE_HIGH;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    NotificationChannel channel1 = new NotificationChannel(
                            CHANNEL_1_ID,
                            CHANNEL_NAME_1,
                            importance);
                    channel1.setDescription("This is Channel 1");
                    channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                    AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

                    channel1.enableLights(true);
                    channel1.setLightColor(Color.GREEN);
                    channel1.enableVibration(true);
                    channel1.setSound(sound, attributes);
                    channel1.getSound();
                    channel1.setShowBadge(true);

                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.createNotificationChannel(channel1);

                    if (manager != null){
                        manager.createNotificationChannel(channel1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refWiFi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    final long sWiFi = snapshot.getValue(Long.class);
                    if (sWiFi == 0){
                        statusNC.setVisibility(View.VISIBLE);
                        statusCN.setVisibility(View.GONE);
                    } else if (sWiFi == 1){
                        statusNC.setVisibility(View.GONE);
                        statusCN.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refSuhu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    final Long suhu = snapshot.getValue(Long.class);
                    dataSuhu.setText(suhu.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refOLED.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    final String oled = snapshot.getValue(String.class);
                    OLED.setText(oled);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refOLED1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String oled1 = snapshot.getValue(String.class);
                OLED1.setText(oled1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void saveData(String jarak, String suhu, String waktu){
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());

        Data data = new Data();
        data.jarak = jarak;
        data.suhu = suhu;
        data.waktu = waktu;
        db.dataDao().insertData(data);

    }
}
