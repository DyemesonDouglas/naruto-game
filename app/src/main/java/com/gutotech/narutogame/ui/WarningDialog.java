package com.gutotech.narutogame.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

import com.gutotech.narutogame.R;

public class WarningDialog extends DialogFragment {
    private String mWarning = "";

    private View.OnClickListener mOnClickListener;

    public WarningDialog() {
    }

    public WarningDialog(String warning) {
        mWarning = warning;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View root = requireActivity().getLayoutInflater().inflate(R.layout.dialog_warning, null);
        builder.setView(root);

        TextView textView = root.findViewById(R.id.warningTextView);
        textView.setText(mWarning);

        Button closeButton = root.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(v);
            }

            dismiss();
        });

        builder.setCancelable(false);

        return builder.create();
    }

    public void setOnCloseListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }
}
