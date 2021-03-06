package com.android.wifimulticast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.wifimulticast.util.ByteAndInt;
import com.android.wifimulticast.util.Constant;
import com.android.wifimulticast.util.FileName;
import com.android.wifimulticast.util.FileState;
import com.android.wifimulticast.util.Person;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;


/**
 * Created by Administrator on 2017/4/12.
 */

public class WifiService extends Service {
    public static final String TAG = "TAG";
    private static Person me = null;//用来保存自身的相关信息
    public InetAddress localInetAddress = null;
    boolean isStopUpdateMe = false;
    private byte[] regBuffer = new byte[Constant.bufferSize];//本机网络注册交互指令

    private String localIp = null;
    private byte[] localIpBytes = null;
    private ServiceBroadcastReceiver receiver = null;
    private SharedPreferences.Editor editor = null;
    private CommunicationBridge comBridge = null;//通讯与协议解析模块
    private byte[] fileSendBuffer = new byte[Constant.bufferSize];//文件发送交互指令
    private WifiManager wifiManager = null;

    //初始化指令缓存
    private void initCmdBuffer() {
        //初始化用户注册指令缓存
        for (int i = 0; i < Constant.bufferSize; i++) regBuffer[i] = 0;
        System.arraycopy(Constant.pkgHead, 0, regBuffer, 0, 3);
        regBuffer[3] = Constant.CMD80;
        regBuffer[4] = Constant.CMD_TYPE1;
        regBuffer[5] = Constant.OPR_CMD1;

        //初始化发送文件指令缓存
        for (int i = 0; i < Constant.bufferSize; i++) fileSendBuffer[i] = 0;
        System.arraycopy(Constant.pkgHead, 0, fileSendBuffer, 0, 3);
        fileSendBuffer[3] = Constant.CMD82;
        fileSendBuffer[4] = Constant.CMD_TYPE1;
        fileSendBuffer[5] = Constant.OPR_CMD1;
    }
//
//    public void joinOrganization() {
//        MulticastSocket multicastSocket = null;
//
//        try {
//            multicastSocket = new MulticastSocket(Constant.PORT);
//            multicastSocket.joinGroup(InetAddress.getByName(Constant.MULTICAST_IP));
//            if (null != multicastSocket && !multicastSocket.isClosed()) {
//                regBuffer[4] = Constant.CMD_TYPE1;
//                DatagramPacket dp = new DatagramPacket(regBuffer, Constant.bufferSize, InetAddress.getByName(Constant.MULTICAST_IP), Constant.PORT);
//                multicastSocket.send(dp);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    //接收文件
    public void receiveFiles(String fileSavePath) {
        comBridge.receiveFiles(fileSavePath);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        initCmdBuffer();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        new CheckNetConnectivity().start();
        regBroadcastReceiver();
        comBridge = new CommunicationBridge();//启动socket连接
        comBridge.start();
        getMyInfomation();//获得自身信息
        new UpdateMe().start();
    }

    private void getMyInfomation() {
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
        int iconId = pre.getInt("headIconId", R.drawable.black_bird);
        editor = pre.edit();
        String nickeName = pre.getString("nickeName", "Zhang San");
        int myId = pre.getInt("myId", Constant.getMyId());
        editor.putInt("myId", myId);
        editor.commit();

        if (null == me) me = new Person();
        me.personHeadIconId = iconId;
        me.personNickeName = nickeName;
        me.personId = myId;
        me.ipAddress = localIp;
        Log.e("TAG", "my id is " + myId);
        //更新注册命令用户数据
        System.arraycopy(ByteAndInt.int2ByteArray(myId), 0, regBuffer, 6, 4);
        System.arraycopy(ByteAndInt.int2ByteArray(iconId), 0, regBuffer, 10, 4);
        for (int i = 14; i < 44; i++) regBuffer[i] = 0;//把原来的昵称内容清空
        byte[] nickeNameBytes = nickeName.getBytes();
        System.arraycopy(nickeNameBytes, 0, regBuffer, 14, nickeNameBytes.length);
    }

