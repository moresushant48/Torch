package io.moresushant48.torch;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnOnOff;
    private Camera camera;
    private Parameters parameters;
    private CameraManager cameraManager;
    private String camId;
    private  boolean state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOnOff = findViewById(R.id.btnOnOff);
        btnOnOff.setText(R.string.turn_on);

        // Check if the device has the flash module.
        checkForFlash();

        // Get the cameraId. Parameter Required for latest APIs
        getCameraId();

        // If device is Kitkat and lower OS
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            camera = Camera.open();
            parameters = camera.getParameters();
        }

        // Button actions : Turn ON & OFF the Flash.
        toggleFlash();
    }

    private void checkForFlash(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            boolean getFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

            if (!getFlash) {
                CharSequence msg = "Your Device dosen't support Flash Light";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                finish();
            }

        }else{

            PackageManager pm = getPackageManager();
            boolean getFlash = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

            if(!getFlash){
                CharSequence msg = "Your Device dosen't support Flash Light";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void getCameraId() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                if (cameraManager != null) {
                    camId = cameraManager.getCameraIdList()[0];
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void toggleFlash(){
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!state) {
                        try {
                            if (cameraManager != null) {
                                cameraManager.setTorchMode(camId, true);
                                state = true;
                                btnOnOff.setText(R.string.turn_off);
                            }
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            if (cameraManager != null) {
                                cameraManager.setTorchMode(camId, false);
                                state = false;
                                btnOnOff.setText(R.string.turn_on);
                            }
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    if (!state) {

                        parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(parameters);
                        camera.startPreview();
                        state = true;
                        btnOnOff.setText(R.string.turn_off);

                    } else {

                        camera.stopPreview();
                        state = false;
                        btnOnOff.setText(R.string.turn_on);
                    }
                }
            }
        });
    }
}
