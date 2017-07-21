package com.hjzgg.stateless.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hujunzheng on 2017/7/20.
 */
public class MapToStringUtil {

    public static String toEqualString(Map<?, ?> map, char separator) {
        List<String> result = new ArrayList<>();
        map.entrySet().parallelStream().reduce(result, (first, second)->{
            first.add(second.getKey() + "=" + second.getValue());
            return first;
        }, (first, second)->{
            if (first == second) {
                return first;
            }
            first.addAll(second);
            return first;
        });

        return StringUtils.join(result, separator);
    }

    public static Map<String, String> maptoMapString(Map<String, ?> map) {
         return map.entrySet().stream().collect(Collectors.toMap(
                (entry) -> {
                    return entry.getKey();
                },
                (entry) -> {
                    if(entry.getValue().getClass().isArray()) {
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<Array.getLength(entry.getValue()); ++i) {
                            Object obj = Array.get(entry.getValue(), i);
                            sb.append(obj.toString()).append(",");
                        }
                        if (sb.length() > 0) {
                            sb.deleteCharAt(sb.length() - 1);
                        }

                        return sb.toString();
                    } else {
                        return entry.getValue().toString();
                    }
                }
        ));
    }

    public static void main(String[] args) {
        Stream<List<Integer>> inputStream = Stream.of(
                Arrays.asList(1),
                Arrays.asList(2, 3),
                Arrays.asList(4, 5, 6)
        );
//        int haha = inputStream.flatMapToInt((xx) -> {
//            return xx.stream().mapToInt((x)->{return x;});
//        }).sum();


//        List<Integer> list = inputStream.flatMap((x) -> {
//            return x.stream().filter((xx) -> {
//                return (xx&1) == 0;
//            });
//        }).collect(Collectors.toList());

        List<Integer> list =inputStream.map((x)->{
            return x.get(0);
        }).collect(Collectors.toList());

        System.out.println(list);
    }
}
