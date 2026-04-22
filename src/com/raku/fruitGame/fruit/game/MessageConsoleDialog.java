package com.raku.fruitGame.fruit.game;

import com.raku.fruitGame.fruit.functionalClass.FruitRecord;
import com.raku.fruitGame.fruit.functionalClass.utility.FruitHistory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * message_icon から開くメッセージフレーム。
 *
 * <p>入力欄とログ欄を持ち、CSV の読み書きや履歴表示などを GUI 上で行えます。</p>
 */
public class MessageConsoleDialog extends JDialog {
    private static final String MESSAGE_FRAME = "assets/fruitGame/textures/misc/message_frame.png";
    private static final Path CSV_PATH = Path.of("fruit_history.csv");

    private static JTextArea outputArea;
    private static JTextField inputField;
    public static String command;
    private static Timer typeTimer;
    private static final Queue<String> pendingSystemLines = new ArrayDeque<>();
    private static @NotNull String currentTypingLine = "";
    private static int currentTypingIndex;
    private static boolean typing;
    private static @Nullable EatSession eatSession;
    private static @Nullable EatNewDraft eatNewDraft;
    private static @NotNull List<BagChoice> bagChoices = List.of();
    private static @NotNull PendingAction pendingAction = PendingAction.NONE;
    private boolean asked = false;

    private enum PendingAction {
        NONE,
        SELECT_BAG_INDEX,
        NEW_MODE,
        NEW_NAME,
        NEW_COLOR,
        NEW_WEIGHT,
        NEW_TASTE,
        NEW_MATURITY,
        NEW_ELAPSED
    }

    private enum EatSource {
        IN_BAG,
        NEW
    }

    private static final class EatSession {
        private final EatSource source;
        private final String fruitName;
        private final String color;
        private final String taste;
        private final FruitStage maturity;
        private long remainingWeight;

        private EatSession(EatSource source, String fruitName, String color, String taste, FruitStage maturity, long remainingWeight) {
            this.source = source;
            this.fruitName = fruitName;
            this.color = color;
            this.taste = taste;
            this.maturity = maturity;
            this.remainingWeight = remainingWeight;
        }

        public String getColor() {
            return color;
        }

        public String getTaste() {
            return taste;
        }

        public FruitStage getMaturity() {
            return maturity;
        }

        public EatSource getSource() {
            return source;
        }
    }

    private static final class EatNewDraft {
        private boolean full;
        private @Nullable String name;
        private String color;
        private long weight;
        private String taste = "おいしい";
        private FruitStage maturity = FruitStage.RIPE;
        private long elapsedSeconds;
    }

    private record BagChoice(int index, String fruitName, String color, long weight, String taste, int quantity) {
    }

