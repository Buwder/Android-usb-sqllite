package com.example.hrv.smarthrv;

public interface Listener {
    /**
     * Called when new incoming data is available.
     */
    public void onNewData(byte[] data);

    /**
     * Called when {@link SerialInputOutputManager#run()} aborts due to an
     * error.
     */
    public void onRunError(Exception e);
    
  
}