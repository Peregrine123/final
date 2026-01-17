package com.jiyu.pojo;

/**
 * Minimal moderation state for short reviews (movie comments).
 */
public enum MovieCommentStatus {
    /** 待审核：对外不可见，作者可见 */
    PENDING,
    /** 已通过：对外可见 */
    APPROVED,
    /** 已驳回：对外不可见，作者可见 */
    REJECTED
}

