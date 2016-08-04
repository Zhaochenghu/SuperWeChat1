package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.BoutiqueFragment;
import cn.ucai.fulicenter.R;

/**
 * Created by Administrator on 2016/8/1.
 */
public class FuliCenterMainActivity extends BaseActivity{
    private final static String TAG = FuliCenterMainActivity.class.getSimpleName();
    RadioButton rbNewGood;
    RadioButton rbBoutique;
    RadioButton rbCategory;
    RadioButton rbCart;
    RadioButton rbPersonal;
    TextView tvCartHint;
    RadioButton[] mrbTabs;

    int index;
    int currentIndex;

    NewGoodFragment mNewGoodFragment;
    BoutiqueFragment mBoutiqueFragment;
    private Fragment[] fragments;

    private int currentTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulicenter_main);
        initView();
        initFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mNewGoodFragment)
                .add(R.id.fragment_container,mBoutiqueFragment)
                .hide(mBoutiqueFragment)
                .show(mNewGoodFragment)
                .commit();
    }

    private void initView() {
        rbNewGood = (RadioButton) findViewById(R.id.layout_new_good);
        rbBoutique = (RadioButton) findViewById(R.id.layout_boutique);
        rbCategory = (RadioButton) findViewById(R.id.layout_category);
        rbCart = (RadioButton) findViewById(R.id.layout_cart);
        rbPersonal = (RadioButton) findViewById(R.id.layout_personal);
        tvCartHint = (TextView) findViewById(R.id.tvCartHint);
        mrbTabs = new RadioButton[5];
        mrbTabs[0] = rbNewGood;
        mrbTabs[1] = rbBoutique;
        mrbTabs[2] = rbCategory;
        mrbTabs[3] = rbCart;
        mrbTabs[4] = rbPersonal;
    }

    private void initFragment() {
        mNewGoodFragment = new NewGoodFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        fragments = new Fragment[5];
        fragments[0] = mNewGoodFragment;
        fragments[1] = mBoutiqueFragment;
    }

    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.layout_new_good:
                index = 0;
                break;
            case R.id.layout_boutique:
                index = 1;
                break;
            case R.id.layout_category:
                index = 2;
                break;
            case R.id.layout_cart:
                index = 3;
                break;
            case R.id.layout_personal:
                index = 4;
                break;
        }
        Log.e(TAG, "index=" + index + ",currentIndex=" + currentIndex);

        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
            setRadioButtonStatus(index);
            currentTabIndex = index;
        }
    }

    private void setRadioButtonStatus(int index) {
       for (int i=0;i<mrbTabs.length;i++) {
           if (index == i) {
               mrbTabs[i].setChecked(true);
           } else {
               mrbTabs[i].setChecked(false);
           }
       }
    }

}
