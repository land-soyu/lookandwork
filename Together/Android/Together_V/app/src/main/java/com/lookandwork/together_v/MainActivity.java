package com.lookandwork.together_v;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.lookandwork.together_v.fragment.MainFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context context;

    public static final int DEPTH_ATTACH = -1;
    public static final int DEPTH_LASTCHANGE = -2;
    public static final int DEPTH_CLEAR = -3;

    private final List<Fragment> mCurrentFragmentDepth = new ArrayList<>();

    MainFragment mainFragment;
    Button bt_main_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        mainFragment = new MainFragment();
        if (savedInstanceState == null)
            savedInstanceState = new Bundle();

        savedInstanceState.putInt("DEPTH", 0);
        mainFragment.setArguments(savedInstanceState);
        mainFragment.setId(getIntent().getStringExtra("id"));

        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_root, mainFragment, "DEPTH-" + 0)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Fragment next_Fragment;
    int next_Depth;
    Bundle next_Argumnets;
    public void movePage(Fragment f, int depth, Bundle arguments) {
        Log.e(TAG, "MainActivity movePage ");

        next_Fragment = f;
        next_Depth = depth;
        next_Argumnets = arguments;

        movePageNext();
    }

    @UiThread
    public void movePageNext() {
        Log.e(TAG, "MainActivity movePageNext ");
        Log.e(TAG, "f = "+next_Fragment.getClass().getName());
        for (Fragment fragment : mCurrentFragmentDepth) {
            if ( next_Fragment.getClass().getName().equals(fragment.getClass().getName()) ) {
                return;
            }
        }
        if (next_Argumnets == null)
            next_Argumnets = new Bundle();

        if (next_Depth == DEPTH_CLEAR) {
            next_Depth = 0;
        } else //noinspection deprecation
            if (next_Depth == DEPTH_ATTACH) {
                next_Depth = mCurrentFragmentDepth.size();
            }

        next_Argumnets.putInt("DEPTH", next_Depth);
        next_Fragment.setArguments(next_Argumnets);

        mCurrentFragmentDepth.add(next_Fragment);
        //  화면 전환시 키보드 아래로 내리는 기능
        View view = getCurrentFocus();
        if ( view != null ) {
            InputMethodManager imm = (InputMethodManager)getSystemService((Context.INPUT_METHOD_SERVICE));
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            // 키보드 아래로 내리고 그 이후에 화면 전환
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            try {
                                transaction
                                        .setCustomAnimations(R.animator.show_from_right, R.animator.hide_to_right)
                                        .add(R.id.layout_root, mCurrentFragmentDepth.get(mCurrentFragmentDepth.size()-1), "DEPTH-" + (mCurrentFragmentDepth.size()-1))
                                        .commitAllowingStateLoss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 100);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if ( mCurrentFragmentDepth.get(mCurrentFragmentDepth.size()-1).getClass().toString().contains("Finish") ) {
                                bt_main_back.setVisibility(View.GONE);
                            }
                        }
                    }, 550);


                }
            });
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                transaction
                        .setCustomAnimations(R.animator.show_from_right, R.animator.hide_to_right)
                        .add(R.id.layout_root, mCurrentFragmentDepth.get(mCurrentFragmentDepth.size()-1), "DEPTH-" + (mCurrentFragmentDepth.size()-1))
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }


            if ( mCurrentFragmentDepth.get(mCurrentFragmentDepth.size()-1).getClass().toString().contains("Finish") ) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bt_main_back.setVisibility(View.GONE);
                    }
                });
            }
        }
    }


    int backCurrentDepth = 0;
    private boolean backAction() {
        backCurrentDepth = mCurrentFragmentDepth.size()-1;
        if (backCurrentDepth >= 0) {
            removePageDepthOver(backCurrentDepth);
            return true;
        }
        return false;
    }
    @UiThread
    private void removePageDepthOver(int depth) {
        Log.e("!!!", "removePageDepthOver depth = "+depth+", mCurrentFragmentDepth.size() = "+mCurrentFragmentDepth.size());
        if (depth >= mCurrentFragmentDepth.size()) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.show_from_right, R.animator.hide_to_right);

        Fragment fragment = mCurrentFragmentDepth.get(mCurrentFragmentDepth.size()-1);
        while (depth < mCurrentFragmentDepth.size()) {
            fragment = mCurrentFragmentDepth.remove(mCurrentFragmentDepth.size()-1);
            transaction.remove(fragment);
        }

        try {
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCurrentFragmentDepth.size() > 0) {
            fragment = mCurrentFragmentDepth.get(mCurrentFragmentDepth.size()-1);
            fragment.onResume();
        }
    }

    @Override
    public void onBackPressed() {
        if (!backAction()) {
            super.onBackPressed();
        }
    }
}
