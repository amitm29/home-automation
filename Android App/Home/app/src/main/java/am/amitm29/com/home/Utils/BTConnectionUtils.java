package am.amitm29.com.home.Utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public class BTConnectionUtils {
    public static InputStream mmInStream;
    private static OutputStream mmOutStream;
    private static BluetoothAdapter myBluetooth = null;
    private static BluetoothSocket btSocket = null;
    public static String message = "";
    public static String mAddress;
    public static UUID mUUID;
    public static boolean isBtConnected = false;


    public static int disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
                isBtConnected = false;
            }
            catch (IOException e)
            { return -1;}      //returns -1 if an exception has occurred
        }
        return 0;       //returns 0 if connection disconnected successfully
    }

    /*
        * creates a BluetoothSocket socket connection with the device
        * with passed in address and UUID
        * and gets the InputStream and OutputStream of the BluetoothSocket
        * return if the connection was successful or not
     */
    public static boolean connect(String address, UUID myUUID){
        try
        {
            if (btSocket == null || !isBtConnected)
            {
                myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                btSocket.connect();//start connection
                mmInStream = btSocket.getInputStream();
                mmOutStream = btSocket.getOutputStream();
            }
        }
        catch (IOException e)
        {
            return false;//if the try failed, you can check the exception here
        }
        mAddress = address;
        mUUID = myUUID;
        return true;
    }

    /*
        * this function writes the String data to the OutputStream of the BluetoothSocket
     */
    public static boolean writeBT(String str){
        if(btSocket!=null) {
            try {
                mmOutStream.write(str.getBytes());
                return true;
            } catch (IOException e) {

                isBtConnected = false;
                return false;
            }
        }
        else
            return false;
    }

//    public static void reconnect(){
//        disconnect();
//        int count = 0;
//        while(count<5) {
//            try {
//                connect(mAddress, mUUID);
//                break;
//            } catch (Exception e) {
//                count++;
//            }
//        }
//
//    }



}
