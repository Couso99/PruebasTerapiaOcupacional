package com.imovil.NEURmetrics.ui.trialActivity.fragments;

import android.app.Activity;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.imovil.NEURmetrics.R;
import com.imovil.NEURmetrics.models.Trial;
import com.imovil.NEURmetrics.models.TrialInfo;
import com.imovil.NEURmetrics.ui.trialActivity.views.recyclerviews.TestsListAdapter;
import com.imovil.NEURmetrics.viewmodels.TrialViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {
    private static final String ARG_TESTS = "tests";

    Activity activity;

    TrialViewModel model;

    TestsListAdapter testsListAdapter;

    private RecyclerView recyclerView;
    private Button nextButton;
    private TextView trialView;

    private Trial trial;

    public ResultsFragment() {
        // Required empty public constructor
    }

    public static ResultsFragment newInstance(Trial trial) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TESTS, trial);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(TrialViewModel.class);


        if (getArguments() != null) {
            model.setTrial((Trial) getArguments().getSerializable(ARG_TESTS));
        }

        trial = model.getTrial();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        activity = getActivity();

        testsListAdapter = new TestsListAdapter();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(testsListAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        testsListAdapter.setTests(trial);

        nextButton = view.findViewById(R.id.finishedButton);

        nextButton.setOnClickListener(v -> {
            model.postTrial();
        });

        model.getIsDataUploaded().observe(requireActivity(), isDataUploaded -> {
            if (isDataUploaded) {
                model.setIsDataUploaded(false);
                activity.finish();
            }
        });

        TrialInfo trialInfo = trial.getTrialInfo();

        trialView = view.findViewById(R.id.trialScoreTextView);
        trialView.setText("Puntuación total:\t"+ trialInfo.getTotalScore() + " /"+trialInfo.getTotalMaxScore());

        return view;
    }
}