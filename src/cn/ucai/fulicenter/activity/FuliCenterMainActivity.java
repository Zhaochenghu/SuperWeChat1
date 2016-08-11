package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.Utils;

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
    CartFragment mCartFragment;
    private Fragment[] fragments;

    private int currentTabIndex;
    public static final int ACTION_LOGIN_PERSONAL = 100;
    public static final int ACTION_LOGIN_CART = 200;
    updateCartNumReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulicenter_main);
        initView();
        initFragment();
        setListener();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mNewGoodFragment)
                .add(R.id.fragment_container,mBoutiqueFragment)
                .add(R.id.fragment_container,mCategoryFragment)
                .add(R.id.fragment_container,mPersonalCenterFragment)
                .add(R.id.fragment_container,mCartFragment)
                .hide(mBoutiqueFragment).hide(mCategoryFragment)
                .hide(mPersonalCenterFragment).hide(mCartFragment)
                .show(mNewGoodFragment)
                .commit();
    }

    private void setListener() {
        setUpdateCartCountListener();
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
        mCartFragment = new CartFragment();
        fragments = new Fragment[5];
        fragments[0] = mNewGoodFragment;
        fragments[1] = mBoutiqueFragment;
        fragments[2] = mCategoryFragment;
        fragments[3] = mCartFragment;
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
                if (DemoHXSDKHelper.getInstance().isLogined()) {
                    index = 3;
                } else {
                    gotoLogin(ACTION_LOGIN_CART);
                }
                break;
            case R.id.layout_personal:
                if (DemoHXSDKHelper.getInstance().isLogined()) {
                    index = 4;
                } else {
                    gotoLogin(ACTION_LOGIN_PERSONAL);
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

    private void gotoLogin(int action) {
        startActivityForResult(new Intent(this,LoginActivity.class),action);
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
        Log.e(TAG, "onActivityResult");
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            if (requestCode == ACTION_LOGIN_PERSONAL) {
                index = 4;
            }
            if (requestCode == ACTION_LOGIN_CART) {
                index = 3;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        if (!DemoHXSDKHelper.getInstance().isLogined() && index == 4) {
            index = 0;
        }
        setFragment();
        setRadioButtonStatus(currentIndex);
        updateCartNum();
    }

    class updateCartNumReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateCartNum();
        }

    }
    private void setUpdateCartCountListener(){
        mReceiver = new updateCartNumReceiver();
        IntentFilter filter = new IntentFilter("update_cart_list");
        filter.addAction("update_user");
        registerReceiver(mReceiver, filter);
    }
    private void updateCartNum() {
        int count = Utils.sumCartCount();
        Log.e(TAG, "count=" + count);
        if (!DemoHXSDKHelper.getInstance().isLogined() || count == 0) {
            tvCartHint.setText(String.valueOf(0));
            tvCartHint.setVisibility(View.GONE);
        } else {
            tvCartHint.setText(String.valueOf(count));
            tvCartHint.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

}
