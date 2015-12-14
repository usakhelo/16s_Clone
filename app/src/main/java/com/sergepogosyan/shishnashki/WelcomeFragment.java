package com.sergepogosyan.shishnashki;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class WelcomeFragment extends DialogFragment {
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  private String mParam1;
  private String mParam2;

  private OnWelcomeListener mListener;
  private static final String TAG = "shishnashki welcome";

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment WelcomeFragment.
   */
  public static WelcomeFragment newInstance(String param1, String param2) {
    WelcomeFragment fragment = new WelcomeFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }
  public WelcomeFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @Override
  @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View content = inflater.inflate(R.layout.fragment_welcome, null);
    Button startButton = (Button)content.findViewById(R.id.start_button);
    final DialogFragment thisDialog = this;
    startButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//        thisDialog.dismiss();
        mListener.onStartGame("");
      }
    });
    builder.setView(content);
    Dialog dialog = builder.create();
    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    dialog.setCanceledOnTouchOutside(false);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    return dialog;
  }

  @Override
  public void onCancel(DialogInterface dialogInterface) {
    mListener.onExitGame();
  }

  @Override
  public void onAttach(Activity context) {
    super.onAttach(context);
    if (context instanceof OnWelcomeListener) {
      mListener = (OnWelcomeListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnWelcomeListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public interface OnWelcomeListener {
    void onStartGame(String str);
    void onExitGame();
  }
}
