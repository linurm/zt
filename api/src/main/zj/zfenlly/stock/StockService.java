package zj.zfenlly.stock;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;



import org.apache.commons.httpclient.HttpException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import zj.zfenlly.main.MainActivity;
import zj.zfenlly.main.MainApplication;
import zj.zfenlly.main.MonitorService;
import zj.zfenlly.other.Observable;
import zj.zfenlly.other.Observer;
import zj.zfenlly.daodb.DaoMaster;
import zj.zfenlly.daodb.DaoSession;
import zj.zfenlly.daodb.Note;
import zj.zfenlly.daodb.NoteDao;
import zj.zfenlly.tools.R;

public class StockService extends Service implements Observer {

    private static final int STOCK_DATA_GET = 1;
    private static final int SCAN_CAMERA_STOP = 2;
    private static final int STOCK_GETLOOP_TICK = 6;
    private static final int STOCK_GETNEXTLOOP_TICK = STOCK_GETLOOP_TICK - 1;

    private static final String ST_CODE = "sh600844";
    private static int numMessages;
    // private SinaStockClient mClient;
    private final String TAG = this.getClass().getName();
    public MainApplication mApplication;
    NotificationManager mNotificationManager;
    Notification mNotification;
    PendingIntent mPendingIntent;
    NotificationCompat.Builder mNotifyBuilder;
    SimulationDisplay sd = null;
    private boolean isstart = false;

    // private boolean pause = false;

    // private Cursor cursor;
    private StockClient mClient = null;
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private NoteDao noteDao;


    //    private void appRegister() {
//        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
//        AppReceiver myReceiver = new AppReceiver();
//        print("register APP receiver");
//        registerReceiver(myReceiver, intentFilter);
//    }
    private boolean NetworkAvailable;
    private sThread mStockThread = new sThread();

    private void print(String msg) {
        Log.i(TAG, msg);
    }

//    private void initNotification() {
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.newquest_icon)
//                        .setContentTitle("My notification")
//                        .setContentText("Hello World!");
//// Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(this, MainActivity.class);
//
//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//// Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(MainActivity.class);
//// Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//// mId allows you to update the notification later on.
//        mNotificationManager.notify(mId, mBuilder.build());
//    }

//    private void notificationSend(String currentText) {
//        mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//// Sets an ID for the notification, so it can be updated
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, MainActivity.class), 0);
//        // 下面需兼容Android 2.x版本是的处理方式
//        // Notification notify1 = new Notification(R.drawable.message,
//        // "TickerText:" + "您有新短消息，请注意查收！", System.currentTimeMillis());
//        Notification notify1 = new Notification();
//        notify1.icon = R.drawable.newquest_icon;
//        notify1.tickerText = "TickerText:您有新短消息，请注意查收！";
//        notify1.when = System.currentTimeMillis();
//        notify1.setLatestEventInfo(this, "Notification Title",
//                "This is the notification message", pendingIntent);
//        notify1.number = 1;
//        notify1.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
//        // 通过通知管理器来发起通知。如果id不同，则每click，在statu那里增加一个提示
//        mNotificationManager.notify(1, notify1);
//    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub
        String qualifier = (String) arg;
        if (qualifier.equals(MainApplication.SERVER_PAUSE)) {
            ;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

//    private int startServiceForeground(Intent intent, int flags, int startId) {
//
//        Intent notificationIntent = new Intent(this, getApplicationContext().getClass());
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//        Notification notification = new NotificationCompat.Builder(this)
//                .setContentTitle("Observer Service")
//                .setContentIntent(pendingIntent)
//                .setOngoing(true).getNotification();
//
//        startForeground(300, notification);
//
//        return START_STICKY;
//    }

    private void initDatabase() {
        final String DATABASE_PATH = Environment.getExternalStorageDirectory()
                + "/" + "xxx/";
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DATABASE_PATH
                + "notes-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        noteDao = daoSession.getNoteDao();
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Notification notification = new Notification();
//        startForeground(-1, notification);
        mApplication = (MainApplication) getApplication();
        mApplication.addObserver(this);
        isstart = true;
        mClient = StockClient.getInstance();
        print(mClient.getUrlString(new String[]{ST_CODE}));
        initDatabase();
        mStockThread.start();
        //mThread.start();
        print("onCreate");
    }

    public boolean isNetworkAvailable() {
        Context context = getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
//                    System.out.println(i + "===状态===" + networkInfo[i].getState());
//                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void initNotify() {
        mNotificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.drawable.octocat;
        CharSequence tickerText = "service is run background";
        mNotification = new Notification(icon, tickerText,
                System.currentTimeMillis());

        mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                0);//
        //PendingIntent.FLAG_UPDATE_CURRENT);

//        mNotification.setLatestEventInfo(getApplicationContext(), "service",
//                "running on background", mPendingIntent);
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(0, mNotification);
//        startForeground(0x111, mNotification);

    }

    private void notificationSend2() {

        mNotificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        // 通过Notification.Builder来创建通知，注意API Level
        // API11之后才支持
        Notification notify2 = new Notification.Builder(this)
                .setSmallIcon(R.drawable.newquest_icon) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                // icon)
                .setTicker("TickerText:" + "您有新短消息，请注意查收！")// 设置在status
                // bar上显示的提示文字
                .setContentTitle("Notification Title")// 设置在下拉status
                // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                .setContentText("This is the notification message")// TextView中显示的详细内容
                .setContentIntent(pendingIntent2) // 关联PendingIntent
                .setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                .getNotification(); // 需要注意build()是在API level
        // 16及之后增加的，在API11中可以使用getNotificatin()来代替
        notify2.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1, notify2);
    }

    private void notificationSend4() {
        mNotificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification myNotify = new Notification();
        myNotify.icon = R.drawable.newquest_icon;
        myNotify.tickerText = "TickerText:您有新短消息，请注意查收！";
        myNotify.when = System.currentTimeMillis();
        myNotify.flags = Notification.FLAG_NO_CLEAR;// 不能够自动清除
        RemoteViews rv = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.notification);
        rv.setTextViewText(R.id.text_content, "hello wrold!");
        myNotify.contentView = rv;
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        myNotify.contentIntent = contentIntent;
        mNotificationManager.notify(1, myNotify);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        print("onStartCommand:" + intent);
        //initNotify();
        //notificationSend4();
