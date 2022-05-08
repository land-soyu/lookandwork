package com.lookandwork.together_v.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lookandwork.together_v.MainActivity;
import com.lookandwork.together_v.R;

public class MainFragment extends Fragment implements View.OnClickListener {
    MainActivity mainActivity;

    TextView text;
    String loginId;
    Button chatjoinbt;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
        Log.e("!!!", "MainFragment onAttach");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("!!!", "MainFragment onCreateView");
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        text = (TextView) view.findViewById(R.id.text);
        text.setText(loginId);
        chatjoinbt = (Button) view.findViewById(R.id.chatjoinbt);
        chatjoinbt.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatjoinbt:
                movePage(new ChatFragment());
                break;
        }
    }


    private void movePage(Fragment f) {
        mainActivity.movePage(f, mainActivity.DEPTH_ATTACH, null);
    }

    public void setId(String s) {
        loginId = s;
    }
}
