package softpatrol.drinkapp.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.database.models.stash.Stash;
import softpatrol.drinkapp.model.event.ChangeCurrentStashEvent;
import softpatrol.drinkapp.model.event.EditCurrentStashEvent;
import softpatrol.drinkapp.network.Definitions;
import softpatrol.drinkapp.network.packet.IPacket;
import softpatrol.drinkapp.network.ITcpResponse;
import softpatrol.drinkapp.network.TcpRequest;
import softpatrol.drinkapp.network.packet.IncomingError;
import softpatrol.drinkapp.network.packet.IncomingMatchForImage;
import softpatrol.drinkapp.network.packet.OutgoingMatchForImage;

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
    View rootView;


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
                            final Bitmap bm = mTextureView.getBitmap();
                            OutgoingMatchForImage outgoingMatchForImage = new OutgoingMatchForImage();
                            outgoingMatchForImage.setHeight(bm.getHeight());
                            outgoingMatchForImage.setWidth(bm.getWidth());
                            //TODO: Dont convert to grayscale like this ffs
                            byte[] bytes = new byte[bm.getHeight()*bm.getWidth()];
                            for(int y = 0;y<bm.getHeight();y++) for(int x = 0;x<bm.getWidth();x++) {
                                byte value = (byte) (((bm.getPixel(x,y) & 0x000000FF)*0.1114)+(((bm.getPixel(x,y)>>8) & 0x000000FF)*0.587)+(((bm.getPixel(x,y)>>16) & 0x000000FF)*0.299));
                                bytes[y*bm.getWidth()+x] = (byte) (value & 0x000000FF);
                            }
                            //TODO: Dont convert to grayscale like this ffs
                            outgoingMatchForImage.setImgData(bytes);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageView imageView = (ImageView) rootView.findViewById(R.id.black_white);
                                    imageView.setImageDrawable(new BitmapDrawable(bm));
                                }
                            });

                            TcpRequest tcpRequest = new TcpRequest(new ITcpResponse() {
                                @Override
                                public void response(IPacket packet) {
                                    if (packet == null) {
                                        Log.d("network","error sending tcp request");
                                    } else if (packet.getTag() == IncomingError.TAG) {
                                        IncomingError error = (IncomingError) packet;
                                        System.out.println(error.getMsg());
                                    }  else {
                                        IncomingMatchForImage imfi = (IncomingMatchForImage) packet;

                                        if (imfi.getMatchId() != 0) {
                                            Log.d("network","Found match with ingredient id = " + imfi.getMatchId());
                                            Log.d("network","Match time = " + imfi.getMatchTime() + " seconds");
                                        } else {
                                            Log.d("network","No match was found :(");
                                        }
                                    }
                                }
                            }, Definitions.IP, Definitions.PORT);
                            tcpRequest.execute(outgoingMatchForImage);
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
    boolean mManual = false;
    private void manualAdd() {
        mManual = !mManual;
    }

    private void clearList() {
        StashFragment.CURRENT_STASH = new Stash();
        StashFragment.CURRENT_STASH.setName("");
        mCurrentStashName.setHint("New Stash");
        StashFragment.CURRENT_STASH_ID = -1;
        ((TestAdapter)mScannedItems.getAdapter()).clearItems();
        DatabaseHandler db = DatabaseHandler.getInstance(getContext());
        for(long l : StashFragment.CURRENT_STASH.getIngredientsIds()) {
            ((TestAdapter)mScannedItems.getAdapter()).addItems(db.getIngredient(l).getName());
        }
        mCurrentStashName.setText(StashFragment.CURRENT_STASH.getName());
        EventBus.getDefault().post(new EditCurrentStashEvent());
    }

    boolean CurrentStashNameFocused = false;
    private void changeStashNameState() {
        CurrentStashNameFocused = !CurrentStashNameFocused;
        Log.d("CurrentStashNameFocused", CurrentStashNameFocused + "");
        if(!CurrentStashNameFocused) {
            mScannedItems.requestFocus();
            hideSoftKeyboard();
        }
    }
    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    public ScanFragment() {}
    public static ScanFragment newInstance(int sectionNumber) {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    EditText mCurrentStashName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentId = getArguments().getInt(ARG_SECTION_NUMBER);
        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);
        this.rootView = rootView;

        mTextureView = (TextureView) rootView.findViewById(R.id.camera_texture);
        mTextureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CurrentStashNameFocused) changeStashNameState();
                AnalyzeInProgress = false;
            }
        });

        mScannedItems = (RecyclerView) rootView.findViewById(R.id.scanned_item_list);
        mScannedItems.requestFocus();
        mCurrentStashName = (EditText) rootView.findViewById(R.id.fragment_scan_stash_name);
        mCurrentStashName.setSelected(false);
        mCurrentStashName.setFocusable(true);
        mCurrentStashName.setFocusableInTouchMode(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        
        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"fonts/font.ttf");
        mCurrentStashName.setTypeface(type);
        setUpRecyclerView();
        DatabaseHandler db = DatabaseHandler.getInstance(getContext());
        for(long l : StashFragment.CURRENT_STASH.getIngredientsIds()) {
            ((TestAdapter)mScannedItems.getAdapter()).addItems(db.getIngredient(l).getName());
        }

        final ImageView torch = (ImageView) rootView.findViewById(R.id.torch);
        torch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mTorch) {
                    torch.setImageDrawable(getContext().getDrawable(R.drawable.flashlight_on_white));
                    torch.setBackground(getContext().getDrawable(R.drawable.torch_button_on));
                }
                else {
                    torch.setImageDrawable(getContext().getDrawable(R.drawable.flashlight_off_white));
                    torch.setBackground(getContext().getDrawable(R.drawable.torch_button_off));
                }
                torch();
            }
        });
        final ImageView manual_add = (ImageView) rootView.findViewById(R.id.manual_add);
        manual_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mManual) {
                    manual_add.setImageDrawable(getContext().getDrawable(R.drawable.automatic_add_button));
                }
                else {
                    manual_add.setImageDrawable(getContext().getDrawable(R.drawable.manual_add));
                    //FAKE ADD
                    long fakeId = (long) (Math.random()*10) + 1;
                    //Debug.debugMessage((BaseActivity) getActivity(), "FOUND INGREDIENT " + fakeId + ": " + DatabaseHandler.getInstance(getContext()).getServerIngredient(fakeId).getName());
                    ((TestAdapter)mScannedItems.getAdapter()).addItems(DatabaseHandler.getInstance(getContext()).getServerIngredient(fakeId).getName());
                    StashFragment.CURRENT_STASH.addIngredientId(fakeId);
                    EventBus.getDefault().post(new EditCurrentStashEvent());
                }
                manualAdd();
            }
        });

        final ImageView clear = (ImageView) rootView.findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();
            }
        });
