package com.uniwork.common.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.*;

/**
 * Tiện ích hỗ trợ copy các thuộc tính không null từ source sang target.
 * Giúp tránh ghi đè các giá trị cũ bằng null khi update một phần dữ liệu.
 */
public class BeanCopyUtils {

    /**
     * Sao chép các thuộc tính khác null từ source sang target.
     *
     * @param source object chứa dữ liệu mới (ví dụ: request hoặc DTO)
     * @param target object đích (ví dụ: entity trong DB)
     */
    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * Sao chép các thuộc tính khác null từ source sang target,
     * đồng thời bỏ qua các field được chỉ định (vd: id, createdDate,...)
     *
     * @param source object chứa dữ liệu mới
     * @param target object đích cần cập nhật
     * @param ignoreFields danh sách field cần bỏ qua
     */
    public static void copyNonNullProperties(Object source, Object target, String... ignoreFields) {
        String[] nullProps = getNullPropertyNames(source);
        Set<String> ignoreSet = new HashSet<>(Arrays.asList(ignoreFields));
        ignoreSet.addAll(Arrays.asList(nullProps));
        BeanUtils.copyProperties(source, target, ignoreSet.toArray(new String[0]));
    }

    /**
     * Lấy danh sách tên thuộc tính có giá trị null.
     */
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        return emptyNames.toArray(new String[0]);
    }
}

