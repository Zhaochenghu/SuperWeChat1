package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.task.DownloadCollectCountListTask;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.view.DisplayUtils;
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
    GoodDetailActivity mContext;
    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;
    WebView wvGoodBrief;
    GoodDetailsBean mGoodDetail;
    int mGoodId;
    boolean isCollect;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        setContentView(R.layout.activity_good_details);
        initView();
        initDate();
        setListener();
    }

    private void setListener() {
        MyOnClickListener listener = new MyOnClickListener();
        ivCollect.setOnClickListener(listener);
        ivShare.setOnClickListener(listener);
        ivCart.setOnClickListener(listener);
    }

    private void initDate() {
        mGoodId = getIntent().getIntExtra(D.GoodDetails.KEY_GOODS_ID, 0);
        Log.e(TAG, "mGoodId=" + mGoodId);
        if (mGoodId > 0) {
            getGoodDetailsByGoodId(new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
                @Override
                public void onSuccess(GoodDetailsBean result) {
                    Log.e(TAG, "result=" + result);
                    if (result != null) {
                        mGoodDetail = result;
                        showGoodDetails();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "error=" + error);
                    finish();
                    Toast.makeText(mContext,"获取商品详情数据失败",Toast.LENGTH_LONG).show();
                }
            });
        } else {
            finish();
            Toast.makeText(mContext,"获取商品详情数据失败",Toast.LENGTH_LONG).show();
        }

    }

    private void showGoodDetails() {
        tvGoodEnglishName.setText(mGoodDetail.getGoodsEnglishName());
        tvGoodName.setText(mGoodDetail.getGoodsName());
        tvGoodPriceCurrent.setText(mGoodDetail.getCurrencyPrice());
        tvGoodPriceShop.setText(mGoodDetail.getShopPrice());
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator, getAlbumImageUrl(mGoodDetail),
                getAlbumImageSize(mGoodDetail));
        wvGoodBrief.loadDataWithBaseURL(null, mGoodDetail.getGoodsBrief(), D.TEXT_HTML, D.UTF_8, null);
    }

    private String[] getAlbumImageUrl(GoodDetailsBean details) {
        String[] albumImageUrl = new String[]{};
        if (details.getProperties() != null && details.getProperties().length > 0) {
            AlbumsBean[] albums = details.getProperties()[0].getAlbums();
            albumImageUrl=new String[albums.length];
            for (int i=0;i<albumImageUrl.length;i++) {
                albumImageUrl[i] = albums[i].getImgUrl();
            }
        }
        return albumImageUrl;
    }

    private int getAlbumImageSize(GoodDetailsBean details) {
        if (details.getProperties() != null && details.getProperties().length > 0) {
            return details.getProperties()[0].getAlbums().length;
        }
        return 0;
    }

    private void getGoodDetailsByGoodId(OkHttpUtils2.OnCompleteListener<GoodDetailsBean> listener){
        OkHttpUtils2<GoodDetailsBean> utils = new OkHttpUtils2<GoodDetailsBean>();
        utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(D.GoodDetails.KEY_GOODS_ID,String.valueOf(mGoodId))
                .targetClass(GoodDetailsBean.class)
                .execute(listener);
    }

    private void initView() {
        DisplayUtils.initBack(mContext);
        ivShare = (ImageView) findViewById(R.id.ivShare);
        ivCollect = (ImageView) findViewById(R.id.iv_good_collect);
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

    @Override
    protected void onResume() {
        super.onResume();
        initCollecStatus();
    }

    private void initCollecStatus(){
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            String userName = FuliCenterApplication.getInstance().getUserName();
            OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
            utils.setRequestUrl(I.REQUEST_IS_COLLECT)
                    .addParam(I.Collect.USER_NAME,userName)
                    .addParam(I.Collect.GOODS_ID,String.valueOf(mGoodId))
                    .targetClass(MessageBean.class)
                    .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                        @Override
                        public void onSuccess(MessageBean result) {
                            Log.e(TAG, "result=" + result);
                            if (result != null && result.isSuccess()) {
                                isCollect = true;
                            } else {
                                isCollect = false;
                            }
                            updateCollectStatus();
                        }
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "error=" + error);
                        }
                    });
        }
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_good_collect:
                    goodColect();
                    break;
                case R.id.ivShare:
                    showShare();
                    break;
                case R.id.ivAddCart:
                    addCart();
                    break;
            }
        }
        //取消或者添加商品收藏
        private void goodColect() {
            if (DemoHXSDKHelper.getInstance().isLogined()) {
                if (isCollect) {
                    //取消收藏
                    OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
                    utils.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                            .addParam(I.Collect.USER_NAME, FuliCenterApplication.getInstance().getUserName())
                            .addParam(I.Collect.GOODS_ID, String.valueOf(mGoodId))
                            .targetClass(MessageBean.class)
                            .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                                                 @Override
                                                 public void onSuccess(MessageBean result) {
                                                     if (result != null && result.isSuccess()) {
                                                         isCollect = false;
                                                         new DownloadCollectCountListTask(mContext, FuliCenterApplication.getInstance().getUserName());
                                                         sendStickyBroadcast(new Intent("update_collect_list"));
                                                     } else {
                                                         Log.e(TAG, "delete fail");
                                                     }
                                                     updateCollectStatus();
                                                     Toast.makeText(mContext, result.getMsg(), Toast.LENGTH_LONG).show();
                                                 }

                                                 @Override
                                                 public void onError(String error) {
                                                     Log.e(TAG, "error=" + error);
                                                 }
                                             });
                } else {
                    //添加收藏
                    OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
                    utils.setRequestUrl(I.REQUEST_ADD_COLLECT)
                            .addParam(I.Collect.USER_NAME,FuliCenterApplication.getInstance().getUserName())
                            .addParam(I.Collect.GOODS_ID,String.valueOf(mGoodDetail.getGoodsId()))
                            .addParam(I.Collect.ADD_TIME,String.valueOf(mGoodDetail.getAddTime()))
                            .addParam(I.Collect.GOODS_ENGLISH_NAME,mGoodDetail.getGoodsEnglishName())
                            .addParam(I.Collect.GOODS_IMG,mGoodDetail.getGoodsImg())
                            .addParam(I.Collect.GOODS_THUMB,mGoodDetail.getGoodsThumb())
                            .addParam(I.Collect.GOODS_NAME,mGoodDetail.getGoodsName())
                            .targetClass(MessageBean.class)
                            .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                                @Override
                                public void onSuccess(MessageBean result) {
                                    Log.e(TAG, "result=" + result);
                                    if (result != null && result.isSuccess()) {
                                        isCollect = true;
                                        new DownloadCollectCountListTask(mContext, FuliCenterApplication.getInstance().getUserName());
                                        sendStickyBroadcast(new Intent("update_collect_list"));
                                    } else {
                                        Log.e(TAG, "delete fail");
                                    }
                                    updateCollectStatus();
                                    Toast.makeText(mContext, result.getMsg(), Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onError(String error) {
                                    Log.e(TAG, "error=" + error);
                                }
                            });
                }
            } else {
                startActivity(new Intent(mContext,LoginActivity.class));
            }
        }
    }

    private void addCart() {
        List<CartBean> cartList = FuliCenterApplication.getInstance().getCartList();
        CartBean cart = new CartBean();
        boolean isExits = false;
        for (CartBean cartBean : cartList) {
            if (cartBean.getGoodsId() == mGoodId) {
                cart.setChecked(cartBean.isChecked());
                cart.setCount(cartBean.getCount()+1);
                cart.setGoods(mGoodDetail);
                cart.setUserName(cartBean.getUserName());
                isExits = true;
            }
            if (!isExits) {
                cart = new CartBean();
                cart.setChecked(true);
                cart.setCount(1);
                cart.setGoods(mGoodDetail);
                cart.setUserName(FuliCenterApplication.getInstance().getUserName());
            }
        }
        new UpdateCartTask(mContext,cart).execute();
    }

    private void updateCollectStatus() {
        if (isCollect) {
            ivCollect.setImageResource(R.drawable.bg_collect_out);
        } else {
            ivCollect.setImageResource(R.drawable.bg_collect_in);
        }
    }
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(mGoodDetail.getShareUrl());
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mGoodDetail.getGoodsName());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(mGoodDetail.getShareUrl());
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(mGoodDetail.getGoodsName());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(mGoodDetail.getShareUrl());

        // 启动分享GUI
        oks.show(this);
    }
}
