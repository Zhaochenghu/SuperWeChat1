package cn.ucai.fulicenter.activity;

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
    int action = I.ACTION_DOWNLOAD;
    TextView tvHint;
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
        } else {
            mCartAdapter.setMore(false);
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
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_cart);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mCartAdapter = new CartAdapter(mContext,mCartList);
        mRecyclerView.setAdapter(mCartAdapter);
        tvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
    }
}
