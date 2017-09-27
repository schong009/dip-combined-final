package com.example.junhaozeng.testdesign.StepService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.junhaozeng.testdesign.Activity.MainActivity;
import com.example.junhaozeng.testdesign.R;
import com.example.junhaozeng.testdesign.Utils.DbManager;
import com.example.junhaozeng.testdesign.Utils.UpdateUiCallBack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StepService extends Service implements SensorEventListener {
    // key for logcat
    private String TAG = "StepService";
    // save steps after a duration
    private static int duration = 30000;
    // count down timer
    private TimeCount time;
    // current date
    private static String CURDATE = "";
    // step counter sensor manager
    private SensorManager sensorManager;
    // current steps
    private int CURSTEPS;
    // has steps recorded or not
    private boolean hasRecord = false;
    // steps counted by sys since this service is on
    private int hasStepCount = 0;
    // previous steps recorded by our app
    private int previousStepCount = 0;
    // notification manager
    private NotificationManager notificationManager;
    // notification builder
    private NotificationCompat.Builder notificationBuilder;
    // database manager
    private DbManager dbManager;
    // broadcast receiver
    private BroadcastReceiver broadcastReceiver;
    // passes this service to activity
    private StepBinder stepBinder = new StepBinder();
    // anonymous class overrode by activity which
    // provides method to update the UI
    private UpdateUiCallBack uiCallBack;
    // id for steps counting notification
    int notifyID_Step = 100;

    /**
     * A bridging class between activity and service
     */
    public class StepBinder extends Binder {
        public StepService getService() { return StepService.this; }
    }

    /**
     * A timer that endlessly counts durations
     */
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            time.cancel();
            save();
            startTimeCount();
        }
    }

    /**
     * Instantiate and start the timer
     */
    private void startTimeCount() {
        if (time == null) {
            time = new TimeCount(duration, 1000);
        }
        time.start();
    }

    /**
     * Initialize the notification bar
     */
    private void initNotification() {
        notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(getString(R.string.app_name))
                .setContentText("Steps Today: " + CURSTEPS)
                .setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = notificationBuilder.build();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        startForeground(notifyID_Step, notification);
        Log.d(TAG, "initNotification()");
    }

    /**
     * Get date today "yyyy-MM-dd"
     * @return date in STRING
     */
    private String getDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * Initialize database manager
     * (put it standalone for debugging)
     */
    private void initDbManager() {
        dbManager = new DbManager(this);
    }

    /**
     * Initialize today's steps by
     * reading from database
     * also updateNotification & UI
     */
    private void initTodayData() {
        CURDATE = getDate();
        int steps = dbManager.readRecord(CURDATE);
        if (steps == -1) {
            CURSTEPS = 0;
        } else if (steps >= 0) {
            CURSTEPS = steps;
        }
        Log.d(TAG, "initTodayData()");
        updateNotification();
    }

    /**
     * Register the service on broadcast receiver
     */
    private void initBroadcastReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        // Screen off
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        // Device off
        intentFilter.addAction(Intent.ACTION_SHUTDOWN);
        // Screen on
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        // Shutting down
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        // Date time changed
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    Log.d(TAG, "screen on");
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    Log.d(TAG, "screen off");
                    duration = 60000;
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    Log.d(TAG, "screen unlock");
                    duration = 30000;
                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                    Log.i(TAG, "receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
                    save();
                } else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
                    Log.i(TAG, "receive ACTION_SHUTDOWN");
                    save();
                } else if (Intent.ACTION_DATE_CHANGED.equals(action)) {
                    save();
                    isNewDay();
                } else if (Intent.ACTION_TIME_CHANGED.equals(action)) {
                    save();
                    isNewDay();
                } else if (Intent.ACTION_TIME_TICK.equals(action)) {
                    save();
                    isNewDay();
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
        Log.d(TAG, "initBroadcastReceiver()");
    }

    /**
     * Check whether it is a new day, if so,
     * call initTodayData()
     */
    private void isNewDay() {
        String time = "00:00";
        if (time.equals(new SimpleDateFormat("HH:mm").format(new Date()))
                || !CURDATE.equals(getDate())) {
            initTodayData();
        }
    }

    /**
     * Update notification bar and UI
     */
    private void updateNotification() {
        Intent hangIntent = new Intent(this, MainActivity.class);
        PendingIntent hangPendingIntent = PendingIntent.getActivity(this, 0, hangIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = notificationBuilder.setContentTitle(getString(R.string.app_name))
                .setContentText("Steps Today: " + CURSTEPS)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(hangPendingIntent)
                .build();
        notificationManager.notify(notifyID_Step, notification);
        if (uiCallBack != null) {
            uiCallBack.updateUi(CURSTEPS);
        }
        Log.d(TAG, "updateNotification()");
    }

    /**
     * Register a UI controller by calling back
     * @param uiCallBack anonymous class overrode by the activity
     */
    public void registerCallBack(UpdateUiCallBack uiCallBack) {
        this.uiCallBack = uiCallBack;
    }

    /**
     * Set up sensor manager
     */
    private void startStepDetector() {
        if (sensorManager != null) {
            sensorManager = null;
        }
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        addCountStepListener();
    }

    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

    /**
     * Register sensor listener
     *
     * Here we are using TYPE_STEP_COUNTER as our sensor
     * which is supported by android 4.4 and above,
     * the codes to get version are: Build.VERSION.SDK_INT
     * which returns an integer and 4.4 <==> 19
     *
     * More about this sensor:
     * 1. it consumes low power
     * 2. it has been counting steps since the device is booted
     * 3. rebooting device would set the value stored to 0
     * 4. even when the phone is sleeping, it is still counting the steps
     * 5. so do not unregister the sensor
     */
    private void addCountStepListener() {
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            Log.v(TAG, "Sensor.TYPE_STEP_COUNTER");
            sensorManager.registerListener(StepService.this,
                    countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.d(TAG, "STEP_COUNTER not available");
        }
    }

    /**
     * Update CURSTEPS, notification bar and UI
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int tempSteps = (int) sensorEvent.values[0];
        if (!hasRecord) {
            hasRecord = true;
            hasStepCount = tempSteps;
        } else {
            int thisStepCount = tempSteps - hasStepCount;
            int thisStep = thisStepCount - previousStepCount;
            CURSTEPS += thisStep;
            previousStepCount = thisStepCount;
        }
        updateNotification();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    /**
     * Save date and step to our database
     */
    private void save() {
        int tempSteps = CURSTEPS;
        int dbSteps = dbManager.readRecord(CURDATE);
        if (dbSteps == -1) {
            dbManager.insertRecord(CURDATE, tempSteps);
        } else if (dbSteps >= 0) {
            // TODO:
            // create a set method in DbManager
            // set the date and step
            dbManager.updateRecord(CURDATE, tempSteps);
        }
    }

    /**
     * Getter for CURSTEPS
     * @return current steps
     */
    public int getStepCount() {
        return CURSTEPS;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initDbManager();
        initNotification();
        initTodayData();
        initBroadcastReceiver();
        new Thread(new Runnable() {
            @Override
            public void run() {
                startStepDetector();
            }
        }).start();
        startTimeCount();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        unregisterReceiver(broadcastReceiver);
        Log.d(TAG, "stepService shuts down");
    }

    @Override
    public IBinder onBind(Intent intent) { return stepBinder; }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}

