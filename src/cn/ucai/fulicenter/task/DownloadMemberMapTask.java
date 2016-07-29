package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.bean.MemberUserAvatar;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/7/20.
 */
public class DownloadMemberMapTask {
    String hxid;
    private final static String TAG = DownloadMemberMapTask.class.getSimpleName();
    Context mcontext;
    public DownloadMemberMapTask(Context context, String username) {
        mcontext=context;
        this.hxid = username;
    }

    public void execute() {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID)
                .addParam(I.Member.GROUP_HX_ID,hxid)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Result result = Utils.getListResultFromJson(s, MemberUserAvatar.class);
                        Log.e(TAG, "result=" + result);
                        List<MemberUserAvatar> list = (List<MemberUserAvatar>) result.getRetData();
                        Log.e(TAG, "list=" + list);
                        if (list != null && list.size() > 0) {
                            Log.e(TAG, "list.size=" + list.size());
                            //SuperWeChatApplication.getInstance().setUserList(list);
                            Map<String, HashMap<String, MemberUserAvatar>> mrmberMap = FuliCenterApplication.getInstance().getMrmberMap();
                            if (!mrmberMap.containsKey(hxid)) {
                                mrmberMap.put(hxid, new HashMap<String, MemberUserAvatar>());
                            }
                            HashMap<String, MemberUserAvatar> hxidMembers = mrmberMap.get(hxid);
                           // Map<String, UserAvatar> userMap = SuperWeChatApplication.getInstance().getUserMap();
                            for (MemberUserAvatar u : list) {
                                hxidMembers.put(u.getMUserName(), u);
                            }
                            mcontext.sendStickyBroadcast(new Intent("update_member_list"));

                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }
}
