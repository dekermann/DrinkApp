package softpatrol.drinkapp.activities.fragments.scan;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Size;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.activities.fragments.Fragment;
import softpatrol.drinkapp.activities.fragments.ScanFragment;
import softpatrol.drinkapp.activities.fragments.stash.StashFragment;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.database.models.ingredient.Ingredient;
import softpatrol.drinkapp.database.models.stash.Stash;
import softpatrol.drinkapp.model.event.ChangeCurrentStashEvent;
import softpatrol.drinkapp.model.event.EditCurrentStashEvent;
import softpatrol.drinkapp.network.packet.OutgoingMatchForImage;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class Scanner extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAIT_LOCK = 1;
    private int mState;
    View rootView;
    SlidingUpPanelLayout mScrollView;


    boolean AnalyzeInProgress = true;
    /**
     * TextureView
     */
    TextureView mTextureView;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener =
            new TextureView.SurfaceTextureListener() {
                long lastUpdate = 0;

                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    setupCamera(width, height);
                    if (mCameraId != null) openCamera();
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
                    if (AnalyzeInProgress) return;
                    lastUpdate = System.currentTimeMillis();

                    AnalyzeInProgress = true;
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap bm = mTextureView.getBitmap();
                            OutgoingMatchForImage outgoingMatchForImage = new OutgoingMatchForImage();
                            outgoingMatchForImage.setHeight(bm.getHeight());
                            outgoingMatchForImage.setWidth(bm.getWidth());
                            //TODO: Dont convert to grayscale like this ffs
                            byte[] bytes = new byte[bm.getHeight() * bm.getWidth()];
                            for (int y = 0; y < bm.getHeight(); y++)
                                for (int x = 0; x < bm.getWidth(); x++) {
                                    byte value = (byte) (((bm.getPixel(x, y) & 0x000000FF) * 0.1114) + (((bm.getPixel(x, y) >> 8) & 0x000000FF) * 0.587) + (((bm.getPixel(x, y) >> 16) & 0x000000FF) * 0.299));
                                    bytes[y * bm.getWidth() + x] = (byte) (value & 0x000000FF);
                                }
                            //TODO: Dont convert to grayscale like this ffs
                            outgoingMatchForImage.setImgData(bytes);

                            /*getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageView imageView = (ImageView) rootView.findViewById(R.id.black_white);
                                    imageView.setImageDrawable(new BitmapDrawable(bm));
                                }
                            });*/

                            /*
                            TcpRequest tcpRequest = new TcpRequest(new ITcpResponse() {
                                @Override
                                public void response(IPacket packet) {
                                    if (packet == null) {
                                        Log.d("network", "error sending tcp request");
                                    } else if (packet.getTag() == IncomingError.TAG) {
                                        IncomingError error = (IncomingError) packet;
                                        System.out.println(error.getMsg());
                                    } else {
                                        IncomingMatchForImage imfi = (IncomingMatchForImage) packet;

                                        if (imfi.getMatchId() != 0) {
                                            Log.d("network", "Found match with ingredient id = " + imfi.getMatchId());
                                            Log.d("network", "Match time = " + imfi.getMatchTime() + " seconds");
                                        } else {
                                            Log.d("network", "No match was found :(");
                                        }
                                    }
                                }
                            }, Definitions.IP, Definitions.PORT);
                            tcpRequest.execute(outgoingMatchForImage);
                            */
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
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
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
        for (Size option : mapSizes) {
            if (width > height) {
                if (option.getWidth() > width && option.getHeight() > height)
                    collectedSizes.add(option);
            } else if (option.getWidth() > height && option.getHeight() > width)
                collectedSizes.add(option);
        }
        if (collectedSizes.size() > 0) {
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
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
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
            if(mTorch) mPreviewCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
            else mPreviewCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
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
    boolean mTorch = false;
    private void torch() {
        mTorch = !mTorch;
        try {
            mPreviewCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
        } catch (Exception e) { return; }
        if(mTorch) mPreviewCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
        else mPreviewCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);

        mPreviewCaptureRequest = mPreviewCaptureRequestBuilder.build();
        try {
            mCameraCaptureSession.setRepeatingRequest(
                    mPreviewCaptureRequest,
                    mCameraSessionCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    boolean CurrentStashNameFocused = false;
    private void changeStashNameState() {
        CurrentStashNameFocused = !CurrentStashNameFocused;
        Log.d("CurrentStashNameFocused", CurrentStashNameFocused + "");
        if(!CurrentStashNameFocused) {
            ScanFragment.mScannedItems.requestFocus();
            hideSoftKeyboard();
        }
    }
    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    public Scanner() {}
    public static Scanner newInstance(int sectionNumber) {
        Scanner fragment = new Scanner();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan_scan, container, false);
        this.rootView = rootView;

        mTextureView = (TextureView) rootView.findViewById(R.id.camera_texture);
        mTextureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CurrentStashNameFocused) changeStashNameState();
                AnalyzeInProgress = false;
            }
        });

        final ImageView torch = (ImageView) rootView.findViewById(R.id.torch);
        torch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mTorch) {
                    torch.setImageDrawable(getContext().getDrawable(R.drawable.torch_on));
                    torch.setBackground(getContext().getDrawable(R.drawable.torch_button_on));
                }
                else {
                    torch.setImageDrawable(getContext().getDrawable(R.drawable.flashlight_off_white));
                    torch.setBackground(getContext().getDrawable(R.drawable.torch_button_off));
                }
                torch();
            }
        });
/*
        mCurrentStashName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/

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