package com.raku.fruitGame.fruit.game;

import com.raku.fruitGame.fruit.functionalClass.utility.FruitHistory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("FieldCanBeLocal")
public class Background extends JPanel {
    private static final String MAIN_MENU_BG = "assets/fruitGame/textures/main_menu/main_menu.png";
    private static final String ORCHARD_BG = "assets/fruitGame/textures/factory/orchard.png";
    private static final String SHOP_BG = "assets/fruitGame/textures/factory/shop.png";
    private static final String ACTION_BUBBLE = "assets/fruitGame/textures/misc/action_bubble.png";

    private static final Path CSV_PATH = Path.of("fruit_history.csv");
    private static final Path FERTILIZER_CSV_PATH = Path.of("fertilizer_history.csv");

    private final Image mainMenuBackground;
    private final Image orchardBackground;
    private final Image shopBackground;
    private final Image actionBubble;
    private final @NotNull Player player;
    private final @NotNull OrchardManager orchardManager;
    private Image currentBackground;
    private final @NotNull DemoTimer demoTimer;
    private @Nullable BlackoutOverlay overlay;
    private long spacePressedAt;
    private boolean spaceConsumed;
    private int touchingTreeIndex = -1;
    private boolean gotoShop = false;
    private boolean gotoOrchard = false;

