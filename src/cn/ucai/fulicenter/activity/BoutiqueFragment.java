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

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/8/3.
 */
public class BoutiqueFragment extends Fragment{
    private final String TAG = BoutiqueFragment.class.getSimpleName();
    FuliCenterMainActivity mContext;
    List<BoutiqueBean> mBoutiqueList;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    BoutiqueAdapter mBoutiqueAdapter;
    int action = I.ACTION_DOWNLOAD;
    TextView tvHint;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (FuliCenterMainActivity)getContext();
        View layout = View.inflate(mContext, R.layout.boutique_fragment, null);
        mBoutiqueList = new ArrayList<BoutiqueBean>();
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
                        && lastItemPosition == mBoutiqueAdapter.getItemCount() - 1) {
                    if (mBoutiqueAdapter.isMore()) {
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
        findNewGoodList(new OkHttpUtils2.OnCompleteListener<BoutiqueBean[]>() {
            @Override
            public void onSuccess(BoutiqueBean[] result) {
                mSwipeRefreshLayout.setRefreshing(false);
                tvHint.setVisibility(View.GONE);
                mBoutiqueAdapter.setMore(true);
                mBoutiqueAdapter.setFooterString(getResources().getString(R.string.load_more));
                Log.e(TAG, "result=" + result);
                if (result != null) {
                    Log.e(TAG, "result.length=" + result.length);
                    ArrayList<BoutiqueBean> boutiqueBeen = Utils.array2List(result);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mBoutiqueAdapter.initItem(boutiqueBeen);

                    } else {
                        mBoutiqueAdapter.addItem(boutiqueBeen);
                    }
                    if (boutiqueBeen.size() < I.PAGE_SIZE_DEFAULT) {
                        mBoutiqueAdapter.setMore(false);
                        mBoutiqueAdapter.setFooterString(getResources().getString(R.string.no_more));
                    }
                } else {
                    mBoutiqueAdapter.setMore(false);
                    mBoutiqueAdapter.setFooterString(getResources().getString(R.string.no_more));
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "error=" + error);
                mSwipeRefreshLayout.setRefreshing(false);
                tvHint.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void findNewGoodList(OkHttpUtils2.OnCompleteListener<BoutiqueBean[]> listener) {
        OkHttpUtils2<BoutiqueBean[]> utils = new OkHttpUtils2<BoutiqueBean[]>();
        utils.setRequestUrl(I.REQUEST_FIND_BOUTIQUES)
                .targetClass(BoutiqueBean[].class)
                .execute(listener);
    }

    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_boutique);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_yellow,
                R.color.ebpay_red,
                R.color.google_green
        );
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_boutique);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mBoutiqueAdapter = new BoutiqueAdapter(mContext,mBoutiqueList);
        mRecyclerView.setAdapter(mBoutiqueAdapter);
        tvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
    }
}
