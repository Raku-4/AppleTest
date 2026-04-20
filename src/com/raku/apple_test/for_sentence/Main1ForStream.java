package com.raku.apple_test.for_sentence;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.LinkedHashMap;

public class Main1ForStream {
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
        Sweetness[] appleTastes = {Sweetness.INSANELY_SWEET, Sweetness.A_LITTLE_SWEET, Sweetness.SWEET, Sweetness.NOT_SWEET, Sweetness.VERY_SWEET};
        Sweetness[] bananaTastes = {Sweetness.VERY_SWEET, Sweetness.SWEET, Sweetness.INSANELY_SWEET, Sweetness.A_LITTLE_SWEET, Sweetness.NOT_SWEET};
        Sweetness[] orangeTastes = {Sweetness.SWEET, Sweetness.NOT_SWEET, Sweetness.A_LITTLE_SWEET, Sweetness.VERY_SWEET, Sweetness.INSANELY_SWEET};
        IntStream.rangeClosed(1, 20).forEach(i -> {
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
        });
        apples.removeIf(apple -> apple.getTaste() == Sweetness.NOT_SWEET);
        IntStream.range(0, apples.size()).forEach(i -> {
            AppleFor apple = apples.get(i);
            apple.setKind(kind[i % kind.length]);
            appleMap.put(apple, quality[(i + 1) % quality.length]);
        });
        IntStream.range(0, bananas.size()).forEach(i -> {
            BananaFor banana = bananas.get(i);
            banana.setKind(kind[(i + apples.size()) % kind.length]);
            bananaMap.put(banana, quality[random.nextInt(quality.length)]);
        });

        IntStream.range(0, oranges.size()).forEach(i -> {
            OrangeFor orange = oranges.get(i);
            orange.setKind(kind[(i + apples.size() + bananas.size()) % kind.length]);
            orangeMap.put(orange, quality[(i + 2) % quality.length]);
        });


        appleMap.entrySet().stream().limit(3)
                .forEach(entry -> System.out.println("[STREAM APPLE MAP] kind=" + entry.getKey().getKind() + ", quality=" + entry.getValue()));
        bananaMap.entrySet().stream().limit(3)
                .forEach(entry -> System.out.println("[STREAM BANANA MAP] kind=" + entry.getKey().getKind() + ", quality=" + entry.getValue()));
        orangeMap.entrySet().stream().limit(3)
                .forEach(entry -> System.out.println("[STREAM ORANGE MAP] kind=" + entry.getKey().getKind() + ", quality=" + entry.getValue()));

        Map<Sweetness, Long> orangeTasteCount = oranges.stream()
                .collect(Collectors.groupingBy(OrangeFor::getTaste, Collectors.counting()));

        System.out.println("[STREAM] Apple count: " + apples.size());
        System.out.println("[STREAM] Banana count: " + bananas.size());
        System.out.println("[STREAM] Orange count: " + oranges.size());
        if (!apples.isEmpty()) System.out.println("[STREAM] First apple taste: " + apples.get(0).getTaste().getSweetness());
        if (!bananas.isEmpty()) System.out.println("[STREAM] First banana taste: " + bananas.get(0).getTaste().getSweetness());
        if (!oranges.isEmpty()) System.out.println("[STREAM] First orange taste: " + oranges.get(0).getTaste().getSweetness());

        IntStream.range(0, Math.min(Math.min(apples.size(), bananas.size()), oranges.size()))
                .forEach(j -> {
                    System.out.println("[STREAM] All apples  : " + apples.get(j).getTaste().getSweetness());
                    System.out.println("[STREAM] All bananas : " + bananas.get(j).getTaste().getSweetness());
                    System.out.println("[STREAM] All oranges : " + oranges.get(j).getTaste().getSweetness());
                });

        for (Sweetness sweetness : Sweetness.values()) {
            System.out.println("[STREAM] " + sweetness.name() + " = " + orangeTasteCount.getOrDefault(sweetness, 0L));
        }
    }
}
