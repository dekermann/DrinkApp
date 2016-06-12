package softpatrol.drinkapp.activities.fragments;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.activities.BaseActivity;
import softpatrol.drinkapp.activities.RootActivity;
import softpatrol.drinkapp.util.Debug;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class ScanFragment extends Fragment {
    int fragmentId;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAIT_LOCK = 1;
    private int mState;


    /**
     * TextureView
     */
    TextureView mTextureView;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener =
            new TextureView.SurfaceTextureListener() {
                long lastUpdate = 0;
                boolean AnalyzeInProgress = false;
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    setupCamera(width, height);
                    if(mCameraId != null)openCamera();
                }
                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

                }
                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return false;
                }
                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                    if(AnalyzeInProgress) return;
                    lastUpdate = System.currentTimeMillis();

                    AnalyzeInProgress = true;
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Debug.debugMessage((BaseActivity) getActivity(), "Before baseApi");

                            TessBaseAPI baseApi = new TessBaseAPI();
                            baseApi.setDebug(true);
                            baseApi.init(RootActivity.DATA_PATH, RootActivity.LANGUAGE);
                            baseApi.setImage(mTextureView.getBitmap());

                            String recognizedText = baseApi.getUTF8Text();

                            baseApi.end();

                            // You now have the text in recognizedText var, you can do anything with it.
                            // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
                            // so that garbage doesn't make it to the display.

                            Debug.debugMessage((BaseActivity) getActivity(), "OCRED TEXT: " + recognizedText);

                            if (RootActivity.LANGUAGE.equalsIgnoreCase("eng")) {
                                recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
                            }

                            recognizedText = recognizedText.trim();

                            if (recognizedText.length() != 0) {
                                Debug.debugMessage((BaseActivity) getActivity(), "FINAL TEXT: " + recognizedText);
                            }
                            AnalyzeInProgress = false;
                        }
                    });
                    thread.start();
                }
            };

    /** Texture View End */

    private Size mPreviewSize;
    private String mCameraId;

    /**
     * Sets up the camera to match texture width/height
     * @param width for camera
     * @param height for camera
     */
    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            for(String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    mPreviewSize = getPreferredPreviewSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                    mCameraId = cameraId;
                    return;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private Size getPreferredPreviewSize(Size[] mapSizes, int width, int height) {
        List<Size> collectedSizes = new ArrayList<>();
        for(Size option : mapSizes) {
            if(width > height) {
                if(option.getWidth() > width && option.getHeight() > height) collectedSizes.add(option);
            }
            else if(option.getWidth() > height && option.getHeight() > width) collectedSizes.add(option);
        }
        if(collectedSizes.size() > 0) {
            return Collections.min(collectedSizes, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                }
            });
        }
        return mapSizes[0];
    }

    private CameraDevice mCameraDevice;
    private CameraDevice.StateCallback mCameraDeviceCallback =
            new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCameraDevice = camera;
                    createCameraPreviewSession();
                    Toast.makeText(getContext(), "Camera Opened", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                    mCameraDevice = null;
                    Toast.makeText(getContext(), "Camera Disconnected", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    mCameraDevice = null;
                    Toast.makeText(getContext(), "Camera Error", Toast.LENGTH_SHORT).show();
                }
            };

    private void openCamera() {
        CameraManager cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraManager.openCamera(mCameraId, mCameraDeviceCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void closeCamera() {
        if(mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
        if(mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    /**
     * Setup link between surface and CameraDevice using CaptureRequestBuilder
     */
    private CaptureRequest mPreviewCaptureRequest;
    private CaptureRequest.Builder mPreviewCaptureRequestBuilder;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraCaptureSession.CaptureCallback mCameraSessionCaptureCallback =
            new CameraCaptureSession.CaptureCallback() {
                private void process(CaptureResult captureResult) {
                    switch (mState) {
                        case STATE_PREVIEW:
                            //Do nothing
                            break;
                        case STATE_WAIT_LOCK:
                            Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                            if(afState == CaptureRequest.CONTROL_AF_STATE_FOCUSED_LOCKED) {
                                unlockFocus();
                                Toast.makeText(getContext(), "Focus Locked", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                }
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    process(result);
                }
                @Override
                public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                    super.onCaptureFailed(session, request, failure);
                    Toast.makeText(getContext(), "Focus Lock Failed", Toast.LENGTH_SHORT).show();
                }
            };
    private ArrayList<Surface> displaySurfaces = new ArrayList<>();
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            mPreviewCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewCaptureRequestBuilder.addTarget(previewSurface);
            displaySurfaces.add(previewSurface);
            exportCaptureRequest();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void exportCaptureRequest() {
        try {
        mCameraDevice.createCaptureSession(displaySurfaces, new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                if(mCameraDevice == null) return;
                try {
                    mPreviewCaptureRequest = mPreviewCaptureRequestBuilder.build();
                    mCameraCaptureSession = session;
                    mCameraCaptureSession.setRepeatingRequest(
                            mPreviewCaptureRequest,
                            mCameraSessionCaptureCallback,
                            mBackgroundHandler
                    );
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                Toast.makeText(getContext(), "onConfigureFailed", Toast.LENGTH_SHORT).show();
            }
        }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    /** Setup Complete */

    /**
     * Lock Focus
     */

    private void lockFocus() {
        mState = STATE_WAIT_LOCK;
        mPreviewCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                CaptureRequest.CONTROL_AF_TRIGGER_START);
        try {
            mCameraCaptureSession.capture(mPreviewCaptureRequestBuilder.build(), mCameraSessionCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void unlockFocus() {
        mState = STATE_PREVIEW;
        mPreviewCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
        try {
            mCameraCaptureSession.capture(mPreviewCaptureRequestBuilder.build(), mCameraSessionCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public ScanFragment() {}
    public static ScanFragment newInstance(int sectionNumber) {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentId = getArguments().getInt(ARG_SECTION_NUMBER);
        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);

        mTextureView = (TextureView) rootView.findViewById(R.id.camera_texture);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        openBackgroundThread();
        if(mTextureView.isAvailable()) {
            setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
            openCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }
    @Override
    public void onPause() {
        closeCamera();
        closeBackgroundThread();
        super.onPause();
    }

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    private void openBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera2 Background Thread");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    private void closeBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}