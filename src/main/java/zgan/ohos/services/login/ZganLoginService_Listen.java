package zgan.ohos.services.login;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import zgan.ohos.services.push.ZganSocketClient;
import zgan.ohos.utils.Frame;
import zgan.ohos.utils.FrameTools;
import zgan.ohos.utils.PreferenceUtil;
import zgan.ohos.utils.SystemUtils;
import zgan.ohos.utils.generalhelper;

public class ZganLoginService_Listen implements Runnable {

    private ZganSocketClient zsc;
    private static final String TAG="ZganLoginService_Listen";
    public static int ServerState = 0;
    private Context _context;
    private boolean iniNetState = false;
    //子线程中的handler，用于APP网络连接后的自动登录操作
    Handler myhandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {//autologin
                Frame frame = (Frame) msg.obj;
                String result = generalhelper.getSocketeStringResult(frame.strData);
                String[] results = result.split(",");
                if (frame.subCmd == 1 && results[0].equals("0")) {
                    SystemUtils.setIsLogin(true);
                    ZganLoginService.BroadError("1");
                    ZganLoginService.toGetServerData(24, 0, PreferenceUtil.getUserName(), myhandler);
                    Log.v(TAG, "ZganLoginService_Listen自动重新登录成功");
                    Log.i(TAG, "ZganLoginService_Listen自动重新登录成功");
                } else if (frame.subCmd == 24) {
                    String communityId = PreferenceUtil.getCommunityId();
                    if (results.length == 2 && results[0].equals("0")) {
                        Log.v(TAG, "ZganLoginService_Listen小区ID：" + results[1]);
                        Log.i(TAG, "ZganLoginService_Listen小区ID：" + results[1]);
                        if (!communityId.equals(results[1])) {
                            PreferenceUtil.setCommunityId(results[1]);
                        }
                    }
                } else {
                    Log.v(TAG, "ZganLoginService_Listen自动重新登录失败");
                    Log.i(TAG, "ZganLoginService_Listen自动重新登录失败");
                }
            }
        }
    };

    public ZganLoginService_Listen(Context context) {
        _context = context;
    }

    public void newSocketClient() {

        if (zsc != null) {
            zsc.toCloseClient();
        }
        zsc = new ZganSocketClient(ZganLoginService.LoginService_IP, ZganLoginService.ZGAN_LOGIN_PORT,
                ZganLoginServiceTools.PushQueue_Send, ZganLoginServiceTools.PushQueue_Receive);
        //zsc.ZganReceiveTime = 500;
        zsc.toStartClient();
        zsc.toStartPing(1, FrameTools.Frame_MainCmd_Ping);
        zsc.ThreadName = "ZganLoginService";
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        newSocketClient();
        boolean isNet = ZganLoginService.isNetworkAvailable(_context);
        iniNetState = isNet;
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                break;
            }
            try {
                isNet = ZganLoginService.isNetworkAvailable(_context);

                if (ServerState == 1) {
                    //用户打开网络后自动登录操作
                    if (!iniNetState) {
                        iniNetState = true;
                        ZganLoginService.toAutoUserLogin(myhandler);
                    }
                    if (!isNet) {
                        ServerState = 2;
                        ZganLoginService.BroadError("网络连接错误");
                        Log.v(TAG, "ZganLoginService_Listen1ServerState=" + ServerState);
                        Log.i(TAG, "ZganLoginService_Listen1ServerState=" + ServerState);
                    }

                    if (zsc.client.isClosed()) {
                        ServerState = 2;
                        Log.v(TAG, "ZganLoginService_Listen2ServerState=" + ServerState);
                        Log.i(TAG, "ZganLoginService_Listen2ServerState=" + ServerState);
                    }

                    if (!zsc.isRun) {
                        ServerState = 2;
                        Log.v(TAG, "ZganLoginService_Listen3ServerState=" + ServerState);
                        Log.i(TAG, "ZganLoginService_Listen3ServerState=" + ServerState);
                    }

                } else if (ServerState == 0) {

                    if (isNet) {
                        Log.i(TAG, "ZganLoginService_Listen client 重新连接");
                        Log.v(TAG, "ZganLoginService_Listen client 重新连接");
                        ServerState = 3;
                        zsc.Server_IP = ZganLoginService.toGetHostIP();
                        Log.v(TAG, "ZganLoginService_Listenconnect to=" + zsc.Server_IP);
                        Log.i(TAG, "ZganLoginService_Listenconnect to=" + zsc.Server_IP);
                        if (zsc.toConnectServer()) {
                            ServerState = 1;
                            Log.v(TAG, "ZganLoginService_Listen5ServerState=" + ServerState);
                            Log.i(TAG, "ZganLoginService_Listen5ServerState=" + ServerState);
                            ZganLoginServiceTools.isConnect = true;
                            //LoginMsgServer(UName);
                            ZganLoginService.toAutoUserLogin(myhandler);
                        } else {
                            Log.v(TAG, zsc.client == null ? "空 socket" : "非空socket");
                            Log.i(TAG, zsc.client == null ? "空 socket" : "非空socket");
                            Log.v(TAG, zsc.client.isClosed() ? "socket关闭状态" : "socket打开状态");
                            Log.i(TAG, zsc.client.isClosed() ? "socket关闭状态" : "socket打开状态");
                            if (zsc.client != null && !zsc.client.isClosed())
                                zsc.toConnectDisconnect();
                            //newSocketClient();
                            ServerState = 0;
                            Log.v(TAG, "ZganLoginService_Listen6ServerState=" + ServerState);
                            Log.i(TAG, "ZganLoginService_Listen6ServerState=" + ServerState);
                        }

                    }

                } else if (ServerState == 2) {
                    //网络断开
                    ZganLoginServiceTools.isConnect = false;
                    Log.v(TAG, "ZganLoginService_Listenclient 断开连接");
                    Log.i(TAG, "ZganLoginService_Listenclient 断开连接");
                    zsc.toConnectDisconnect();
                    ServerState = 0;
                    Log.v(TAG, "7ServerState=" + ServerState);
                   // Log.v(TAG, "client 重新连接");
                    //toConnectServer();
                }
            }catch (Exception e)
            {
                Log.v(TAG,"ZganLoginService_Listen"+e.getMessage());
                Log.i(TAG,"ZganLoginService_Listen"+e.getMessage());
                continue;
            }
        }
        //ZganLoginService.myhandler.sendEmptyMessageDelayed(0,500);
    }

    public boolean toConnectServer() {
        Log.v(TAG, zsc.isRun ? "繁忙" : "空闲");
        if (!zsc.isRun)
            if (zsc.toConnectServer()) {
                ServerState = 1;
                return true;
            }
        return false;
    }
    public void toDisConnectServer()
    {
        Log.v(TAG,"toDisConnectServer");
        ServerState=0;
        zsc.toCloseClient();
    }

}