    public Background() {
        setOpaque(true);
        setBackground(new Color(210, 245, 255));
        setFocusable(true);
        this.mainMenuBackground = AssetImageLoader.load(MAIN_MENU_BG);
        this.orchardBackground = AssetImageLoader.load(ORCHARD_BG);
        this.shopBackground = AssetImageLoader.load(SHOP_BG);
        this.actionBubble = AssetImageLoader.load(ACTION_BUBBLE);
        this.player = new Player();
        this.orchardManager = new OrchardManager();
        this.currentBackground = mainMenuBackground != null ? mainMenuBackground : orchardBackground;
        this.demoTimer = new DemoTimer(this);
        loadInitialHistory();
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

    public void gotoOrchard () {
        this.gotoOrchard = true;
    }

    public void gotoShopMap () {
        this.gotoShop = true;
    }

    public void stopAnimation() {
        this.demoTimer.stop();
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public void resetForNewStart() {
        orchardManager.clearAllPlants();
        player.resetStatusForNewStart();
        player.clearHeldItem();
        touchingTreeIndex = -1;
        spacePressedAt = 0L;
        spaceConsumed = false;
        try {
            Files.deleteIfExists(FERTILIZER_CSV_PATH);
        } catch (IOException ignored) {
            // 初期化時に消せなくてもゲーム進行は継続
        }
    }

    public void reloadHistoryAndOrchard() {
        loadInitialHistory();
    }

    @Override
    public void paintComponent(@NotNull Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 黒幕保持フェーズに入った瞬間に、背景を orchard へ切り替える。
        if (overlay != null && overlay.consumeEnteredHoldPhase() && orchardBackground != null && gotoOrchard) {
            currentBackground = orchardBackground;
            ControlPanel.resetAllButton();
        }

        if (overlay != null && overlay.consumeEnteredHoldPhase() && shopBackground != null && gotoShop) {}

        if (currentBackground != null) {
            g2.drawImage(currentBackground, 0, 0, width, height, this);
        } else {
            g2.setColor(getBackground());
            g2.fillRect(0, 0, width, height);
        }

        // orchard表示中は human の歩行アニメーションを描画する。
        if (currentBackground == orchardBackground) {
            orchardManager.updateLayout(width, height);
            List<Rectangle> orchardBounds = orchardManager.getTreeBounds();
            player.setBlockedAreas(orchardBounds);
            if (orchardManager.updateGrowth(System.currentTimeMillis())) {
                syncOrchardToHistory();
            }
            player.draw(g2, width, height, this);
            drawHungerHud(g2, width, height);
            drawActionBubbleIfNeeded(g2);
            updateSpaceUseIfNeeded();
        } else {
            player.setBlockedAreas(List.of());
        }

        g2.dispose();
    }

    @Override
    public void paint(@NotNull Graphics g) {
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
        bindKey(inputMap, actionMap, "released S", "s-released", ()  -> player.setMoveDown(false));
    }

    private void bindActionKeys() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        bindKey(inputMap, actionMap, "pressed T", "t-pressed", this::eatHeldItemWithMessage);
        bindKey(inputMap, actionMap, "pressed SPACE", "space-pressed", this::startSpaceUse);
        bindKey(inputMap, actionMap, "released SPACE", "space-released", this::stopSpaceUse);
        bindKey(inputMap, actionMap, "pressed H", "h-harvest", this::harvestTouchedTree);
        bindKey(inputMap, actionMap, "pressed P", "p-fertilize", this::fertilizeTouchedTree);
    }

    private void drawActionBubbleIfNeeded(@NotNull Graphics2D g2) {
        Rectangle playerBounds = player.getBounds();
        touchingTreeIndex = orchardManager.findTouchedTree(playerBounds);
        if (touchingTreeIndex < 0) {
            return;
        }
        if (actionBubble != null) {
            int bubbleW = Math.max(48, playerBounds.width);
            int bubbleH = Math.max(48, playerBounds.height / 2);
            int bubbleX = playerBounds.x + (playerBounds.width - bubbleW) / 2;
            int bubbleY = Math.max(0, playerBounds.y - bubbleH - 8);
            g2.drawImage(actionBubble, bubbleX, bubbleY, bubbleW, bubbleH, this);
        }
    }

    private void startSpaceUse() {
        if (currentBackground != orchardBackground) {
            return;
        }
        if (touchingTreeIndex >= 0) {
            OrchardManager.InteractionResult planted = orchardManager.tryPlant(touchingTreeIndex, player.getHeldFruit());
            if (planted == OrchardManager.InteractionResult.PLANTED) {
                if (player.hasHeldItem()) {
                    player.clearHeldItem();
                }
                ActionLogCsv.logEvent("plant", "tree:" + touchingTreeIndex,
                        "planted " + Objects.requireNonNull(orchardManager.getSlot(touchingTreeIndex)).species.getBaseName());
                syncOrchardToHistory();
            }
        }
        spacePressedAt = System.currentTimeMillis();
        spaceConsumed = false;
    }

    private void stopSpaceUse() {
        spacePressedAt = 0L;
        spaceConsumed = false;
    }

    private void updateSpaceUseIfNeeded() {
        if (spacePressedAt == 0L || spaceConsumed || currentBackground != orchardBackground) {
            return;
        }
        if (System.currentTimeMillis() - spacePressedAt >= 1500L) {
            if (touchingTreeIndex >= 0) {
                OrchardManager.InteractionResult grew = orchardManager.tryGrowByHold(touchingTreeIndex);
                if (grew == OrchardManager.InteractionResult.GREW) {
                    ActionLogCsv.logEvent("grow", "tree:" + touchingTreeIndex,
                            "manual grow by long SPACE");
                    syncOrchardToHistory();
                }
            } else if (player.hasHeldItem() && !isFertilizer(player.getHeldItemName())) {
                player.useHeldItem();
            }
            spaceConsumed = true;
            spacePressedAt = 0L;
        }
    }

    private void harvestTouchedTree() {
        if (currentBackground != orchardBackground || touchingTreeIndex < 0) {
            return;
        }
        FruitState harvested = orchardManager.tryHarvest(touchingTreeIndex);
        if (harvested == null) {
            return;
        }
        SoundPlayer.playUi("harvest.wav");

        player.setHeldFruit(harvested);
        FruitHistory.recordCreation(
                harvested.name(),
                harvested.color(),
                harvested.weight(),
                harvested.description().contains("最高") ? "最高においしい" : "おいしい",
                harvested.description().contains(FruitStage.PERFECT) ? FruitStage.PERFECT : FruitStage.RIPE,
                0L,
                -1
        );
        syncOrchardToHistory();
        persistHistoryQuietly();
        ActionLogCsv.logEvent("harvest", harvested.name(), "harvested from tree " + touchingTreeIndex);
    }

    private void eatHeldItemWithMessage() {
        if (!player.hasHeldItem()) {
            return;
        }
        FruitState eaten = player.getHeldFruit();
        String name = player.getHeldItemName();
        if (isFertilizer(name)) {
            ControlPanel.showAteLog("肥料は食べられない");
            return;
        }
        FruitHistory.removeLatestBagRecord(name);
        persistHistoryQuietly();
        int recovered = player.recoverHungerFromFruit(eaten);
        player.clearHeldItem();
        ActionLogCsv.logEvent("eat", name, "ate by T key");
        ControlPanel.showAteLog(name + " (+" + recovered + " 空腹度)");
    }

    private void drawHungerHud(@NotNull Graphics2D g2, int width, int height) {
        int hunger = player.getHunger();
        int barW = Math.max(180, width / 5);
        int barH = 18;
        int x = width - barW - 20;
        int y = 18;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(x - 8, y - 8, barW + 16, barH + 32, 12, 12);

        g2.setColor(new Color(240, 240, 240));
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        g2.drawString("空腹度 " + hunger + "/100", x, y + 4);

        g2.setColor(new Color(60, 60, 60));
        g2.fillRoundRect(x, y + 10, barW, barH, 10, 10);

        int fillW = (int) Math.round(barW * (hunger / 100.0));
        Color fill = hunger <= 15 ? new Color(220, 70, 70) : (hunger <= 40 ? new Color(230, 170, 50) : new Color(90, 205, 110));
        g2.setColor(fill);
        g2.fillRoundRect(x, y + 10, Math.max(0, fillW), barH, 10, 10);

        g2.setColor(new Color(25, 25, 25));
        g2.drawRoundRect(x, y + 10, barW, barH, 10, 10);

        if (player.isStarving()) {
            g2.setColor(new Color(255, 210, 210));
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            g2.drawString("空腹で移動速度が低下中", x, y + 44);
        }
    }

    private void fertilizeTouchedTree() {
        if (currentBackground != orchardBackground || touchingTreeIndex < 0) {
            return;
        }
        if (!isFertilizer(player.getHeldItemName())) {
            return;
        }
        if (!orchardManager.tryFertilize(touchingTreeIndex)) {
            return;
        }
        OrchardManager.OrchardSlot slot = orchardManager.getSlot(touchingTreeIndex);
        String targetName = (slot == null || slot.species == null)
                ? "果物"
                : slot.species.getJapaneseName();
        int treeId = touchingTreeIndex + 1;
        FruitHistory.removeLatestBagRecord(player.getHeldItemName());
        persistHistoryQuietly();
        player.clearHeldItem();
        SoundPlayer.playUi("fertilize.wav");
        ActionLogCsv.logEvent("fertilize", "tree:" + touchingTreeIndex,
                "treeId=" + treeId + ", fruit=" + targetName + ", 120s->unripe x2");
        ControlPanel.showMessageLog(targetName + "の木(ID:" + treeId + ")に肥料を与えた。120秒後に未熟果実が2個実る。");
        persistFertilizerQuietly();
    }

    private void loadInitialHistory() {
        if (!Files.exists(CSV_PATH)) {
            return;
        }
        try {
            FruitHistory.loadCsv(CSV_PATH);
            orchardManager.restoreFromHistory(FruitHistory.viewAll());
            orchardManager.restoreFertilizerElapsed(FertilizerCsv.load(FERTILIZER_CSV_PATH), System.currentTimeMillis());
        } catch (IOException ignored) {
            // 起動時に読めなくてもゲームは継続
        }
    }

    private void syncOrchardToHistory() {
        long now = System.currentTimeMillis();
        for (int i = 0; i < orchardManager.getSlots().size(); i++) {
            OrchardManager.OrchardSlot slot = orchardManager.getSlot(i);
            if (slot == null) {
                continue;
            }
            if (slot.stage == FruitStage.EMPTY) {
                FruitHistory.removeTreeRecord(i);
                continue;
            }

            String fruitName = OrchardManager.toFruitName(slot.species, slot.stage);
            FruitStage maturity = switch (slot.stage) {
                case UNRIPE -> FruitStage.UNRIPE;
                case RIPE -> FruitStage.RIPE;
                case PERFECT -> FruitStage.PERFECT;
                case EMPTY ->  FruitStage.EMPTY;
            };
            String taste = switch (slot.stage) {
                case UNRIPE -> "なし";
                case RIPE -> "おいしい";
                case PERFECT -> "最高においしい";
                case EMPTY -> "";
            };

            FruitHistory.upsertTreeState(
                    i,
                    fruitName,
                    slot.stage == FruitStage.UNRIPE ? "緑" : slot.species.getRipeColor(),
                    Math.max(1L, slot.fruitCount),
                    taste,
                    maturity,
                    slot.elapsedSeconds(now)
            );
        }
        persistHistoryQuietly();
        persistFertilizerQuietly();
    }

    private void persistHistoryQuietly() {
        try {
            FruitHistory.saveCsv(CSV_PATH);
        } catch (IOException ignored) {
            // 保存失敗時もゲーム進行は継続
        }
    }

    private void persistFertilizerQuietly() {
        try {
            FertilizerCsv.save(FERTILIZER_CSV_PATH, orchardManager.snapshotFertilizerElapsed(System.currentTimeMillis()));
        } catch (IOException ignored) {
            // 保存失敗時もゲーム進行は継続
        }
    }

    private static boolean isFertilizer(@Nullable String itemName) {
        return itemName != null && (itemName.contains("肥料") || itemName.toLowerCase().contains("fertilizer"));
    }

    private static void bindKey(@NotNull InputMap inputMap, @NotNull ActionMap actionMap, String keyStroke, String actionName, @NotNull Runnable action) {
        inputMap.put(KeyStroke.getKeyStroke(keyStroke), actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                action.run();
            }
        });
    }
}
