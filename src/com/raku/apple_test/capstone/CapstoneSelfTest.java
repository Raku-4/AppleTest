package com.raku.apple_test.capstone;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CapstoneSelfTest {
    public static void main(String[] args) {
        Zone orchard = new Zone("test-orchard");
        Zone field = new Zone("test-field");

        AppleEntity apple = Catalog.APPLE.create(orchard, "apple");
        apple.setSugarBrix(12);
        BananaEntity banana = Catalog.BANANA.create(orchard, "banana");
        banana.setSugarBrix(15);
        OnionEntity onion = Catalog.ONION.create(field, "onion");
        onion.setWeight(150);
        BroccoliEntity broccoli = Catalog.BROCCOLI.create(field, "broccoli");
        broccoli.setWeight(180);

        assertEquals("apple", Catalog.APPLE.id(), "apple id");
        assertEquals("[F]apple", apple.getType().labeledId(), "apple labeledId");
        assertEquals("[V]onion", onion.getType().labeledId(), "onion labeledId");

        List<AbstractFood> foods = List.of(apple, banana, onion, broccoli);
        Summary summary = Summary.summarize(foods, createTempCsv());

        foods.forEach(CsvSupport::recordFood);
        CsvSupport.recordFood(apple);
        assertEquals(4, CsvSupport.size(), "deduplicated history size");

        Path roundTripCsv = createRoundTripCsv();
        try {
            CsvSupport.SaveCSV(roundTripCsv);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("failed to save csv", e);
        }

        try {
            CsvSupport.loadCSV(roundTripCsv);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("failed to load csv", e);
        }

        long fruitExpected = 2L;
        long vegetableExpected = 2L;
        assertEqualsLong(fruitExpected, summary.countByCategory().get(FoodCategory.FRUIT), "fruit count");
        assertEqualsLong(vegetableExpected, summary.countByCategory().get(FoodCategory.VEGETABLE), "vegetable count");
        assertEquals(12.0, summary.averageMetricByType().get("[F]apple"), "apple metric");
        assertEquals(15.0, summary.averageMetricByType().get("[F]banana"), "banana metric");
        assertEquals(150.0, summary.averageMetricByType().get("[V]onion"), "onion metric");
        assertEquals(180.0, summary.averageMetricByType().get("[V]broccoli"), "broccoli metric");
        assertEquals(2, summary.csvDataLines(), "csv data lines");
        assertEquals(4, CsvSupport.size(), "saved history size");
        assertEquals(4, CsvSupport.size(), "reloaded history size");
        assertEquals(1, CsvSupport.getFoods("apple").size(), "apple reload count");
        assertEquals(4, CsvSupport.getFoods().size(), "reloaded map size");

        System.out.println("CapstoneSelfTest passed");
    }

    private static @NotNull Path createTempCsv() {
        try {
            Path temp = Files.createTempFile("capstone-self-test", ".csv");
            Files.write(temp, List.of(
                    "timestamp,foodName,labeledId,zone",
                    "2026-04-12T00:00:00,[F]apple,[F]apple,test-orchard",
                    "2026-04-12T00:00:00,[V]onion,[V]onion,test-field"
            ));
            temp.toFile().deleteOnExit();
            return temp;
        } catch (java.io.IOException e) {
            throw new IllegalStateException("failed to create temp csv", e);
        }
    }

    private static @NotNull Path createRoundTripCsv() {
        try {
            Path temp = Files.createTempFile("capstone-roundtrip", ".csv");
            temp.toFile().deleteOnExit();
            return temp;
        } catch (java.io.IOException e) {
            throw new IllegalStateException("failed to create temp csv", e);
        }
    }

    private static void assertEquals(@NotNull String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected=" + expected + " actual=" + actual);
        }
    }

    private static void assertEqualsLong(long expected, @NotNull Long actual, String label) {
        if (actual != expected) {
            throw new AssertionError(label + " expected=" + expected + " actual=" + actual);
        }
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected=" + expected + " actual=" + actual);
        }
    }

    private static void assertEquals(double expected, @NotNull Double actual, String label) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(label + " expected=" + expected + " actual=" + actual);
        }
    }
}