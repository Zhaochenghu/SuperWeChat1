package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CategoryAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryParentBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/8/4.
 */
public class CategoryFragment extends Fragment {
    private final static String TAG = CategoryFragment.class.getSimpleName();
    FuliCenterMainActivity mContext;
    ExpandableListView mExpandableListView;
    List<CategoryParentBean> mGroupList;
    List<ArrayList<CategoryChildBean>> mChildList;
    CategoryAdapter mAdapter;

    int groupCount;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (FuliCenterMainActivity) getContext();
        View layout = View.inflate(mContext, R.layout.fragment_category, null);
        mGroupList = new ArrayList<CategoryParentBean>();
        mChildList = new ArrayList<ArrayList<CategoryChildBean>>();
        mAdapter = new CategoryAdapter(mContext, mGroupList, mChildList);
        initData();
        initView(layout);
        return layout;
    }

    private void initData() {
        findCategoryGroupList(new OkHttpUtils2.OnCompleteListener<CategoryParentBean[]>() {
            @Override
            public void onSuccess(CategoryParentBean[] result) {
                Log.e(TAG, "result=" + result);
                if (result != null) {
                    ArrayList<CategoryParentBean> groupList = Utils.array2List(result);
                    if (groupList != null) {
                        Log.e(TAG, "groupList=" + groupList.size());
                        mGroupList = groupList;
                        int i = 0;
                        for (CategoryParentBean g : groupList) {
                            mChildList.add(new ArrayList<CategoryChildBean>());
                            findCategoryChildList(g.getId(),i);
                            i++;
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "group,error=" + error);
            }
        });
    }

    private void findCategoryChildList(int parentId,final int index){
        final OkHttpUtils2<CategoryChildBean[]> utils2 = new OkHttpUtils2<CategoryChildBean[]>();
        utils2.setRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN)
                .addParam(I.CategoryChild.PARENT_ID,String.valueOf(parentId))
                .addParam(I.PAGE_ID,String.valueOf(I.PAGE_ID_DEFAULT))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CategoryChildBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<CategoryChildBean[]>() {
                    @Override
                    public void onSuccess(CategoryChildBean[] result) {
                        groupCount++;
                        Log.e(TAG, "result=" + result);
                        if (result != null) {
                            ArrayList<CategoryChildBean> childlist = utils2.array2List(result);
                            if (childlist != null) {
                                mChildList.set(index, childlist);
                            }
                        }
                        if (groupCount == mGroupList.size()) {
                            mAdapter.addAll(mGroupList,mChildList);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "child,error=" + error);
                    }
                });

    }

    private void findCategoryGroupList(OkHttpUtils2.OnCompleteListener<CategoryParentBean[]> listener) {
        OkHttpUtils2<CategoryParentBean[]> utils2 = new OkHttpUtils2<CategoryParentBean[]>();
        utils2.setRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP)
                .targetClass(CategoryParentBean[].class)
                .execute(listener);
    }

    private void initView(View layout) {
        mExpandableListView = (ExpandableListView) layout.findViewById(R.id.elvCategory);
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setAdapter(mAdapter);
    }
}
