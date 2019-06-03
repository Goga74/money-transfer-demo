package com.izam.data.account;

import lombok.Getter;

@Getter
public enum RepositoryStatus {
    OK(200),
    BAD_REQUEST(400),
    NOT_FOUND(404);

    private int code;

    RepositoryStatus(int code) {
        this.code = code;
    }
}
