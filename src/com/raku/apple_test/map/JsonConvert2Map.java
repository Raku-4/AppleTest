/**
package com.raku.apple_test.map;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Gson は Forge/Minecraft に同梱されています
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Nullable;

public class JsonConvert2Map {
    // JSON文字列 {"name":"Alice","age":25} を解析して Map に変換（手書き・簡易版）
    public static @NotNull Map<String, Object> parseJson(String json) {
        Map<String, Object> result = new HashMap<>();
        json = json.trim();
        char[] charJson = json.toCharArray();

        for (int i = 0; i < charJson.length; i++) {
            String string = String.valueOf(charJson[i]);
            switch (string) {
                case "{", "}" -> {
                }
                case "\"" -> {
                    char[] stringKeyArray = new char[100];
                    char[] stringValueArray = new char[100];
                    String stringKeyMap = "";
                    String stringValueMap;
                    boolean bool = true;
                    int quoteCounter = 0;
                    int arrayNumber = 0;

                    for (int j = i + 1; bool && j < charJson.length; j++) {
                        String string2 = String.valueOf(charJson[j]);
                        if (string2.equals("\"")) quoteCounter++;

                        if (quoteCounter == 0) {
                            if (arrayNumber < stringKeyArray.length) {
                                stringKeyArray[arrayNumber] = charJson[j];
                                arrayNumber++;
                            }
                        }

                        if (quoteCounter == 1) {
                            stringKeyMap = new String(stringKeyArray, 0, arrayNumber).trim();
                            arrayNumber = 0;
                        }

                        if (quoteCounter == 2) {
                            if (arrayNumber < stringValueArray.length) {
                                stringValueArray[arrayNumber] = charJson[j];
                                arrayNumber++;
                            }
                        }

                        if (quoteCounter == 3) {
                            stringValueMap = new String(stringValueArray, 0, arrayNumber).trim();
                            result.put(stringKeyMap, stringValueMap);
                            quoteCounter = 0;
                            arrayNumber = 0;
                            bool = false;
                        }
                    }
                }
            }

        }
        return result;
    }

    // 安全版: Gson を使って JSON を Map<String, Object> に変換
    public static @NotNull Map<String, Object> parseJsonWithGson(String json) {
        JsonElement root = JsonParser.parseString(json);
        if (!root.isJsonObject()) return new HashMap<>();
        return jsonObjectToMap(root.getAsJsonObject());
    }

    private static @NotNull Map<String, Object> jsonObjectToMap(@NotNull JsonObject obj) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
            map.put(e.getKey(), jsonElementToJava(e.getValue()));
        }
        return map;
    }

    private static @NotNull Object jsonArrayToList(@NotNull JsonArray arr) {
        List<Object> list = new ArrayList<>(arr.size());
        for (JsonElement el : arr) list.add(jsonElementToJava(el));
        return list;
    }

    private static Object jsonElementToJava(@Nullable JsonElement el) {
        if (el == null || el instanceof JsonNull || el.isJsonNull()) return null;
        if (el.isJsonObject()) return jsonObjectToMap(el.getAsJsonObject());
        if (el.isJsonArray()) return jsonArrayToList(el.getAsJsonArray());
        JsonPrimitive p = el.getAsJsonPrimitive();
        if (p.isBoolean()) return p.getAsBoolean();
        if (p.isNumber()) {
            // 整数/小数を保持
            Number n = p.getAsNumber();
            // 小数点を含むかで型を分ける
            String s = p.getAsString();
            return (s.contains(".") ? n.doubleValue() : n.longValue());
        }
        return p.getAsString();
    }

    public static void main(String[] args) {
        String sample = "{\"name\":\"Alice\",\"age\":25}";

        // 手書き版（値が文字列以外だと落とす可能性あり）
        Map<String, Object> naive = parseJson(sample);
        System.out.println("naive => " + naive);

        // 安全版（推奨）
        Map<String, Object> map = parseJsonWithGson(sample);
        for (Map.Entry<String, Object> elem : map.entrySet()) {
            Object v = elem.getValue();
            if (v instanceof Number num) {
                System.out.println(elem.getKey() + ":" + num);
            } else {
                System.out.println(elem.getKey() + "：" + v);
            }
        }
    }
}
 */