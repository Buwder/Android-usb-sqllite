package com.example.hrv.smarthrv;


import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service  {

	 //private final static String TAG = BluetoothLeService.class.getSimpleName();
	private final static String TAG = "SmartHRV";

	
	    //ͨ��BluetoothManager����ȡBluetoothAdapter �������з���,��Ҫ��ע��ͻ�ȡ,���ҵ���������һ��������,ͨ��init����
	    private BluetoothManager mBluetoothManager;
	    //��������Ҫ,��Ҫ����������,�ر�,��ȡ����豸�ȵ�
	    private BluetoothAdapter mBluetoothAdapter;
	    
	    //b) BluetoothGatt��Ϊ������ʹ�úʹ������ݣ�BluetoothGattCallback���������״̬���ܱ��ṩ�����ݡ�
	    //�̳�BluetoothProfile��ͨ��BluetoothGatt���������豸��connect��,���ַ���discoverServices����������Ӧ�����Է��ص�BluetoothGattCallback 
	    private BluetoothGatt mBluetoothGatt;

	    public final static String ACTION_GATT_CONNECTED =
	            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	    public final static String ACTION_GATT_DISCONNECTED =
	            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	    public final static String ACTION_GATT_SERVICES_DISCOVERED =
	            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	    public final static String ACTION_DATA_AVAILABLE =
	            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	    public final static String EXTRA_DATA =
	            "com.example.bluetooth.le.EXTRA_DATA";

	    public final static UUID UUID_NOTIFY =
	            UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
	    public final static UUID UUID_SERVICE =
	        UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
	    
	    public BluetoothGattCharacteristic mNotifyCharacteristic;
	    private Listener mListener;
	    
	    public synchronized Listener getListener() {
	        return mListener;
	    }
	    
	    public void WriteValue(String strValue)
	    {
	    	mNotifyCharacteristic.setValue(strValue.getBytes());
	    	mBluetoothGatt.writeCharacteristic(mNotifyCharacteristic);
	    }
	    
	    public void findService(List<BluetoothGattService> gattServices)
	    {
	    	Log.i(TAG, "Count is:" + gattServices.size());
	    	for (BluetoothGattService gattService : gattServices) 
	    	{
	    		//Log.i(TAG, gattService.getUuid().toString());
				//Log.i(TAG, UUID_SERVICE.toString());
	    		if(gattService.getUuid().toString().equalsIgnoreCase(UUID_SERVICE.toString()))
	    		{
	    			List<BluetoothGattCharacteristic> gattCharacteristics =
	                    gattService.getCharacteristics();
	    			//Log.i(TAG, "Count is:" + gattCharacteristics.size());
	    			for (BluetoothGattCharacteristic gattCharacteristic :
	                    gattCharacteristics) 
	    			{
	    				if(gattCharacteristic.getUuid().toString().equalsIgnoreCase(UUID_NOTIFY.toString()))
	    				{
	    					//Log.i(TAG, gattCharacteristic.getUuid().toString());
	    					//Log.i(TAG, UUID_NOTIFY.toString());
	    					mNotifyCharacteristic = gattCharacteristic;
	    					setCharacteristicNotification(gattCharacteristic, true);
	    					broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
	    					return;
	    				}
	    			}
	    		}
	    	}
	    }
	    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
	    public boolean setMTU(int mtu){
	      Log.d("BLE","setMTU "+mtu);

	   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
	          if(mtu>20){
	              boolean ret =   mBluetoothGatt.requestMtu(mtu);
	             Log.d("BLE","requestMTU "+mtu+" ret="+ret);
	             return ret;
	        }
	     }

	        return false;
	    }
	    // Implements callback methods for GATT events that the app cares about.  For example,
	    // connection change and services discovered.
	    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
	        @Override
	        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
	            String intentAction;
	            Log.i(TAG, "oldStatus=" + status + " NewStates=" + newState);
	            if(status == BluetoothGatt.GATT_SUCCESS)
	            {
	            	
	            if (newState == BluetoothProfile.STATE_CONNECTED) {
	                intentAction = ACTION_GATT_CONNECTED;
	                
	                broadcastUpdate(intentAction);
	                Log.i(TAG, "Connected to GATT server.");
	                // Attempts to discover services after successful connection.
	                Log.i(TAG, "Attempting to start service discovery:" +
	                		mBluetoothGatt.discoverServices());
	            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
	                intentAction = ACTION_GATT_DISCONNECTED;
	                mBluetoothGatt.close();
	                mBluetoothGatt = null;
	                Log.i(TAG, "Disconnected from GATT server.");
	                broadcastUpdate(intentAction);
	            }
	           
	            
	        	}
	        }

	        @Override
	        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
	            if (status == BluetoothGatt.GATT_SUCCESS) {
	            	Log.w(TAG, "onServicesDiscovered received: " + status);
	            	findService(gatt.getServices());
	            } else {
	            	if(mBluetoothGatt.getDevice().getUuids() == null)
	                Log.w(TAG, "onServicesDiscovered received: " + status);
	            }
	        }

	        @Override
	        public void onCharacteristicRead(BluetoothGatt gatt,
	                                         BluetoothGattCharacteristic characteristic,
	                                         int status) {
	            if (status == BluetoothGatt.GATT_SUCCESS) {
	                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
	            }
	        }

	        @Override
	        public void onCharacteristicChanged(BluetoothGatt gatt,
	                                            BluetoothGattCharacteristic characteristic) {
	            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
	           // Log.e(TAG, "OnCharacteristicWrite");
	        }
	        
	        @Override
	        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
	        								int status)
	        {
//	        	Log.e(TAG, "OnCharacteristicWrite");
	        }
	        
	        @Override
	        public void onDescriptorRead(BluetoothGatt gatt,
	                                         BluetoothGattDescriptor bd,
	                                         int status) {
//	        	Log.e(TAG, "onDescriptorRead");
	        }
	        
	        @Override
	        public void onDescriptorWrite(BluetoothGatt gatt,
	        								 BluetoothGattDescriptor bd,
	                                         int status) {
//	        	Log.e(TAG, "onDescriptorWrite");
	        }
	        
	        @Override
	        public void onReadRemoteRssi(BluetoothGatt gatt, int a, int b)
	        {
//	        	Log.e(TAG, "onReadRemoteRssi");
	        }
	        
	        @Override
	        public void onReliableWriteCompleted(BluetoothGatt gatt, int a)
	        {
//	        	Log.e(TAG, "onReliableWriteCompleted");
	        }
	        
	    };

	    private void broadcastUpdate(final String action) {
	        final Intent intent = new Intent(action);
	        sendBroadcast(intent);
	    }

	    private void broadcastUpdate(final String action,
	                                 final BluetoothGattCharacteristic characteristic) {
	    	
	    	final Listener listener = getListener();
            if (listener != null) {
                final byte[] data =  characteristic.getValue();;
              
                listener.onNewData(data);
            }
	       

	    }

	    public class LocalBinder extends Binder {
	    	public  BluetoothLeService getService() {
	            return BluetoothLeService.this;
	        }
	    }

	    @Override
	    public IBinder onBind(Intent intent) {
	        return mBinder;
	    }

	    @Override
	    public boolean onUnbind(Intent intent) {
	        // After using a given device, you should make sure that BluetoothGatt.close() is called
	        // such that resources are cleaned up properly.  In this particular example, close() is
	        // invoked when the UI is disconnected from the Service.
	        close();
	        return super.onUnbind(intent);
	    }

	    private final IBinder mBinder = new LocalBinder();

	    /**
	     * ��ʼ��������������������ɹ� ����true ����falsh
	     *
	     * @return Return true if the initialization is successful.
	     */
	    public boolean initialize() {
	        // For API level 18 and above, get a reference to BluetoothAdapter through
	        // BluetoothManager.
	        if (mBluetoothManager == null) {
	            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
	            if (mBluetoothManager == null) {
//	                Log.e(TAG, "Unable to initialize BluetoothManager.");
	                return false;
	            }
	        }

	        mBluetoothAdapter = mBluetoothManager.getAdapter();
	        if (mBluetoothAdapter == null) {
//	            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
	            return false;
	        }

	        return true;
	    }

	    /**
	     * ���ӵ���������
	     *
	     * @param ��ַ�������������
	     *
	     * @return Return true if the connection is initiated successfully. The connection result
	     *         is reported asynchronously through the
	     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	     *         callback.
	     */
	    public boolean connect(final String address,Listener listener) {
	    	//�жϵ�����������δ��ʼ������ ������ַ����nullʱ����false
	        if (mBluetoothAdapter == null || address == null) {
	            Log.w(TAG, "BluetoothAdapterû�г�ʼ����δָ���ĵ�ַ");
	            return false;
	        }
	/*
	        // Previously connected device.  Try to reconnect.
	        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
	                && mBluetoothGatt != null) {
	            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
	            if (mBluetoothGatt.connect()) {
	                mConnectionState = STATE_CONNECTING;
	                return true;
	            } else {
	                return false;
	            }
	        }
	*/
	        //���ݵ�ַ��ȡ��������
	        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
	        if (device == null) {
	            Log.w(TAG, "�豸û�ҵ��޷�����");
	            return false;
	        }
	        // We want to directly connect to the device, so we are setting the autoConnect
	        // parameter to false.
	        if(mBluetoothGatt != null)
	        {
	        	mBluetoothGatt.close();
	            mBluetoothGatt = null;
	        }
	        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
	        //mBluetoothGatt.connect();
	        mListener = listener;
	        Log.d(TAG, "���Դ���һ���µ�����");
	        return true;
	    }

	    /**
	     * Disconnects an existing connection or cancel a pending connection. The disconnection result
	     * is reported asynchronously through the
	     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	     * callback.
	     */
	    public void disconnect() {
	        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
	            Log.w(TAG, "BluetoothAdapterû�г�ʼ��");
	            return;
	        }
	        mBluetoothGatt.disconnect();
	    }

	    /**
	     * After using a given BLE device, the app must call this method to ensure resources are
	     * released properly.
	     */
	    public void close() {
	        if (mBluetoothGatt == null) {
	            return;
	        }
	        mBluetoothGatt.close();
	        mBluetoothGatt = null;
	    }

	    /**
	     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
	     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	     * callback.
	     *
	     * @param characteristic The characteristic to read from.
	     */
	    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
	        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
	            Log.w(TAG, "BluetoothAdapterû�г�ʼ��");
	            return;
	        }
	        mBluetoothGatt.readCharacteristic(characteristic);
	    }

	    /**
	     * Enables or disables notification on a give characteristic.
	     *
	     * @param characteristic Characteristic to act on.
	     * @param enabled If true, enable notification.  False otherwise.
	     */
	    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
	                                              boolean enabled) {
	        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
	            Log.w(TAG, "BluetoothAdapterû�г�ʼ��");
	            return;
	        }
	        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
	/*
	        // This is specific to Heart Rate Measurement.
	        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
	            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
	                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
	            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
	            mBluetoothGatt.writeDescriptor(descriptor);
	        }
	        */
	    }

	    /**
	     * Retrieves a list of supported GATT services on the connected device. This should be
	     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
	     *
	     * @return A {@code List} of supported services.
	     */
	    public List<BluetoothGattService> getSupportedGattServices() {
	        if (mBluetoothGatt == null) return null;

	        return mBluetoothGatt.getServices();
	    }

}
