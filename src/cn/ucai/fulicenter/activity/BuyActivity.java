package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * Created by Administrator on 2016/8/11.
 */
public class BuyActivity extends BaseActivity {
    BuyActivity mContext;
    EditText edOrderName;
    EditText edOrderPhone;
    Spinner spinProvince;
    EditText edOrderStreet;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_buy);
        initView();
    }

    private void initView() {
        DisplayUtils.initBackWithTitle(mContext,"填写收货地址");
        edOrderName = (EditText) findViewById(R.id.et_order_name);
        edOrderPhone = (EditText) findViewById(R.id.et_order_phone);
        edOrderStreet = (EditText) findViewById(R.id.et_order_street);
        spinProvince = (Spinner) findViewById(R.id.spin_order_province);
    }
}