    public MessageConsoleDialog(JFrame owner) {
        super(owner, "Message Frame", false);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setUndecorated(true);
        setSize(760, 500);
        setLocationRelativeTo(owner);

        outputArea = createOutputArea();
        inputField = createInputField();
        typeTimer = new Timer(24, ignored -> onTypeTick());

        JPanel root = new MessageFramePanel();
        root.setLayout(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        root.add(scrollPane, BorderLayout.CENTER);
        root.add(inputField, BorderLayout.SOUTH);

        setContentPane(root);

        getRootPane().getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "clear-message-log");
        getRootPane().getActionMap().put("clear-message-log",
                new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearOutputLog();
            }
        });

        appendSystem("message_frame を開きました。help でコマンド一覧を表示します。");
        appendSystem("8x8.json の文字セットに合わせてログを表示します。");
    }

    public void showAteLog(@Nullable String fruitName) {
        setVisible(true);
        appendSystem((fruitName == null ? "アイテム" : fruitName) + "を食べた！");
    }

    public void appendExternalSystemLog(@Nullable String message) {
        appendSystem(message == null ? "" : message);
    }

    public @NotNull JTextArea createOutputArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setForeground(new Color(245, 245, 245));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        return area;
    }

    public @NotNull JTextField createInputField() {
        JTextField field = new JTextField();
        field.setOpaque(true);
        field.setBackground(new Color(255, 255, 255, 230));
        field.setForeground(new Color(20, 20, 20));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 120, 120)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        field.addActionListener(this::onSubmit);
        field.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "clear-input");
        field.getActionMap().put("clear-input", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearOutputLog();
            }
        });
        return field;
    }

    public void onSubmit(ActionEvent event) {
        String raw = inputField.getText();
        if (raw == null) {
            return;
        }
        command = raw.trim();
        if (command.isEmpty()) {
            return;
        }

        appendUser(command);
        inputField.setText("");
        executeCommand(command);
    }

    public void executeCommand(@NotNull String command) {
        if (handleEatPending(command)) {
            return;
        }

        if (eatSession != null && command.equalsIgnoreCase("cancel")) {
            addFertilizerAndCancelSession();
            return;
        }

        if (eatSession != null && isStrictInteger(command)) {
            eatCurrentFruit(Long.parseLong(command));
            return;
        }

        String[] parts = command.split("\\s+");
        String op = parts[0].toLowerCase();

        try {
            switch (op) {
                case "help" -> {
                    appendSystem("help: コマンド一覧");
                    appendSystem("list <name> : 指定果物の履歴表示");
                    appendSystem("listall : 全履歴表示");
                    appendSystem("add <name> <color> <weight> : 履歴1件追加");
                    appendSystem("addfull <name> <color> <weight> <taste> <maturity> <elapsedSec> [treeId]");
                    appendSystem("eat /in_bag : バッグから食べる果物を選択");
                    appendSystem("eat /new : 新しく果物を作って食べる");
                    appendSystem("cancel : eat中断(肥料追加)");
                    appendSystem("exit : message_frame を閉じる");
                    appendSystem("loadCsv : fruit_history.csv 読み込み");
                    appendSystem("saveCsv : fruit_history.csv 保存");
                    appendSystem("resetCsv : fruit_history.csv リセット");
                    appendSystem("clear : ログ消去");
                }
                case "clear" -> outputArea.setText("");
                case "exit" -> setVisible(false);
                case "list" -> {
                    if (parts.length < 2) {
                        appendError("使い方: list <name>");
                        return;
                    }
                    String fruitName = parts[1];
                    List<FruitRecord> list = FruitHistory.getHistoryView(fruitName);
                    if (list.isEmpty()) {
                        appendSystem("履歴なし: " + fruitName);
                        return;
                    }
                    appendSystem("-- " + fruitName + " の履歴 --");
                    for (FruitRecord record : list) {
                        appendSystem(record.fruitName() + " / " + record.color() + " / " + record.weight() + "g"
                                + " / " + record.maturity() + " / tree=" + record.treeId());
                    }
                }
                case "listall" -> {
                    Map<String, List<FruitRecord>> all = FruitHistory.viewAll();
                    if (all.isEmpty()) {
                        appendSystem("履歴は空です。");
                        return;
                    }
                    appendSystem("-- 全履歴 --");
                    for (Map.Entry<String, List<FruitRecord>> entry : all.entrySet()) {
                        appendSystem("[" + entry.getKey() + "]");
                        for (FruitRecord record : entry.getValue()) {
                            appendSystem("  " + record.color() + " / " + record.weight() + "g / "
                                    + record.maturity() + " / tree=" + record.treeId());
                        }
                    }
                }
                case "add" -> {
                    if (parts.length < 4) {
                        appendError("使い方: add <name> <color> <weight>");
                        return;
                    }
                    String name = parts[1];
                    if (isFertilizer(name)) {
                        appendError("肥料は add/addfull では生成できません。cancel で追加されます。");
                        return;
                    }
                    String color = parts[2];
                    long weight = Long.parseLong(parts[3]);
                    FruitHistory.recordCreation(name, color, weight);
                    persistCsvQuietly();
                    appendSystem("追加しました: " + name + ", " + color + ", " + weight + "g");
                }
                case "addfull" -> {
                    if (parts.length < 7) {
                        appendError("使い方: addfull <name> <color> <weight> <taste> <maturity> <elapsedSec> [treeId]");
                        return;
                    }
                    String name = parts[1];
                    if (isFertilizer(name)) {
                        appendError("肥料は add/addfull では生成できません。cancel で追加されます。");
                        return;
                    }
                    String color = parts[2];
                    long weight = Long.parseLong(parts[3]);
                    String taste = parts[4];
                    FruitStage maturity  = FruitStage.fromString(parts[5]);
                    long elapsedSec = Long.parseLong(parts[6]);
                    int treeId = parts.length >= 8 ? Integer.parseInt(parts[7]) : -1;
                    FruitHistory.recordCreation(name, color, weight, taste, maturity, elapsedSec, treeId);
                    persistCsvQuietly();
                    appendSystem("追加しました: " + name + " / " + maturity + " / tree=" + treeId);
                }
                case "eat" -> {
                    if (eatSession != null) {
                        appendError("いまは " + eatSession.fruitName + " を食べています。食べきるか cancel してください。");
                        return;
                    }
                    if (parts.length < 2) {
                        appendError("使い方: eat /in_bag または eat /new");
                        return;
                    }
                    if ("/in_bag".equalsIgnoreCase(parts[1])) {
                        beginEatFromBag();
                    } else if ("/new".equalsIgnoreCase(parts[1])) {
                        beginEatNewWizard();
                    } else {
                        appendError("使い方: eat /in_bag または eat /new");
                    }
                }
                case "loadcsv" -> {
                    FruitHistory.loadCsv(CSV_PATH);
                    appendSystem("読み込み完了: " + CSV_PATH.toAbsolutePath());
                }
                case "savecsv" -> {
                    FruitHistory.saveCsv(CSV_PATH);
                    appendSystem("保存完了: " + CSV_PATH.toAbsolutePath());
                }
                case "resetcsv" -> {
                    appendSystem("本当にリセットしますか。リセットする場合には、Yes を入力してください。");
                    asked = true;
                    createInputField();
                }
                case "yes" -> {
                    if (asked) {
                        FruitHistory.resetCsv(CSV_PATH);
                        appendSystem("リセットを完了しました。: " + CSV_PATH.toAbsolutePath());
                    }
                    else {appendError("不明なコマンドです: " + "yes" + "(help 参照)");}
                }
                default -> {
                    appendError("不明なコマンドです: " + op + " (help 参照)");
                    if (asked) {
                        if (!Objects.equals(command, "resetcsv")) {
                            asked = false;
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            appendError("weight は整数で入力してください。");
        } catch (IOException e) {
            appendError("CSV 処理エラー: " + e.getMessage());
        }
    }

    private static boolean handleEatPending(@NotNull String command) {
        if (pendingAction == PendingAction.NONE) {
            return false;
        }
        switch (pendingAction) {
            case SELECT_BAG_INDEX -> {
                if (!isStrictInteger(command)) {
                    return false;
                }
                int index = Integer.parseInt(command);
                for (BagChoice choice : bagChoices) {
                    if (choice.index == index) {
                        if (isFertilizer(choice.fruitName)) {
                            appendError("肥料は食べられません。別の番号を選んでください。");
                            return true;
                        }
                        eatSession = new EatSession(EatSource.IN_BAG, choice.fruitName, choice.color, choice.taste, FruitStage.RIPE, choice.weight);
                        pendingAction = PendingAction.NONE;
                        appendSystem(choice.fruitName + " を食べ始めました。数値(g)を入力してください。cancel で中断できます。");
                        return true;
                    }
                }
                appendError("有効な番号を入力してください。");
                return true;
            }
            case NEW_MODE -> {
                if (command.equalsIgnoreCase("add")) {
                    eatNewDraft = new EatNewDraft();
                    eatNewDraft.full = false;
                    pendingAction = PendingAction.NEW_NAME;
                    appendSystem("新規果物名を入力してください。");
                    return true;
                }
                if (command.equalsIgnoreCase("addfull")) {
                    eatNewDraft = new EatNewDraft();
                    eatNewDraft.full = true;
                    pendingAction = PendingAction.NEW_NAME;
                    appendSystem("新規果物名を入力してください。");
                    return true;
                }
                appendError("add または addfull を入力してください。");
                return true;
            }
            case NEW_NAME -> {
                if (isFertilizer(command)) {
                    appendError("肥料は eat /new では作れません。");
                    return true;
                }
                Objects.requireNonNull(eatNewDraft).name = command;
                pendingAction = PendingAction.NEW_COLOR;
                appendSystem("色を入力してください。");
                return true;
            }
            case NEW_COLOR -> {
                Objects.requireNonNull(eatNewDraft).color = command;
                pendingAction = PendingAction.NEW_WEIGHT;
                appendSystem("重さ(g)を入力してください。");
                return true;
            }
            case NEW_WEIGHT -> {
                if (!isStrictInteger(command)) {
                    appendError("重さは整数で入力してください。");
                    return true;
                }
                Objects.requireNonNull(eatNewDraft).weight = Long.parseLong(command);
                if (eatNewDraft.weight <= 0L) {
                    appendError("重さは1以上で入力してください。");
                    return true;
                }
                if (!eatNewDraft.full) {
                    finishNewEatDraft();
                    return true;
                }
                pendingAction = PendingAction.NEW_TASTE;
                appendSystem("味を入力してください。");
                return true;
            }
            case NEW_TASTE -> {
                Objects.requireNonNull(eatNewDraft).taste = command;
                pendingAction = PendingAction.NEW_MATURITY;
                appendSystem("熟成度を入力してください。");
                return true;
            }
            case NEW_MATURITY -> {
                Objects.requireNonNull(eatNewDraft).maturity = command.transform(FruitStage::fromString);
                pendingAction = PendingAction.NEW_ELAPSED;
                appendSystem("経過秒(elapsedSec)を入力してください。");
                return true;
            }
            case NEW_ELAPSED -> {
                if (!isStrictInteger(command)) {
                    appendError("elapsedSec は整数で入力してください。");
                    return true;
                }
                Objects.requireNonNull(eatNewDraft).elapsedSeconds = Long.parseLong(command);
                finishNewEatDraft();
                return true;
            }
        }
        return false;
    }

    private static void beginEatFromBag() {
        bagChoices = collectBagChoices();
        if (bagChoices.isEmpty()) {
            appendSystem("バッグに食べられる果物がありません。");
            return;
        }
        appendSystem("-- バッグ一覧 --");
        for (BagChoice choice : bagChoices) {
            appendSystem("[" + choice.index + "] " + choice.fruitName + " / x" + choice.quantity + " / "
                    + choice.color + " / " + choice.weight + "g / " + choice.taste);
        }
        appendSystem("食べたい番号を入力してください。");
        pendingAction = PendingAction.SELECT_BAG_INDEX;
    }

    private static void beginEatNewWizard() {
        eatNewDraft = null;
        pendingAction = PendingAction.NEW_MODE;
        appendSystem("add か addfull を入力してください。(eat /new 用)");
    }

    private static void finishNewEatDraft() {
        if (eatNewDraft == null) {
            pendingAction = PendingAction.NONE;
            return;
        }
        FruitHistory.recordCreation(
                eatNewDraft.name,
                eatNewDraft.color,
                eatNewDraft.weight,
                eatNewDraft.taste,
                eatNewDraft.maturity,
                eatNewDraft.elapsedSeconds,
                -1
        );
        persistCsvQuietly();

        eatSession = new EatSession(
                EatSource.NEW,
                eatNewDraft.name,
                eatNewDraft.color,
                eatNewDraft.taste,
                eatNewDraft.maturity,
                eatNewDraft.weight
        );
        pendingAction = PendingAction.NONE;
        appendSystem(eatNewDraft.name + " を作成して食べ始めました。数値(g)を入力してください。cancel で中断できます。");
    }

    private static void eatCurrentFruit(long amount) {
        if (eatSession == null) {
            appendError("先に eat /in_bag または eat /new を実行してください。");
            return;
        }
        if (amount <= 0L) {
            appendError("食べる量は1以上で入力してください。");
            return;
        }

        eatSession.remainingWeight -= amount;
        if (eatSession.remainingWeight <= 0L) {
            FruitHistory.removeLatestBagRecord(eatSession.fruitName);
            persistCsvQuietly();
            appendSystem(eatSession.fruitName + " を食べきりました。バッグから削除しました。");
            eatSession = null;
            return;
        }

        FruitRecord latest = FruitHistory.findLatestBagRecord(eatSession.fruitName);
        if (latest != null) {
            FruitHistory.replaceLatestBagRecord(
                    eatSession.fruitName,
                    new FruitRecord(
                            latest.fruitName(),
                            latest.color(),
                            eatSession.remainingWeight,
                            latest.taste(),
                            latest.maturity(),
                            latest.elapsedSeconds(),
                            latest.treeId()
                    )
            );
            persistCsvQuietly();
        }
        appendSystem(eatSession.fruitName + " の残りは " + eatSession.remainingWeight + "g です。");
    }

    private static void addFertilizerAndCancelSession() {
        FruitHistory.recordCreation("肥料", "茶色", 1L, "不明", FruitStage.EMPTY, 0L, -1);
        persistCsvQuietly();
        appendSystem("eat を中断しました。バッグに肥料を追加しました。");
        eatSession = null;
        pendingAction = PendingAction.NONE;
        eatNewDraft = null;
        bagChoices = List.of();
    }

    private static @NotNull List<BagChoice> collectBagChoices() {
        List<BagChoice> out = new ArrayList<>();
        int idx = 0;
        for (Map.Entry<String, List<FruitRecord>> entry : FruitHistory.viewAll().entrySet()) {
            String fruitName = entry.getKey();
            List<FruitRecord> list = entry.getValue();
            int qty = 0;
            FruitRecord latest = null;
            for (FruitRecord record : list) {
                if (record.treeId() < 0) {
                    qty++;
                    latest = record;
                }
            }
            if (qty <= 0) {
                continue;
            }
            out.add(new BagChoice(idx++, fruitName, latest.color(), latest.weight(), latest.taste(), qty));
        }
        return out;
    }

    private static boolean isStrictInteger(@Nullable String text) {
        if (text == null) {
            return false;
        }
        return text.matches("-?\\d+");
    }

    private static boolean isFertilizer(@Nullable String name) {
        if (name == null) {
            return false;
        }
        String n = name.toLowerCase();
        return n.contains("肥料") || n.contains("fertilizer");
    }

    private static void persistCsvQuietly() {
        try {
            FruitHistory.saveCsv(CSV_PATH);
        } catch (IOException ignored) {
            // GUI操作の継続を優先
        }
    }

    private static void appendUser(String text) {
        appendLineImmediate("[you] " + text);
    }

    public static void appendSystem(String text) {
        enqueueSystemLine("[system] " + text);
    }

    private static void appendError(String text) {
        appendLineImmediate("[error] " + text);
    }

    private static void appendLineImmediate(@NotNull String text) {
        String filtered = PixelFont8x8.filter(text);
        outputArea.append(filtered + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private static void enqueueSystemLine(@NotNull String text) {
        pendingSystemLines.offer(PixelFont8x8.filter(text));
        startTypingIfNeeded();
    }

    private static void startTypingIfNeeded() {
        if (typing) {
            return;
        }
        String line = pendingSystemLines.poll();
        if (line == null) {
            return;
        }
        typing = true;
        currentTypingLine = line;
        currentTypingIndex = 0;
        typeTimer.start();
    }

    private void onTypeTick() {
        if (!typing) {
            typeTimer.stop();
            return;
        }

        if (currentTypingIndex < currentTypingLine.length()) {
            char ch = currentTypingLine.charAt(currentTypingIndex++);
            SoundPlayer.playTypeTick(ch);
            outputArea.append(String.valueOf(ch));
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
            return;
        }

        outputArea.append("\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
        typing = false;
        if (pendingSystemLines.isEmpty()) {
            typeTimer.stop();
        } else {
            startTypingIfNeeded();
        }
    }

    private void clearOutputLog() {
        outputArea.setText("");
        pendingSystemLines.clear();
        typing = false;
        currentTypingLine = "";
        currentTypingIndex = 0;
        typeTimer.stop();
    }

    private static class MessageFramePanel extends JPanel {
        private final Image frameImage;

        private MessageFramePanel() {
            this.frameImage = AssetImageLoader.load(MESSAGE_FRAME);
            setOpaque(true);
            setBackground(new Color(245, 245, 245));
        }

        @Override
        protected void paintComponent(@NotNull Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            if (frameImage != null) {
                g2.drawImage(frameImage, 0, 0, getWidth(), getHeight(), this);
            }

            g2.dispose();
        }

        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull Dimension getPreferredSize() {
            return new Dimension(760, 500);
        }
    }

    /**
     * 8x8.json の map 文字集合を使って表示文字をフィルタする簡易クラス。
     */
    private static final class PixelFont8x8 {
        private static final Set<Character> ALLOWED = loadMapChars();

        private PixelFont8x8() {
        }

        static @NotNull String filter(@NotNull String text) {
            StringBuilder out = new StringBuilder(text.length());
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (ch == '\n' || ch == '\r' || ch == '\t' || ch == ' ' || ALLOWED.contains(ch)) {
                    out.append(ch);
                } else {
                    // 8x8未定義文字でも可読性を優先して原文を表示する。
                    out.append(ch);
                }
            }
            return out.toString();
        }

        private static @NotNull Set<Character> loadMapChars() {
            Set<Character> set = new HashSet<>();
            String path = "assets/fruitGame/textures/characters/8x8.json";
            try (InputStream stream = openFontJsonStream(path)) {
                if (stream == null) {
                    return set;
                }
                String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                Matcher mapBlock = Pattern.compile("\"map\"\\s*:\\s*\\[(.*?)]", Pattern.DOTALL).matcher(json);
                if (!mapBlock.find()) {
                    return set;
                }

                String inside = mapBlock.group(1);
                Matcher rows = Pattern.compile("\"(.*?)\"").matcher(inside);
                while (rows.find()) {
                    String row = rows.group(1);
                    for (int i = 0; i < row.length(); i++) {
                        set.add(row.charAt(i));
                    }
                }
            } catch (IOException ignored) {
                // 読み込み失敗時は空集合（フィルタは '?' 優先）
            }
            return set;
        }

        private static @Nullable InputStream openFontJsonStream(@NotNull String resourcePath) throws IOException {
            InputStream classpath = MessageConsoleDialog.class.getClassLoader().getResourceAsStream(resourcePath);
            if (classpath != null) {
                return classpath;
            }
            Path file = Path.of("src", "resources", resourcePath.replace("/", File.separator));
            if (Files.exists(file)) {
                return Files.newInputStream(file);
            }
            return null;
        }
    }
}

