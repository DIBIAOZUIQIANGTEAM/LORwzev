package com.wsns.lor.http.progress;

import android.content.Context;

import com.wsns.lor.http.entity.User;
import com.wsns.lor.view.layout.VRefreshLayout;

/**
 * Created by liukun on 16/3/10.
 */
public interface ProgressCancelListener {
    void onCancelProgress();
    void setVRefreshLayout(VRefreshLayout vRefreshLayout);
    void setUser( User user);
}
