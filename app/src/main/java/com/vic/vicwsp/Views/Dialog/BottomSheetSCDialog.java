package com.vic.vicwsp.Views.Dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.Complaints.ComplaintsListActivity;
import com.vic.vicwsp.Views.Activities.CreditNotes.CreditNotes;
import com.vic.vicwsp.Views.Activities.Support.SupportListActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetSCDialog extends BottomSheetDialogFragment {

    private ConstraintLayout supportConstraint, complaintConstraint, cancelConstraint, creditConstraint;
    private View v;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bottom_sheet_complaint_support, container, false);

        initViews();

        return v;
    }

    private void initViews() {
        supportConstraint = v.findViewById(R.id.supportConstraint);
        supportConstraint.setOnClickListener(v -> {
            dismiss();
            startActivity(new Intent(getContext(), SupportListActivity.class));
        });

        complaintConstraint = v.findViewById(R.id.complaintConstraint);
        complaintConstraint.setOnClickListener(v -> {
            dismiss();
            startActivity(new Intent(getContext(), ComplaintsListActivity.class));
        });

        creditConstraint = v.findViewById(R.id.creditConstraint);
        if (SharedPreference.getSimpleString(getContext(), Constants.isMerchant).equals("1"))
            creditConstraint.setVisibility(View.VISIBLE);
        else
            creditConstraint.setVisibility(View.GONE);

        creditConstraint.setOnClickListener(v -> {
            dismiss();
            startActivity(new Intent(getContext(), CreditNotes.class));
        });

        cancelConstraint = v.findViewById(R.id.cancelConstraint);
        cancelConstraint.setOnClickListener(v -> {
            dismiss();
        });


    }

}
