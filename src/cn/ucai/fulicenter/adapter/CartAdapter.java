package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by Administrator on 2016/8/1.
 */
public class CartAdapter extends RecyclerView.Adapter<ViewHolder>{
    Context mContext;
    List<CartBean> mCartList;
    CartViewHolder mCartViewHolder;
    boolean isMore;
    public CartAdapter(Context context, List<CartBean> list) {
        mContext = context;
        mCartList = new ArrayList<CartBean>();
        mCartList.addAll(list);
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder = new CartViewHolder(inflater.inflate(R.layout.item_cart, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CartViewHolder) {
            mCartViewHolder = (CartViewHolder) holder;
            final CartBean cart = mCartList.get(position);
            mCartViewHolder.cbCart.setChecked(cart.isChecked());
            if (cart.getGoods() != null) {
                ImageUtils.setGoodImage(mContext, mCartViewHolder.ivImageCartThumb, cart.getGoods().getGoodsThumb());
                mCartViewHolder.tvCartName.setText(cart.getGoods().getGoodsName());
                mCartViewHolder.tvCartNum.setText("("+cart.getCount()+")");
                mCartViewHolder.tvCartJag.setText(cart.getGoods().getCurrencyPrice());
            }
            mCartViewHolder.cbCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    cart.setChecked(isChecked);
                    new UpdateCartTask(mContext,cart).execute();
                }
            });
            mCartViewHolder.ivImageCartAdd.setOnClickListener(new ChangeCountListener(cart,1));
            mCartViewHolder.ivImageCartDel.setOnClickListener(new ChangeCountListener(cart,-1));
        }
    }

    @Override
    public int getItemCount() {
        return mCartList != null ? mCartList.size() : 0;
    }

    public void initItem(List<CartBean> list) {
        if (mCartList != null) {
            mCartList.clear();
        }
        mCartList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(List<CartBean> list) {
        mCartList.addAll(list);
        notifyDataSetChanged();
    }

    class CartViewHolder extends ViewHolder {
        RelativeLayout layout;
        CheckBox cbCart;
        ImageView ivImageCartThumb;
        TextView tvCartName;
        ImageView ivImageCartAdd;
        ImageView ivImageCartDel;
        TextView tvCartNum;
        TextView tvCartJag;
        public CartViewHolder(View itemView) {
            super(itemView);
            /*layout = (RelativeLayout) itemView.findViewById(R.id.layout_cart_Lin);*/
            cbCart = (CheckBox) itemView.findViewById(R.id.item_cart_cb);
            ivImageCartThumb = (ImageView) itemView.findViewById(R.id.image_cart_thumb);
            tvCartName = (TextView) itemView.findViewById(R.id.text_cart_name);
            ivImageCartAdd = (ImageView) itemView.findViewById(R.id.image_cart_add);
            ivImageCartDel = (ImageView) itemView.findViewById(R.id.image_cart_del);
            tvCartNum = (TextView) itemView.findViewById(R.id.text_cart_num);
            tvCartJag = (TextView) itemView.findViewById(R.id.text_cart_jag);
        }
    }

    class ChangeCountListener implements View.OnClickListener {
        CartBean cartBean;
        int setCount;
        public ChangeCountListener(CartBean cart,int count){
            this.cartBean = cart;
            this.setCount = count;
        }

        @Override
        public void onClick(View view) {
            this.cartBean.setCount(cartBean.getCount()+setCount);
            new UpdateCartTask(mContext,cartBean).execute();
        }
    }

}
