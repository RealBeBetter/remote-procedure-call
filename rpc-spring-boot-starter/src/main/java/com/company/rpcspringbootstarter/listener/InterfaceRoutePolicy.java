package com.company.rpcspringbootstarter.listener;

/**
 * @author wei.song
 * @since 2023/1/23 12:33
 */
public class InterfaceRoutePolicy {

    /**
     * 选择接口类，暂时只考虑实现一个接口的情景
     *
     * @param interfaces 接口
     * @return {@link Class}<{@link ?}>
     */
    public static Class<?> selectInterfaceClass(Class<?>[] interfaces) {
        return interfaces[0];
    }

}
