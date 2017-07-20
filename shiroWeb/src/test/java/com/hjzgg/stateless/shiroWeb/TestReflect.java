package com.hjzgg.stateless.shiroWeb;

import com.hjzgg.stateless.auth.shiro.StatelessAuthcFilter;

/**
 * Created by hujunzheng on 2017/7/19.
 */
public class TestReflect {
    public static void main(String[] args) {
        Class<?> clazz = StatelessAuthcFilter.class;
        try {
            clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