    private void regBroadcastReceiver() {
        receiver = new ServiceBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.WIFIACTION);
        filter.addAction(Constant.ETHACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        comBridge.release();
        isStopUpdateMe = true;
        Log.d(TAG, "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class CommunicationBridge extends Thread {
        private MulticastSocket multicastSocket = null;
        private byte[] recvBuffer = new byte[Constant.bufferSize];
        private int fileSenderUid = 0;//用来保存文件发送者的id号
        private boolean isBusyNow = false;//现在是否正在收发文件，如果该状态为true则表示现在正在进行收发文件操作，这时需要向其它发送文件的用户发送忙指令
        private String fileSavePath = null;//用来保存接收到的文件

        private ArrayList<FileName> tempFiles = null;//用来临时保存需要发送的文件名
        private int tempUid = 0;//用来临时保存需要发送文件的用户id(接受文件方的用户id)
        private ArrayList<FileState> receivedFileNames = new ArrayList<FileState>();
        private ArrayList<FileState> beSendFileNames = new ArrayList<FileState>();

        private FileHandler fileHandler = null;//文件处理线程，用来收发文件
//        private AudioHandler audioHandler = null;//音频处理模块，用来收发音频数据

        public CommunicationBridge() {
            fileHandler = new FileHandler();
            fileHandler.start();
//            audioHandler = new AudioHandler();
//            audioHandler.start();
        }

        //打开组播端口，准备组播通讯
        @Override
        public void run() {
            super.run();
            try {
                multicastSocket = new MulticastSocket(Constant.PORT);
                multicastSocket.setLoopbackMode(true);
                multicastSocket.joinGroup(InetAddress.getByName(Constant.MULTICAST_IP));
                System.out.println("Socket started...");
                while (!multicastSocket.isClosed() && null != multicastSocket) {
                    for (int i = 0; i < Constant.bufferSize; i++) {
                        recvBuffer[i] = 0;
                    }
                    DatagramPacket rdp = new DatagramPacket(recvBuffer, recvBuffer.length);
                    multicastSocket.receive(rdp);
                    parsePackage(recvBuffer);
                }
            } catch (Exception e) {
                try {
                    if (null != multicastSocket && !multicastSocket.isClosed()) {
                        multicastSocket.leaveGroup(InetAddress.getByName(Constant.MULTICAST_IP));
                        multicastSocket.close();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        //解析接收到的数据包
        private void parsePackage(byte[] pkg) {
            int CMD = pkg[3];//命令字
            int cmdType = pkg[4];//命令类型
            int oprCmd = pkg[5];//操作命令

            //获得用户ID号
            byte[] uId = new byte[4];
            System.arraycopy(pkg, 6, uId, 0, 4);
            int userId = ByteAndInt.byteArray2Int(uId);
            Log.e("TAG", "CMD: " + CMD + " type: " + cmdType + " : " + userId);
            switch (CMD) {
                case Constant.CMD80:
                    switch (cmdType) {
                        case Constant.CMD_TYPE1:
                            //如果该信息不是自己发出则给对方发送回应包,并把对方加入用户列表
                            if (userId != me.personId) {
                                updatePerson(userId, pkg);
                                //发送应答包
                                byte[] ipBytes = new byte[4];//获得请求方的ip地址
                                System.arraycopy(pkg, 44, ipBytes, 0, 4);
                                try {
                                    InetAddress targetIp = InetAddress.getByAddress(ipBytes);
                                    regBuffer[4] = Constant.CMD_TYPE2;//把自己的注册信息修改成应答信息标志，把自己的信息发送给请求方
                                    DatagramPacket dp = new DatagramPacket(regBuffer, Constant.bufferSize, targetIp, Constant.PORT);
                                    multicastSocket.send(dp);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case Constant.CMD_TYPE2:
                            updatePerson(userId, pkg);
                            break;
                        case Constant.CMD_TYPE3:
//                            childrenMap.remove(userId);
//                            personKeys.remove(Integer.valueOf(userId));
//                            sendPersonHasChangedBroadcast();
                            break;
                    }
                    break;
                case Constant.CMD81:// 收到信息
                    switch (cmdType) {
                        case Constant.CMD_TYPE1:
//                            List<Message> messages = null;
//                            if (msgContainer.containsKey(userId)) {
//                                messages = msgContainer.get(userId);
//                            } else {
//                                messages = new ArrayList<Message>();
//                            }
//                            byte[] msgBytes = new byte[Constant.msgLength];
//                            System.arraycopy(pkg, 10, msgBytes, 0, Constant.msgLength);
//                            String msgStr = new String(msgBytes).trim();
//                            Message msg = new Message();
//                            msg.msg = msgStr;
//                            msg.receivedTime = getCurrentTime();
//                            messages.add(msg);
//                            msgContainer.put(userId, messages);
//
//                            Intent intent = new Intent();
//                            intent.setAction(Constant.hasMsgUpdatedAction);
//                            intent.putExtra("userId", userId);
//                            intent.putExtra("msgCount", messages.size());
//                            sendBroadcast(intent);
                            break;
                        case Constant.CMD_TYPE2:
                            break;
                    }
                    break;
                case Constant.CMD82:
                    switch (cmdType) {
                        case Constant.CMD_TYPE1://收到文件传输请求
                            switch (oprCmd) {
                                case Constant.OPR_CMD1:
                                    //发送广播，通知界面有文件需要传输
                                    if (!isBusyNow) {
                                        //	isBusyNow = true;
                                        fileSenderUid = userId;//保存文件发送者的id号，以便后面若接收者拒绝接收文件时可以通过该id找到发送者，并给发送者发送拒绝接收指令
//                                        Person person = childrenMap.get(Integer.valueOf(userId));
//                                        Intent intent = new Intent();
//                                        intent.putExtra("person", person);
//                                        intent.setAction(Constant.receivedSendFileRequestAction);
//                                        sendBroadcast(intent);
                                    } else {//如果当前正在收发文件则向对方发送忙指令
//                                        Person person = childrenMap.get(Integer.valueOf(userId));
//                                        fileSendBuffer[4] = Constant.CMD_TYPE2;
//                                        fileSendBuffer[5] = Constant.OPR_CMD4;
//                                        byte[] meIdBytes = ByteAndInt.int2ByteArray(me.personId);
//                                        System.arraycopy(meIdBytes, 0, fileSendBuffer, 6, 4);
//                                        try {
//                                            DatagramPacket dp = new DatagramPacket(fileSendBuffer, Constant.bufferSize, InetAddress.getByName(person.ipAddress), Constant.PORT);
//                                            multicastSocket.send(dp);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
                                    }
                                    break;
                                case Constant.OPR_CMD5://接收对方传过来的文件名信息
                                    byte[] fileNameBytes = new byte[Constant.fileNameLength];
                                    byte[] fileSizeByte = new byte[8];
                                    System.arraycopy(pkg, 10, fileNameBytes, 0, Constant.fileNameLength);
                                    System.arraycopy(pkg, 100, fileSizeByte, 0, 8);
                                    FileState fs = new FileState();
                                    fs.fileName = new String(fileNameBytes).trim();
                                    fs.fileSize = Long.valueOf(ByteAndInt.byteArrayToLong(fileSizeByte));
                                    receivedFileNames.add(fs);
                                    break;
                            }
                            break;
                        case Constant.CMD_TYPE2:
                            switch (oprCmd) {
                                case Constant.OPR_CMD2://对方同意接收文件
//                                    fileHandler.startSendFile();
                                    System.out.println("Start send file to remote user ...");
                                    break;
                                case Constant.OPR_CMD3://对方拒绝接收文件
//                                    Intent intent = new Intent();
//                                    intent.setAction(Constant.remoteUserRefuseReceiveFileAction);
//                                    sendBroadcast(intent);
                                    System.out.println("Remote user refuse to receive file ...");
                                    break;
                                case Constant.OPR_CMD4://对方现在忙
                                    System.out.println("Remote user is busy now ...");
                                    break;
                            }
                            break;
                    }
                    break;
                case Constant.CMD83://83命令，语音通讯相关
                    switch (cmdType) {
                        case Constant.CMD_TYPE1:
                            switch (oprCmd) {
                                case Constant.OPR_CMD1://接收到远程语音通话请求
                                    System.out.println("Received a talk request ... ");

//                                    Person person = childrenMap.get(Integer.valueOf(userId));
//                                    Intent intent = new Intent();
//                                    intent.putExtra("person", person);
//                                    intent.setAction(Constant.receivedTalkRequestAction);
//                                    sendBroadcast(intent);
                                    break;
                                case Constant.OPR_CMD2:
                                    //收到关闭指令，关闭语音通话
                                    System.out.println("Received remote user stop talk cmd ... ");
//                                    Intent i = new Intent();
//                                    i.setAction(Constant.remoteUserClosedTalkAction);
//                                    sendBroadcast(i);
                                    break;
                            }
                            break;
                        case Constant.CMD_TYPE2:
                            switch (oprCmd) {
                                case Constant.OPR_CMD1:
                                    //被叫应答，开始语音通话

                                    break;
                            }
                            break;
                    }
                    break;
            }
        }

        //更新或加用户信息到用户列表中
        private void updatePerson(int userId, byte[] pkg) {
            Person person = new Person();
            getPerson(pkg, person);
//            childrenMap.put(userId, person);
//            if (!personKeys.contains(Integer.valueOf(userId)))
//                personKeys.add(Integer.valueOf(userId));
//            if (!children.contains(childrenMap)) children.add(childrenMap);
//            sendPersonHasChangedBroadcast();
        }

        //关闭Socket连接
        private void release() {
            try {
                regBuffer[4] = Constant.CMD_TYPE3;//把命令类型修改成注消标志，并广播发送，从所有用户中退出
                DatagramPacket dp = new DatagramPacket(regBuffer, Constant.bufferSize, InetAddress.getByName(Constant.MULTICAST_IP), Constant.PORT);
                multicastSocket.send(dp);
                System.out.println("Send logout cmd ...");

                multicastSocket.leaveGroup(InetAddress.getByName(Constant.MULTICAST_IP));
                multicastSocket.close();

                System.out.println("Socket has closed ...");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fileHandler.release();
//                audioHandler.release();
            }
        }

        //分析数据包并获取一个用户信息
        private void getPerson(byte[] pkg, Person person) {

            byte[] personIdBytes = new byte[4];
            byte[] iconIdBytes = new byte[4];
            byte[] nickeNameBytes = new byte[30];
            byte[] personIpBytes = new byte[4];

            System.arraycopy(pkg, 6, personIdBytes, 0, 4);
            System.arraycopy(pkg, 10, iconIdBytes, 0, 4);
            System.arraycopy(pkg, 14, nickeNameBytes, 0, 30);
            System.arraycopy(pkg, 44, personIpBytes, 0, 4);

//            person.personId = ByteAndInt.byteArray2Int(personIdBytes);
//            person.personHeadIconId = ByteAndInt.byteArray2Int(iconIdBytes);
//            person.personNickeName = (new String(nickeNameBytes)).trim();
//            person.ipAddress = Constant.intToIp(ByteAndInt.byteArray2Int(personIpBytes));
//            person.timeStamp = System.currentTimeMillis();
        }

        //注册自己到网络中
        public void joinOrganization() {
            try {
                if (null != multicastSocket && !multicastSocket.isClosed()) {
                    regBuffer[4] = Constant.CMD_TYPE1;//恢复成注册请求标志，向网络中注册自己
                    DatagramPacket dp = new DatagramPacket(regBuffer, Constant.bufferSize, InetAddress.getByName(Constant.MULTICAST_IP), Constant.PORT);
                    multicastSocket.send(dp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //向对方发送请求接收文件指令
//        public void sendFiles(int personId, ArrayList<FileName> files) {
//            if (personId > 0 && null != files && files.size() > 0) {
//                try {
//                    tempUid = personId;
//                    tempFiles = files;
//                    Person person = childrenMap.get(Integer.valueOf(tempUid));
//                    fileSendBuffer[4] = Constant.CMD_TYPE1;
//                    fileSendBuffer[5] = Constant.OPR_CMD5;
//                    byte[] meIdBytes = ByteAndInt.int2ByteArray(me.personId);
//                    System.arraycopy(meIdBytes, 0, fileSendBuffer, 6, 4);
//                    int fileNameLength = Constant.fileNameLength + 10;//清除头文件包的文件名存储区域，以便写新的文件名
//                    //把要传送的所有文件名传送给对方
//                    for (final FileName file : tempFiles) {
//                        //收集生成要发送文件的文相关资料
//                        FileState fs = new FileState(file.fileSize, 0, file.getFileName());
//                        beSendFileNames.add(fs);
//
//                        byte[] fileNameBytes = file.getFileName().getBytes();
//                        for (int i = 10; i < fileNameLength; i++) fileSendBuffer[i] = 0;
//                        System.arraycopy(fileNameBytes, 0, fileSendBuffer, 10, fileNameBytes.length);//把文件名放入头数据包
//                        System.arraycopy(ByteAndInt.longToByteArray(file.fileSize), 0, fileSendBuffer, 100, 8);
//                        DatagramPacket dp = new DatagramPacket(fileSendBuffer, Constant.bufferSize, InetAddress.getByName(person.ipAddress), Constant.PORT);
//                        multicastSocket.send(dp);
//                    }
//                    //对方发送请求接收文件指令
//                    fileSendBuffer[5] = Constant.OPR_CMD1;
//                    DatagramPacket dp = new DatagramPacket(fileSendBuffer, Constant.bufferSize, InetAddress.getByName(person.ipAddress), Constant.PORT);
//                    multicastSocket.send(dp);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        //向对方响应同意接收文件指令
        public void receiveFiles(String fileSavePath) {
            this.fileSavePath = fileSavePath;
//            Person person = childrenMap.get(Integer.valueOf(fileSenderUid));
//            fileSendBuffer[4] = Constant.CMD_TYPE2;
//            fileSendBuffer[5] = Constant.OPR_CMD2;
//            byte[] meIdBytes = ByteAndInt.int2ByteArray(me.personId);
//            System.arraycopy(meIdBytes, 0, fileSendBuffer, 6, 4);
//            try {
//                DatagramPacket dp = new DatagramPacket(fileSendBuffer, Constant.bufferSize, InetAddress.getByName(person.ipAddress), Constant.PORT);
//                multicastSocket.send(dp);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        //向文件发送者发送拒绝接收文件指令
//        public void refuseReceiveFile() {
//            //	isBusyNow = false;
//            Person person = childrenMap.get(Integer.valueOf(fileSenderUid));
//            fileSendBuffer[4] = Constant.CMD_TYPE2;
//            fileSendBuffer[5] = Constant.OPR_CMD3;
//            byte[] meIdBytes = ByteAndInt.int2ByteArray(me.personId);
//            System.arraycopy(meIdBytes, 0, fileSendBuffer, 6, 4);
//            try {
//                DatagramPacket dp = new DatagramPacket(fileSendBuffer, Constant.bufferSize, InetAddress.getByName(person.ipAddress), Constant.PORT);
//                multicastSocket.send(dp);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        //获得欲接收文件的文件名
        public ArrayList<FileState> getReceivedFileNames() {
            return receivedFileNames;
        }

        //获得欲发送文件的文件名
        public ArrayList<FileState> getBeSendFileNames() {
            return beSendFileNames;
        }


        //=========================TCP文件传输模块==================================================================
        //基于Tcp传输的文件收发模块
        private class FileHandler extends Thread {
            private ServerSocket sSocket = null;

            public FileHandler() {
            }

            @Override
            public void run() {
                super.run();
                try {
                    sSocket = new ServerSocket(Constant.PORT);
                    System.out.println("File Handler socket started ...");
                    while (!sSocket.isClosed() && null != sSocket) {
                        Socket socket = sSocket.accept();
                        socket.setSoTimeout(5000);
                        new SaveFileToDisk(socket).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //开始给对方发送文件
//            public void startSendFile() {
//                //获得接收方信息
//                Person person = childrenMap.get(Integer.valueOf(tempUid));
//                final String userIp = person.ipAddress;
//                //组合头数据包，该数据包中包括要发送的文件名
//                final byte[] sendFileCmd = new byte[Constant.bufferSize];
//                for (int i = 0; i < Constant.bufferSize; i++) sendFileCmd[i] = 0;
//                System.arraycopy(Constant.pkgHead, 0, sendFileCmd, 0, 3);
//                sendFileCmd[3] = Constant.CMD82;
//                sendFileCmd[4] = Constant.CMD_TYPE1;
//                sendFileCmd[5] = Constant.OPR_CMD6;
//                System.arraycopy(ByteAndInt.int2ByteArray(me.personId), 0, sendFileCmd, 6, 4);
//                for (final FileName file : tempFiles) {//采用多线程发送文件
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            Socket socket = null;
//                            OutputStream output = null;
//                            InputStream input = null;
//                            try {
//                                socket = new Socket(userIp, Constant.PORT);
//                                byte[] fileNameBytes = file.getFileName().getBytes();
//                                int fileNameLength = Constant.fileNameLength + 10;//清除头文件包的文件名存储区域，以便写新的文件名
//                                for (int i = 10; i < fileNameLength; i++) sendFileCmd[i] = 0;
//                                System.arraycopy(fileNameBytes, 0, sendFileCmd, 10, fileNameBytes.length);//把文件名放入头数据包
//                                System.arraycopy(ByteAndInt.longToByteArray(file.fileSize), 0, sendFileCmd, 100, 8);
//                                output = socket.getOutputStream();//构造一个输出流
//                                output.write(sendFileCmd);//把头数据包发给对方
//                                output.flush();
//                                sleep(1000);//sleep 1秒钟，等待对方处理完
//                                //定义数据发送缓冲区
//                                byte[] readBuffer = new byte[Constant.readBufferSize];//文件读写缓存
//                                input = new FileInputStream(new File(file.fileName));//打开一个文件输入流
//                                int readSize = 0;
//                                int length = 0;
//                                long count = 0;
//                                FileState fs = getFileStateByName(file.getFileName(), beSendFileNames);
//                                while (-1 != (readSize = input.read(readBuffer))) {//循环把文件内容发送给对方
//                                    output.write(readBuffer, 0, readSize);//把内容写到输出流中发送给对方
//                                    output.flush();
//                                    length += readSize;
//
//                                    count++;
//                                    if (count % 10 == 0) {
//                                        fs.currentSize = length;
//                                        fs.percent = ((int) ((Float.valueOf(length) / Float.valueOf(fs.fileSize)) * 100));
////                                        Intent intent = new Intent();
////                                        intent.setAction(Constant.fileSendStateUpdateAction);
////                                        sendBroadcast(intent);
//                                    }
//                                }
//                                fs.currentSize = length;
//                                fs.percent = ((int) ((Float.valueOf(length) / Float.valueOf(fs.fileSize)) * 100));
////                                Intent intent = new Intent();
////                                intent.setAction(Constant.fileSendStateUpdateAction);
////                                sendBroadcast(intent);
//                            } catch (Exception e) {
//                                //往界面层发送文件传输出错信息
////                                Intent intent = new Intent();
////                                intent.putExtra("msg", e.getMessage());
////                                intent.setAction(Constant.dataSendErrorAction);
////                                sendBroadcast(intent);
//                                e.printStackTrace();
//                            } finally {
//                                try {
//                                    if (null != output) output.close();
//                                    if (null != input) input.close();
//                                    if (!socket.isClosed()) socket.close();
//                                } catch (Exception e1) {
//                                    e1.printStackTrace();
//                                }
//                            }
//                        }
//                    }.start();
//                }
//            }

            //根据文件名从文件状态列表中获得该文件状态
            private FileState getFileStateByName(String fileName, ArrayList<FileState> fileStates) {
                for (FileState fileState : fileStates) {
                    if (fileState.fileName.equals(fileName)) {
                        return fileState;
                    }
                }
                return null;
            }

            public void release() {
                try {
                    System.out.println("File handler socket closed ...");
                    if (null != sSocket) sSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //保存接收到的数据
            private class SaveFileToDisk extends Thread {
                private Socket socket = null;

                public SaveFileToDisk(Socket socket) {
                    this.socket = socket;
                }

                @Override
                public void run() {
                    super.run();
                    OutputStream output = null;
                    InputStream input = null;
                    try {
                        byte[] recvFileCmd = new byte[Constant.bufferSize];//接收对方第一次发过来的数据，该数据包中包含了要发送的文件名
                        input = socket.getInputStream();
                        input.read(recvFileCmd);//读取对方发过来的数据
                        int cmdType = recvFileCmd[4];//按协议这位为命令类型
                        int oprCmd = recvFileCmd[5];//操作命令
                        if (cmdType == Constant.CMD_TYPE1 && oprCmd == Constant.OPR_CMD6) {
                            byte[] fileNameBytes = new byte[Constant.fileNameLength];//从收到的数据包中提取文件名
                            System.arraycopy(recvFileCmd, 10, fileNameBytes, 0, Constant.fileNameLength);
                            StringBuffer sb = new StringBuffer();
                            String fName = new String(fileNameBytes).trim();
                            sb.append(fileSavePath).append(File.separator).append(fName);//组合成完整的文件名
                            String fileName = sb.toString();
                            File file = new File(fileName);//根据获得的文件名创建文件
                            //定义数据接收缓冲区，准备接收对方传过来的文件内容
                            byte[] readBuffer = new byte[Constant.readBufferSize];
                            output = new FileOutputStream(file);//打开文件输出流准备把接收到的内容写到文件中
                            int readSize = 0;
                            int length = 0;
                            long count = 0;
                            FileState fs = getFileStateByName(fName, receivedFileNames);

                            while (-1 != (readSize = input.read(readBuffer))) {//循环读取内容
                                output.write(readBuffer, 0, readSize);//把接收到的内容写到文件中
                                output.flush();
                                length += readSize;
                                count++;
                                if (count % 10 == 0) {
                                    fs.currentSize = length;
                                    fs.percent = ((int) ((Float.valueOf(length) / Float.valueOf(fs.fileSize)) * 100));
//                                    Intent intent = new Intent();
//                                    intent.setAction(Constant.fileReceiveStateUpdateAction);
//                                    sendBroadcast(intent);
                                }
                            }
                            fs.currentSize = length;
                            fs.percent = ((int) ((Float.valueOf(length) / Float.valueOf(fs.fileSize)) * 100));
//                            Intent intent = new Intent();
//                            intent.setAction(Constant.fileReceiveStateUpdateAction);
//                            sendBroadcast(intent);
                        } else {
//                            Intent intent = new Intent();
//                            intent.putExtra("msg", getString(R.string.error));
//                            intent.setAction(Constant.dataReceiveErrorAction);
//                            sendBroadcast(intent);
                        }
                    } catch (Exception e) {
//                        Intent intent = new Intent();
//                        intent.putExtra("msg", e.getMessage());
//                        intent.setAction(Constant.dataReceiveErrorAction);
//                        sendBroadcast(intent);
                        e.printStackTrace();
                    } finally {
                        try {
                            if (null != input) input.close();
                            if (null != output) output.close();
                            if (!socket.isClosed()) socket.close();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
        //=========================TCP文件传输模块结束==============================================================

        //=========================TCP语音传输模块==================================================================
        //基于Tcp语音传输模块
//        private class AudioHandler extends Thread {
//            private ServerSocket sSocket = null;
//
//            //	private G711Codec codec;
//            public AudioHandler() {
//            }
//
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    sSocket = new ServerSocket(Constant.AUDIO_PORT);//监听音频端口
//                    System.out.println("Audio Handler socket started ...");
//                    while (!sSocket.isClosed() && null != sSocket) {
//                        Socket socket = sSocket.accept();
//                        socket.setSoTimeout(5000);
//                        audioPlay(socket);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
////            //用来启动音频播放子线程
////            public void audioPlay(Socket socket) {
////                new AudioPlay(socket).start();
////            }
////
////            //用来启动音频发送子线程
////            public void audioSend(Person person) {
////                new AudioSend(person).start();
////            }
//
//            public void release() {
//                try {
//                    System.out.println("Audio handler socket closed ...");
//                    if (null != sSocket) sSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            //音频播线程
////            public class AudioPlay extends Thread {
////                Socket socket = null;
////
////                public AudioPlay(Socket socket) {
////                    this.socket = socket;
////                    //	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
////                }
////
////                @Override
////                public void run() {
////                    super.run();
////                    try {
////                        InputStream is = socket.getInputStream();
////                        //获得音频缓冲区大小
////                        int bufferSize = AudioTrack.getMinBufferSize(8000,
////                                AudioFormat.CHANNEL_CONFIGURATION_MONO,
////                                AudioFormat.ENCODING_PCM_16BIT);
////
////                        //获得音轨对象
////                        AudioTrack player = new AudioTrack(AudioManager.STREAM_MUSIC,
////                                8000,
////                                AudioFormat.CHANNEL_CONFIGURATION_MONO,
////                                AudioFormat.ENCODING_PCM_16BIT,
////                                bufferSize,
////                                AudioTrack.MODE_STREAM);
////
////                        //设置喇叭音量
////                        player.setStereoVolume(1.0f, 1.0f);
////                        //开始播放声音
////                        player.play();
////                        byte[] audio = new byte[160];//音频读取缓存
////                        int length = 0;
////
////                        while (!isStopTalk) {
////                            length = is.read(audio);//从网络读取音频数据
////                            if (length > 0 && length % 2 == 0) {
////                                //	for(int i=0;i<length;i++)audio[i]=(byte)(audio[i]*2);//音频放大1倍
////                                player.write(audio, 0, length);//播放音频数据
////                            }
////                        }
////                        player.stop();
////                        is.close();
////                        socket.close();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
//
//            //音频发送线程
////            public class AudioSend extends Thread {
////                Person person = null;
////
////                public AudioSend(Person person) {
////                    this.person = person;
////                    //	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
////                }
////
////                @Override
////                public void run() {
////                    super.run();
////                    Socket socket = null;
////                    OutputStream os = null;
////                    AudioRecord recorder = null;
////                    try {
////                        socket = new Socket(person.ipAddress, Constant.AUDIO_PORT);
////                        socket.setSoTimeout(5000);
////                        os = socket.getOutputStream();
////                        //获得录音缓冲区大小
////                        int bufferSize = AudioRecord.getMinBufferSize(8000,
////                                AudioFormat.CHANNEL_CONFIGURATION_MONO,
////                                AudioFormat.ENCODING_PCM_16BIT);
////
////                        //获得录音机对象
////                        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
////                                8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
////                                AudioFormat.ENCODING_PCM_16BIT,
////                                bufferSize * 10);
////
////                        recorder.startRecording();//开始录音
////                        byte[] readBuffer = new byte[640];//录音缓冲区
////
////                        int length = 0;
////
////                        while (!isStopTalk) {
////                            length = recorder.read(readBuffer, 0, 640);//从mic读取音频数据
////                            if (length > 0 && length % 2 == 0) {
////                                os.write(readBuffer, 0, length);//写入到输出流，把音频数据通过网络发送给对方
////                            }
////                        }
////                        recorder.stop();
////                        os.close();
////                        socket.close();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
//        }
        //=========================TCP语音传输模块结束==================================================================
    }

    private class ServiceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.WIFIACTION) || intent.getAction().equals(Constant.ETHACTION)) {
                new CheckNetConnectivity().start();
//            } else if (intent.getAction().equals(Constant.imAliveNow)) {
//                ;
            }
        }
    }

    private class CheckNetConnectivity extends Thread {
        public void run() {
            try {
                if (!wifiManager.isWifiEnabled()) {
                    return;
                }
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            if (inetAddress.isReachable(1000)) {
                                localInetAddress = inetAddress;
                                localIp = inetAddress.getHostAddress().toString();
                                localIpBytes = inetAddress.getAddress();
                                System.arraycopy(localIpBytes, 0, regBuffer, 44, 4);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class UpdateMe extends Thread {
        @Override
        public void run() {
            while (!isStopUpdateMe) {
                try {
                    comBridge.joinOrganization();
                    sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
