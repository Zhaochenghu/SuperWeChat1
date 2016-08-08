package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.BoutiqueFragment;
import cn.ucai.fulicenter.DemoHXSDKHelper;
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
    CategoryFragment mCategoryFragment;
    PersonalCenterFragment  mPersonalCenterFragment;
    private Fragment[] fragments;

    private int currentTabIndex;
    public static final int ACTION_LOGIN = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulicenter_main);
        initView();
        initFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mNewGoodFragment)
                .add(R.id.fragment_container,mBoutiqueFragment)
                .add(R.id.fragment_container,mCategoryFragment)
                .add(R.id.fragment_container,mPersonalCenterFragment)
                .hide(mBoutiqueFragment).hide(mCategoryFragment)
                .hide(mPersonalCenterFragment)
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
        mCategoryFragment = new CategoryFragment();
        mPersonalCenterFragment = new PersonalCenterFragment();
        fragments = new Fragment[5];
        fragments[0] = mNewGoodFragment;
        fragments[1] = mBoutiqueFragment;
        fragments[2] = mCategoryFragment;
        fragments[4] = mPersonalCenterFragment;
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
                if (DemoHXSDKHelper.getInstance().isLogined()) {

                    index = 4;
                } else {
                    gotoLogin();
                }
                break;
        }
        Log.e(TAG, "index=" + index + ",currentIndex=" + currentIndex);
        setFragment();
    }

    private void setFragment() {
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

    private void gotoLogin() {
        startActivityForResult(new Intent(this,LoginActivity.class),ACTION_LOGIN);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_LOGIN) {
            if (DemoHXSDKHelper.getInstance().isLogined()) {

            } else {
                setRadioButtonStatus(currentIndex);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DemoHXSDKHelper.getInstance().isLogined()) {

        } else {
            index = currentIndex;
            if (index == 4) {
                index = 0;
            }
            setFragment();
        }
    }
}
