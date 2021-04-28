package com.imovil.recordapp;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TapLettersFragment extends Fragment {
    private Activity activity;

    private Test test;
    private List<String> textArray;
    private String fileName, outputFilename;
    private Button finishedButton;
    private List<TextView> tappableTextViews = new ArrayList<>();

    public static TapLettersFragment newInstance() {
        return new TapLettersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tap_letters_fragment, container, false);

        activity = getActivity();

        if (getArguments() != null) {
            test = (Test) getArguments().getSerializable("test");
            textArray = test.getTextArray();
        }

        LinearLayout linearLayoutVertical = view.findViewById(R.id.linearLayoutTapLetters);
        LinearLayout linearLayout = null;
        for (int i=0;i<textArray.size();i++)
        {
            if (i%10==0)
            {
                linearLayout = new LinearLayout(activity);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayoutVertical.addView(linearLayout);
            }
            TextView textView = new TappableTextView(activity, null, textArray.get(i));
            textView.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
            textView.setPadding(32,16,32,16);
            textView.setTextSize(64);
            tappableTextViews.add(textView);
            linearLayout.addView(textView);
        }

        outputFilename = test.getName() + "_screenshot.jpeg";
        fileName = ((TrialInterface) activity).getFilePath(outputFilename);

        finishedButton = view.findViewById(R.id.finishedButton);
        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int errors = calculateErrors("A");
                int score = 0;
                if (errors<2)
                    score = 1;

                Bitmap imagen = screenShot(linearLayoutVertical);
                FileOutputStream fileOutputStream;
                try {
                    fileOutputStream = new FileOutputStream(new File(fileName));
                    imagen.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((TrialInterface) activity).uploadFile(fileName, "image/*");

                test.setScore(score);
                test.setOutputFilename(outputFilename);
                ((TrialInterface) activity).nextTest();
            }
        });

        return view;
    }

    private int calculateErrors (String letter) {
        int errors=0;
        TappableTextView textView;
        for (int i=0; i<tappableTextViews.size(); i++) {
            textView = (TappableTextView) tappableTextViews.get(i);
            if (textView.strcmp(letter)) {
                if (!textView.isChecked()) {
                    textView.mark(TappableTextView.UNCHECKED_ERROR);
                    errors++;
                } else textView.mark(TappableTextView.CHECKED_SUCCESS);
            }
            else if (textView.isChecked()) {
                textView.mark(TappableTextView.CHECKED_ERROR);
                errors++;
            }
        }
        return errors;
    }

    private Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}