/*
        mCurrentStashName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/
        mCurrentStashName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mCurrentStashName.setHint("");
                if(!CurrentStashNameFocused) changeStashNameState();
            }
        });

        mCurrentStashName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                StashFragment.CURRENT_STASH.setName(mCurrentStashName.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCurrentStashName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event == null) {
                    changeStashNameState();
                    return true;
                }
                return false;
            }
        });

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

    //----- SCANNED LIST -----
    public static RecyclerView mScannedItems;
    private LinearLayoutManager mLinearLayourManager;
    private void setUpRecyclerView() {
        mLinearLayourManager = new LinearLayoutManager(getContext());
        mScannedItems.setLayoutManager(mLinearLayourManager);
        mScannedItems.setAdapter(new TestAdapter());
        mScannedItems.setHasFixedSize(true);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
    }

    private boolean listIsAtTop()   {
        return (mLinearLayourManager.findFirstVisibleItemPosition() == 0);
    }

    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.TRANSPARENT);
                xMark = ContextCompat.getDrawable(getContext(), R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) getContext().getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                TestAdapter testAdapter = (TestAdapter)recyclerView.getAdapter();
                if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                TestAdapter adapter = (TestAdapter) mScannedItems.getAdapter();
                boolean undoOn = adapter.isUndoOn();
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                //xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mScannedItems);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to their new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        mScannedItems.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.TRANSPARENT);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }

    /**
     * RecyclerView adapter enabling undo on a swiped away item.
     */
    class TestAdapter extends RecyclerView.Adapter {

        private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

        List<String> items;
        List<String> itemsPendingRemoval;
        boolean undoOn; // is undo on, you can turn it on from the toolbar menu

        private Handler handler = new Handler(); // hanlder for running delayed runnables
        HashMap<String, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

        public TestAdapter() {
            items = new ArrayList<>();
            itemsPendingRemoval = new ArrayList<>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TestViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TestViewHolder viewHolder = (TestViewHolder)holder;
            final String item = items.get(position);

            if (itemsPendingRemoval.contains(item)) {
                // we need to show the "undo" state of the row
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
                viewHolder.titleTextView.setVisibility(View.GONE);
            } else {
                // we need to show the "normal" state
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
                viewHolder.titleTextView.setVisibility(View.VISIBLE);
                viewHolder.titleTextView.setText(item);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        /**
         *  Utility method to add some rows for testing purposes. You can add rows from the toolbar menu.
         */
        public void addItems(String name) {
            Log.d("ADAPTED", "Adding item: " + name);
            for(String scannedItem : items) if(scannedItem.equals(name)) return; //Can only scan same type once
            items.add(0, name);
            if(listIsAtTop()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mScannedItems.scrollToPosition(0);
                        notifyItemInserted(0);
                    }
                });
            }
            else notifyItemInserted(0);
        }

        public void clearItems() {
            for(int i = 0;i<items.size();) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }

        public void setUndoOn(boolean undoOn) {
            this.undoOn = undoOn;
        }

        public boolean isUndoOn() {
            return undoOn;
        }

        public void pendingRemoval(int position) {
            final String item = items.get(position);
            if (!itemsPendingRemoval.contains(item)) {
                itemsPendingRemoval.add(item);
                // this will redraw row in "undo" state
                notifyItemChanged(position);
                // let's create, store and post a runnable to remove the item
                Runnable pendingRemovalRunnable = new Runnable() {
                    @Override
                    public void run() {
                        remove(items.indexOf(item));
                    }
                };
                handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
                pendingRunnables.put(item, pendingRemovalRunnable);
            }
        }

        public void remove(int position) {
            String item = items.get(position);
            if (itemsPendingRemoval.contains(item)) {
                itemsPendingRemoval.remove(item);
            }
            if (items.contains(item)) {
                items.remove(position);
                notifyItemRemoved(position);
            }
        }

        public boolean isPendingRemoval(int position) {
            String item = items.get(position);
            return itemsPendingRemoval.contains(item);
        }
    }

    /**
     * ViewHolder capable of presenting two states: "normal" and "undo" state.
     */
    static class TestViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;

        public TestViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.scanned_item, parent, false));
            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
            Typeface type = Typeface.createFromAsset(parent.getContext().getAssets(),"fonts/font.ttf");
            titleTextView.setTypeface(type);
        }

    }
    /*
    * Messaging service between stuff
     */

    @Subscribe
    public void onCurrentStashEvent(ChangeCurrentStashEvent stashEvent) {
        ((TestAdapter)mScannedItems.getAdapter()).clearItems();
        DatabaseHandler db = DatabaseHandler.getInstance(getContext());
        for(long l : StashFragment.CURRENT_STASH.getIngredientsIds()) {
            ((TestAdapter)mScannedItems.getAdapter()).addItems(db.getIngredient(l).getName());
        }
        mCurrentStashName.setText(StashFragment.CURRENT_STASH.getName());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onEntered() {
        if(CurrentStashNameFocused) changeStashNameState();
        mCurrentStashName.setHint("New Stash");
    }
}