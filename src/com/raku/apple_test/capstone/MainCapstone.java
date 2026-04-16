package com.raku.apple_test.capstone;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class MainCapstone {
    public static final @NotNull Zone orchard = new Zone("果樹園");
    public static final @NotNull Zone field = new Zone("畑");

    public static void main(String[] args) throws IOException {
        Path path = Path.of("food_capstone.csv");

        try {
            CsvSupport.loadCSV(path);
            System.out.println("Loaded food capstone.csv of" + ' ' + CsvSupport.size() + ' ' + "items successfully.");
        } catch (IOException e){
            System.out.println("Error reading food_capstone.csv.");
        }

        List<AbstractFood> foods = new ArrayList<>();
        List<SpawnLog> logs = new ArrayList<>();

        // ここは factory 経由の生成。
        // `Catalog.APPLE` には `AppleEntity::new` が登録されているので、
        // `create(...)` を呼ぶと、内部でそのコンストラクタ参照が実行される。
        AppleEntity appleFactory = Catalog.APPLE.create(orchard, "apple");
        appleFactory.setColor("green");
        appleFactory.setSugarBrix(12);
        foods.add(appleFactory);
        logs.add(new SpawnLog(appleFactory.getType().labeledId(), appleFactory.getZone().name(), true));

        BananaEntity bananaFactory = Catalog.BANANA.create(orchard, "banana");
        bananaFactory.setColor("yellow");
        bananaFactory.setSugarBrix(15);
        bananaFactory.setKcal(100);
        foods.add(bananaFactory);
        logs.add(new SpawnLog(bananaFactory.getType().labeledId(), bananaFactory.getZone().name(), true));

        OnionEntity onionFactory = Catalog.ONION.create(field, "onion");
        onionFactory.setKind("shallot");
        onionFactory.setColor("purple");
        onionFactory.setWeight(150);
        foods.add(onionFactory);
        logs.add(new SpawnLog(onionFactory.getType().labeledId(), onionFactory.getZone().name(), true));

        BroccoliEntity broccoliFactory = Catalog.BROCCOLI.create(field, "broccoli");
        broccoliFactory.setVividness("high");
        broccoliFactory.setColor("green");
        broccoliFactory.setSize("M");
        broccoliFactory.setWeight(180);
        foods.add(broccoliFactory);
        logs.add(new SpawnLog(broccoliFactory.getType().labeledId(), broccoliFactory.getZone().name(), true));

        // こちらは比較用の直接 `new`。
        // factory を通さず、コンストラクタをそのまま呼んでいる。
        foods.add(new AppleEntity(orchard, "red", 14, "apple"));
        foods.add(new BananaEntity(orchard, "blue", 11, 90, "banana"));
        foods.add(new OnionEntity(field, "yellow onion", "white", 200, "onion"));
        foods.add(new BroccoliEntity(field, "very high", "green", "L", 220, "broccoli"));

        System.out.println("=== Describe ===");
        foods.forEach(f -> System.out.println(f.describe()));

        System.out.println();
        System.out.println("=== Spawn Logs (factory only) ===");
        logs.forEach(System.out::println);

        Summary summary = Summary.summarize(foods, path);

        System.out.println();
        System.out.println("=== Count By Category ===");
        summary.countByCategory().forEach((k, v) -> System.out.println(k + " -> " + v));

        System.out.println();
        System.out.println("=== Avg Metric By Type (fruit=sugar, vegetable=weight) ===");
        summary.averageMetricByType().forEach((k, v) -> System.out.println(k + " -> " + String.format("%.2f", v)));

        System.out.println();
        System.out.println("csv data lines: " + summary.csvDataLines());

        for (AbstractFood food : foods) {
            CsvSupport.recordFood(food);
        }

        Scanner scanner = new Scanner(System.in);
        CsvSupport.want2save(scanner, path);

        scanner.close();
    }
}

