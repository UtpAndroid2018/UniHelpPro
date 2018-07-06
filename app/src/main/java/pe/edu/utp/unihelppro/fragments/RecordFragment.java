package pe.edu.utp.unihelppro.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.recording.ScreenUtils;
import pe.edu.utp.unihelppro.recording.VoiceView;

public class RecordFragment extends DialogFragment implements VoiceView.OnRecordListener {
    private static final String IMAGE_DIRECTORY = "/UniHelpPro";
    private static final String IMAGE_DIRECTORY_NAME = "UniHelpPro";
    private VoiceView mVoiceView;
    private MediaRecorder mMediaRecorder;
    private Handler mHandler;
    private boolean mIsRecording = false;
    private static final String TAG = "RecordFragment";
    private String mCurrentAudioPath;

    public static interface OnCaptureAudio{
        public abstract void OnCaptureAudio(String path );
    }
    private OnCaptureAudio mListener;

    public RecordFragment() {
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    public static RecordFragment newInstance() {
        RecordFragment f = new RecordFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    public static RecordFragment newInstance(boolean small) {
        RecordFragment f = new RecordFragment();
        Bundle args = new Bundle();
        args.putBoolean("small", small);
        f.setArguments(args);
        return f;
    }


    public void setListener( OnCaptureAudio listener) {
        mListener = listener;
    }

    private void sendResult() {
        mListener.OnCaptureAudio( mCurrentAudioPath );
        dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view = inflater.inflate(R.layout.fragment_record, container, false);
        mVoiceView = (VoiceView) view.findViewById(R.id.voiceview);
        mVoiceView.setOnRecordListener(this);

        mHandler = new Handler(Looper.getMainLooper());

        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = 0.0f;
        float y = 0.0f;
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_UP,
                x,
                y,
                metaState
        );
        mVoiceView.dispatchTouchEvent(motionEvent);

        return view;
    }


    @Override
    public void onRecordStart() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String audioFileName = "AUDIO_" + timeStamp;

            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            File wallpaperDirectory = new File( Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".mp3");
            f.createNewFile();
            mCurrentAudioPath = f.getAbsolutePath();
            mMediaRecorder.setOutputFile( mCurrentAudioPath );
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mIsRecording = true;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    float radius = (float) Math.log10(Math.max(1, mMediaRecorder.getMaxAmplitude() - 500)) * ScreenUtils.dp2px( getActivity(), 20);
                    mVoiceView.animateRadius(radius);
                    if (mIsRecording) {
                        mHandler.postDelayed(this, 50);
                    }
                }
            });
        } catch (IOException e) {
            Toast.makeText(getActivity(), "MediaRecorder prepare failed!", Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(TAG, "onCancel");
        if(mIsRecording){
            mMediaRecorder.stop();
            mIsRecording = false;
        }
        //mMediaRecorder.release();

        super.onCancel(dialog);
    }

    @Override
    public void onRecordFinish() {
        Log.d(TAG, "onRecordFinish");
        mIsRecording = false;
        mMediaRecorder.stop();
        sendResult();
        dismiss();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if(mIsRecording){
            mMediaRecorder.stop();
            mIsRecording = false;
        }
        mMediaRecorder.release();
        super.onDestroy();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if( this.mListener == null ) {
            try {
                this.mListener = (OnCaptureAudio) context;
            }
            catch (final ClassCastException e) {
                //throw new ClassCastException(context.toString() + " must implement OnCaptureAudio");
            }
        }
    }
}
