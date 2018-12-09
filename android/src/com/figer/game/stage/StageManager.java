package com.figer.game.stage;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.badlogic.gdx.utils.Array;

public class StageManager {
    public static final int NULL = -1;
    public static final int INITIAL = 0;
    private int number;
    private int requestNumber;

    private BluetoothDevice connectedDevice;
    private Handler handler;

    public StageManager(){
        number = NULL;
        requestNumber = INITIAL;
    }

    public int getNumber(){
        return number;
    }

    public void requestNumber(int request){
        requestNumber = request;
    }

    public void update(Array<Stage> stages){
        if (number != requestNumber) {
            if (number != StageManager.NULL)
                stages.get(number).onDeactivating();
            number = requestNumber;
            stages.get(requestNumber).onActivating();
        }
    }

    public BluetoothDevice getConnectedDevice() {
        return connectedDevice;
    }

    public Handler getHandler(){
        return handler;
    }

    public void setConnectedDevice(BluetoothDevice connectedDevice) {
        this.connectedDevice = connectedDevice;
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }
}
