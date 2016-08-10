package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;

/**
 * Created by Administrator on 2016/8/3.
 */
public class CartFragment extends Fragment{
    private final String TAG = CartFragment.class.getSimpleName();
    FuliCenterMainActivity mContext;
    List<CartBean> mCartList;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    CartAdapter mCartAdapter;
    UpdateCartReceiver mUpdateCartReceiver;
    int action = I.ACTION_DOWNLOAD;
    TextView tvHint;
    TextView tvSumPrice,tvSavaPrice,tvBuy,tvNothing;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (FuliCenterMainActivity)getContext();
        View layout = View.inflate(mContext, R.layout.fragment_cart, null);
        mCartList = new ArrayList<CartBean>();
        initView(layout);
        setListener();
        initData();
        return layout;
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
        setUpdateCartListener();
    }

    private void setPullUpRefreshListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastItemPosition;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int a = RecyclerView.SCROLL_STATE_DRAGGING;//1
                int b = RecyclerView.SCROLL_STATE_IDLE;//0
                int c = RecyclerView.SCROLL_STATE_SETTLING;//2
                Log.e(TAG, "newState=" + newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastItemPosition == mCartAdapter.getItemCount() - 1) {
                    if (mCartAdapter.isMore()) {
                        action = I.ACTION_PULL_UP;
                        initData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int f = mLinearLayoutManager.findFirstVisibleItemPosition();
                int l = mLinearLayoutManager.findLastVisibleItemPosition();
                Log.e(TAG, "f=" + f + ",l=" + l);
                lastItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                action = I.ACTION_PULL_DOWN;
                mSwipeRefreshLayout.setRefreshing(true);
                tvHint.setVisibility(View.VISIBLE);
                initData();
            }
        });
    }

    private void initData() {
        List<CartBean> cartList = FuliCenterApplication.getInstance().getCartList();
        Log.e(TAG, "cartList = " + cartList);
        mCartList.clear();
        mCartList.addAll(cartList);
        mSwipeRefreshLayout.setRefreshing(false);
        tvHint.setVisibility(View.GONE);
        mCartAdapter.setMore(true);
        if (mCartList != null && mCartList.size() > 0) {
            Log.e(TAG, "result.length=" + mCartList.size());
            if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                mCartAdapter.initItem(mCartList);
            } else {
                mCartAdapter.addItem(mCartList);
            }
            if (mCartList.size() < I.PAGE_SIZE_DEFAULT) {
                mCartAdapter.setMore(false);
            }
            tvNothing.setVisibility(View.GONE);
            sumPrice();
        } else {
            mCartAdapter.setMore(false);
            tvNothing.setVisibility(View.VISIBLE);
        }
    }
    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_cart);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_yellow,
                R.color.ebpay_red,
                R.color.google_green
        );
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_cart);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mCartAdapter = new CartAdapter(mContext,mCartList);
        mRecyclerView.setAdapter(mCartAdapter);
        tvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
        tvSumPrice = (TextView) layout.findViewById(R.id.tv_cart_sum_price);
        tvSavaPrice = (TextView) layout.findViewById(R.id.tv_cart_save_price);
        tvBuy = (TextView) layout.findViewById(R.id.tv_cart_buy);
        tvNothing = (TextView) layout.findViewById(R.id.tv_no);
        tvNothing.setVisibility(View.VISIBLE);
    }

    class UpdateCartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }
    private void setUpdateCartListener(){
        mUpdateCartReceiver = new UpdateCartReceiver();
        IntentFilter filter = new IntentFilter("update_cart_list");
        mContext.registerReceiver(mUpdateCartReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUpdateCartReceiver != null) {
            mContext.unregisterReceiver(mUpdateCartReceiver);
        }
    }

    private void sumPrice(){
        if (mCartList != null && mCartList.size() > 0) {
            int sumPrice = 0;
            int rankPrice = 0;
            for (CartBean cart : mCartList) {
                GoodDetailsBean good = cart.getGoods();
                if (good != null&&cart.isChecked()) {
                    sumPrice += convertPrice(good.getCurrencyPrice())*cart.getCount();
                    rankPrice += convertPrice(good.getRankPrice());
                }
            }
            tvSumPrice.setText("合計: ¥"+sumPrice);
            tvSavaPrice.setText("節省: ¥"+(sumPrice-rankPrice));
        } else {
            tvSumPrice.setText("合計: ¥00.00");
            tvSavaPrice.setText("節省: ¥00.00");
        }
    }

    private int convertPrice(String price) {
        price = price.substring(price.indexOf("¥") + 1);
        return Integer.valueOf(price);
    }
}
