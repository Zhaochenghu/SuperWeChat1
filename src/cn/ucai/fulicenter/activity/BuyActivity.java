package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pingplusplus.android.PingppLog;
import com.pingplusplus.libone.PaymentHandler;
import com.pingplusplus.libone.PingppOne;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * Created by Administrator on 2016/8/11.
 */
public class BuyActivity extends BaseActivity implements PaymentHandler{
    private static String URL = "http://218.244.151.190/demo/charge";
    BuyActivity mContext;
    EditText edOrderName;
    EditText edOrderPhone;
    Spinner spinProvince;
    EditText edOrderStreet;
    Button btnBuy;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        setContentView(R.layout.activity_buy);
        initView();
        setListener();
        //设置需要使用的支付方式
        PingppOne.enableChannels(new String[]{"wx", "alipay", "upacp", "bfb", "jdpay_wap"});

        // 提交数据的格式，默认格式为json
        // PingppOne.CONTENT_TYPE = "application/x-www-form-urlencoded";
        PingppOne.CONTENT_TYPE = "application/json";

        PingppLog.DEBUG = true;
    }

    private void setListener() {
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiveName = edOrderName.getText().toString();
                if (TextUtils.isEmpty(receiveName)) {
                    edOrderName.setError("收货姓名不能为空");
                    edOrderName.requestFocus();
                    return;
                }
                String mobile = edOrderPhone.getText().toString();
                if (TextUtils.isEmpty(mobile)) {
                    edOrderPhone.setError("手机号码不能为空");
                    edOrderPhone.requestFocus();
                    return;
                }
                if (!mobile.matches("[\\d]{11}")) {
                    edOrderPhone.setError("手机号码格式错误");
                    edOrderPhone.requestFocus();
                    return;
                }
                String area = spinProvince.getSelectedItem().toString();
                if (TextUtils.isEmpty(area)) {
                    Toast.makeText(BuyActivity.this, "收货地区不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                String address = edOrderStreet.getText().toString();
                if (TextUtils.isEmpty(address)) {
                    edOrderStreet.setError("街道地址不能为空");
                    edOrderStreet.requestFocus();
                    return;
                }
                gotoStatements();
            }
        });
    }
    private int convertPrice(String price) {
        price = price.substring(price.indexOf("￥") + 1);
        return Integer.valueOf(price);
    }

    private void gotoStatements() {
        // 产生个订单号
        String orderNo = new SimpleDateFormat("yyyyMMddhhmmss")
                .format(new Date());

        // 计算总金额（以分为单位）
        int amount = 0;
        JSONArray billList = new JSONArray();
        List<CartBean> cartList = FuliCenterApplication.getInstance().getCartList();
        for (CartBean cart : cartList) {
            GoodDetailsBean goods = cart.getGoods();
            if (goods != null && cart.isChecked()) {
                amount += convertPrice(goods.getRankPrice()) * cart.getCount();
                billList.put(goods.getGoodsName() + "x" + cart.getCount());
            }
        }
        // 构建账单json对象
        JSONObject bill = new JSONObject();

        // 自定义的额外信息 选填
        JSONObject extras = new JSONObject();
        try {
            extras.put("extra1", "extra1");
            extras.put("extra2", "extra2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            bill.put("order_no", orderNo);
            bill.put("amount", amount);
            bill.put("extras", extras);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //壹收款: 创建支付通道的对话框
        PingppOne.showPaymentChannels(getSupportFragmentManager(), bill.toString(), URL, this);

    }

    private void initView() {
        DisplayUtils.initBackWithTitle(mContext,"填写收货地址");
        edOrderName = (EditText) findViewById(R.id.et_order_name);
        edOrderPhone = (EditText) findViewById(R.id.et_order_phone);
        edOrderStreet = (EditText) findViewById(R.id.et_order_street);
        spinProvince = (Spinner) findViewById(R.id.spin_order_province);
        btnBuy = (Button) findViewById(R.id.btn_buy);
    }

    @Override
    public void handlePaymentResult(Intent data) {
        if (data != null) {

            // result：支付结果信息
            // code：支付结果码
            //-2:用户自定义错误
            //-1：失败
            // 0：取消
            // 1：成功
            // 2:应用内快捷支付支付结果

            if (data.getExtras().getInt("code") != 2) {
                PingppLog.d(data.getExtras().getString("result") + "  " + data.getExtras().getInt("code"));
            } else {
                String result = data.getStringExtra("result");
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (resultJson.has("error")) {
                        result = resultJson.optJSONObject("error").toString();
                    } else if (resultJson.has("success")) {
                        result = resultJson.optJSONObject("success").toString();
                    }
                    PingppLog.d("result::" + result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
