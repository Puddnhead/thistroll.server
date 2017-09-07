package com.thistroll.domain.enums;

/**
 * Runtime environments
 *
 * Created by MVW on 9/5/2017.
 */
public enum Environments {

    DEV("dev"),
    PROD("prod");

    private String value;

    Environments(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
