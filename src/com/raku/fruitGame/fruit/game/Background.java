package com.raku.fruitGame.fruit.game;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class Background extends JPanel {
    private static final String MAIN_MENU_BG = "assets/fruitGame/textures/main_menu/main_menu.png";
    private static final String ORCHARD_BG = "assets/fruitGame/textures/factory/orchard.png";
    private static final String ACTION_BUBBLE = "assets/fruitGame/textures/misc/action_bubble.png";

    // orchard 画像を 0-100% 座標で見たときの樹木範囲。
    private static final int[][] TREE_RANGES = {
            {6, 4, 13, 19}, {14, 4, 21, 19}, {22, 4, 29, 19},
            {6, 18, 13, 33}, {14, 18, 21, 33}, {22, 18, 29, 33},
            {5, 40, 14, 54}, {14, 40, 23, 54}, {23, 40, 32, 54},
            {5, 55, 14, 69}, {14, 55, 23, 69}, {23, 55, 32, 69},
            {5, 71, 14, 86}, {14, 71, 23, 86}, {23, 71, 32, 86},
            {50, 3, 59, 17}, {61, 3, 70, 17}, {71, 3, 80, 17}, {81, 3, 90, 17},
            {71, 25, 81, 39}, {82, 25, 92, 39},
            {36, 15, 46, 30}, {45, 25, 55, 40}, {56, 25, 66, 40}, {50, 41, 60, 56},
            {41, 56, 51, 71}, {58, 56, 68, 71},
            {71, 47, 81, 58}, {83, 47, 93, 58}, {71, 60, 81, 71}, {83, 60, 93, 71},
            {71, 73, 81, 84}, {83, 73, 93, 84},
            {38, 80, 47, 95}, {47, 80, 56, 95}, {56, 80, 65, 95}
    };

    private final Image mainMenuBackground;
    private final Image orchardBackground;
    private final Image actionBubble;
    private final Player player;
    private Image currentBackground;
    private final DemoTimer demoTimer;
    private BlackoutOverlay overlay;
    private long spacePressedAt;
    private boolean spaceConsumed;

    public Background() {
        setOpaque(true);
        setBackground(new Color(210, 245, 255));
        setFocusable(true);
        this.mainMenuBackground = AssetImageLoader.load(MAIN_MENU_BG);
        this.orchardBackground = AssetImageLoader.load(ORCHARD_BG);
        this.actionBubble = AssetImageLoader.load(ACTION_BUBBLE);
        this.player = new Player();
        this.currentBackground = mainMenuBackground != null ? mainMenuBackground : orchardBackground;
        this.demoTimer = new DemoTimer(this);
        bindMovementKeys();
        bindActionKeys();
    }

    public void startTransition() {
        this.overlay = new BlackoutOverlay(600L, 2000L);
        this.overlay.start();
        this.demoTimer.start();
        requestFocusInWindow();
        repaint();
    }

    public void stopAnimation() {
        this.demoTimer.stop();
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 黒幕保持フェーズに入った瞬間に、背景を orchard へ切り替える。
        if (overlay != null && overlay.consumeEnteredHoldPhase() && orchardBackground != null) {
            currentBackground = orchardBackground;
            ControlPanel.resetAllButton();
        }

        if (currentBackground != null) {
            g2.drawImage(currentBackground, 0, 0, width, height, this);
        } else {
            g2.setColor(getBackground());
            g2.fillRect(0, 0, width, height);
        }

        // orchard表示中は human の歩行アニメーションを描画する。
        if (currentBackground == orchardBackground) {
            List<Rectangle> orchardBounds = buildOrchardBounds(width, height);
            player.setBlockedAreas(orchardBounds);
            player.draw(g2, width, height, this);
            drawActionBubbleIfNeeded(g2, orchardBounds);
            updateSpaceUseIfNeeded();
        } else {
            player.setBlockedAreas(List.of());
        }

        g2.dispose();
    }

    @Override
    public void paint(Graphics g) {
        // super.paint(...) で背景・子コンポーネント（ボタン）まで描画する。
        super.paint(g);

        if (overlay != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            if (!overlay.isFinished()) {
                overlay.paint(g2, width, height);
            } else {
                overlay = null;
                // orchard上の歩行アニメを継続させるため、ここでは timer を止めない。
            }

            g2.dispose();
        }
    }

    private void bindMovementKeys() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        bindKey(inputMap, actionMap, "pressed LEFT", "left-pressed", () -> player.setMoveLeft(true));
        bindKey(inputMap, actionMap, "released LEFT", "left-released", () -> player.setMoveLeft(false));
        bindKey(inputMap, actionMap, "pressed RIGHT", "right-pressed", () -> player.setMoveRight(true));
        bindKey(inputMap, actionMap, "released RIGHT", "right-released", () -> player.setMoveRight(false));
        bindKey(inputMap, actionMap, "pressed UP", "up-pressed", () -> player.setMoveUp(true));
        bindKey(inputMap, actionMap, "released UP", "up-released", () -> player.setMoveUp(false));
        bindKey(inputMap, actionMap, "pressed DOWN", "down-pressed", () -> player.setMoveDown(true));
        bindKey(inputMap, actionMap, "released DOWN", "down-released", () -> player.setMoveDown(false));

        bindKey(inputMap, actionMap, "pressed A", "a-pressed", () -> player.setMoveLeft(true));
        bindKey(inputMap, actionMap, "released A", "a-released", () -> player.setMoveLeft(false));
        bindKey(inputMap, actionMap, "pressed D", "d-pressed", () -> player.setMoveRight(true));
        bindKey(inputMap, actionMap, "released D", "d-released", () -> player.setMoveRight(false));
        bindKey(inputMap, actionMap, "pressed W", "w-pressed", () -> player.setMoveUp(true));
        bindKey(inputMap, actionMap, "released W", "w-released", () -> player.setMoveUp(false));
        bindKey(inputMap, actionMap, "pressed S", "s-pressed", () -> player.setMoveDown(true));
        bindKey(inputMap, actionMap, "released S", "s-released", () -> player.setMoveDown(false));
    }

    private void bindActionKeys() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        bindKey(inputMap, actionMap, "pressed T", "t-pressed", player::clearHeldItem);
        bindKey(inputMap, actionMap, "pressed SPACE", "space-pressed", this::startSpaceUse);
        bindKey(inputMap, actionMap, "released SPACE", "space-released", this::stopSpaceUse);
    }

    private static @NotNull List<Rectangle> buildOrchardBounds(int width, int height) {
        List<Rectangle> bounds = new ArrayList<>(TREE_RANGES.length);
        for (int[] range : TREE_RANGES) {
            int x1 = width * range[0] / 100;
            int y1 = height * range[1] / 100;
            int x2 = width * range[2] / 100;
            int y2 = height * range[3] / 100;
            bounds.add(new Rectangle(x1, y1, Math.max(1, x2 - x1), Math.max(1, y2 - y1)));
        }
        return bounds;
    }

    private void drawActionBubbleIfNeeded(Graphics2D g2, @NotNull List<Rectangle> orchardBounds) {
        Rectangle playerBounds = player.getBounds();
        for (Rectangle tree : orchardBounds) {
            if (tree != null && playerBounds.intersects(tree)) {
                if (actionBubble != null) {
                    int bubbleW = Math.max(48, playerBounds.width);
                    int bubbleH = Math.max(48, playerBounds.height / 2);
                    int bubbleX = playerBounds.x + (playerBounds.width - bubbleW) / 2;
                    int bubbleY = Math.max(0, playerBounds.y - bubbleH - 8);
                    g2.drawImage(actionBubble, bubbleX, bubbleY, bubbleW, bubbleH, this);
                }
                break;
            }
        }
    }

    private void startSpaceUse() {
        if (!player.hasHeldItem()) {
            return;
        }
        spacePressedAt = System.currentTimeMillis();
        spaceConsumed = false;
    }

    private void stopSpaceUse() {
        spacePressedAt = 0L;
        spaceConsumed = false;
    }

    private void updateSpaceUseIfNeeded() {
        if (spacePressedAt == 0L || spaceConsumed || !player.hasHeldItem()) {
            return;
        }
        if (System.currentTimeMillis() - spacePressedAt >= 1500L) {
            player.useHeldItem();
            spaceConsumed = true;
            spacePressedAt = 0L;
        }
    }

    private static void bindKey(@NotNull InputMap inputMap, @NotNull ActionMap actionMap, String keyStroke, String actionName, Runnable action) {
        inputMap.put(KeyStroke.getKeyStroke(keyStroke), actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                action.run();
            }
        });
    }
}
