package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.R;

import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by Administrator on 2016/8/4.
 */
public class CategoryAdapter extends BaseExpandableListAdapter {
    Context  mContext;
    List<CategoryGroupBean> mGroupList;
    List<ArrayList<CategoryChildBean>> mChildList;

    public CategoryAdapter(Context mContext,
                           List<CategoryGroupBean> mGroupList,
                           List<ArrayList<CategoryChildBean>> mChildList) {
        this.mContext = mContext;
        this.mGroupList = new ArrayList<CategoryGroupBean>();
        mGroupList.addAll(mGroupList);
        this.mChildList = new ArrayList<ArrayList<CategoryChildBean>>();
        mChildList.addAll(mChildList);
    }
    @Override
    public int getGroupCount() {
        return mGroupList!=null?mGroupList.size():0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList.get(groupPosition).size();
    }

    @Override
    public CategoryGroupBean getGroup(int groupPosition) {
        if (mGroupList!=null) return mGroupList.get(groupPosition);
        return null;
    }

    @Override
    public CategoryChildBean getChild(int groupPosition, int childPosition) {
        if (mChildList.get(groupPosition) != null
                && mChildList.get(groupPosition).get(childPosition) != null)
            return mChildList.get(groupPosition).get(childPosition);
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GropViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_category_group, null);
            holder = new GropViewHolder();
            CategoryGroupBean group = getGroup(groupPosition);
            ImageUtils.setGroupCategoryImage(mContext, holder.ivGroupThumb, group.getImageUrl());
            holder.tvGroupName.setText(group.getName());
            holder.ivIndIcator.setImageResource(R.drawable.expand_off);
            convertView.setTag(holder);
        } else {
            holder=(GropViewHolder) convertView.getTag();
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup viewGroup) {
        ChildViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_cateogry_child, null);
            holder = new ChildViewHolder();
            CategoryChildBean child = getChild(groupPosition, childPosition);
            if (child != null) {
                ImageUtils.setChildCategoryImage(mContext, holder.ivCategoryChildThumb, child.getImageUrl());
                holder.tvCategoryChildName.setText(child.getName());
            }
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class GropViewHolder{
        ImageView  ivGroupThumb;
        TextView  tvGroupName;
        ImageView ivIndIcator;
    }

    class ChildViewHolder{
        RelativeLayout layout_category_child;
        ImageView ivCategoryChildThumb;
        TextView tvCategoryChildName;
    }
}
