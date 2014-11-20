package com.example.fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.util.InetAddressUtils;

import android.util.Log;

public class SocketUtil {

    private static final int QUEUE_LENGTH = 5;

    private static Socket clientSocket;
    private static ISocketResult socketListener = null;
    private static String serverAddress;
    private static int port;
    private static String message;

    private static ServerSocket serverSocket;
    private static ArrayList<Socket> clientList;

    private static boolean isConnecting = false;

    /**
     * 创建客户端socket
     * 
     * @param ip 服务端地�?
     * @param port 端口
     * @param iSocketResult 回调
     */
    public static void createSocket(String ip, int port, ISocketResult iSocketResult) {
        socketListener = iSocketResult;
        serverAddress = ip;
        SocketUtil.port = port;

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    socketListener.onConnecting();
                    clientSocket = new Socket();
                    SocketAddress socAddress = new InetSocketAddress(serverAddress, SocketUtil.port);
                    clientSocket.connect(socAddress, 5000);
                    socketListener.onConnectSuccess();
                } catch (Exception e) {
                    socketListener.onConnectFailed();
                }
            }
        }).start();

    }

    /**
     * 客户端发送消�?
     * 
     * @param msg 消息
     */
    public static void sendClientMessage(String msg) {
        message = msg;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PrintWriter out =
                        new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())),
                            true);
                    out.println(message);
                    socketListener.onSendSuccess();
                    receiveClientMessage();
                } catch (Exception e) {
                    socketListener.onSendFailed();
                }
            }
        }).start();

    }

    /**
     * 接收消息
     * 
     */
    public static void receiveClientMessage() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client = clientSocket;
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String res = "";
                    String msg;
                    while (isConnecting) {
                        while ((msg = br.readLine()) != null) {
                            socketListener.onReceiveMessage(msg);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 创建服务端socket
     * 
     * @param port 端口
     * @param iSocketResult 回调
     */
    public static void createServerSocket(int port, ISocketResult iSocketResult) {
        socketListener = iSocketResult;
        SocketUtil.port = port;
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    // 创建ServerSocket
                    serverSocket = new ServerSocket(SocketUtil.port);
                    while (true) {
                        // 接受客户端请�?
                        Socket client = serverSocket.accept();
                        clientList.add(client);
                        clientSocket = client;
                        receiveClientMessage();
                        if (clientList.size() >= QUEUE_LENGTH)
                            break;
                    }
                } catch (Exception e) {

                }
            }
        }).start();

    }

    /**
     * 服务端发送消�?
     * 
     * @param msg 消息
     */
    public static void sendServerMessage(String msg) {
        message = msg;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < clientList.size(); i++) {
                        PrintWriter out =
                            new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientList.get(i)
                                .getOutputStream())), true);
                        out.println(message);
                    }
                    socketListener.onSendSuccess();
                } catch (Exception e) {
                    socketListener.onSendFailed();
                }
            }
        }).start();

    }

    public static String getLocAddress() {

        String ipaddress = "";

        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历�?��的网络接�?
            while (en.hasMoreElements()) {
                NetworkInterface networks = en.nextElement();
                // 得到每一个网络接口绑定的�?��ip
                Enumeration<InetAddress> address = networks.getInetAddresses();
                // 遍历每一个接口绑定的�?��ip
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
                        ipaddress = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("", "获取本地ip地址失败");
            e.printStackTrace();
        }

        // System.out.println("本机IP:" + ipaddress);

        return ipaddress;

    }

    public static void setISocketResultListener(SocketUtil.ISocketResult iResult) {
        socketListener = iResult;
    }

    public static void stopConnect() {
        isConnecting = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
                for (int i = 0; i < clientList.size(); i++) {
                    clientList.get(i).close();
                }
            } else if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (Exception e) {

        }
    }

    public static boolean isIpAddress(String msg) {
        Pattern pattern =
            Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
        Matcher matcher = pattern.matcher(msg);
        return matcher.matches();
    }

    public abstract interface ISocketResult {
        public void onConnecting();// 连接�?

        public void onConnectFailed();// 连接失败

        public void onConnectSuccess();// 连接成功

        public void onSendSuccess();

        public void onSendFailed();

        public void onReceiveMessage(String res);
    }
}
