package com.totoro.xkf.sample;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * @date 2020/10/22  4:54 PM
 * @author wujian
 * @description 非粘性 liveData
 * <p>
 *   场景： 不清空数据，只接受订阅之后的数据更新
 *
 *   区别： UnPeekLiveData 会自动延时清空数据
 *         MutableLiveData 会推送最后一次的数据
 *
 * modification history:
 */
public class UnStickyMutableLiveData<T> extends MutableLiveData<T> {

    int mStickyVersion = -1;

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<? super T> observer) {
        super.observe(owner, new Observer<T>() {
            int lastVersion = mStickyVersion;

            @Override
            public void onChanged(T t) {
                if (mStickyVersion > lastVersion) {
                    observer.onChanged(t);
                    lastVersion = mStickyVersion;
                }
            }
        });
    }

    @Override
    public void observeForever(@NonNull final Observer<? super T> observer) {
        super.observeForever(new Observer<T>() {
            int lastVersion = mStickyVersion;

            @Override
            public void onChanged(T t) {
                if (mStickyVersion > lastVersion) {
                    observer.onChanged(t);
                    lastVersion = mStickyVersion;
                }
            }
        });
    }

    @Override
    public void setValue(T value) {
        mStickyVersion++;
        super.setValue(value);
    }

    @Override
    public void postValue(T value) {
        mStickyVersion++;
        super.postValue(value);
    }
}
