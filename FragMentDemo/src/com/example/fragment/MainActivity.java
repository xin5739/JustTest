package com.example.fragment;

import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.fragment.QuickSortUtil.minMethod;
import com.example.fragmentdemo.R;

import de.greenrobot.event.EventBus;

public class MainActivity extends FragmentActivity implements SocketUtil.ISocketResult, UserDialog.IdialogClickResult {

    Button btn;
    UserFragment mFragment;
    FrameLayout frameLayout;

    int mDownX, mDownY;
    int maxLen = 10;
    EditText editText;

    TextView disPlay;
    BatteryReceiver batteryReceiver;
    WifiManager wifiManager;
    InjectReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                btn.setText("����");
                mFragment.setBtnText("");
                Character array[] = new Character[editText.getText().length()];
                for (int i = 0; i < editText.getText().length(); i++) {
                    array[i] = new Character(editText.getText().charAt(i));
                }
                QuickSortUtil.sort(array, 0, editText.getText().length() - 1);
                String res = "";
                for (int i = 0; i < editText.getText().length(); i++) {
                    res += array[i];
                }
                editText.setText(res);
                getNum();
            }
        });
        loadFragment();

        editText = (EditText) findViewById(R.id.edit_test);
        disPlay = (TextView) findViewById(R.id.num_display);
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        mReceiver = new InjectReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("Intent.ACTION_INJECT2");
        // ��̬ע��BroadcastReceiver
        registerReceiver(mReceiver, filter);
    }

    // ��/�ر� wifi
    public boolean openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            return wifiManager.setWifiEnabled(true);
        } else {
            return wifiManager.setWifiEnabled(false);
        }

    }

    public void getNum() {
        try {
            String string = "";
            // �õ�contentresolver����
            ContentResolver cr = getContentResolver();
            // ȡ�õ绰���п�ʼһ��Ĺ�꣬������moveToNext()
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                // ȡ����ϵ�˵���������
                int nameIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
                String contact = cursor.getString(nameIndex);
                string += (contact + ":" + "/n");

                // ȡ����ϵ�˵�ID����ֵ
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                // ��ѯ��λ��ϵ�˵ĵ绰���룬���ƵĿ��Բ�ѯemail��photo
                Cursor phone =
                    cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);// ��һ��������ȷ����ѯ�绰�ţ������������ǲ�ѯ����ĳ���˵Ĺ���ֵ
                // һ���˿����м�������
                while (phone.moveToNext()) {
                    String strPhoneNumber =
                        phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    string += (strPhoneNumber + "/n");
                }
                phone.close();
            }
            cursor.close();
            // ������ʾ����
            disPlay.setText(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class InjectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "Intent.ACTION_INJECT2") {
                String res = intent.getStringExtra("inject");
                Log.i("aaaa", "resultWho:a frag " + res);
                UserDialog.isSaveChangeDialog(MainActivity.this, MainActivity.this, res.substring(0, res.indexOf("*")),
                    res.substring(res.indexOf("*") + 1));
            }
        }

    }

    /**
     * �ж������Ƿ����
     * 
     * @param context
     * @return
     */
    public static boolean isConnectNet(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            if (connManager.getActiveNetworkInfo().isAvailable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * �㲥������
     */
    class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // �ж����Ƿ���Ϊ�����仯��Broadcast Action
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                // ��ȡ��ǰ����
                int level = intent.getIntExtra("level", 0);
                // �������̶ܿ�
                int scale = intent.getIntExtra("scale", 100);
                // ����ת�ɰٷֱ�
                disPlay.setText("��ص���Ϊ" + ((level * 100) / scale) + "%");
            }
        }

    }

    public void getLocation() {// ��ȡLocationͨ��LocationManger��ȡ��
        LocationManager locManger = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location loc = locManger.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.d("aaaaa ", "aaaaa loc" + " " + loc);
        if (loc != null)
            disPlay.setText(loc.getLatitude() + " " + loc.getLongitude());
    }

    private void loadFragment() {
        mFragment = (UserFragment) this.getSupportFragmentManager().findFragmentById(R.id.fl_fragment);
        if (mFragment == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            mFragment = new UserFragment();
            ft.replace(R.id.fl_fragment, mFragment).commit();
        }
        frameLayout = (FrameLayout) findViewById(R.id.fl_fragment);
        frameLayout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                int x = (int) arg1.getRawX();
                int y = (int) arg1.getRawY();
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    mDownX = x;
                    mDownY = y;
                } else if (arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    frameLayout.setX(frameLayout.getX() + (x - mDownX));
                    frameLayout.setY(frameLayout.getY() + (y - mDownY));
                    mDownX = x;
                    mDownY = y;
                }
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mReceiver);
    }

    public void onEvent(String str) {
        btn.setText("����");
        Node array[] = new Node[4];
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            array[i] = new Node();
            array[i].x = random.nextInt() % 20;
            array[i].y = random.nextInt() % 20;
        }
        minMethod<Node> method = new minMethod<Node>() {

            @Override
            public Node min(Node t1, Node t2) {

                if (t1.x < t2.x)
                    return t1;
                else
                    return t2;
            }
        };
        QuickSortUtil.sort(array, 0, 3, method);
        String res = "";
        for (int i = 0; i < array.length; i++) {
            res += array[i].x + "  " + array[i].y + "\n";
        }
        editText.setText(res);
    }

    @Override
    public void onConnecting() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectFailed() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectSuccess() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendSuccess() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendFailed() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceiveMessage(String res) {
        // TODO Auto-generated method stub
        Log.i("aaaa", "resultWho:a frag " + res);
        UserDialog.isSaveChangeDialog(this, this, res.substring(0, res.indexOf("*")),
            res.substring(res.indexOf("*") + 1));
    }

    @Override
    public void clickOnYes(String userName) {
        // TODO Auto-generated method stub
        Intent a = new Intent();
        a.setAction("Intent.ACTION_INJECT1");
        a.putExtra("inject", 1);
        MainActivity.this.sendBroadcast(a);
    }

    @Override
    public void clickOnNo() {
        // TODO Auto-generated method stub
        Intent a = new Intent();
        a.setAction("Intent.ACTION_INJECT1");
        a.putExtra("inject", 0);
        MainActivity.this.sendBroadcast(a);
    }
}