//        startServiceForeground(intent, flags, startId);
//        if(intent == null) {
//            return Service.START_NOT_STICKY;
//        }

//        appRegister();

        return flags;
        //return super.onStartCommand(intent, flags, startId);
    }

    public boolean isSaved(Note note) {
        QueryBuilder<Note> qb = noteDao.queryBuilder();

        qb.where(NoteDao.Properties.Stockid.eq(note.getStockid()));
        if (qb.list().size() > 0)
            qb.where(NoteDao.Properties.Date.eq(note.getDate()));

        // print("date: " + note.getDate() + ": " + qb.list().size());
        // qb.buildCount().count();
        return qb.list().size() > 0 ? true : false;// 锟斤拷锟斤拷锟秸藏憋拷
    }

    private int addNote(Note note) {

        // String comment = "Added on " + df.format(new Date());
        // Note note = new Note(null, id, content, time);
        if (isSaved(note)) {
            print("is already exist!!!");
            return -1;
        } else {
            noteDao.insert(note);
            print("Inserted new note, ID: " + note.getId() + "time: "
                    + note.getDate());
            return 0;
        }

        // cursor.requery();
    }

    @SuppressWarnings("rawtypes")
    private int storeNotes(List<Note> al) {
        Note note;
        for (Iterator i = al.iterator(); i.hasNext(); ) {
            note = (Note) i.next();
            return addNote(note);
        }
        return 0;
    }

    private void displayNote(Note note) {
        StockInfo stockInfo = StockInfo.parseNoteInfo(note);

        if (EventBus.getDefault().hasSubscriberForEvent(StockEvent.class)) {
            EventBus.getDefault().post(new StockEvent(stockInfo));
            print("=======================");
        }
        print("-------------------------");
        // print(stockInfo.toString());
    }

    @SuppressWarnings("rawtypes")
    private void Display(List<Note> al) {
        if (al == null)
            return;
        Note note;
        for (Iterator i = al.iterator(); i.hasNext(); ) {
            note = (Note) i.next();
            displayNote(note);
        }
    }

