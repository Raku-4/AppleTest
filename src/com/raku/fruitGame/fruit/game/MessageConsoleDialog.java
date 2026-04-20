package com.raku.fruitGame.fruit.game;

import com.raku.fruitGame.fruit.functionalClass.FruitRecord;
import com.raku.fruitGame.fruit.functionalClass.utility.FruitHistory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * message_icon から開くメッセージフレーム。
 *
 * <p>入力欄とログ欄を持ち、CSV の読み書きや履歴表示などを GUI 上で行えます。</p>
 */
public class MessageConsoleDialog extends JDialog {
    private static final String MESSAGE_FRAME = "assets/fruitGame/textures/misc/message_frame.png";
    private static final Path CSV_PATH = Path.of("fruit_history.csv");

    private final JTextArea outputArea;
    private final JTextField inputField;

    public MessageConsoleDialog(JFrame owner) {
        super(owner, "Message Frame", false);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setSize(760, 500);
        setLocationRelativeTo(owner);

        outputArea = createOutputArea();
        inputField = createInputField();

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

        appendSystem("message_frame を開きました。help でコマンド一覧を表示します。");
        appendSystem("8x8.json の文字セットに合わせてログを表示します。");
    }

    private @NotNull JTextArea createOutputArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setForeground(new Color(32, 32, 32));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        return area;
    }

    private @NotNull JTextField createInputField() {
        JTextField field = new JTextField();
        field.setOpaque(true);
        field.setBackground(new Color(255, 255, 255, 230));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 120, 120)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        field.addActionListener(this::onSubmit);
        field.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "clear-input");
        field.getActionMap().put("clear-input", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                field.setText("");
            }
        });
        return field;
    }

    private void onSubmit(ActionEvent event) {
        String raw = inputField.getText();
        if (raw == null) {
            return;
        }
        String command = raw.trim();
        if (command.isEmpty()) {
            return;
        }

        appendUser(command);
        inputField.setText("");
        executeCommand(command);
    }

    private void executeCommand(@NotNull String command) {
        String[] parts = command.split("\\s+");
        String op = parts[0].toLowerCase();

        try {
            switch (op) {
                case "help" -> {
                    appendSystem("help: コマンド一覧");
                    appendSystem("list <name> : 指定果物の履歴表示");
                    appendSystem("listall : 全履歴表示");
                    appendSystem("add <name> <color> <weight> : 履歴1件追加");
                    appendSystem("loadcsv : fruit_history.csv 読み込み");
                    appendSystem("savecsv : fruit_history.csv 保存");
                    appendSystem("clear : ログ消去");
                }
                case "clear" -> outputArea.setText("");
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
                        appendSystem(record.fruitName() + " / " + record.color() + " / " + record.weight() + "g");
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
                            appendSystem("  " + record.color() + " / " + record.weight() + "g");
                        }
                    }
                }
                case "add" -> {
                    if (parts.length < 4) {
                        appendError("使い方: add <name> <color> <weight>");
                        return;
                    }
                    String name = parts[1];
                    String color = parts[2];
                    long weight = Long.parseLong(parts[3]);
                    FruitHistory.recordCreation(name, color, weight);
                    appendSystem("追加しました: " + name + ", " + color + ", " + weight + "g");
                }
                case "loadcsv" -> {
                    FruitHistory.loadCsv(CSV_PATH);
                    appendSystem("読み込み完了: " + CSV_PATH.toAbsolutePath());
                }
                case "savecsv" -> {
                    FruitHistory.saveCsv(CSV_PATH);
                    appendSystem("保存完了: " + CSV_PATH.toAbsolutePath());
                }
                default -> appendError("不明なコマンドです: " + op + " (help 参照)");
            }
        } catch (NumberFormatException e) {
            appendError("weight は整数で入力してください。");
        } catch (IOException e) {
            appendError("CSV 処理エラー: " + e.getMessage());
        }
    }

    private void appendUser(String text) {
        appendLine("[you] " + text);
    }

    private void appendSystem(String text) {
        appendLine("[system] " + text);
    }

    private void appendError(String text) {
        appendLine("[error] " + text);
    }

    private void appendLine(String text) {
        String filtered = PixelFont8x8.filter(text);
        outputArea.append(filtered + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private static class MessageFramePanel extends JPanel {
        private final Image frameImage;

        private MessageFramePanel() {
            this.frameImage = AssetImageLoader.load(MESSAGE_FRAME);
            setOpaque(true);
            setBackground(new Color(245, 245, 245));
        }

        @Override
        protected void paintComponent(Graphics g) {
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
                    out.append('?');
                }
            }
            return out.toString();
        }

        private static Set<Character> loadMapChars() {
            Set<Character> set = new HashSet<>();
            String path = "assets/fruitGame/textures/characters/8x8.json";
            try (InputStream stream = MessageConsoleDialog.class.getClassLoader().getResourceAsStream(path)) {
                if (stream == null) {
                    return set;
                }
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    String line;
                    boolean inMap = false;
                    while ((line = reader.readLine()) != null) {
                        String trimmed = line.trim();
                        if (trimmed.startsWith("\"map\"")) {
                            inMap = true;
                            continue;
                        }
                        if (!inMap) {
                            continue;
                        }
                        if (trimmed.startsWith("]")) {
                            break;
                        }
                        int first = trimmed.indexOf('"');
                        int last = trimmed.lastIndexOf('"');
                        if (first >= 0 && last > first) {
                            String mapRow = trimmed.substring(first + 1, last);
                            for (int i = 0; i < mapRow.length(); i++) {
                                set.add(mapRow.charAt(i));
                            }
                        }
                    }
                }
            } catch (IOException ignored) {
                // 読み込み失敗時は空集合（フィルタは '?' 優先）
            }
            return set;
        }
    }
}

