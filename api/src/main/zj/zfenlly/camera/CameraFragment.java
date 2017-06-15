package zj.zfenlly.camera;

import android.annotation.SuppressLint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import zj.zfenlly.main.BaseFragment;
import zj.zfenlly.tools.R;


@SuppressLint("ValidFragment")
public class CameraFragment extends BaseFragment {

    private final String TAG = this.getClass().getName();//


    Camera.Parameters parameters;
    private int mColorRes = -1;
    private SurfaceHolder surfaceHolder;
    private Camera pCamera;
    @ViewInject(R.id.surface_camera)
    private SurfaceView surfaceView;

    public CameraFragment() {
        this(R.color.white, "camera");
    }

    // .substring(this.getClass().getName().lastIndexOf(".") + 1);

    public CameraFragment(int colorRes, String name) {
        super(name, false);
        mColorRes = colorRes;
    }



    private void print(String msg) {
        Log.i(TAG, msg);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mColorRes", mColorRes);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mColorRes = savedInstanceState.getInt("mColorRes");
        int color = getResources().getColor(mColorRes);
        View view = inflater.inflate(R.layout.fragment_camera, null);
        view.setBackgroundColor(color);
        ViewUtils.inject(this, view);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.setKeepScreenOn(true);
        surfaceView.setFocusable(true);
        //surfaceView.setBackgroundColor(TRIM_MEMORY_BACKGROUND);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                print("surfaceDestroyed");
                //topCam();
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // TODO Auto-generated method stub是
                if (null == pCamera) {
                    pCamera = Camera.open();
                    try {
                        pCamera.setPreviewDisplay(surfaceHolder);
                        initCamera();
                        pCamera.startPreview();
                        print("startPreview");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                //实现自动对焦
                print("autoFocus");
                pCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            print("onAutoFocus success");
                            //initCamera();//实现相机的参数初始化
                            //camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
                        }
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        print("onStart");


    }

    @Override
    public void onResume() {
        super.onResume();
        print("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        print("onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        print("onStop");
        stopCam();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        print("onDestroyView");

    }

    //surfaceDestroyed

    @Override
    public void onDestroy() {
        super.onDestroy();
        print("onDestroy");
        //stopCam();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getActivity().setContentView(R.layout.download);
        print("onCreate");
    }

    public void stopCam() {
        if (pCamera != null) {
            Camera p = pCamera;
            p.setPreviewCallback(null);
            p.stopPreview();
            p.release();
            pCamera = null;
        }
    }

    //控制图像的正确显示方向
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }

    }

    //实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Log.e("Came_e", "图像出错");
        }
    }

    public void turnLightOff(Camera mCamera) {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        // Check if camera flash exists
        if (flashModes == null) {
            return;
        }
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            // Turn off the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            } else {
                print("FLASH_MODE_OFF not supported");
            }
        }
    }

    //相机参数的初始化设置
    private void initCamera() {
        if (pCamera == null) return;
        parameters = pCamera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        //parameters.setPictureSize(surfaceView.getWidth(), surfaceView.getHeight());  // 部分定制手机，无法正常识别该方法。
        //parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        setDispaly(parameters, pCamera);


        pCamera.setParameters(parameters);

        //turnLightOff(pCamera);

        //pCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上

    }
}