//    @SuppressLint("HandlerLeak")
//    public Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(android.os.Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case STOCK_DATA_GET:
//                    List<Note> list = null;
//
//                    if (isNetworkAvailable() == false) {
//                        print("network is unavailable");
//                        break;
//                    }
//                    try {
//                        list = mClient.getStockInfoDB(new String[]{"sh601006"});
//                    } catch (HttpException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (StockInfo.ParseStockInfoException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    if (list != null) {
//                        if (storeNotes(list) == -1) {
//                            mStockThread.set_times(STOCK_GETNEXTLOOP_TICK);
//                        }
//                    }
//                    break;
//                case SCAN_CAMERA_STOP:
//
//                    break;
//                default:
//                    break;
//            }
//
//        }
//
//        ;
//    };

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        print("onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        print("onTrimMemory");
        Intent intentScan = new Intent("zj.intent.RESTART");
        //intentScan.addCategory(Intent.CATEGORY_DEFAULT);
        //intentScan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        getApplicationContext().sendBroadcast(intentScan);
    }

    ;

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        Intent localIntent = new Intent();
//        localIntent.setClass(this, StockService.class);  //销毁时重新启动Service
//        this.startService(localIntent);
        Intent intentScan = new Intent("zj.intent.monitor");
        //intentScan.addCategory(Intent.CATEGORY_DEFAULT);
        //intentScan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        getApplicationContext().sendBroadcast(intentScan);

        try {
            Thread.sleep(50);// 10ms
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        print("onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        print("onDestroy");
        stopForeground(true);
        isstart = false;
        if (mStockThread.isAlive()) {
            try {
                mStockThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        if (mThread.isAlive()) {
//            try {
//                mThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        mApplication.deleteObserver(this);
        Intent intentScan = new Intent("zj.intent.RESTART");
        //intentScan.addCategory(Intent.CATEGORY_DEFAULT);
        //intentScan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        getApplicationContext().sendBroadcast(intentScan);
//        startService(new Intent(this, StockService.class));
//        Intent localIntent = new Intent();
//        localIntent.setClass(this, StockService.class);  //销毁时重新启动Service
//        this.startService(localIntent);

    }

    public class SimulationDisplay {
        public int db_num;
        public int db_index;
        private boolean isInit = false;
        private QueryBuilder<Note> qb;

        // 10s one time
        public SimulationDisplay(String stockId) {
            simulationDisplayInit(stockId);
        }

        private boolean simulationDisplayInit(String stockId) {
            if (!isInit) {
                qb = noteDao.queryBuilder();
                qb.where(NoteDao.Properties.Stockid.eq(stockId));
                db_num = qb.list().size();
                db_index = 0;
                isInit = true;
            }
            return isInit;
        }

        public Note getNoteFromDB() {

            if (db_num < db_index)
                return null;
            Note note = qb.list().get(db_index);
            db_index += 1;
            print("db_index: " + db_index);
            StockInfo stockInfo = StockInfo.parseNoteInfo(note);
            if (EventBus.getDefault().hasSubscriberForEvent(StockEvent.class)) {
                EventBus.getDefault().post(new StockEvent(stockInfo));
            }
            return note;
        }

        public void SimulationOFF() {
            isInit = false;
            qb = null;
            db_num = 0;
            db_index = 0;
        }

    }

    public class sThread extends Thread {
        int times;
        int sm_times;
        long tmp_times = 0, last_times = 0;//, cmp_times = 1000;

        public void set_times(int times) {
            this.times = times;
        }

        @Override
        public void run() {
            // rs.Poll(1);
            print("mStockThread thread run");
            //List<Note> list = null;
            while (isstart) {

                try {
                    Thread.sleep(10);// 10ms
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                tmp_times = System.currentTimeMillis();
                if (last_times + 1000 < tmp_times) {//1s
                    sm_times++;
                    times++;
                    last_times = last_times + 1000 * ((tmp_times - last_times) / 1000);
                }
                // 10s

                if (sm_times > 3) {
                    sm_times = 0;

                    if (mApplication.isServerDisplayRun()) {
//                        print("?????????????");

                        if (mApplication.isServerSimulation()) {
//                            print("!!!!!!!!!!!!!!!!!");
                            if (sd == null) {
                                print("new");
                                sd = new SimulationDisplay(ST_CODE);
                            }
                            sd.getNoteFromDB();
                        }
                    }
                }
                if (times >= STOCK_GETLOOP_TICK) {
                    times = 0;
                    try {
                        if (mClient != null) {
                            List<Note> list = null;
                            if (StockTime.checkTime()) {
                                if (isNetworkAvailable() == false) {
                                    print("network is unavaliable");
                                    break;
                                }
                                try {
                                    list = mClient.getStockInfoDB(new String[]{ST_CODE});
                                } catch (HttpException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (StockInfo.ParseStockInfoException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                if (list != null) {
                                    if (storeNotes(list) == -1) {
                                        mStockThread.set_times(STOCK_GETNEXTLOOP_TICK);
                                    }
                                }
                            } else {
                                print("is out of time");
                            }
                            if (!mApplication.isServerSimulation() && mApplication.isServerDisplayRun()) {

                                if (sd instanceof SimulationDisplay) {
                                    sd.SimulationOFF();
                                    print("off");
                                    sd = null;
                                }
                                Display(list);
                            }
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
            print("mStockThread thread exit");
        }
    }


//    private boolean isstart = false;

    //private MThread mThread = new MThread();

    public class MThread extends Thread {
        boolean isServiceRunning = false;

        @Override
        public void run() {
            while (isstart) {
                ActivityManager manager = (ActivityManager) getApplicationContext()
                        .getSystemService(Context.ACTIVITY_SERVICE);
                isServiceRunning = false;
                for (ActivityManager.RunningServiceInfo service : manager
                        .getRunningServices(Integer.MAX_VALUE)) {
//                print(service.service
//                        .getClassName());
                    if (MonitorService.class.getName().equals(service.service
                            .getClassName())) {
                        isServiceRunning = true;
//                        print(StockService.class.getName() + " is running");
//                    while(true){
//
//                    }

                    }
                }
                if (!isServiceRunning) {
                    Intent intentScan = new Intent("zj.intent.monitor");
                    //intentScan.addCategory(Intent.CATEGORY_DEFAULT);
                    //intentScan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    getApplicationContext().sendBroadcast(intentScan);
                    print("start service");
                }

                try {
                    Thread.sleep(5);// 10ms
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    // public boolean isPause() {
    // return pause;
    // }
    //
    // public void setPause(boolean pause) {
    // this.pause = pause;
    // }

}
