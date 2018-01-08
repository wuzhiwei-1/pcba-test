package com.actions.pcbatest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Integer;
import java.lang.String;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.text.DecimalFormat;

import android.os.*;
import android.os.ServiceManager;
import android.os.INetworkManagementService;
import android.content.ServiceConnection;
import android.content.ContentResolver;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.EthernetManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;
import com.actions.support.SupportManager;

public class PcbaTestActivity extends Activity implements SurfaceHolder.Callback {
	@SuppressWarnings("unused")
	private static String TAG = "PcbaTestActivity";

	private static final int MSG_FLAG_TIMER_INT = 11;
	private static final int MSG_FLAG_DDR_UPDATE = 12;
	private static final int MSG_FLAG_FLASH_UPDATE = 13;
	private static final int MSG_FLAG_WIFI_UPDATE = 14;
	private static final int MSG_FLAG_GSENSOR_UPDATE = 15;
	private static final int MSG_FLAG_CAMERA_UPDATE = 16;
	private static final int MSG_FLAG_CARD_UPDATE = 17;
	private static final int MSG_FLAG_UHOST_UPDATE = 18;
    private static final int MSG_FLAG_UHOST2_UPDATE = 182;
	private static final int MSG_FLAG_MOUSE_UPDATE = 19;
	private static final int MSG_FLAG_USB_UPDATE = 20;
	private static final int MSG_FLAG_HDMI_UPDATE = 21;
	private static final int MSG_FLAG_HEADSET_UPDATE = 22;
	private static final int MSG_FLAG_KEY_UPDATE = 23;
	private static final int MSG_FLAG_TOUCH_UPDATE = 24;
	private static final int MSG_FLAG_START_RECORDING = 25;
	private static final int MSG_FLAG_START_REPLAYING = 26;
	private static final int MSG_FLAG_BLUETOOTH_UPDATE = 27;
	private static final int MSG_FLAG_RTC_UPDATE = 28;
	private static final int MSG_FLAG_SIM_UPDATE = 29;

	private static final int MSG_FLAG_INFRARED_UPDATE = 30;
	private static final int MSG_FLAG_ETHERNET_UPDATE = 31;
	private static final String DEFAULT = "FALSE";

    //serial port vari
    private SerialPort sp1;
    private SerialPort sp2;

	// widget
	private TextView tvDdrResultText;
	private TextView tvFlashResultText;
	private TextView tvWifiResultText;
	private TextView tvGsensorResultText;
	private TextView tvCameraResultText;
	private TextView tvBatteryResultText;
	private TextView tvBacklightResultText;
	private TextView tvLcdResultText;
	private TextView tvBluetoothResultText;
	private TextView tvRTCResultText;
	private TextView tvSIMResultText;
    private TextView tvSerialPortResultText;
    private TextView tvSerialPortResultText_b;

	private TextView tvCardResultText;
	private TextView tvUhostResultText;
	private TextView tvUsbResultText;
	private TextView tvHdmiResultText;
	private TextView tvHeadsetResultText;
	private TextView tvKeyResultText;
	private TextView tvVoiceResultText;
	private TextView tvTouchResultText;
	private TextView tvMouseResultText;
	private TextView tvRecorderPlayerStatus;
	private TextView tvInfraredResultText;
	private TextView tvEthernetResultText;
	private TextView tvUsb2ResultText;
    private TextView tvUhost2ResultText;


	private Button unloadButton;
	private Button restoreButton;
	private Button mPhomeButton;
	private CheckBox chexkbox_1;
	private Button calibButton;
	private boolean unload = false;

	private Button btVoiceButton;
	private List<Test> testItems = null;
	private VisualizerFx mVisualizerFx;
	private VUMeter mVUMeter;

	// wifi
	private int first_wifi_state = 0;
	private int last_wifi_state = 0;
	private WifiCtrl mWifiCtrl;
	private boolean need_search_net = false;
	private static MessageHandler messageHandler;

	// bluetooth
	private boolean last_bluetooth_state = false;

	// Gsensor info
	private SensorManager mSensorManager = null;
	private Sensor mSensor = null;
	private SensorEventListener mSensorListener = null;
	private static int mSensorFoundCount = 0;
	private float x = 0;
	private float y = 0;
	private float z = 0;
	private boolean xGsensorChange = false;
	private boolean yGsensorChange = false;
	private boolean zGsensorChange = false;

	// Camera
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera = null;
	private int mCurrentIndex = -1;
	private SurfaceHolder mHolder;
	private int mCameraTimerCount = 0;

	// storage
	private StorageManager mStorageManager;
	private Method mMethodGetPaths;
	private Method mMethodGetVolumeState;
	private String[] mount_paths = null;
	private boolean storageNandType;

	// HDMI
	public static final String HDMI_ACTION = "android.intent.action.HDMI_PLUGGED";
	public final static String EXTRA_HDMI_PLUGGED_STATE = "state";

	// Timer
	private Timer timer = new Timer();
	private long mTimerCount = 0;

	// USB
	private static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
	private static final String USB_CONNECTED = "connected";
	private static final String USB_FUNCTION_MASS_STORAGE = "mass_storage";
	private static final String USG_FUNCTIONS_MTP = "mtp";

	// media
	private MediaPlayer mMediaPlayer;
	private File mRecordFile;
	private static final String RECORD_FILE_NAME = "TestRecordFile.aac";
	private Recorder mRecorder;
	private int mRecordTimerCount = 0;
	// android.media.MediaRecorder.OutputFormat.OUTPUT_FORMAT_WAV;
	private final int RECORD_OUTPUT_FORMAT_WAV = 12;
	// MediaRecorder.AudioEncoder.ADPCM;
	private final int RECORD_ENCODE_ADPCM = 7;

	// vibrator
	Vibrator vibrator;

	// Bootup Complete
	private static final String ACTION_BOOTUP_COMPLETE = "android.intent.action.BOOT_COMPLETED";
	private boolean isBootupComplete = false;

	// Wakeup
	WakeLock mWakeLock;

	// Mouse
	private boolean isMouseConnected = false;

	// Ethernet
	private boolean isEthernetConnected = false;
	private int isEthernetOpened = 0; // //0是关闭，1是打开
	private EthernetManager mEthManager;

	// bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mBluetoothDeviceFound = false;

	private boolean isInitialized = false;
	private Context mContext;
	private boolean mStopFlag = false;
	//
	boolean file1_ok = false;
	boolean file2_ok = false;
	// USB2.0
	boolean usb1 = false;
	boolean usb2 = false;

    //eth ---
    private TextView ethResult;

	// ====================================================================================
	// main activity
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = PcbaTestActivity.this;

		if (!isInitialized) {
			initialize();
		}

