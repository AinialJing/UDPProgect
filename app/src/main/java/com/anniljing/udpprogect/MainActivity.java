package com.anniljing.udpprogect;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PORT = 2000;
    private AppCompatTextView ip;
    private EditText etTargetIp;
    private AppCompatEditText msg;
    private RecyclerView mRecyclerView;
    private MessageAdapter mMessageAdapter;
    private List<MessageEntity> mEntities;
    private String targetIp;
    private DatagramSocket receivedSocket;
    private byte[] receiveData;
    private DatagramPacket packet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip = findViewById(R.id.ip);
        etTargetIp = findViewById(R.id.targetIp);
        msg = findViewById(R.id.msg);
        mRecyclerView = findViewById(R.id.rv);
        ip.setText(getIPAddress());
        try {
            receivedSocket = new DatagramSocket(null);
            receivedSocket.setReuseAddress(true);
            receivedSocket.bind(new InetSocketAddress(PORT));
            receiveData = new byte[1024];
            packet = new DatagramPacket(receiveData, receiveData.length);
            new Thread(new MyReceiveThread()).start();
        } catch (SocketException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        mEntities = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(mEntities);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMessageAdapter);
    }

    public void sendMsg(View view) {
        targetIp = etTargetIp.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    String message = msg.getText().toString();
                    byte[] msgs = message.getBytes("UTF-8");
                    InetAddress address = InetAddress.getByName(targetIp);
                    DatagramPacket packet = new DatagramPacket(msgs, msgs.length);
                    packet.setAddress(address);
                    packet.setPort(PORT);
                    socket.send(packet);
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setRole(MessageEntity.ROLE_HOST);
                    messageEntity.setMessage(message);
                    mEntities.add(messageEntity);
                    etTargetIp.post(() -> mMessageAdapter.notifyDataSetChanged());
                } catch (SocketException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }).start();
    }

    public String getIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(':') == -1) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class MyReceiveThread implements Runnable {
        @Override
        public void run() {
            //要循环接收数据
            while (true) {
                try {
                    Log.d(TAG, "udp waiting....");
                    receivedSocket.receive(packet);
                    //获取数据的时候，要使用带有offset的
                    String msg = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                    Log.e(TAG, "Received msg:" + msg);
                    targetIp = packet.getAddress().getHostAddress();
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setRole(MessageEntity.ROLE_CLIENT);
                    messageEntity.setMessage(msg);
                    mEntities.add(messageEntity);
                    etTargetIp.post(() -> mMessageAdapter.notifyDataSetChanged());
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    receivedSocket.close();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receivedSocket != null) {
            receivedSocket.disconnect();
            receivedSocket.close();
        }
    }
}