package com.raku.apple_test.for_sentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main1ForClassic {
    public static void main(String[] args) {
        Random random = new Random();

        List<AppleFor> apples = new ArrayList<>();
        List<BananaFor> bananas = new ArrayList<>();
        List<OrangeFor> oranges = new ArrayList<>();

        Map<AppleFor, String> appleMap = new LinkedHashMap<>();
        Map<BananaFor, String> bananaMap = new LinkedHashMap<>();
        Map<OrangeFor, String> orangeMap = new LinkedHashMap<>();

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

        for (int idx = apples.size() - 1; idx >= 0; idx--) {
            if (apples.get(idx).getTaste() == Sweetness.NOT_SWEET) {
                apples.remove(idx);
            }
        }

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

        for (int i = 0; i < oranges.size(); i++) {
            OrangeFor orange = oranges.get(i);
            orange.setKind(kind[(i + apples.size() + bananas.size()) % kind.length]);
            orangeMap.put(orange, quality[(i + 2) % quality.length]);
        }

        Iterator<Map.Entry<AppleFor, String>> appleIterator = appleMap.entrySet().iterator();
        int printed = 0;
        while (appleIterator.hasNext() && printed < 3) {
            Map.Entry<AppleFor, String> entry = appleIterator.next();
            System.out.println("[CLASSIC APPLE MAP] kind=" + entry.getKey().getKind() + ", quality=" + entry.getValue());
            printed++;
        }

        BananaFor[] bananaKeys = bananaMap.keySet().toArray(new BananaFor[0]);
        for (int i = 0; i < bananaKeys.length && i < 3; i++) {
            BananaFor key = bananaKeys[i];
            System.out.println("[CLASSIC BANANA MAP] kind=" + key.getKind() + ", quality=" + bananaMap.get(key));
        }

        Iterator<Map.Entry<OrangeFor, String>> orangeIterator = orangeMap.entrySet().iterator();
        printed = 0;
        while (orangeIterator.hasNext() && printed < 3) {
            Map.Entry<OrangeFor, String> entry = orangeIterator.next();
            System.out.println("[CLASSIC ORANGE MAP] kind=" + entry.getKey().getKind() + ", quality=" + entry.getValue());
            printed++;
        }

        Map<Sweetness, Integer> orangeTasteCount = new HashMap<>();
        for (int i = 0; i < oranges.size(); i++) {
            Sweetness taste = oranges.get(i).getTaste();
            Integer count = orangeTasteCount.get(taste);
            if (count == null) {
                orangeTasteCount.put(taste, 1);
            } else {
                orangeTasteCount.put(taste, count + 1);
            }
        }

        System.out.println("[CLASSIC] Apple count: " + apples.size());
        System.out.println("[CLASSIC] Banana count: " + bananas.size());
        System.out.println("[CLASSIC] Orange count: " + oranges.size());

        if (!apples.isEmpty()) {
            System.out.println("[CLASSIC] First apple taste: " + apples.get(0).getTaste().getSweetness());
        }
        if (!bananas.isEmpty()) {
            System.out.println("[CLASSIC] First banana taste: " + bananas.get(0).getTaste().getSweetness());
        }
        if (!oranges.isEmpty()) {
            System.out.println("[CLASSIC] First orange taste: " + oranges.get(0).getTaste().getSweetness());
        }

        int limit = Math.min(Math.min(apples.size(), bananas.size()), oranges.size());
        for (int j = 0; j < limit; j++) {
            System.out.println("[CLASSIC] All apples  : " + apples.get(j).getTaste().getSweetness());
            System.out.println("[CLASSIC] All bananas : " + bananas.get(j).getTaste().getSweetness());
            System.out.println("[CLASSIC] All oranges : " + oranges.get(j).getTaste().getSweetness());
        }

        for (Sweetness sweetness : Sweetness.values()) {
            System.out.println("[CLASSIC] " + sweetness.name() + " = " + orangeTasteCount.getOrDefault(sweetness, 0));
        }
    }
}

