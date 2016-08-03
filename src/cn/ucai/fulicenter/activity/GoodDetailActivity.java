package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

/**
 * Created by Administrator on 2016/8/3.
 */
public class GoodDetailActivity extends BaseActivity {
    private final static String TAG = GoodDetailActivity.class.getSimpleName();
    ImageView ivShare;
    ImageView ivCollect;
    ImageView ivCart;
    TextView tvCartCount;

    TextView tvGoodEnglishName;
    TextView tvGoodName;
    TextView tvGoodPriceCurrent;
    TextView tvGoodPriceShop;

    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;
    WebView wvGoodBrief;

    int mGoodId;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_good_details);
        initView();
        initDate();
    }

    private void initDate() {
        mGoodId = getIntent().getIntExtra(D.GoodDetails.KEY_GOODS_ID, 0);
        Log.e(TAG, "mGoodId=" + mGoodId);

    }

    private void initView() {
        ivShare = (ImageView) findViewById(R.id.ivShare);
        ivCollect = (ImageView) findViewById(R.id.ivCollect);
        ivCart = (ImageView) findViewById(R.id.ivAddCart);
        tvCartCount = (TextView) findViewById(R.id.tvCartCount);
        tvGoodEnglishName = (TextView) findViewById(R.id.tvGoodEnglishName);
        tvGoodPriceCurrent = (TextView) findViewById(R.id.tvCurrencyPrice);
        tvGoodPriceShop = (TextView) findViewById(R.id.tvShopPrice);
        tvGoodName = (TextView) findViewById(R.id.tvGoodName);
        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        wvGoodBrief = (WebView) findViewById(R.id.wvGoodBrief);
        WebSettings settings = wvGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);
    }
}
