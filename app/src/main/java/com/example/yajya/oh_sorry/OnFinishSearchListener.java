package com.example.yajya.oh_sorry;

import java.util.List;

/**
 * Map에서 장소 검색에 사용됨
 */

public interface OnFinishSearchListener {
    public void onSuccess(List<Item> itemList);

    public void onFail();
}
