package java.com.raku.apple_test.map;

import java.util.*;

public class Main1Map {
    public static void main(String[] args) {
        Map<String, Object> appleMap = new HashMap<>();

        appleMap.put("weight : ", 150);
        appleMap.put("color : ", "red");
        appleMap.put("price : ", 200);

        for (Map.Entry<String, Object> elem : appleMap.entrySet()) {
            try {
                String string = elem.getKey();
                Integer integer = (Integer) elem.getValue();
                System.out.println(string + integer);
            } catch (NumberFormatException e) {
                String string = elem.getKey();
                String string1 = elem.getValue().toString();

                System.out.println(string + string1);
            }
        }

        String string, string1;
        int integer;

        List<List<Object>> bananaElem = Arrays.asList(new ArrayList<>(), new ArrayList<>());
        bananaElem.get(0).add("weight : ");
        bananaElem.get(0).add("color : ");
        bananaElem.get(0).add("price : ");
        bananaElem.get(1).add("120");
        bananaElem.get(1).add("yellow");
        bananaElem.get(1).add("150");

        for (Object element : bananaElem.get(0)) {
            string = element.toString();
            try {
                integer = Integer.parseInt(bananaElem.get(1).get(bananaElem.get(0).indexOf(element)).toString());
                System.out.println(string + integer);
            } catch (NumberFormatException e) {
                string1 = bananaElem.get(1).get(bananaElem.get(0).indexOf(element)).toString();
                System.out.println(string + string1);
            }
        }
    }
}
