package com.android.wifimulticast.util;

/**
 * Created by Administrator on 2017/4/14.
 */

public class Constant {
    public static final int CMD80 = 80;
    public static final int CMD81 = 81;
    public static final int CMD82 = 82;
    public static final int CMD83 = 83;
    public static final int CMD_TYPE1 = 1;
    public static final int CMD_TYPE2 = 2;
    public static final int CMD_TYPE3 = 3;
    public static final int OPR_CMD1 = 1;
    public static final int OPR_CMD2 = 2;
    public static final int OPR_CMD3 = 3;
    public static final int OPR_CMD4 = 4;
    public static final int OPR_CMD5 = 5;
    public static final int OPR_CMD6 = 6;
    public static final int OPR_CMD10 = 10;
    public static final String MULTICAST_IP = "239.0.5.100";
    public static final int PORT = 8251;

    public static final int bufferSize = 256;
    public static final int msgLength = 180;
    public static final int fileNameLength = 90;
    public static final int readBufferSize = 4096;//文件读写缓存
    public static final byte[] pkgHead = "AND".getBytes();

    public static final String WIFIACTION="android.net.conn.CONNECTIVITY_CHANGE";
    public static final String ETHACTION = "android.intent.action.ETH_STATE";

    //生成唯一ID码
    public static int getMyId(){
        int id = (int)(Math.random()*1000000);
        return id;
    }
}
