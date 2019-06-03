package com.izam.app.api;

import com.izam.data.account.RepositoryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusCode {
    OK(200),
    CREATED(201),
    ACCEPTED(202),

    BAD_REQUEST(400),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405);

    private int code;

    public static StatusCode fromRepoStatus(int repoStatus) {
        if (repoStatus == RepositoryStatus.BAD_REQUEST.getCode()) {
            return BAD_REQUEST;
        } else if (repoStatus == RepositoryStatus.NOT_FOUND.getCode()) {
            return NOT_FOUND;
        }
        return OK;
    }
}