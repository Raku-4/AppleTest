package com.raku.fruitGame.fruit.game;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * バッグアイコンから開くインベントリ画面。
 *
 * <p>CSV を読み込んでアイテム一覧を作り、3x5 (15個) 単位でページ表示します。</p>
 */
public class InventoryDialog extends JDialog {
    private static final String INVENTORY_BG = "assets/fruitGame/textures/misc/inventory.png";
    private static final String RIGHT_ARROW = "assets/fruitGame/textures/misc/right_arrow.png";

    private final InventoryPanel inventoryPanel;
    private final JButton nextButton;
    private final JButton prevButton;
    private final Consumer<FruitState> confirmCallback;

    private final List<FruitState> items = new ArrayList<>();
    private int page;
    private int selectedIndex = -1;

    public InventoryDialog(JFrame owner, BiConsumer<String, Image> confirmCallback) {
        this(owner, state -> {
            if (confirmCallback != null) {
                confirmCallback.accept(state.name(), state.icon());
            }
        });
    }

    public InventoryDialog(JFrame owner, Consumer<FruitState> confirmCallback) {
        super(owner, "Inventory", false);
        this.confirmCallback = confirmCallback;
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setSize(1000, 760);
        setLocationRelativeTo(owner);

        inventoryPanel = new InventoryPanel();

        prevButton = createArrowButton(false);
        prevButton.addActionListener(ignored -> {
            if (page > 0) {
                page--;
                refreshControls();
                inventoryPanel.repaint();
            }
        });

        nextButton = createArrowButton(true);
        nextButton.addActionListener(ignored -> {
            if (page < maxPage()) {
                page++;
                refreshControls();
                inventoryPanel.repaint();
            }
        });

        JPanel arrowPanel = new JPanel();
        arrowPanel.setOpaque(false);
        arrowPanel.add(prevButton);
        arrowPanel.add(nextButton);

        setLayout(new BorderLayout());
        add(inventoryPanel, BorderLayout.CENTER);
        add(arrowPanel, BorderLayout.SOUTH);

        getRootPane().getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "close-inventory");
        getRootPane().getActionMap().put("close-inventory", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmSelection();
                setVisible(false);
            }
        });
    }

    public void open() {
        loadItemsFromCsv();
        page = 0;
        selectedIndex = -1;
        refreshControls();
        setVisible(true);
    }

    private void loadItemsFromCsv() {
        items.clear();
        items.addAll(FruitStateFactory.createInventoryStates());
    }

    private int maxPage() {
        if (items.isEmpty()) {
            return 0;
        }
        return (items.size() - 1) / 15;
    }

    private void refreshControls() {
        prevButton.setEnabled(page > 0);
        nextButton.setEnabled(page < maxPage());
    }

    private void confirmSelection() {
        if (confirmCallback == null || selectedIndex < 0 || selectedIndex >= items.size()) {
            return;
        }
        confirmCallback.accept(items.get(selectedIndex));
    }

    private @NotNull JButton createArrowButton(boolean right) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(48, 48));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        Image base = AssetImageLoader.load(RIGHT_ARROW);
        if (base != null) {
            Image iconImage = right ? base : flipHorizontally(base);
            button.setIcon(new ImageIcon(iconImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        } else {
            button.setText(right ? ">" : "<");
        }
        return button;
    }

    private static @NotNull Image flipHorizontally(@NotNull Image image) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        if (w <= 0 || h <= 0) {
            return image;
        }
        BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = src.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D og = out.createGraphics();
        og.drawImage(src, w, 0, -w, h, null);
        og.dispose();
        return out;
    }

    private static @NotNull String resolveFruitIconPath(@NotNull String fruitName) {
        String key = fruitName.toLowerCase(Locale.ROOT);
        if (key.contains("banana") || key.contains("ばなな")) {
            return "assets/fruitGame/textures/fruits/ripe_banana.png";
        }
        if (key.contains("orange") || key.contains("おれんじ") || key.contains("オレンジ")) {
            return "assets/fruitGame/textures/fruits/ripe_orange.png";
        }
        if (key.contains("grape") || key.contains("ぶどう") || key.contains("ブドウ")) {
            return "assets/fruitGame/textures/fruits/ripe_grape.png";
        }
        // 対応がない場合はリンゴで代用
        return "assets/fruitGame/textures/fruits/ripe_apple.png";
    }

    private final class InventoryPanel extends JPanel {
        private final Image inventoryImage;

        private int hoverIndex = -1;
        private long hoverStartedAt;
        private int mouseX;
        private int mouseY;

        private InventoryPanel() {
            this.inventoryImage = AssetImageLoader.load(INVENTORY_BG);
            setOpaque(true);
            setBackground(new Color(20, 20, 20));
            setFocusable(true);

            MouseAdapter mouse = new MouseAdapter() {
                @Override
                public void mouseMoved(@NotNull MouseEvent e) {
                    updateHover(e.getX(), e.getY());
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    selectItem(e.getX(), e.getY());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hoverIndex = -1;
                    repaint();
                }
            };
            addMouseMotionListener(mouse);
            addMouseListener(mouse);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelW = getWidth();
            int panelH = getHeight();
            int invW = Math.max(500, panelW * 3 / 4);
            int invH = Math.max(500, panelH * 3 / 4);
            int invX = (panelW - invW) / 2;
            int invY = (panelH - invH) / 2;

            if (inventoryImage != null) {
                g2.drawImage(inventoryImage, invX, invY, invW, invH, this);
            } else {
                g2.setColor(new Color(55, 55, 55));
                g2.fill(new RoundRectangle2D.Double(invX, invY, invW, invH, 16, 16));
            }

            int cols = 5;
            int rows = 3;
            int cellW = invW / cols;
            int cellH = invH / rows;

            int start = page * 15;
            int end = Math.min(start + 15, items.size());
            for (int i = start; i < end; i++) {
                int local = i - start;
                int col = local % cols;
                int row = local / cols;

                int cx = invX + col * cellW;
                int cy = invY + row * cellH;

                drawItemCell(g2, cx, cy, cellW, cellH, items.get(i), i == selectedIndex);
            }

            drawPageLabel(g2, panelW, invY + invH + 24);
            drawHoverTooltip(g2);

            g2.dispose();
        }

        private void drawItemCell(@NotNull Graphics2D g2, int x, int y, int w, int h, @NotNull FruitState item, boolean selected) {
            g2.setColor(selected ? new Color(255, 215, 0, 40) : new Color(0, 0, 0, 20));
            g2.fillRoundRect(x + 3, y + 3, w - 6, h - 6, 10, 10);
            g2.setColor(selected ? new Color(255, 215, 0) : new Color(0, 0, 0, 20));
            g2.drawRoundRect(x + 3, y + 3, w - 6, h - 6, 10, 10);

            int iconW = Math.max(28, w / 3);
            int iconH = Math.max(28, h / 3);
            int iconX = x + (w - iconW) / 2;
            int iconY = y + 10;

            if (item.icon() != null) {
                g2.drawImage(item.icon(), iconX, iconY, iconW, iconH, this);
            }

            g2.setColor(new Color(30, 30, 30));
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            String qty = "x" + item.quantity();
            g2.drawString(qty, x + w - 30, y + h - 12);
        }

        private void drawPageLabel(@NotNull Graphics2D g2, int panelW, int y) {
            g2.setColor(new Color(230, 230, 230));
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            String text = "Page " + (page + 1) + " / " + (maxPage() + 1);
            int textW = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, (panelW - textW) / 2, y);
        }

        private void updateHover(int mx, int my) {
            this.mouseX = mx;
            this.mouseY = my;

            int panelW = getWidth();
            int panelH = getHeight();
            int invW = Math.max(500, panelW * 3 / 4);
            int invH = Math.max(500, panelH * 3 / 4);
            int invX = (panelW - invW) / 2;
            int invY = (panelH - invH) / 2;

            int cols = 5;
            int rows = 3;
            int cellW = invW / cols;
            int cellH = invH / rows;

            int col = (mx - invX) / cellW;
            int row = (my - invY) / cellH;

            int index = -1;
            if (col >= 0 && col < cols && row >= 0 && row < rows) {
                int local = row * cols + col;
                int global = page * 15 + local;
                if (global < items.size()) {
                    index = global;
                }
            }

            if (index != hoverIndex) {
                hoverIndex = index;
                hoverStartedAt = System.currentTimeMillis();
            }
            repaint();
        }

        private void selectItem(int mx, int my) {
            int panelW = getWidth();
            int panelH = getHeight();
            int invW = Math.max(500, panelW * 3 / 4);
            int invH = Math.max(500, panelH * 3 / 4);
            int invX = (panelW - invW) / 2;
            int invY = (panelH - invH) / 2;

            int cols = 5;
            int rows = 3;
            int cellW = invW / cols;
            int cellH = invH / rows;

            int col = (mx - invX) / cellW;
            int row = (my - invY) / cellH;
            int index = -1;
            if (col >= 0 && col < cols && row >= 0 && row < rows) {
                int local = row * cols + col;
                int global = page * 15 + local;
                if (global < items.size()) {
                    index = global;
                }
            }
            selectedIndex = index;
            repaint();
        }

        private void drawHoverTooltip(Graphics2D g2) {
            if (hoverIndex < 0 || hoverIndex >= items.size()) {
                return;
            }
            long elapsed = System.currentTimeMillis() - hoverStartedAt;
            if (elapsed < 1000L) {
                return;
            }

            FruitState item = items.get(hoverIndex);
            String line1 = item.name();
            String line2 = "個数: " + item.quantity();
            String line3 = item.description();

            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            int w = Math.max(g2.getFontMetrics().stringWidth(line1), Math.max(g2.getFontMetrics().stringWidth(line2), g2.getFontMetrics().stringWidth(line3))) + 16;
            int h = 52;
            int x = Math.min(mouseX, getWidth() - w - 8);
            int y = Math.min(mouseY, getHeight() - h - 8);

            g2.setColor(new Color(0, 0, 0, 210));
            g2.fillRoundRect(x, y, w, h, 10, 10);
            g2.setColor(new Color(255, 255, 255));
            g2.drawString(line1, x + 8, y + 14);
            g2.drawString(line2, x + 8, y + 28);
            g2.drawString(line3, x + 8, y + 42);
        }
    }

}

