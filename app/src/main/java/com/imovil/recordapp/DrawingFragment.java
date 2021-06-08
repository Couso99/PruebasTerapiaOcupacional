package com.imovil.recordapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

public class DrawingFragment extends Fragment {
    Activity activity;

    private Test test;
    private TrialInfo trialInfo;
    private DrawingArea drawingArea;
    private Button finishedButton;
    private TextView commentTextView;

    private static String fileName = null, outputFilename;

    public DrawingFragment() {
        // Required empty public constructor
    }

    public static DrawingFragment newInstance(Test test, TrialInfo trialInfo) {
        DrawingFragment fragment = new DrawingFragment();
        Bundle args = new Bundle();
        args.putSerializable(TestActivity.ARG_TEST, test);
        args.putSerializable(TestActivity.ARG_TRIAL_INFO, trialInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            test = (Test) getArguments().getSerializable(TestActivity.ARG_TEST);
            trialInfo = (TrialInfo) getArguments().getSerializable(TestActivity.ARG_TRIAL_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drawing, container, false);

        activity = getActivity();

        //init_headers();

        //commentTextView = view.findViewById(R.id.commentTextView);
        drawingArea = (DrawingArea) view.findViewById(R.id.drawing_area);
        drawingArea.initTrailDrawer();
        //drawingArea.setImageURI(Uri.fromFile(new File(getContext().getExternalCacheDir()+ File.separator+"fondo_app2.jpg")));

        if (test.getParametersNumber() != 0) {
            String fname = test.getParameters().get(0);
                File file = new File(((TrialInterface)activity).getFilePath(fname));
                if (file.exists()) {
                    drawingArea.setImageURI(Uri.fromFile(file));
                    drawingArea.setAdjustViewBounds(true);
                } else {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    sleep(200);
                                    if (file.exists()) {
                                        activity.runOnUiThread(() ->
                                        {
                                            drawingArea.setImageURI(Uri.fromFile(file));
                                            drawingArea.setAdjustViewBounds(true);
                                        });
                                        break;
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    thread.start();
                }
            }

        //String title;
        //if ((title = test.getTitle()) != null)
            //commentTextView.setText(title);

        outputFilename = test.getName() +'_'+trialInfo.getUserID()+"_"+trialInfo.getStartTime()+ "_draw.jpeg";
        fileName = ((TrialInterface) activity).getFilePath(outputFilename);
        //.getExternalCacheDir().getAbsolutePath();

        finishedButton = view.findViewById(R.id.finishedButton);
        finishedButton.setOnClickListener(v -> {
            Bitmap image = screenShot(drawingArea);
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream(new File(fileName));
                image.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((TrialInterface) activity).uploadFile(fileName, "image/*");
            test.setOutputFilename(outputFilename);
            ((TrialInterface) activity).nextTest();
        });

        return view;
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


}