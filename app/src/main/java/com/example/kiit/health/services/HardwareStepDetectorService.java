package com.example.kiit.health.services;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.example.kiit.health.utils.AndroidVersionHelper;

public class HardwareStepDetectorService extends AbstractStepDetectorService {

    private float mStepOffset = -1;

    /**
     * Creates an HardwareStepDetectorService.
     */
    public HardwareStepDetectorService(){
        this("");
        // required empty constructor
    }

    /**
     * Creates an HardwareStepDetectorService.
     *
     * @param name Name for the worker thread, use it for debugging purposes
     */
    public HardwareStepDetectorService(String name) {
        super(name);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
                this.onStepDetected(1);
                break;
            case Sensor.TYPE_STEP_COUNTER:
                if (this.mStepOffset < 0) {
                    this.mStepOffset = event.values[0];
                }
                if (this.mStepOffset > event.values[0]) {
                    // this should never happen?
                    return;
                }
                // publish difference between last known step count and the current ones.
                this.onStepDetected((int) (event.values[0] - mStepOffset));
                // Set offset to current value so we know it at next event
                mStepOffset = event.values[0];
                break;
        }
    }

    @SuppressLint("InlinedApi")
    @Override
    public int getSensorType() {
        if (AndroidVersionHelper.supportsStepDetector(getPackageManager())) {
            return Sensor.TYPE_STEP_DETECTOR;
        } else {
            return 0;
        }
    }
}
