package com.hjzgg.stateless.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "hjz");
        jsonObject.put("age", 25);

        System.out.println(MapToStringUtil.toEqualString(jsonObject, ','));
    }
}