		isInitialized = true;
		compareRTCTime();

	}



	private void initialize() {

		// Debug.startMethodTracing();
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
		try {
			testItems = PcbaUtils.getXMLConfig();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.e(TAG, "init");
		/*if (!test_haha()) {
			finish();
			return;
		}*/
		tvRecorderPlayerStatus = (TextView) findViewById(R.id.recorder_player_states);
		tvRecorderPlayerStatus.setText(getString(R.string.waiting_music));

		((TextView) findViewById(R.id.autotest_item_name_1)).setText(R.string.item_lable_ddr);
		((TextView) findViewById(R.id.autotest_item_name_2)).setText(R.string.item_lable_flash);
		((TextView) findViewById(R.id.autotest_item_name_3)).setText(R.string.item_lable_wifi);
		((TextView) findViewById(R.id.autotest_item_name_4)).setText(R.string.item_lable_gsensor);
		((TextView) findViewById(R.id.autotest_item_name_5)).setText(R.string.item_lable_camera);
//		((TextView) findViewById(R.id.autotest_item_name_6)).setVisibility(View.GONE); // .setText(R.string.item_lable_battery);
//		((TextView) findViewById(R.id.autotest_item_name_7)).setVisibility(View.GONE); // .setText(R.string.item_lable_backlight);
//		((TextView) findViewById(R.id.autotest_item_name_8)).setVisibility(View.GONE); // .setText(R.string.item_lable_lcd);
        ((TextView) findViewById(R.id.autotest_item_name_6)).setText(R.string.item_lable_battery);
        ((TextView) findViewById(R.id.autotest_item_name_7)).setText(R.string.item_lable_backlight);
        ((TextView) findViewById(R.id.autotest_item_name_8)).setText(R.string.item_lable_lcd);
		((TextView) findViewById(R.id.autotest_item_name_9)).setText(R.string.item_lable_bluetooth);
		((TextView) findViewById(R.id.autotest_item_name_10)).setText(R.string.item_lable_rtc);
		((TextView) findViewById(R.id.autotest_item_name_11)).setText(R.string.item_lable_sim);
        ((TextView) findViewById(R.id.autotest_item_name_12)).setText(R.string.item_lable_serial_port);
		//((TextView) findViewById(R.id.autotest_item_name_11)).setVisibility(View.GONE);
		// ((TextView)findViewById(R.id.autotest_item_name_9)).setVisibility(View.GONE);

		tvDdrResultText = (TextView) findViewById(R.id.autotest_item_result_1);
		tvFlashResultText = (TextView) findViewById(R.id.autotest_item_result_2);
		tvWifiResultText = (TextView) findViewById(R.id.autotest_item_result_3);
		tvGsensorResultText = (TextView) findViewById(R.id.autotest_item_result_4);
		tvCameraResultText = (TextView) findViewById(R.id.autotest_item_result_5);
		tvBatteryResultText = (TextView) findViewById(R.id.autotest_item_result_6);
		tvBacklightResultText = (TextView) findViewById(R.id.autotest_item_result_7);
		tvLcdResultText = (TextView) findViewById(R.id.autotest_item_result_8);
		tvBluetoothResultText = (TextView) findViewById(R.id.autotest_item_result_9);
		tvRTCResultText = (TextView) findViewById(R.id.autotest_item_result_10);
		tvSIMResultText = (TextView) findViewById(R.id.autotest_item_result_11);
        tvSerialPortResultText = (TextView) findViewById(R.id.autotest_item_result_12);
        tvSerialPortResultText_b = (TextView) findViewById(R.id.autotest_item_result_12_b);
		//tvSIMResultText.setVisibility(View.GONE);
		unloadButton = (Button) findViewById(R.id.manualtest_item_result_uninstall_apk);
		restoreButton = (Button) findViewById(R.id.manualtest_item_result_restore_);
		mPhomeButton = (Button) findViewById(R.id.autotest_item_result_11_phonebutton);
		chexkbox_1 = (CheckBox) findViewById(R.id.checkbox_1);
		calibButton = (Button) findViewById(R.id.calibration);
		unloadButton.setVisibility(View.GONE);
		chexkbox_1.setVisibility(View.GONE);
		restoreButton.setVisibility(View.GONE);
		mPhomeButton.setVisibility(View.GONE);

//		tvBatteryResultText.setVisibility(View.GONE);
//		tvBacklightResultText.setVisibility(View.GONE);
//		tvLcdResultText.setVisibility(View.GONE);
		// tvBluetoothResultText.setVisibility(View.GONE);

		String tempstr = getDdrTestInitText();
		tvDdrResultText.setText(tempstr);
		tvDdrResultText.setTextColor(tempstr.contains("PASS") ? Color.BLUE : Color.RED);

//		String storage_type = runCommandLine("cat /proc/storage_type");
//		if (storage_type.trim().equalsIgnoreCase("nand")) {
//			storageNandType = true;
//			tempstr = "PASS (" + getAvailableFlashTestInitTest() + "/" + getFlashTestInitText() + ")";
//		} /*else if (storage_type.trim().equalsIgnoreCase("emmc")) {
//			storageNandType = false;
//			tempstr = "PASS(" + getFlashTestInitText() + ")";
//		}*/else {
//			storageNandType = false;
//			tempstr = "PASS(" + getFlashTestInitText() + ")";
//		}
//
//		if (tempstr.equals("FAIL")) {
//			tempstr = getFlashTestInitText2();
//		}


		tempstr = "PASS(" + getFlashTestInitText() + ")";
		tvFlashResultText.setText(tempstr);
		tvFlashResultText.setTextColor(tempstr.contains("PASS") ? Color.BLUE : Color.RED);

		tvWifiResultText.setText(getWifiTestInitText());
		tvGsensorResultText.setText(getGsensorTestInitText());
		tvCameraResultText.setText(getCameraTestInitText());
		tvBatteryResultText.setText(getBatteryTestInitText());
		tvBacklightResultText.setText(getBacklightTestInitText());
		tvLcdResultText.setText(getLcdTestInitText());
		tvBluetoothResultText.setText(getBluetoothTestInitText());

		((TextView) findViewById(R.id.manualtest_item_name_1)).setText(R.string.item_lable_card);
		((TextView) findViewById(R.id.manualtest_item_name_2)).setText(R.string.item_lable_uhost);
        ((TextView) findViewById(R.id.manualtest_item_name_2p1)).setText(R.string.item_lable_uhost2);
		((TextView) findViewById(R.id.manualtest_item_name_3)).setText(R.string.item_lable_usb);
		((TextView) findViewById(R.id.manualtest_item_name_4)).setText(R.string.item_lable_hdmi);
		((TextView) findViewById(R.id.manualtest_item_name_5)).setText(R.string.item_lable_headset);
		((TextView) findViewById(R.id.manualtest_item_name_6)).setText(R.string.item_lable_key);
		((TextView) findViewById(R.id.manualtest_item_name_7)).setText(R.string.item_lable_voice);
		((TextView) findViewById(R.id.manualtest_item_name_8)).setText(R.string.item_lable_tp);
		((TextView) findViewById(R.id.manualtest_item_name_9)).setText(R.string.item_lable_mouse);
		((TextView) findViewById(R.id.manualtest_item_name_10)).setText(R.string.item_lable_infrared);
		((TextView) findViewById(R.id.manualtest_item_name_11)).setText(R.string.item_lable_ethernet);
		((TextView) findViewById(R.id.manualtest_item_name_12)).setText(R.string.item_lable_usb2);

		tvCardResultText = (TextView) findViewById(R.id.manualtest_item_result_1);
		tvUhostResultText = (TextView) findViewById(R.id.manualtest_item_result_2);
        tvUhost2ResultText = (TextView) findViewById(R.id.manualtest_item_result_2p1);
		tvUsbResultText = (TextView) findViewById(R.id.manualtest_item_result_3);
		tvHdmiResultText = (TextView) findViewById(R.id.manualtest_item_result_4);
		tvHeadsetResultText = (TextView) findViewById(R.id.manualtest_item_result_5);
		tvKeyResultText = (TextView) findViewById(R.id.manualtest_item_result_6);
		tvVoiceResultText = (TextView) findViewById(R.id.manualtest_item_result_7);
		tvTouchResultText = (TextView) findViewById(R.id.manualtest_item_result_8);
		tvMouseResultText = (TextView) findViewById(R.id.manualtest_item_result_9);
		tvInfraredResultText = (TextView) findViewById(R.id.manualtest_item_result_10);
		tvEthernetResultText = (TextView) findViewById(R.id.manualtest_item_result_11);
		tvUsb2ResultText = (TextView) findViewById(R.id.manualtest_item_result_12);

		// disable voice item
		((TextView) findViewById(R.id.manualtest_item_name_7)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.manualtest_item_result_7)).setVisibility(View.GONE);
		// ((TextView)
		// findViewById(R.id.manualtest_item_name_2)).setVisibility(View.GONE);
		// ((TextView)
		// findViewById(R.id.manualtest_item_result_2)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.manualtest_item_name_8)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.manualtest_item_result_8)).setVisibility(View.GONE);

		tvCardResultText.setText(getCardTestInitText());
		tvUhostResultText.setText(getUhostTestInitText());
		tvUsbResultText.setText(getUsbTestInitText());
		tvHdmiResultText.setText(getHdimTestInitText());
		tvHeadsetResultText.setText(getHeadsetTestInitText());
		tvKeyResultText.setText(getKeyTestInitText());
		tvVoiceResultText.setText(getVoiceTestInitText());
		tvTouchResultText.setText(getTouchTestInitText());
		tvMouseResultText.setText(getMouseTestInitText());
		tvInfraredResultText.setText(getInfraredInitText());
		tvEthernetResultText.setText(getEthernetInitText());
		tvUsb2ResultText.setText(getUsb2InitText());

		btVoiceButton = (Button) findViewById(R.id.start_or_stop_record);
		btVoiceButton.setOnClickListener(mVoiceButtonListener);
		unloadButton.setOnClickListener(unloadLinstener);
		restoreButton.setOnClickListener(restoreListener);
		mPhomeButton.setOnClickListener(phoneListener);
		calibButton.setOnClickListener(calibrationLinstener);
		chexkbox_1.setOnCheckedChangeListener(checkListener_1);

		mVisualizerFx = (VisualizerFx) findViewById(R.id.visualizer_fx);
		mVUMeter = (VUMeter) findViewById(R.id.vumeter);
		mVUMeter.setVisibility(View.GONE);

		Looper looper = Looper.myLooper();
		messageHandler = new MessageHandler(looper);

		// wifi
		mWifiCtrl = new WifiCtrl(this);
		dealWifiCtrlThread();
		need_search_net = true;
		// Ethernet
		//if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
			//mEthManager = (EthernetManager) getSystemService(Context.ETHERNET_SERVICE);
			//mEthManager.loadDriver(true);
			//mEthManager.setEnabled(true);
		//}

        //eth


		// bluetooth
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		dealBluetoothThread();

		// Gsensor
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mSensorListener = new SensorEventListener() {
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}

			public void onSensorChanged(SensorEvent event) {
				mSensorFoundCount = 0;
				String sensorinfo = null;
				if ((Math.abs(event.values[0]) > 50) || (Math.abs(event.values[1]) > 50) || (Math.abs(event.values[2]) > 50)) {
					sensorinfo = String.format("FAIL (%.1f, %.1f, %.1f)", event.values[0], event.values[1], event.values[2]);
					sendUpdateMessage(MSG_FLAG_GSENSOR_UPDATE, sensorinfo);
					return;
				}

				if (!(x == event.values[0]) && !xGsensorChange) {
					xGsensorChange = true;
				}
				if (!(y == event.values[1]) && !yGsensorChange) {
					yGsensorChange = true;
				}
				if (!(z == event.values[2]) && !zGsensorChange) {
					zGsensorChange = true;
				}
				if ((!xGsensorChange || !yGsensorChange || !zGsensorChange)) {
					sensorinfo = String.format("FAIL (%.1f, %.1f, %.1f)", event.values[0], event.values[1], event.values[2]);
				} else {
					sensorinfo = String.format("PASS (%.1f, %.1f, %.1f)", event.values[0], event.values[1], event.values[2]);
				}
				x = event.values[0];
				y = event.values[1];
				z = event.values[2];

				sendUpdateMessage(MSG_FLAG_GSENSOR_UPDATE, sensorinfo);
			}
		};



		// Camera
		mSurfaceView = (SurfaceView) findViewById(R.id.camera_surfaceview);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		// mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// media intent
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addDataScheme("file");
		
		registerReceiver(MediaPlugReceiver, intentFilter);

		mStorageManager = (StorageManager) this.getSystemService(Activity.STORAGE_SERVICE);
		try {
			mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
			Class[] argsClass = new Class[1];
			String str = new String();
			argsClass[0] = str.getClass();
			mMethodGetVolumeState = mStorageManager.getClass().getMethod("getVolumeState", argsClass);
		} catch (NoSuchMethodException e) {
			// e.printStackTrace();
		}
		updateCardOrUhostInfo();

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// AudioManager.setOutputDevice(AudioManager.DEVICE_OUT_AUX_DIGITAL);
		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

		// vibrator.vibrate(new long[]{1000, 10, 100, 1000}, -1);
		// Toast.makeText(this, getString(R.string.vibration_confirm),
		// 0).show();

		setTimerTask();
		mSensorFoundCount = 60;

		// gsensor
		if (mSensorManager != null) {
			mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
		}
		mSensorFoundCount = 60;

		// usb
