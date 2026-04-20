package com.raku.apple_test.for_sentence;

import java.util.*;
import java.util.stream.IntStream;

public class Main1For {
    public static void main(String[] args) {
        Random random = new Random();

        List<AppleFor> apples = new ArrayList<>();
        List<BananaFor> bananas = new ArrayList<>();
        List<OrangeFor> oranges = new ArrayList<>();

        Map<AppleFor, String> appleMap = new HashMap<>();
        Map<BananaFor, String> bananaMap = new HashMap<>();
        Map<OrangeFor, String> orangeMap = new HashMap<>();

        String[] appleColors = {"red", "green", "yellow", "pink", "dark-red"};
        String[] bananaColors = {"yellow", "green", "brown", "gold", "light-yellow"};
        String[] orangeColors = {"orange", "dark-red", "light-yellow", "amber", "tangerine"};

        String[] kind = {"アメリカ産", "インド産", "日本産", "中国産", "ブラジル産"};
        String[] quality = {"High", "Medium", "Low"};

        Sweetness[] appleTastes = {
                Sweetness.INSANELY_SWEET,
                Sweetness.A_LITTLE_SWEET,
                Sweetness.SWEET,
                Sweetness.NOT_SWEET,
                Sweetness.VERY_SWEET
        };
        Sweetness[] bananaTastes = {
                Sweetness.VERY_SWEET,
                Sweetness.SWEET,
                Sweetness.INSANELY_SWEET,
                Sweetness.A_LITTLE_SWEET,
                Sweetness.NOT_SWEET
        };
        Sweetness[] orangeTastes = {
                Sweetness.SWEET,
                Sweetness.NOT_SWEET,
                Sweetness.A_LITTLE_SWEET,
                Sweetness.VERY_SWEET,
                Sweetness.INSANELY_SWEET
        };

        // 1) 生成: 配列 + 古風なfor(index)でバラバラ値を投入
        for (int i = 1; i <= 20; i++) {
            String appleColor = appleColors[i % appleColors.length];
            String bananaColor = bananaColors[(i * 2) % bananaColors.length];
            String orangeColor = orangeColors[(i * 3) % orangeColors.length];

            long appleWeight = 85 + (i * 9L) + ((i % 3) * 4L);
            long bananaWeight = 95 + (i * 7L) + ((i % 4) * 5L);
            long orangeWeight = 90 + (i * 8L) + ((i % 5) * 3L);

            Sweetness appleTaste = appleTastes[(i * 2) % appleTastes.length];
            Sweetness bananaTaste = bananaTastes[(i * 3) % bananaTastes.length];
            Sweetness orangeTaste = orangeTastes[(i * 4) % orangeTastes.length];

            if (i % 2 == 0) {
                apples.add(new AppleFor(appleColor, appleWeight, i));
                bananas.add(new BananaFor(bananaColor, bananaWeight, i, bananaTaste));
                oranges.add(new OrangeFor(orangeColor, orangeWeight, i));
            } else {
                apples.add(new AppleFor(appleColor, appleWeight, i, appleTaste));
                bananas.add(new BananaFor(bananaColor, bananaWeight, i));
                oranges.add(new OrangeFor(orangeColor, orangeWeight, i, orangeTaste));
            }
        }

        // 2) 削除: for-eachでremoveは危険なので、後ろからindex forで削除
        /*
         * Modern example:
         * apples.removeIf(apple -> apple.getTaste() == Sweetness.NOT_SWEET);
         */
        for (int idx = apples.size() - 1; idx >= 0; idx--) {
            if (apples.get(idx).getTaste() == Sweetness.NOT_SWEET) {
                apples.remove(idx);
            }
        }

        // 3) 代入 + Map格納: index forでkind/qualityを割り当て
        /*
         * Modern example:
         * IntStream.range(0, apples.size()).forEach(i -> {
         *     AppleFor apple = apples.get(i);
         *     apple.setKind(kind[i % kind.length]);
         *     appleMap.put(apple, quality[(i + 1) % quality.length]);
         * });
         */
        for (int i = 0; i < apples.size(); i++) {
            AppleFor apple = apples.get(i);
            apple.setKind(kind[i % kind.length]);
            appleMap.put(apple, quality[(i + 1) % quality.length]);
        }

        for (int i = 0; i < bananas.size(); i++) {
            BananaFor banana = bananas.get(i);
            banana.setKind(kind[(i + apples.size()) % kind.length]);
            bananaMap.put(banana, quality[random.nextInt(quality.length)]);
        }

        // Orange: indexOfを避けてIntStreamでindex付き処理
        IntStream.range(0, oranges.size()).forEach(i -> {
            OrangeFor orange = oranges.get(i);
            orange.setKind(kind[(i + apples.size() + bananas.size()) % kind.length]);
            if (!orangeMap.containsValue(quality[i % quality.length])) {
                orangeMap.put(orange, quality[random.nextInt(quality.length)]);
            }
        });

        /*
         * Equivalent classic loop:
         * for (int i = 0; i < oranges.size(); i++) {
         *     OrangeFor orange = oranges.get(i);
         *     orange.setKind(kind[(i + apples.size() + bananas.size()) % kind.length]);
         *     if (!orangeMap.containsValue(quality[i % quality.length])) {
         *         orangeMap.put(orange, quality[random.nextInt(quality.length)]);
         *     }
         * }
         */

        // 4) Map走査その1: iteratorを明示した古風スタイル
        /*
         * Modern example:
         * appleMap.entrySet().stream()
         *         .limit(3)
         *         .forEach(e -> System.out.println("[APPLE MAP] kind=" + e.getKey().getKind() + ", quality=" + e.getValue()));
         */
        Iterator<Map.Entry<AppleFor, String>> appleIterator = appleMap.entrySet().iterator();
        int printed = 0;
        while (appleIterator.hasNext() && printed < 3) {
            Map.Entry<AppleFor, String> entry = appleIterator.next();
            System.out.println("[APPLE MAP] kind=" + entry.getKey().getKind() + ", quality=" + entry.getValue());
            printed++;
        }

        // 5) Map走査その2: keySetを配列化してindex for
        BananaFor[] bananaKeys = bananaMap.keySet().toArray(new BananaFor[0]);
        for (int i = 0; i < bananaKeys.length && i < 3; i++) {
            BananaFor key = bananaKeys[i];
            System.out.println("[BANANA MAP] kind=" + key.getKind() + ", quality=" + bananaMap.get(key));
        }

        // 6) List走査 + 集計: すべてindex forで実施
        /*
         * Modern example:
         * Map<Sweetness, Integer> orangeTasteCount = oranges.stream()
         *         .collect(Collectors.groupingBy(
         *                 OrangeFor::getTaste,
         *                 Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
         *         ));
         */
        Map<Sweetness, Integer> orangeTasteCount = new HashMap<>();
        for (int i = 0; i < oranges.size(); i++) {
            Sweetness taste = oranges.get(i).getTaste();
            orangeTasteCount.merge(taste, 1, Integer::sum);
        }

        System.out.println("Apple count: " + apples.size());
        System.out.println("Banana count: " + bananas.size());
        System.out.println("Orange count: " + oranges.size());

        if (!apples.isEmpty()) {
            System.out.println("First apple taste: " + apples.get(0).getTaste().getSweetness());
        }
        if (!bananas.isEmpty()) {
            System.out.println("First banana taste: " + bananas.get(0).getTaste().getSweetness());
        }
        if (!oranges.isEmpty()) {
            System.out.println("First orange taste: " + oranges.get(0).getTaste().getSweetness());
        }

        int limit = Math.min(Math.min(apples.size(), bananas.size()), oranges.size());
        /*
         * Modern example:
         * IntStream.range(0, limit).forEach(j -> {
         *     System.out.println("All apples  : " + apples.get(j).getTaste().getSweetness());
         *     System.out.println("All bananas : " + bananas.get(j).getTaste().getSweetness());
         *     System.out.println("All oranges : " + oranges.get(j).getTaste().getSweetness());
         * });
         */
        for (int j = 0; j < limit; j++) {
            System.out.println("All apples  : " + apples.get(j).getTaste().getSweetness());
            System.out.println("All bananas : " + bananas.get(j).getTaste().getSweetness());
            System.out.println("All oranges : " + oranges.get(j).getTaste().getSweetness());
        }

        System.out.println("-- orange taste count --");
        Sweetness[] allSweetness = Sweetness.values();

        for (int i = 0; i < allSweetness.length; i++) {
            Sweetness key = allSweetness[i];
            int count = orangeTasteCount.getOrDefault(key, 0);
            System.out.println(key.name() + " = " + count);
        }
    }
}