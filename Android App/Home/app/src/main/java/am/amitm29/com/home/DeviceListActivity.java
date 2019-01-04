package am.amitm29.com.home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import am.amitm29.com.home.Adapters.DeviceListAdapter;
import am.amitm29.com.home.Database.Device;
import am.amitm29.com.home.Utils.BTConnectionUtils;

/*
    * This Activity shows the list of paired bluetooth devices
 */
public class DeviceListActivity extends AppCompatActivity implements DeviceListAdapter.ListItemClickListener{

    RecyclerView deviceRV;

    //Adapter
    DeviceListAdapter mAdapter;
    private static DeviceListActivity mInstance;
    public ProgressDialog progress;
    public static String address;
    public ConnectedThread mConnectedThread;
    private static String LOG_TAG = "DeviceListActivity";



    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Device List");

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        deviceRV = findViewById(R.id.devices_rv);

        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null)
        {
            //Show a message that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        deviceRV.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(deviceRV.getContext(),
                layoutManager.getOrientation());
        deviceRV.addItemDecoration(dividerItemDecoration);

        deviceRV.setItemAnimator(new DefaultItemAnimator());

        deviceRV.setHasFixedSize(true);
        mAdapter = new DeviceListAdapter(this, null, this);
        deviceRV.setAdapter(mAdapter);

        mInstance = this;

    }

    /*
        * gets the list of paired bluetooth devices
     */
    private ArrayList<Device> getPairedDevicesList() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList<Device> list = new ArrayList<Device>();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                //Get the device's name and the address
                list.add(new Device(bt.getName(), bt.getAddress(), bt.getBluetoothClass().getMajorDeviceClass()));
            }
            return list;
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Devices Found.", Toast.LENGTH_LONG).show();
            return null;
        }
    }
    @Override
    public void onListItemClicked(int clickedItemIndex) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<Device> list = getPairedDevicesList();
        mAdapter.swapList(list);
    }

    public void connectBTAndInitialize()
    {
        new ConnectBT().execute(); //start AsyncTask to connect
    }

    public static DeviceListActivity getmInstance() {
        if(mInstance==null)
            return new DeviceListActivity();
        else
            return mInstance;
    }



    private class ConnectBT extends AsyncTask<Void, Void, Void>
    {
        private boolean connectSuccess = true;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(DeviceListActivity.this, "Connecting...", "Please wait!");
            //show a progress dialog
        }

        //while the progress dialog is shown, the connection is done in background
        @Override
        protected Void doInBackground(Void... devices)
        {

            /*
                * try to connect to the device thrice
                * so that if the connection doesn't establish in one attempt
                * then user won't have to click on connect again and again
             */
            int trialNum = 0;
            while (trialNum<3) {
                try {
                    connectSuccess = BTConnectionUtils.connect(address, myUUID);
                    if (!connectSuccess)
                        throw new Exception();
                    break;
                } catch (Exception e) {
                    trialNum += 1;
                    Log.d(LOG_TAG, "trialNum : " + trialNum);
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!connectSuccess)
            {
                Toast.makeText(DeviceListActivity.this,"Connection Failed. Is it a SPP Bluetooth? Try again.", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
            else
            {
                Toast.makeText(DeviceListActivity.this,"Connected.", Toast.LENGTH_SHORT).show();
                BTConnectionUtils.isBtConnected = true;
                progress.dismiss();

                mConnectedThread = new ConnectedThread();
                mConnectedThread.start();

                finish();
            }

        }
    }


    /*
        * this thread is responsible for getting the input data
        * it receives all the data that is sent by the bluetooth device
        *
        * the data is received in the form of bytes
        * which are then sent to the handler to obtain the string message
        * and then the handler calls the static method handleIncomingData
        * of the HomeActivity which then calls respective methods depending
        * on the incoming data
     */
    public static class ConnectedThread extends Thread {
        public ConnectedThread() {}

        public void run() {
            byte[] buffer = new byte[2048];
            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += BTConnectionUtils.mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for (int i = begin; i < bytes; i++) {
                        if (buffer[i] == "#".getBytes()[0]) {
                            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if (i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.d("Message", "ConnectedThread IO exception occurred");
                    break;
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int)msg.arg1;
            int end = (int)msg.arg2;

            switch(msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    BTConnectionUtils.message = writeMessage;
                    Log.d("Message", BTConnectionUtils.message);
                    HomeActivity.handleIncomingData(BTConnectionUtils.message);
                    break;
            }
        }
    };


}