//		IntentFilter intentFilter1 = new IntentFilter(ACTION_USB_STATE);
//		registerReceiver(USBListener, intentFilter1);

		// hdmi
		// openHdmi();
		IntentFilter intentFilter2 = new IntentFilter(HDMI_ACTION);
		registerReceiver(HdmiListener, intentFilter2);

		// Headset
		IntentFilter intentFilter3 = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
		registerReceiver(HeadsetListener, intentFilter3);

		// Headset
		IntentFilter intentFilter4 = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		registerReceiver(NoisyReceiver, intentFilter4);

		// bootup complete
		IntentFilter intentFilter5 = new IntentFilter(ACTION_BOOTUP_COMPLETE);
		registerReceiver(BootupCompleteReceiver, intentFilter5);

		// bluetooth
		IntentFilter intentFilter6 = new IntentFilter();
		intentFilter6.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter6.addAction(BluetoothDevice.ACTION_DISAPPEARED);
		intentFilter6.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
		intentFilter6.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		intentFilter6.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		intentFilter6.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		intentFilter6.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
		intentFilter6.addAction(BluetoothDevice.ACTION_ALIAS_CHANGED);
		intentFilter6.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intentFilter6.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		intentFilter6.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		mContext.registerReceiver(mBluetoothReceiver, intentFilter6);

		IntentFilter intentFilter7 = new IntentFilter(Intent.ACTION_SHUTDOWN);
		mContext.registerReceiver(shundownReceiver, intentFilter7);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "PcbaTest");

		// getContentResolver().setOnTouchListener(this);

		EnablePointerLocationOptions(true);
		bootStart();

		ReadCoinfigFile();

		//ethernet
		checkEthernetState();
		registerReceiver(mEthernetPlugReceiver, new IntentFilter(EthernetManager.ETHERNET_STATE_CHANGED_ACTION));

        //battery intent
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(BatteryReceiver,batteryFilter);

        //sim card test
        new Thread(new Runnable() {
            @Override
            public void run() {
                simCard();
            }
        }).start();

        //serial prot test
        serialPort();

	}

    private void serialPort(){
        try {
             sp1 = new SerialPort("/dev/ttyS0", 115200, new SerialPortListener() {
                @Override
                public void updateSerialPort(boolean result) {
                    if(result){
                        tvSerialPortResultText.setText("ttyS0 PASS");
                        tvSerialPortResultText.setTextColor(Color.BLUE);
                    }else{
                        tvSerialPortResultText.setText("ttyS0 FAIL");
                        tvSerialPortResultText.setTextColor(Color.RED);
                    }
                }
            });
            sp1.test();
        }catch (Exception e){
            tvSerialPortResultText.setText("ttyS0 FAIL");
            tvSerialPortResultText.setTextColor(Color.RED);
            e.printStackTrace();
        }

        try {
            sp2 = new SerialPort("/dev/ttyS5", 115200, new SerialPortListener() {
                @Override
                public void updateSerialPort(boolean result) {
                    if(result){
                        tvSerialPortResultText_b.setText("     ttyS5 PASS");
                        tvSerialPortResultText_b.setTextColor(Color.BLUE);
                    }else{
                        tvSerialPortResultText_b.setText("     ttyS5 FAIL");
                        tvSerialPortResultText_b.setTextColor(Color.RED);
                    }
                }
            });
            sp2.test();
        }catch (Exception e){
            tvSerialPortResultText_b.setText("     ttyS5 FAIL");
            tvSerialPortResultText_b.setTextColor(Color.RED);
            e.printStackTrace();
        }

    }

    private void simCard(){

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(null == connManager){
            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"FAIL (NO NET)");
            return;
        }
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if(activeNetInfo == null || !activeNetInfo.isAvailable()){
            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"FAIL (NO NET)");
            return;
        }
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiInfo != null){
            mWifiCtrl.closeNetCard();
        }
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(null != networkInfo){
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if(null != state){
                if(state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING){
                    switch(activeNetInfo.getSubtype()){
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (2G)");
                            break;
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (2G)");
                            break;
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (2G)");
                            break;
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (2G)");
                            break;
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (2G)");
                            break;



//如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (3G)");
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (3G)");
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (3G)");
                            break;
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (3G)");
                            break;
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (3G)");
                            break;
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (3G)");
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (3G)");
                            break;
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (3G)");
                            break;
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (3G)");
                            break;
//如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (4G)");
                            break;
                        default:
//中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (3G)");
                                break;
                            } else {
                                sendUpdateMessage(MSG_FLAG_SIM_UPDATE,"PASS (ghost net)");
                                break;
                            }
                    }
                }
            }
        }


    }
	
	private void bootStart() {
		File file = new File("/data/data/com.actions.pcbatest/pcba_start.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		getPackageManager().setComponentEnabledSetting(new ComponentName(this, SystemBootCompletedReceiver.class),
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

	}

	private void ReadCoinfigFile() {
		if (testItems != null) {
			if (!testItems.get(1).getUsable().equalsIgnoreCase(DEFAULT)) {
				// btVoiceButton.callOnClick();
				if (mVisualizerFx != null) {
					mVisualizerFx.stop();
				}
				start_record();
			}
				
			if (testItems.get(2).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.autotest_item_name_1)).setVisibility(View.GONE);
				tvDdrResultText.setVisibility(View.GONE);
			}
			if (testItems.get(3).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.autotest_item_name_2)).setVisibility(View.GONE);
				tvFlashResultText.setVisibility(View.GONE);
			}
			if (testItems.get(4).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.autotest_item_name_3)).setVisibility(View.GONE);
				tvWifiResultText.setVisibility(View.GONE);
			}
			if (testItems.get(5).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.autotest_item_name_4)).setVisibility(View.GONE);
				tvGsensorResultText.setVisibility(View.GONE);
			}
			if (testItems.get(6).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.autotest_item_name_5)).setVisibility(View.GONE);
				tvCameraResultText.setVisibility(View.GONE);
			}
			if (testItems.get(7).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.autotest_item_name_9)).setVisibility(View.GONE);
				tvBluetoothResultText.setVisibility(View.GONE);
			}
			if (testItems.get(8).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.autotest_item_name_10)).setVisibility(View.GONE);
				tvRTCResultText.setVisibility(View.GONE);
			}

					
			if (testItems.get(9).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.manualtest_item_name_1)).setVisibility(View.GONE);
				tvCardResultText.setVisibility(View.GONE);
			}
			if (testItems.get(10).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.manualtest_item_name_2)).setVisibility(View.GONE);
				tvUhostResultText.setVisibility(View.GONE);
                ((TextView) findViewById(R.id.manualtest_item_name_2p1)).setVisibility(View.GONE);
                tvUhost2ResultText.setVisibility(View.GONE);
			}
			if (testItems.get(11).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.manualtest_item_name_3)).setVisibility(View.GONE);
				tvUsbResultText.setVisibility(View.GONE);
			}
			if (testItems.get(12).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.manualtest_item_name_4)).setVisibility(View.GONE);
				tvHdmiResultText.setVisibility(View.GONE);
			}
			if (testItems.get(13).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.manualtest_item_name_5)).setVisibility(View.GONE);
				tvHeadsetResultText.setVisibility(View.GONE);
			}
			if (testItems.get(14).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.manualtest_item_name_6)).setVisibility(View.GONE);
				tvKeyResultText.setVisibility(View.GONE);
			}
			if (testItems.get(15).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.manualtest_item_name_9)).setVisibility(View.GONE);
				tvMouseResultText.setVisibility(View.GONE);
			}
			if (testItems.get(16).getUsable().equalsIgnoreCase(DEFAULT)) {
				calibButton.setVisibility(View.GONE);
			}
			if (testItems.get(17).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.manualtest_item_name_10)).setVisibility(View.GONE);
				tvInfraredResultText.setVisibility(View.GONE);
			}
			if (testItems.get(18).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.manualtest_item_name_11)).setVisibility(View.GONE);
				tvEthernetResultText.setVisibility(View.GONE);
			}
			if (testItems.get(19).getUsable().equalsIgnoreCase(DEFAULT)) {
				((TextView) findViewById(R.id.manualtest_item_name_12)).setVisibility(View.GONE);
				tvUsb2ResultText.setVisibility(View.GONE);
			}
            if(testItems.get(31).getUsable().equalsIgnoreCase(DEFAULT)){
                ((TextView) findViewById(R.id.autotest_item_name_6)).setVisibility(View.GONE);
                tvBatteryResultText.setVisibility(View.GONE);
            }
            if(testItems.get(32).getUsable().equalsIgnoreCase(DEFAULT)){
                ((TextView) findViewById(R.id.autotest_item_name_7)).setVisibility(View.GONE);
                tvBacklightResultText.setVisibility(View.GONE);
            }
            if(testItems.get(33).getUsable().equalsIgnoreCase(DEFAULT)){
                ((TextView) findViewById(R.id.autotest_item_name_8)).setVisibility(View.GONE);
                tvLcdResultText.setVisibility(View.GONE);
            }
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.e(TAG, "dcx -------onConfigurationChanged");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.e(TAG, "onStart");

	}

	@Override
	public void onPause() {
		super.onPause();
		Log.e(TAG, "onPause");
		if (mWakeLock.isHeld()) {
			mWakeLock.release();
		}

		// camera
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		EnablePointerLocationOptions(false);
		EnableTouchPointerOptions(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e(TAG, "onResume");
		mWakeLock.acquire();
		EnablePointerLocationOptions(true);
		EnableTouchPointerOptions(true);
		// camera
		if (mCamera == null && mCurrentIndex != -1) {
			mCamera = Camera.open(mCurrentIndex);
			Camera.Parameters p = mCamera.getParameters();
			List<Size> supportedPreviewSizes = p.getSupportedPreviewSizes();
			Size pictureSize = supportedPreviewSizes.get(supportedPreviewSizes.size() - 1);
			p.setPreviewSize(pictureSize.width, pictureSize.height);
			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(p);
			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
			} catch (IOException e) {
				// e.printStackTrace();
			}

		}
		chexkbox_1.setVisibility(View.GONE);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e(TAG, "---onStop");

	}

	@Override
	public void onDestroy() {

        //close serial port thread
        if(sp1 != null){
            sp1.closeThread();
        }

        if(sp2 != null){
            sp2.closeThread();
        }

		super.onDestroy();
		Log.e(TAG, "onDestroy");

		// wifi
		last_wifi_state = mWifiCtrl.getNetCardWorkState();
		if ((last_wifi_state == WifiManager.WIFI_STATE_ENABLED || last_wifi_state == WifiManager.WIFI_STATE_ENABLING) && (first_wifi_state != last_wifi_state)) {
			mWifiCtrl.closeNetCard();
		}
		need_search_net = false;

		mStopFlag = true;
		// bluetooth
		if (!last_bluetooth_state && mBluetoothAdapter != null) {
			mBluetoothAdapter.disable();
		}

		// disk
		if (MediaPlugReceiver != null) {
			unregisterReceiver(MediaPlugReceiver);
		}
//		// usb
//		if (USBListener != null) {
//			unregisterReceiver(USBListener);
//		}

		if (HdmiListener != null) {
			// hdmi
			closeHdmi();
			unregisterReceiver(HdmiListener);
		}
		if(mEthernetPlugReceiver != null){
			unregisterReceiver(mEthernetPlugReceiver);
		}

		// ?????
		releaseMediaPlayer();

		if (unload) {

			mVisualizerFx.pause();
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}
		}

		mVisualizerFx.release();
		if (mRecordFile != null) {
			mRecordFile.delete();
		}
		// headset
		unregisterReceiver(HeadsetListener);
		unregisterReceiver(NoisyReceiver);

		// bootup complete
		if (BootupCompleteReceiver != null) {
			unregisterReceiver(BootupCompleteReceiver);
		}

		if (shundownReceiver != null) {
			unregisterReceiver(shundownReceiver);
		}
		// shundown

		vibrator.cancel();

		timer.cancel();
		// Debug.stopMethodTracing();

		// shutdown ethernet
		//if (mEthManager != null) {
			//mEthManager.setEnabled(false);
			//mEthManager.loadDriver(false);
		//}

		// gsensor
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(mSensorListener);
		}

		if (mBluetoothReceiver != null) {
			unregisterReceiver(mBluetoothReceiver);
		}

		EnablePointerLocationOptions(false);

		if (testItems != null) {
			testItems.clear();
		}
		//ethernet
		regainEthernetState(isEthernetOpened);

        unregisterReceiver(BatteryReceiver);

	}

	// Camera???
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		int number = Camera.getNumberOfCameras();
		if (number > 0 && mCurrentIndex == -1) {
			try {
				mCamera = Camera.open(0);
				mCurrentIndex = 0;
			} catch (Exception e) {

			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mHolder = holder;
		Log.i(TAG, "surfaceChanged() called.");
		try {
			mCamera.stopPreview();

			android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
			android.hardware.Camera.getCameraInfo(Camera.getNumberOfCameras() - 1, info);
			int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
			int degrees = 0;
			switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			}
			int result;
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (info.orientation + degrees) % 360;
				result = (360 - result) % 360; // compensate the mirror
			} else { // back-facing
				result = (info.orientation - degrees + 360) % 360;
			}
			mCamera.setDisplayOrientation(result);

			Camera.Parameters p = mCamera.getParameters();
			List<Size> supportedPreviewSizes = p.getSupportedPreviewSizes();
			Size pictureSize = supportedPreviewSizes.get(supportedPreviewSizes.size() - 1);
			// Size pictureSize = supportedPreviewSizes.get(0);
			p.setPreviewSize(pictureSize.width, pictureSize.height);
			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(p);
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
			sendUpdateMessage(MSG_FLAG_CAMERA_UPDATE, "PASS");
		} catch (Exception e) {
			e.printStackTrace();
			// ??????
			mCamera = null;
			sendUpdateMessage(MSG_FLAG_CAMERA_UPDATE, "FAIL");
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	// mVoiceButtonListener
	private OnClickListener mVoiceButtonListener = new OnClickListener() {
		public void onClick(View v) {
			sendUpdateMessage(MSG_FLAG_TOUCH_UPDATE, "PASS");

			mVisualizerFx.stop();
			start_record();
			btVoiceButton.setEnabled(false);
		}
	};

	// unloadLinstener
	private OnClickListener unloadLinstener = new OnClickListener() {
		public void onClick(View v) {

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(R.string.uninstall_confirm);

			// Add the buttons
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK button
					Log.v(TAG, "close --");
					onDestroy();
					if (chexkbox_1.isChecked()) {
						if (file1_ok && file2_ok) {
							Log.e(TAG, "-------start  support server ----------!");
							unload = true;
							try {
								// system/lib
								DataOutputStream localDataOutputStream = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
								localDataOutputStream.writeBytes("chmod 777 /data/data/com.actions.pcbatest/files/vendorsupportserve \n");
								localDataOutputStream.writeBytes("/data/data/com.actions.pcbatest/files/vendorsupportserve & \n");
								localDataOutputStream.writeBytes("exit\n");
								localDataOutputStream.flush();
								file1_ok = false;
								file2_ok = false;
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							SupportManager supportManager = new SupportManager();
							supportManager.monitorPackageRemove("com.actions.pcbatest", "");

							try {
								DataOutputStream localDataOutputStream = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
								localDataOutputStream.writeBytes("rm -r  /system/lib/libvendorsupportservice.so \n");
								localDataOutputStream.flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					Log.e(TAG, "--DELETE intent  ");
					// �???????
					// Uri uri = Uri.fromParts("package",
					// PcbaTestActivity.this.getPackageName(), null);
					// Intent intent = new Intent(Intent.ACTION_DELETE);
					// intent.setData(uri);
					// startActivity(intent);
					// ????????�?????? ???????
					// ??android:sharedUserId="android.uid.system ?????
					try {
						java.lang.Process process = Runtime.getRuntime().exec("pm uninstall com.actions.pcbatest");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});

			builder.show();
			// Create the AlertDialog

		}
	};
	private OnClickListener restoreListener = new OnClickListener() {
		public void onClick(View v) {
			startActivity(new Intent(android.provider.Settings.ACTION_PRIVACY_SETTINGS));
		}

	};
	private OnClickListener phoneListener = new OnClickListener() {
		public void onClick(View v) {
			// 璺宠浆鍒版嫧鎵撶數璇濈晫闈�			
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:10086"));
			PcbaTestActivity.this.startActivity(intent);

		}
	};
	private OnClickListener calibrationLinstener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setComponent(new ComponentName("com.actions.sensor.calib", "com.actions.sensor.calib.SensorActivity"));
			startActivity(intent);
		}
	};

	private OnCheckedChangeListener checkListener_1 = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			Log.i(TAG, "checked !");

		}
	};

	private boolean test_haha() {
		String str;
		boolean retval = false;
		if (SystemProperties.get("ro.paipro.capable", "false").equalsIgnoreCase("true")){
        return  retval = true;
  	 }else {
       retval = false;
   	}
		
		try {
			FileReader fr = new FileReader("/proc/cpuinfo");
			BufferedReader br = new BufferedReader(fr);

			while ((str = br.readLine()) != null) {
				if (str.contains("520") || str.contains("gs70") || str.contains("atv5") || str.contains("gs90")) {
					retval = true;
					break;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return retval;
	}

	// ==============================================================================
	// CARD銆乁HOST
	// private boolean is_startup = true;

	@SuppressLint("NewApi")
	private void updateCardOrUhostInfo() {
		mount_paths = getVolumePaths();
		if (mount_paths != null) {
			for (int j = 0; j < mount_paths.length; j++) {
				if (mount_paths[j].contains("ext") && mount_paths[j].contains("sd")) {
					if (getVolumeState(mount_paths[j]).equals(Environment.MEDIA_MOUNTED)) {
						try {
							Log.e(TAG, "mount_paths[j] = " + mount_paths[j]);
							StatFs stat = new StatFs(mount_paths[j]);
							// 获得SD卡和手机内存的使用情况,返回 Int ，大小，以字节为单位，一个文件系统
							long blockSize = stat.getBlockSize();
							Log.e(TAG, "blockSize 文件系统/mnt/sd-ext一个块的大小(byte) ： " + blockSize);
							// 获得SD卡和手机内存的使用情况,返回 Int ，获取该区域可用的文件系统数
							long totalBlocks = stat.getBlockCount();
							Log.e(TAG, "totalBlocks /mnt/sd-ext文件系统上总共的块 ：" + totalBlocks);
							 long availablelocks = stat.getAvailableBlocks();
							String ssize;
//							long size_kB = getCardPhysicalSize();
							long size_kB = totalBlocks*blockSize;
							Log.e(TAG, "size_kB = " + size_kB);
							if (size_kB > 1048576L) {
								ssize = String.format("(%.2fGB)", size_kB / 1024 /1024/ 1024.0f);
							} else {
								ssize = "(" + (size_kB / 1024/1024) + "MB)";
							}

							try {
								byte[] databuf = new byte[1024 * 1024];
								File file = new File(mount_paths[j] + File.separator + "pcba_test_data.bin");
								FileOutputStream localFileOutputStream = new FileOutputStream(file);
								localFileOutputStream.write(databuf);
								localFileOutputStream.flush();
								localFileOutputStream.close();
								file.delete();
								sendUpdateMessage(MSG_FLAG_CARD_UPDATE, "PASS" + ssize);
							} catch (IOException e) {
								e.printStackTrace();
								Log.e(TAG, "Write Error exception" + e.toString());
								sendUpdateMessage(MSG_FLAG_CARD_UPDATE, "FAIL" + ssize + "(WRITE ERROR)");
							}
						} catch (Exception e) {
							Log.e(TAG, "Read Error exception " + e.toString());
							sendUpdateMessage(MSG_FLAG_CARD_UPDATE, "FAIL(READ ERROR)");
						}
					}
					// else {
					// if (is_startup) {
					// sendUpdateMessage(MSG_FLAG_CARD_UPDATE, getString(R.string.is_waiting_plugin));
					// }
					// }
				} else if (mount_paths[j].contains("uhost1") || mount_paths[j].contains("udisk")) {
					if (getVolumeState(mount_paths[j]).equals(Environment.MEDIA_MOUNTED)) {
                        Log.v("UP","uhost1uhot1udisk");
						try {
							StatFs stat = new StatFs(mount_paths[j]);
							long blockSize = stat.getBlockSize();
							long totalBlocks = stat.getBlockCount();
							Log.e(TAG, "blockSize = " + blockSize);
							Log.e(TAG, "totalBlocks = " + totalBlocks);
							String ssize;
							if (blockSize * totalBlocks > 1073741824L) {
								float size = blockSize * totalBlocks / 1024 / 1024 / 1024.0f;
								ssize = String.format("(%.2fGB)", size);
							} else {
								ssize = "(" + (blockSize * totalBlocks / 1024 / 1024) + "MB)";
							}

							// read /write
							try {
								byte[] databuf = new byte[1024 * 1024];
								File file = new File(mount_paths[j] + File.separator + "pcba_test_data.bin");
								file.delete();
								FileOutputStream localFileOutputStream = new FileOutputStream(file);
								localFileOutputStream.write(databuf);
								localFileOutputStream.flush();
								localFileOutputStream.close();
								file.delete();
								sendUpdateMessage(MSG_FLAG_UHOST_UPDATE, "PASS" + ssize);
							} catch (IOException localException) {
								sendUpdateMessage(MSG_FLAG_UHOST_UPDATE, "FAIL" + ssize + "(WRITE ERROR)");
							}

						} catch (Exception e) {
							sendUpdateMessage(MSG_FLAG_UHOST_UPDATE, "FAIL(READ ERROR)");
						}

					}
					// else {
					// if (is_startup) {
					// sendUpdateMessage(MSG_FLAG_UHOST_UPDATE, getString(R.string.is_waiting_plugin));
					// }
					// }
				}else if(mount_paths[j].contains("uhost2")){
                    if (getVolumeState(mount_paths[j]).equals(Environment.MEDIA_MOUNTED)) {
                        Log.v("UP","uhost1uhot2udisk");
                        try {
                            StatFs stat = new StatFs(mount_paths[j]);
                            long blockSize = stat.getBlockSize();
                            long totalBlocks = stat.getBlockCount();
                            Log.e(TAG, "blockSize = " + blockSize);
                            Log.e(TAG, "totalBlocks = " + totalBlocks);
                            String ssize;
                            if (blockSize * totalBlocks > 1073741824L) {
                                float size = blockSize * totalBlocks / 1024 / 1024 / 1024.0f;
                                ssize = String.format("(%.2fGB)", size);
                            } else {
                                ssize = "(" + (blockSize * totalBlocks / 1024 / 1024) + "MB)";
                            }

                            // read /write
                            try {
                                byte[] databuf = new byte[1024 * 1024];
                                File file = new File(mount_paths[j] + File.separator + "pcba_test_data.bin");
                                file.delete();
                                FileOutputStream localFileOutputStream = new FileOutputStream(file);
                                localFileOutputStream.write(databuf);
                                localFileOutputStream.flush();
                                localFileOutputStream.close();
                                file.delete();
                                sendUpdateMessage(MSG_FLAG_UHOST2_UPDATE, "PASS" + ssize);
                            } catch (IOException localException) {
                                sendUpdateMessage(MSG_FLAG_UHOST2_UPDATE, "FAIL" + ssize + "(WRITE ERROR)");
                            }

                        } catch (Exception e) {
                            sendUpdateMessage(MSG_FLAG_UHOST2_UPDATE, "FAIL(READ ERROR)");
                        }

                    }
                } else if(mount_paths[j].contains("uhost")){
                    if (getVolumeState(mount_paths[j]).equals(Environment.MEDIA_MOUNTED)) {

                        try {
                            StatFs stat = new StatFs(mount_paths[j]);
                            long blockSize = stat.getBlockSize();
                            long totalBlocks = stat.getBlockCount();
                            Log.e(TAG, "blockSize = " + blockSize);
                            Log.e(TAG, "totalBlocks = " + totalBlocks);
                            String ssize;
                            if (blockSize * totalBlocks > 1073741824L) {
                                float size = blockSize * totalBlocks / 1024 / 1024 / 1024.0f;
                                ssize = String.format("(%.2fGB)", size);
                            } else {
                                ssize = "(" + (blockSize * totalBlocks / 1024 / 1024) + "MB)";
                            }

                            // read /write
                            try {
                                byte[] databuf = new byte[1024 * 1024];
                                File file = new File(mount_paths[j] + File.separator + "pcba_test_data.bin");
                                file.delete();
                                FileOutputStream localFileOutputStream = new FileOutputStream(file);
                                localFileOutputStream.write(databuf);
                                localFileOutputStream.flush();
                                localFileOutputStream.close();
                                file.delete();
                                sendUpdateMessage(MSG_FLAG_USB_UPDATE, "PASS" + ssize);
                            } catch (IOException localException) {
                                sendUpdateMessage(MSG_FLAG_USB_UPDATE, "FAIL" + ssize + "(WRITE ERROR)");
                            }

                        } catch (Exception e) {
                            sendUpdateMessage(MSG_FLAG_USB_UPDATE, "FAIL(READ ERROR)");
                        }

                        Log.v("SATA","uhost0uhost0");
                    }
                }
			}
		}
		// is_startup = false;
	}

	private String[] getVolumePaths() {
		String[] paths = null;
		try {
			paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		Log.e(TAG, " getVolumePaths :" + paths.length);
		for (String s : paths) {
			Log.e(TAG, " getVolumePaths path:" + s);
		}

		return paths;
	}

	private String getVolumeState(String volume) {
		String state = null;
		try {
			state = (String) mMethodGetVolumeState.invoke(mStorageManager, volume);
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
		} catch (InvocationTargetException e) {
			// e.printStackTrace();
		}
		return state;
	}

	private final BroadcastReceiver MediaPlugReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e(TAG, " MediaPlugReceiver BroadcastReceiver");
			if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED))// SD
			{
				//String a = intent.getExtra("path").toString();
				//Log.e(TAG, " BroadcastReceiver path :" + a);
				updateCardOrUhostInfo();
				
				
			} else if (intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)
					|| intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED) || intent.getAction().equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {
			}
		}
	};

	// =========================================================================
	private String getDdrTestInitText() {
		String cmdinfo = runCommandLine("cat /proc/meminfo");
		int size = 0;
		String retval = "FAIL";
		if (cmdinfo != null) {
			String[] lines = cmdinfo.split("\n");
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				if (line.contains("MemTotal")) {
					String strarray[] = line.split(" ");
					for (int j = 0; j < strarray.length; j++) {
						try {
							size = Integer.parseInt(strarray[j]);
						} catch (NumberFormatException e) {
						}
						if (size > 0) {
							retval = "PASS (" + size / 1024 + "MB)";
							break;
						}
					}
					break;
				}
			}
		}
		return retval;
	}

	// ==========================================================
	private String getFlashTestInitText() {
		StatFs stat = new StatFs(Environment.getFlashStorageDirectory().getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();

		String retval;
		long size_kB = totalBlocks*blockSize;
		Log.e(TAG, "size_kB = " + size_kB);
		if (size_kB > 1048576L) {
			retval = String.format("(%.2fGB)", size_kB / 1024 /1024/ 1024.0f);
		} else {
			retval = "(" + (size_kB / 1024/1024) + "MB)";
		}

		return retval;


		// runCommandLine("busybox insmod blk1drv.ko is_probe=1");
		// get totalFlash size
		/*String cmdinfo = null;
		if (storageNandType) {
			cmdinfo = runCommandLine("cat /proc/nand/phy_cap");
		} else {
			cmdinfo = runCommandLine("cat /proc/mmc/phy_cap");
		}

		String retval;
		int size = 0;
		try {
			size = Integer.parseInt(cmdinfo.trim());
		} catch (NumberFormatException e) {
		}

		if (size > 1024) {
			float size_GB = 0;
			if (storageNandType) {
				size_GB = size / 1024.0f;
			} else {
				size_GB = size / 1024.0f;
				//size_GB = size_GB / 2048.0f;
			}

			DecimalFormat decimalFormat=new DecimalFormat(".0");

			retval=decimalFormat.format(size_GB) + " GB";
			//retval = String.format("%s GB", (int) size_GB);
		} else if (size > 0) {
			retval = String.format("%d MB", size);
		} else {
			retval = "FAIL";
		}
		return retval;*/
	}

	private String getAvailableFlashTestInitTest() {

		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getAvailableBlocks();

		String retval;
		long size_kB = totalBlocks*blockSize;
		Log.e(TAG, "size_kB = " + size_kB);
		if (size_kB > 1048576L) {
			retval = String.format("(%.2fGB)", size_kB / 1024 /1024/ 1024.0f);
		} else {
			retval = "(" + (size_kB / 1024/1024) + "MB)";
		}

		return retval;

		// get avaliable Flash size
		/*String cmdinfo = runCommandLine("cat /proc/nand/log_cap");
		float totalSize = Integer.parseInt(runCommandLine("cat /proc/nand/phy_cap").trim());
		float avaliableSize = 0;
		String avalilable;
		try {
			avaliableSize = Integer.parseInt(cmdinfo.trim());

		} catch (NumberFormatException e) {
		}


		if (avaliableSize > 1024) {
			float size_GB = avaliableSize / 1024.0f;
			Log.e(TAG, "avaliableSize:" + avaliableSize);
			Log.e(TAG, "Totalsize:" + totalSize);
			int total = (int) (totalSize / 1024.0f);

			if (size_GB >= total) {
				size_GB = total;
				avalilable = String.format("%d GB", (int) size_GB);
			} else {
				avalilable = String.format("%.1f GB", size_GB);
			}

		} else if (avaliableSize > 0) {
			avalilable = String.format("%d MB", avaliableSize);
		} else {
			avalilable = "FAIL";
		}
		return avalilable;*/
	}

	// ==========================================================
	private String getFlashTestInitText2() {
		String cmdinfo = runCommandLine("cat /proc/partitions");
		long size = 0;
		long blocksize = 0;
		String retval = "FAIL";
		if (cmdinfo != null) {
			String[] lines = cmdinfo.split("\n");
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				if (line.contains("act")) {
					String strarray[] = line.split(" ");
					for (int j = strarray.length - 1; j > 0; j--) {
						blocksize = 0;
						try {
							blocksize = Integer.parseInt(strarray[j]);
						} catch (NumberFormatException e) {
						}
						if (blocksize > 0) {
							size += blocksize;
							break;
						}
					}
				}
			}
		}
		if (size > 0) {
			if (size > 1048576) {
				retval = String.format("PASS (%.2fGB)", size / 1024 / 1024.0f);
			} else {
				retval = String.format("PASS (%d MB)", size / 1024);
			}
		}
		return retval;
	}

	private long getCardPhysicalSize() {
		String cmdinfo = runCommandLine("cat /proc/partitions");
		long size = 0;
		if (cmdinfo != null) {
			String[] lines = cmdinfo.split("\n");
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				if (line.contains("mmcblk0") && !line.contains("mmcblk0p")) {
					String strarray[] = line.split(" ");
					for (int j = strarray.length - 1; j > 0; j--) {
						try {
							size = Integer.parseInt(strarray[j]);
							Log.v(TAG, "getCardPhysicalSize size = " + size);
						} catch (NumberFormatException e) {
						}
						if (size > 0) {
							break;
						}
					}
					break;
				}
			}
		}
		return size;
	}

	private String getWifiTestInitText() {
		return getString(R.string.testing);
	}

	private String getGsensorTestInitText() {
		return getString(R.string.testing);
	}

	private String getCameraTestInitText() {
		return getString(R.string.testing);
	}

	private String getBatteryTestInitText() {
		return getString(R.string.testing);
	}

	private String getBacklightTestInitText() {
		return getString(R.string.testing);
	}

	private String getLcdTestInitText() {
		return getString(R.string.testing);
	}

	private String getBluetoothTestInitText() {
		return getString(R.string.testing);
	}

	private String getCardTestInitText() {
		return getString(R.string.is_waiting_plugin);
	}

	private String getUhostTestInitText() {
		return getString(R.string.is_waiting_plugin);
	}

	private String getUsbTestInitText() {
		return getString(R.string.testing);
	}

	private String getHdimTestInitText() {
		return getString(R.string.testing);
	}

	private String getHeadsetTestInitText() {
		return getString(R.string.is_waiting_plugin);
	}

	private String getKeyTestInitText() {
		return getString(R.string.key_notify);
	}

	private String getVoiceTestInitText() {
		return getString(R.string.voice_notify);
	}

	private String getTouchTestInitText() {
		return getString(R.string.touch_notify);
	}

	private String getMouseTestInitText() {
		return getString(R.string.is_waiting_plugin);
	}

	private String getInfraredInitText() {
		return getString(R.string.is_waiting_control);
	}

	private String getEthernetInitText() {
		return getString(R.string.is_waiting_plugin);
	}

	private String getUsb2InitText() {
		return getString(R.string.is_waiting_plugin);
	}

	// bluetooth================================================
	public void dealBluetoothThread() {
		new Thread() {
			@Override
			public void run() {
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (mBluetoothAdapter == null) {
					//
					sendUpdateMessage(MSG_FLAG_BLUETOOTH_UPDATE, getString(R.string.bluetooth_not_support));
					Log.i(TAG, "no bluetooth");
					return;
				} else {
					Log.i(TAG, "bluetooth=" + mBluetoothAdapter.toString());
				}
				last_bluetooth_state = mBluetoothAdapter.isEnabled();
				//
				if (!last_bluetooth_state) {

					boolean retval = mBluetoothAdapter.enable();
					if (!retval) {
						sendUpdateMessage(MSG_FLAG_BLUETOOTH_UPDATE, "FAIL (Fail to open)");
						return;
					}
					sendUpdateMessage(MSG_FLAG_BLUETOOTH_UPDATE, getString(R.string.bluetooth_is_openning));

				} else {
					Log.i(TAG, "Bluetooth is enable.");
				}

				int state = mBluetoothAdapter.getState();
				int count = 0;
				do {
					try {
						sleep(1000);
						count++;
						if (count >= 100) {
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				} while (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON);

				if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
					Log.e(TAG, "get device state:" + state);
					sendUpdateMessage(MSG_FLAG_BLUETOOTH_UPDATE, "FAIL (Fail to open)");
					return;
				}

				//
				Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
				for (int i = 0; i < devices.size(); i++) {
					BluetoothDevice device = (BluetoothDevice) devices.iterator().next();
				}

				sendUpdateMessage(MSG_FLAG_BLUETOOTH_UPDATE, getString(R.string.bluetooth_is_searching));
				mBluetoothAdapter.startDiscovery();

				int times = 0;
				while (!mStopFlag && times < 2 && !mBluetoothDeviceFound) {
					try {
						sleep(300000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					try {
						Log.i(TAG, "The " + times + "th Change Scanmode");
						if (mBluetoothAdapter.isDiscovering()) {
							mBluetoothAdapter.cancelDiscovery();
						}
						mBluetoothAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 300);
						mBluetoothAdapter.startDiscovery();
					} catch (Exception e) {
						e.printStackTrace();
						sendUpdateMessage(MSG_FLAG_BLUETOOTH_UPDATE, "FAIL (Unable to change scanmode)");
						return;
					}
				}
				if (!mBluetoothDeviceFound) {
					sendUpdateMessage(MSG_FLAG_BLUETOOTH_UPDATE, "FAIL (Unable to find device)");
				}
			}
		}.start();
	}

	private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i(TAG, "action=" + action);

			BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (dev != null) {
				Log.i(TAG, "device=" + dev.getName());
				mBluetoothDeviceFound = true;
				sendUpdateMessage(MSG_FLAG_BLUETOOTH_UPDATE, "PASS (" + dev.getName() + ")");
			}

			if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
				mBluetoothAdapter.startDiscovery();
			}

			if (BluetoothDevice.ACTION_FOUND.equals(action) || BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				mBluetoothAdapter.cancelDiscovery();
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int connectState = device.getBondState();
				if (connectState == BluetoothDevice.BOND_NONE && BluetoothDevice.ACTION_FOUND.equals(action)) {
					try {
						Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
						createBondMethod.invoke(device);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (connectState == BluetoothDevice.BOND_BONDED) {
					final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
					UUID uuid = UUID.fromString(SPP_UUID);
					BluetoothSocket socket;
					try {
						socket = device.createRfcommSocketToServiceRecord(uuid);
						socket.connect();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				if (device != null) {
					mBluetoothAdapter.cancelDiscovery();
					int connectState = device.getBondState();
					switch (connectState) {
					case BluetoothDevice.BOND_NONE:
						try {
							Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
							createBondMethod.invoke(device);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;

					case BluetoothDevice.BOND_BONDED:
						connectBluetooth(device);
						break;
					}
				}
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device != null) {
					int connectState = device.getBondState();
					switch (connectState) {
					case BluetoothDevice.BOND_NONE:
						break;
					case BluetoothDevice.BOND_BONDING:
						break;
					case BluetoothDevice.BOND_BONDED:
						connectBluetooth(device);
						break;
					}
				}
			}
		}
	};

	private void connectBluetooth(BluetoothDevice btDev) {
		final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		UUID uuid = SPP_UUID;
		try {
			BluetoothSocket btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
			Log.d("BlueToothTestActivity", "");
			btSocket.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// wifi???================================================
	public void dealWifiCtrlThread() {
		new Thread() {
			@Override
			public void run() {
				if (!mWifiCtrl.wifiIsEnable()) {
					sendUpdateMessage(MSG_FLAG_WIFI_UPDATE, "FAIL,(fail to open)");
					Log.i(TAG, "no wifi");
					return;
				}

				first_wifi_state = mWifiCtrl.getNetCardWorkState();
				last_wifi_state = first_wifi_state;
				// Log.i(TAG, "last_wifi_state = "+last_wifi_state);

				if (last_wifi_state != WifiManager.WIFI_STATE_ENABLED && last_wifi_state != WifiManager.WIFI_STATE_ENABLING) {
					sendUpdateMessage(MSG_FLAG_WIFI_UPDATE, getString(R.string.wifi_is_openning));

					// mWifiCtrl.openNetCard();
				}
				mWifiCtrl.openNetCard();

				int n = 100;
				while (mWifiCtrl.getNetCardWorkState() != WifiManager.WIFI_STATE_ENABLED) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// e.printStackTrace();
					}
					if ((n--) < 0) {
						break;
					}
				}

				sendUpdateMessage(MSG_FLAG_WIFI_UPDATE, getString(R.string.wifi_is_searching));


				boolean wifi_is_ok = false;
				for (int i = 0; i < 15; i++) {
					if (mWifiCtrl.isNetWorkOK()) {
						mWifiCtrl.updateConnectInfo();
						String ssid = mWifiCtrl.getConnectedSSID();
						if (ssid != null && ssid.length() > 2 && !ssid.contains("unknown ssid")) {
							Log.e(TAG, "connect to :" + ssid);
							String info = "PASS (" + getString(R.string.wifi_level) + mWifiCtrl.getRssi() + " | Connect to " + ssid + ")";
							sendUpdateMessage(MSG_FLAG_WIFI_UPDATE, info);
							wifi_is_ok = true;
							break;
							// Log.i(TAG, info);
						} else {
							mWifiCtrl.scan();
							ScanResult result = mWifiCtrl.getScanResult(0);
							if (result != null) {
								String info = "PASS (" + getString(R.string.wifi_level) + result.level + "dBm |Search to " + result.SSID + ")";
								sendUpdateMessage(MSG_FLAG_WIFI_UPDATE, info);
								wifi_is_ok = true;
								break;
								// Log.i(TAG, info);
							}
						}
					} else {
					}
					if (!need_search_net)
						break;

					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// e.printStackTrace();
					}
				}
				if (!wifi_is_ok) {
					sendUpdateMessage(MSG_FLAG_WIFI_UPDATE, "FAIL(Unable to find device)");
					// Log.i(TAG, "WIFI FAIL");
				}
			}
		}.start();
	}

	// USB=====================================================
	private boolean usb_is_connected = false;
	private final BroadcastReceiver USBListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_USB_STATE)) {

				if (intent.getBooleanExtra(USB_CONNECTED, false) && intent.getBooleanExtra(USB_FUNCTION_MASS_STORAGE, false)) {
					usb_is_connected = true;
					sendUpdateMessage(MSG_FLAG_USB_UPDATE, "PASS(" + getString(R.string.is_connected) + ")");
				} else if (intent.getBooleanExtra(USB_CONNECTED, false) && intent.getBooleanExtra(USG_FUNCTIONS_MTP, false)) {
					Log.e(TAG, "--  mtp  mtp  -----------");
					usb_is_connected = true;
					sendUpdateMessage(MSG_FLAG_USB_UPDATE, "PASS(" + getString(R.string.is_mtp_connected) + ")");
				} else {
					if (usb_is_connected) {
						sendUpdateMessage(MSG_FLAG_USB_UPDATE, "PASS(" + getString(R.string.is_unconnected) + ")");
					} else {
						sendUpdateMessage(MSG_FLAG_USB_UPDATE, getString(R.string.is_waiting_plugin));
					}
				}
			}
		}
	};

	// HDMI=====================================================
	private boolean hdmi_is_open = false;

	private void openHdmi() {
		// if
		// (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_TVOUT_HDMI))
		// {
		// return;
		// }

		String hdmiMode = SystemProperties.get("ro.settings.config.hdmi", "off");
		if (hdmiMode.equals("autodetect")) {
			hdmi_is_open = true;
		} else if (hdmiMode.equals("on")) {
			hdmi_is_open = true;
		} else {
			SystemProperties.set("ro.settings.config.hdmi", "on");
			TvoutUtils.getInstanceByName(TvoutUtils.TVOUT_HDMI).switchToSelectModeByModeName("1280x720P_50HZ_16:9");
		}
	}

	private void closeHdmi() {
		/*if (!hdmi_is_open) {
			// TvoutUtils.getInstanceByName(TvoutUtils.TVOUT_HDMI).switchToSelectModeByModeName("");
			TvoutUtils.getInstanceByName(TvoutUtils.TVOUT_HDMI).closeTvoutDisplay();
			SystemProperties.set("ro.settings.config.hdmi", "off");
		} else if (hdmi_is_connected) {
			TvoutUtils.getInstanceByName(TvoutUtils.TVOUT_HDMI).closeTvoutDisplay();
			SystemProperties.set("ro.settings.config.hdmi", "off");
		}*/
	}

	private boolean hdmi_is_connected = false;
	private final BroadcastReceiver HdmiListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String data = runCommandLine("cat /sys/class/misc/hdmi/hdmi_vid_table");
			if (intent.getAction().equals(HDMI_ACTION)) {
				if (intent.getBooleanExtra(EXTRA_HDMI_PLUGGED_STATE, false)) {
					hdmi_is_connected = true;
					if (data != null && !data.equals("")) {
						sendUpdateMessage(MSG_FLAG_HDMI_UPDATE, "PASS(" + data + ")");
					} else {
						sendUpdateMessage(MSG_FLAG_HDMI_UPDATE, "PASS(" + getString(R.string.is_connected) + ")");
					}
				} else {
					if (hdmi_is_connected) {
						sendUpdateMessage(MSG_FLAG_HDMI_UPDATE, "PASS(" + getString(R.string.is_unconnected) + ")");
					} else {
						sendUpdateMessage(MSG_FLAG_HDMI_UPDATE, getString(R.string.is_waiting_plugin));
					}
				}
			}
		}
	};

	// HEADSET=====================================================
	private boolean headset_is_connected = false;
	private final BroadcastReceiver HeadsetListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v(TAG, "HeadsetListener");
			if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
				boolean mIsHeadsetOn = (intent.getIntExtra("state", 0) == 1);
				if (mIsHeadsetOn) {
					headset_is_connected = true;
					sendUpdateMessage(MSG_FLAG_HEADSET_UPDATE, "PASS(" + getString(R.string.is_connected) + ")");
				} else {
					if (headset_is_connected) {
						sendUpdateMessage(MSG_FLAG_HEADSET_UPDATE, "PASS(" + getString(R.string.is_unconnected) + ")");

					} else {
						sendUpdateMessage(MSG_FLAG_HEADSET_UPDATE, getString(R.string.is_waiting_plugin));
					}
				}
			}
		}
	};
	public static boolean checkEthernet(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conn.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		return networkInfo.isConnected();
	}

	private boolean vol_up_ok = false;
	private boolean vol_down_ok = false;

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		sendUpdateMessage(MSG_FLAG_INFRARED_UPDATE, "PASS");
		boolean hit_key = false;
		boolean vol_up_is_down = false;
		boolean vol_down_is_down = false;

		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (event.getAction() == KeyEvent.ACTION_UP) {
				vol_up_ok = true;
			} else if (event.getAction() == KeyEvent.ACTION_DOWN) {
				vol_up_is_down = true;
			}
			hit_key = true;
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (event.getAction() == KeyEvent.ACTION_UP) {
				vol_down_ok = true;
			} else if (event.getAction() == KeyEvent.ACTION_DOWN) {
				vol_down_is_down = true;
			}
			hit_key = true;
			break;
			case KeyEvent.KEYCODE_MENU:
				Intent agingTestIntent = new Intent();
				agingTestIntent.setClassName("com.actions.agingtest","com.actions.agingtest.AgingTestActivity");
				startActivity(agingTestIntent);
				break ;

		default:
			break;
		}
		if (hit_key) {
			String info;

			if (vol_up_is_down) {
				info = "Vol+: down";
			} else {
				if (vol_up_ok) {
					info = "Vol+: ok";
				} else {
					info = "Vol+: wait";
				}
			}

			if (vol_down_is_down) {
				info += "  Vol-: down";
			} else {
				if (vol_down_ok) {
					info += "  Vol-: ok";
				} else {
					info += "  Vol-: wait";
				}
			}
			if (vol_up_ok && vol_down_ok) {
				sendUpdateMessage(MSG_FLAG_KEY_UPDATE, "PASS(" + info + ")");
			} else {
				sendUpdateMessage(MSG_FLAG_KEY_UPDATE, info);
			}
		}
		return super.dispatchKeyEvent(event);
	}


	// ======================================================================================
	// RECORD
	private void start_record() {
		mRecordTimerCount = 0;
		sendUpdateMessage(MSG_FLAG_START_RECORDING, null);
		// BUGFIX:BUG00299498 No permission in reading and writing in CTS firmware.
		mRecordFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + RECORD_FILE_NAME);
		if (mRecorder == null) {
			mRecorder = new Recorder();
		}
		mRecorder.stopRecording();
		mRecordFile.delete();
		mVUMeter.setRecorder(mRecorder);
		mRecorder.setOutputFile(mRecordFile.getAbsolutePath());
		mRecorder.startRecording(RECORD_OUTPUT_FORMAT_WAV, ".wav", this);
		mRecordTimerCount = 50;
	}

	private void releaseMediaPlayer() {
		if (mMediaPlayer != null) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	// ===========================================================================
	// Runcommanline
	private String runCommandLine(String cmdline) {
		try {
			java.lang.Process process = Runtime.getRuntime().exec(cmdline);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			reader.close();
			process.waitFor();
			process.destroy();
			return output.toString();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			// e.printStackTrace();
			return null;
		}
	}

	// =================================================
	TimerTask timertask = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = MSG_FLAG_TIMER_INT;
			messageHandler.sendMessage(message);
		}
	};

	// setTimerTask
	private void setTimerTask() {
		timer = new Timer(); //  timer
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = MSG_FLAG_TIMER_INT;
				messageHandler.sendMessage(message);
			}
		}, 0, 100);
	}

	// SendMessage
	private void sendUpdateMessage(int type, String data) {
		Message message = Message.obtain();
		message.what = type;
		message.obj = data;
		messageHandler.sendMessage(message);
	}

	// handler
	private class MessageHandler extends Handler {
		public MessageHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			String msg_info = "";
			int result_color = Color.GRAY;
			if (msg.obj != null) {
				msg_info = msg.obj.toString();
				if (msg_info.contains("PASS")) {
					result_color = Color.BLUE;
				} else if (msg_info.contains("FAIL")) {
					result_color = Color.RED;
				}
			}

			switch (msg.what) {

			// Timer
			case MSG_FLAG_TIMER_INT:
				mTimerCount++;
				if (mTimerCount == 80 && !isBootupComplete) {
					mVisualizerFx.start();
					tvRecorderPlayerStatus.setText(getString(R.string.playing_music));

					vibrator.vibrate(new long[] { 1000, 10, 100, 1000 }, -1);
					Toast.makeText(PcbaTestActivity.this, getString(R.string.vibration_confirm), Toast.LENGTH_SHORT).show();
				}

				if (mRecordTimerCount > 0) {
					mRecordTimerCount--;
					if (mRecordTimerCount == 0) {
						mRecorder.stopRecording();

						mVisualizerFx.setVisibility(View.VISIBLE);
						mVUMeter.setVisibility(View.GONE);
						tvRecorderPlayerStatus.setText(getString(R.string.voice_is_replaying));
						tvVoiceResultText.setText("PASS(" + getString(R.string.voice_is_replaying) + ")");
						tvVoiceResultText.setTextColor(Color.BLUE);
						if (testItems != null && (testItems.size() > 0)) {
							if (!testItems.get(1).getUsable().equalsIgnoreCase(DEFAULT)) {
								mVisualizerFx.changePlayFile(Uri.fromFile(mRecordFile), false);
							} else {
								mVisualizerFx.changePlayFile(Uri.fromFile(mRecordFile), true);
							}
						} else {
							mVisualizerFx.changePlayFile(Uri.fromFile(mRecordFile), true);
						}
						btVoiceButton.setEnabled(true);
					}
				} else {
					if ((testItems != null) && (testItems.size() > 0)) {
						if (!testItems.get(1).getUsable().equalsIgnoreCase(DEFAULT)) {
							mRecordTimerCount--;
							if (mRecordTimerCount == -100) {
								if (mVisualizerFx != null) {
									mVisualizerFx.stop();
								}
								start_record();
							}
						}
					}
				}

				// Camera			
				if (mCameraTimerCount > 30) {
					int number = Camera.getNumberOfCameras();
					Log.i(TAG, "Camera.getNumberOfCameras() = " + number);
					if ((number >= 2 && mCamera != null) || (number > 0 && mCurrentIndex == -1)) {
						mCurrentIndex++;
						if (mCurrentIndex >= number) {
							mCurrentIndex = 0;
						}
						if (mCamera != null) {
							mCamera.stopPreview();
							mCamera.release();
						}

						try {
							mCamera = Camera.open(mCurrentIndex);

							android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
							android.hardware.Camera.getCameraInfo(mCurrentIndex, info);
							int rotation = PcbaTestActivity.this.getWindowManager().getDefaultDisplay().getRotation();
							int degrees = 0;
							switch (rotation) {
							case Surface.ROTATION_0:
								degrees = 0;
								break;
							case Surface.ROTATION_90:
								degrees = 90;
								break;
							case Surface.ROTATION_180:
								degrees = 180;
								break;
							case Surface.ROTATION_270:
								degrees = 270;
								break;
							}
							int result;
							if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
								result = (info.orientation + degrees) % 360;
								result = (360 - result) % 360; // compensate the
																// mirror
							} else { // back-facing
								result = (info.orientation - degrees + 360) % 360;
							}
							mCamera.setDisplayOrientation(result);

							Camera.Parameters p = mCamera.getParameters();
							List<Size> supportedPreviewSizes = p.getSupportedPreviewSizes();
							Size pictureSize = supportedPreviewSizes.get(supportedPreviewSizes.size() - 1);
							p.setPreviewSize(pictureSize.width, pictureSize.height);
							p.setFlashMode(Parameters.FLASH_MODE_TORCH);
							mCamera.setParameters(p);
							mCamera.setPreviewDisplay(mHolder);
							mCamera.startPreview();
							Toast.makeText(PcbaTestActivity.this, getString(R.string.switch_camera), Toast.LENGTH_SHORT).show();
							sendUpdateMessage(MSG_FLAG_CAMERA_UPDATE, "PASS");
						} catch (Exception e) {
							e.printStackTrace();
							mCamera = null;
							sendUpdateMessage(MSG_FLAG_CAMERA_UPDATE, "FAIL");
						}
					}
					mCameraTimerCount = 0;
				} else {
					mCameraTimerCount++;
					// Log.i(TAG, "mCameraTimerCount :"+mCameraTimerCount);
					if (mCameraTimerCount == 10) {
						if (mCamera == null) {
							return;
						}
						if (mCamera != null) {
							Camera.Parameters p = mCamera.getParameters();
							p.setFlashMode(Parameters.FLASH_MODE_OFF);
							mCamera.setParameters(p);
						}
					}
					if (mCameraTimerCount == 20) {
						if (mCamera == null) {
							return;
						}
						if (mCamera != null) {
							Camera.Parameters p = mCamera.getParameters();
							p.setFlashMode(Parameters.FLASH_MODE_TORCH);
							mCamera.setParameters(p);
						}
					}
					if (mCameraTimerCount == 29) {
						if (mCamera == null) {
							return;
						}
						if (mCamera != null) {
							Camera.Parameters p = mCamera.getParameters();
							p.setFlashMode(Parameters.FLASH_MODE_OFF);
							mCamera.setParameters(p);
						}
					}
				}

				// gsensor
				if (mSensorFoundCount > 0) {
					mSensorFoundCount--;
					if (mSensorFoundCount <= 0) {
						tvGsensorResultText.setText("FAIL");
						tvGsensorResultText.setTextColor(Color.RED);
					}
				}
				if (mTimerCount % 5 == 0) {

					if (checkEthernet(mContext)) {
						tvEthernetResultText.setText("PASS(" + getString(R.string.is_connected) + ")");
						tvEthernetResultText.setTextColor(Color.BLUE);
						isEthernetConnected = true;
					} else {
						if (isEthernetConnected) {
							tvEthernetResultText.setText("PASS(" + getString(R.string.is_unconnected) + ")");
							tvEthernetResultText.setTextColor(Color.BLUE);
						}
					}


					File mousefile = new File("/dev/input/mouse0");
					if (mousefile.exists()) {
						String mouse =  runCommandLine("cat /sys/class/input/mouse0/device/name");
						Log.v(TAG,"mouse:"+mouse);
						if (mouse.contains("Mouse") || mouse.contains("mouse")){
							tvMouseResultText.setText("PASS(" + getString(R.string.is_connected) + ")");
							tvMouseResultText.setTextColor(Color.BLUE);
							isMouseConnected = true;
						}
					} else {
						if (isMouseConnected) {
							tvMouseResultText.setText("PASS(" + getString(R.string.is_unconnected) + ")");
							tvMouseResultText.setTextColor(Color.BLUE);
						}
					}
					File mousefile1 = new File("/dev/input/mouse1");
					if (mousefile1.exists()) {
						String mouse1 =  runCommandLine("cat /sys/class/input/mouse1/device/name");
						Log.v(TAG,"mouse:"+mouse1);
						if (mouse1.contains("Mouse") || mouse1.contains("mouse")){
							tvMouseResultText.setText("PASS(" + getString(R.string.is_connected) + ")");
							tvMouseResultText.setTextColor(Color.BLUE);
							isMouseConnected = true;
						}
					} else {
						if (isMouseConnected) {
							tvMouseResultText.setText("PASS");
						}
					}

					// check usb2.0
					//old check version
					File usbFile1 = new File("/sys/devices/platform/aotg_hcd.0/usb1/1-1/1-1.1");
					File usbFile2 = new File("/sys/devices/platform/aotg_hcd.0/usb1/1-1/1-1.2");
					if (usb1 && usb2) {
						tvUsb2ResultText.setText("usb1:ok usb2:ok");
						tvUsb2ResultText.setTextColor(Color.BLUE);
					} else {
						if (usbFile1.exists()) {
							usb1 = true;
							if (!usb2) {
								tvUsb2ResultText.setText("usb1:ok usb2:wait");
							}
						}
						if (usbFile2.exists()) {
							usb2 = true;
							if (!usb1) {
								tvUsb2ResultText.setText("usb1:wait usb2:ok");
							}
						}
					}

 					// new check version
					String content = runCommandLine("ls /sys/devices/e01d0000.usb");
					String usb = null;
					//Log.e(TAG,"content:"+content);
					String names[] = content.split("\n");
					for (int i= 0; i<names.length;i++){
						if (names[i].contains("usb")){
							usb = names[i];
							break;
						}
					}
					if (usb == null) return;
					int number = Integer.valueOf(usb.substring(usb.length() - 1, usb.length()));
					Log.e(TAG,"content:"+usb);
					File usbFile3 = null;
					if (!usb1){
						usbFile3 = new File("/sys/devices/e01d0000.usb/"+usb+"/"+number+"-1/"+number+"-1:1.0");
					}
					File usbFile4 = null;
					if (!usb2){
						usbFile4 = new File("/sys/devices/e01d0000.usb/"+usb+"/"+number+"-1/"+number+"-1:2.0");
					}
					if (usb1 && usb2) {
						tvUsb2ResultText.setText("usb1:ok usb2:ok");
						tvUsb2ResultText.setTextColor(Color.BLUE);
					} else {
						if (usbFile3 != null && usbFile3.exists()) {
							usb1 = true;
							if (!usb2) {
								tvUsb2ResultText.setText("usb1:ok usb2:wait");
							}
						}
						if (usbFile4 != null && usbFile4.exists()) {
							usb2 = true;
							if (!usb1) {
								tvUsb2ResultText.setText("usb1:wait usb2:ok");
							}
						}
					}
				}

				break;
			case MSG_FLAG_START_RECORDING:
				mVisualizerFx.setVisibility(View.GONE);
				mVUMeter.setVisibility(View.VISIBLE);
				tvRecorderPlayerStatus.setText(getString(R.string.voice_is_recording));
				tvVoiceResultText.setText(getString(R.string.voice_is_recording));
				break;
			case MSG_FLAG_START_REPLAYING:
				mVisualizerFx.setVisibility(View.VISIBLE);
				mVUMeter.setVisibility(View.GONE);
				tvRecorderPlayerStatus.setText(getString(R.string.voice_is_replaying));
				tvVoiceResultText.setText("PASS(" + getString(R.string.voice_is_replaying) + ")");
				tvVoiceResultText.setTextColor(Color.BLUE);
				break;
			case MSG_FLAG_WIFI_UPDATE:
				if (!msg_info.contains("PASS")) {
					tvWifiResultText.setText(msg_info);
					tvWifiResultText.setTextColor(result_color);
				} else {
					tvWifiResultText.setTextColor(result_color);
					SpannableStringBuilder style = new SpannableStringBuilder(msg_info);
					style.setSpan(new ForegroundColorSpan(Color.GREEN), 12, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					tvWifiResultText.setText(style);
				}
				break;
			case MSG_FLAG_BLUETOOTH_UPDATE:
				tvBluetoothResultText.setText(msg_info);
				tvBluetoothResultText.setTextColor(result_color);
				break;
			case MSG_FLAG_GSENSOR_UPDATE:
				tvGsensorResultText.setText(msg_info);
				tvGsensorResultText.setTextColor(result_color);
				break;
			case MSG_FLAG_CAMERA_UPDATE:
				tvCameraResultText.setText(msg_info);
				tvCameraResultText.setTextColor(result_color);
				break;
			case MSG_FLAG_CARD_UPDATE:
				tvCardResultText.setText(msg_info);
				tvCardResultText.setTextColor(result_color);
				break;
			case MSG_FLAG_UHOST_UPDATE:
				tvUhostResultText.setText(msg_info);
				tvUhostResultText.setTextColor(result_color);
				break;

                case MSG_FLAG_UHOST2_UPDATE:
                    tvUhost2ResultText.setText(msg_info);
                    tvUhost2ResultText.setTextColor(result_color);
                    break;

			case MSG_FLAG_MOUSE_UPDATE:
				tvMouseResultText.setText(msg_info);
				tvMouseResultText.setTextColor(result_color);
				break;
			case MSG_FLAG_USB_UPDATE:
				tvUsbResultText.setText(msg_info);
				tvUsbResultText.setTextColor(result_color);
				break;
			case MSG_FLAG_HDMI_UPDATE:
				tvHdmiResultText.setText(msg_info);
				tvHdmiResultText.setTextColor(result_color);
				break;
			case MSG_FLAG_HEADSET_UPDATE:
				tvHeadsetResultText.setText(msg_info);
				tvHeadsetResultText.setTextColor(result_color);
				break;
			case MSG_FLAG_KEY_UPDATE:
				tvKeyResultText.setText(msg_info);
				tvKeyResultText.setTextColor(result_color);
				break;
			case MSG_FLAG_TOUCH_UPDATE:
				tvTouchResultText.setText(msg_info);
				tvTouchResultText.setTextColor(result_color);
				break;
			case MSG_FLAG_RTC_UPDATE:
				tvRTCResultText.setText(msg_info);
				tvRTCResultText.setTextColor(result_color);
				break;
			case MSG_FLAG_SIM_UPDATE:
				tvSIMResultText.setText(msg_info);
				tvSIMResultText.setTextColor(result_color);
				break;
			case MSG_FLAG_INFRARED_UPDATE:
				tvInfraredResultText.setText(msg_info);
				tvInfraredResultText.setTextColor(result_color);
				break;
			case MSG_FLAG_ETHERNET_UPDATE:
				tvEthernetResultText.setText(msg_info);
				tvEthernetResultText.setTextColor(result_color);
				break;
			default:
				break;
			}
			super.handleMessage(msg);

		}
	}

	private final BroadcastReceiver NoisyReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String intentAction = intent.getAction();
			if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intentAction)) {
				mVisualizerFx.fadeIn();
			}
		}
	};

	private final BroadcastReceiver BootupCompleteReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String intentAction = intent.getAction();
			if (ACTION_BOOTUP_COMPLETE.equals(intentAction)) {
				isBootupComplete = true;

				mVisualizerFx.start();
				tvRecorderPlayerStatus.setText(getString(R.string.playing_music));
				vibrator.vibrate(new long[] { 1000, 10, 100, 1000 }, -1);
				Toast.makeText(PcbaTestActivity.this, getString(R.string.vibration_confirm), Toast.LENGTH_SHORT).show();
			}
		}
	};
	private final BroadcastReceiver shundownReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String intentAction = intent.getAction();
			if (intentAction.equals(intent.ACTION_SHUTDOWN)) {
				Log.e(TAG, "dcx  -SHUTDOWN ----");
				String path = PcbaTestActivity.this.getApplicationContext().getFilesDir().getAbsolutePath();

				/*File file = new File(path + "/vendorsupportserve");
				if (file.exists()) {
					file.delete();
				}
				file = new File(path + "/libvendorsupportservice.so");
				if (file.exists()) {
					file.delete();
				}

				try {
					DataOutputStream localDataOutputStream = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
					localDataOutputStream.writeBytes("rm -r  /system/lib/libvendorsupportservice.so \n");
					localDataOutputStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				//finish();
			}
		}

	};

	private void EnablePointerLocationOptions(boolean enable) {
		Settings.System.putInt(getContentResolver(), Settings.System.POINTER_LOCATION, enable ? 1 : 0);
	}

	private void EnableTouchPointerOptions(boolean enable) {
		Settings.System.putInt(getContentResolver(), Settings.System.SHOW_TOUCHES, enable ? 1 : 0);
	}

	private void compareRTCTime() {
		String ext = runCommandLine("cat /sys/class/rtc/rtc0/device/rtc/rtc0/rtc0ext_osc");
		ext = ext.replace("\n", "");
		if (ext != "" && Integer.parseInt(ext) == 0) {
			sendUpdateMessage(MSG_FLAG_RTC_UPDATE, "FAIL");
			return;
		}
		String rtcdate = runCommandLine("cat /sys/class/rtc/rtc0/device/rtc/rtc0/date");
		String rtctime = runCommandLine("cat /sys/class/rtc/rtc0/device/rtc/rtc0/time");
		Log.e(TAG, "rtctime" + rtctime);
		Log.e(TAG, "rtcdate" + rtcdate);
		if (rtcdate != "") {
			rtcdate = rtcdate.replace("\n", "");
		} else {
			rtcdate = "unknow";
		}
		String[] times = null;
		if (rtctime != "") {
			times = rtctime.split("\\:");
		}

		int rtchour = Integer.parseInt(times[0]);
		int rtcmin = Integer.parseInt(times[1]);
        int rtcsec = Integer.parseInt(times[2].trim());
        int realRtchour = rtchour + 8;

		String[] rtcdates = rtcdate.split("\\-");
		int rtcyear = Integer.parseInt(rtcdates[0]);
		int rtcmonth = Integer.parseInt(rtcdates[1]);
		int rtcday = Integer.parseInt(rtcdates[2]);
		// SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// String date = sDateFormat.format(new java.util.Date());
		// Log.e(TAG, "date"+date);

		// Calendar c = Calendar.getInstance();
		// int hour = c.get(Calendar.HOUR_OF_DAY);
		// int minute = c.get(Calendar.MINUTE);
		// rtchour = rtchour + 8;
		// if (rtchour >= 24){
		// rtchour = rtchour - 24;
		// }
		Time t = new Time("GMT+8");
		t.setToNow();
		int hour = t.hour;
		int minute = t.minute;
        int sec = t.second;
		Log.e(TAG, "h:" + hour + ":" + minute);
		int year = t.year;
		int month = t.month + 1;
		int day = t.monthDay;

        int miniDif = rtcmin - minute;
        int secDif = rtcsec - sec;
        int hourDif = rtchour - hour;

		if (month < 10) {

		}
		if ((rtchour == hour) && (rtcmin == minute) && (Math.abs(rtcsec - sec) < 5) && (year == rtcyear) && (month == rtcmonth) && (day == rtcday)) {
			sendUpdateMessage(MSG_FLAG_RTC_UPDATE, "PASS (" + rtcdate + " "+realRtchour+":"+rtcmin+":"+rtcsec+")");
		} else {
			sendUpdateMessage(MSG_FLAG_RTC_UPDATE, "FAIL (RTC time-system time = " + hourDif + "h" + miniDif + "min" + " "+secDif + "s");
		}
	}
	private void checkEthernetState(){
		ContentResolver cr = mContext.getContentResolver();
		try {
			isEthernetOpened = Settings.Global.getInt(cr, "ethernet_on");   //保存在Global数据库中
			Log.e(TAG,"isEthernetOpened :"+isEthernetOpened);
			changeEthernetState(isEthernetOpened);
		} catch (Settings.SettingNotFoundException e) {
		}
	}
	private void regainEthernetState(int state) {
		if (state == 0) {     //0是关闭，1是打开
			try {
				IBinder b = ServiceManager.getService(mContext.NETWORKMANAGEMENT_SERVICE);
				INetworkManagementService NMS = INetworkManagementService.Stub.asInterface(b);
				NMS.setInterfaceDown("eth0"); // 恢复
			} catch (Exception e) {
				Log.e(TAG, "EthernetBootReceiver setInterfaceDown/Up failed!");
			}
		}
	}
	private void changeEthernetState(int state){
		if (state == 0) {     //0是关闭，1是打开
			try {
				IBinder b = ServiceManager.getService(mContext.NETWORKMANAGEMENT_SERVICE);
				INetworkManagementService NMS = INetworkManagementService.Stub.asInterface(b);
				NMS.setInterfaceUp("eth0"); //打开
			} catch (Exception e) {
				Log.e(TAG, "EthernetBootReceiver setInterfaceDown/Up failed!");
			}
		}
	}

	private BroadcastReceiver mEthernetPlugReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// maybe isn't called
			Log.e(TAG, "mEthernetPlugReceiver : " + intent.getAction());
			if (intent.getAction().equals(EthernetManager.ETHERNET_STATE_CHANGED_ACTION)) {
				Log.e(TAG, "got the broadcast message of eth0 plug in/out.");
				int state = intent.getIntExtra(EthernetManager.EXTRA_ETHERNET_STATE, -1);
				Log.e(TAG, "state:" + state);
				if (state == EthernetManager.ETHERNET_STATE_ENABLED) {
					tvEthernetResultText.setText("PASS(" + getString(R.string.is_connected) + ")");
					tvEthernetResultText.setTextColor(Color.BLUE);
				} else if (state == EthernetManager.ETHERNET_STATE_DISABLED) {
				}
			}
		}
	};

    private BroadcastReceiver BatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("BatteryReceiver onReceive:","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

                int status = intent.getIntExtra("status",-1);
                int current = intent.getIntExtra("level",-1);
                int total = intent.getIntExtra("scale",-1);

                Log.i("status:","sssssssssssssssssssss: "+status);
                Log.i("current:","cucucucucu: "+current);
                Log.i("total:","tttttttttttttttt: "+total);

                int percent = current*100/total;


                if(current == -1 || total == -1){
                    Log.i("BatteryReceiver onReceive:","dddddddddddddddddddddddddddddddddd");
                    tvBatteryResultText.setText("no battery");
                    tvBatteryResultText.setTextColor(Color.GRAY);
                }else {
                    Log.i("BatteryReceiver onReceive:","eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
                    tvBatteryResultText.setText("current capacity: " + percent + "%");
                    tvBatteryResultText.setTextColor(Color.BLUE);
                }

            if(BatteryManager.BATTERY_STATUS_UNKNOWN == status){
                Log.i("BatteryReceiver onReceive:","ccccccccccccccccccccccccccccccccccc");
                tvBatteryResultText.setText("no battery");
                tvBatteryResultText.setTextColor(Color.GRAY);
            }

        }
    };

}
