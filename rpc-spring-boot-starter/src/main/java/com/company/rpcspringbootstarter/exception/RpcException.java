package com.company.rpcspringbootstarter.exception;

/**
 * @author wei.song
 * @since 2023/1/18 14:50
 */
public class RpcException extends RuntimeException {

    public RpcException(String message) {
        super(message);
    }

}
