package com.hundsun.myconst;

public interface IEnum {
    String getCode();

    String getDescription();

    default String getRelKey() {
        return null;
    }

    default String getLocalCode() {
        return null;
    }

    default String getLocalDescription() {
        return null;
    }
}
