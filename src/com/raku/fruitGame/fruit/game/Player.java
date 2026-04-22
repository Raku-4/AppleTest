package com.raku.fruitGame.fruit.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * orchard 上を歩くプレイヤー描画と移動更新を担当します。
 */
public class Player {
    private static final String WALK_RIGHT_0 = "assets/fruitGame/textures/human/walking_right.png";
    private static final String WALK_RIGHT_1 = "assets/fruitGame/textures/human/walking_right2.png";
    private static final String WALK_LEFT_0 = "assets/fruitGame/textures/human/walking_left.png";
    private static final String WALK_LEFT_1 = "assets/fruitGame/textures/human/walking_left2.png";
    private static final String FRONT = "assets/fruitGame/textures/human/facing_or_walking_front.png";
    private static final String BACK = "assets/fruitGame/textures/human/facing_or_walking_behind.png";
    private static final String IDLE_RIGHT = "assets/fruitGame/textures/human/facing_or_walking_right.png";
    private static final String IDLE_LEFT = "assets/fruitGame/textures/human/facing_or_walking_left.png";

    // 800x600の画面で1秒に80px移動する設定。
    private static final double SPEED_PX_PER_SEC = 80.0D;
    // 左右アニメの切り替え周期（0.5秒）。
    private static final long WALK_FRAME_INTERVAL_MS = 500L;
    // プレイヤー表示サイズの倍率。
    private static final double PLAYER_SCALE = 0.65D;
    private static final int MAX_HUNGER = 100;
    private static final int MIN_HUNGER = 0;
    private static final double HUNGER_DECAY_PER_SEC = 0.6D;
    private static final double STARVING_SPEED_MULTIPLIER = 0.55D;

    private final Image @NotNull [] walkRightFrames;
    private final Image @NotNull [] walkLeftFrames;
    private final Image frontFrame;
    private final Image backFrame;
    private final Image idleRightFrame;
    private final Image idleLeftFrame;
    private @NotNull List<Rectangle> blockedAreas = Collections.emptyList();

    private double x = -1;
    private double y = -1;
    private int lastDrawW;
    private int lastDrawH;

    private @Nullable FruitState heldFruit;
    private double hunger = MAX_HUNGER;

    private int frameIndex;
    private long lastFrameSwitchAt;
    private long lastUpdateAt;

    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveUp;
    private boolean moveDown;

    private @NotNull Direction facing = Direction.RIGHT;

    private enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    public Player() {
        this.walkRightFrames = new Image[] {
                AssetImageLoader.load(WALK_RIGHT_0),
                AssetImageLoader.load(WALK_RIGHT_1)
        };
        this.walkLeftFrames = new Image[] {
                AssetImageLoader.load(WALK_LEFT_0),
                AssetImageLoader.load(WALK_LEFT_1)
        };
        this.frontFrame = AssetImageLoader.load(FRONT);
        this.backFrame = AssetImageLoader.load(BACK);
        this.idleRightFrame = AssetImageLoader.load(IDLE_RIGHT);
        this.idleLeftFrame = AssetImageLoader.load(IDLE_LEFT);
        this.lastFrameSwitchAt = 0L;
        this.lastUpdateAt = 0L;
    }

    public void setMoveLeft(boolean value) {
        this.moveLeft = value;
    }

    public void setMoveRight(boolean value) {
        this.moveRight = value;
    }

    public void setMoveUp(boolean value) {
        this.moveUp = value;
    }

    public void setMoveDown(boolean value) {
        this.moveDown = value;
    }

    public void setBlockedAreas(@Nullable List<Rectangle> blockedAreas) {
        this.blockedAreas = blockedAreas == null ? Collections.emptyList() : blockedAreas;
    }

    public @NotNull Rectangle getBounds() {
        return new Rectangle((int) Math.round(x), (int) Math.round(y), Math.max(1, lastDrawW), Math.max(1, lastDrawH));
    }

    public boolean hasHeldItem() {
        return heldFruit != null;
    }

    public @Nullable String getHeldItemName() {
        return heldFruit == null ? null : heldFruit.name();
    }

    public void setHeldItem(String name, Image icon) {
        this.heldFruit = new FruitState(name, "", 0L, 1, name, icon);
    }

    public void setHeldFruit(@Nullable FruitState fruit) {
        this.heldFruit = fruit;
    }

    public @Nullable FruitState getHeldFruit() {
        return heldFruit;
    }

    public void clearHeldItem() {
        this.heldFruit = null;
    }

    public void resetStatusForNewStart() {
        this.hunger = MAX_HUNGER;
    }

    public int getHunger() {
        return (int) Math.round(clamp(hunger, MIN_HUNGER, MAX_HUNGER));
    }

    public boolean isStarving() {
        return getHunger() <= 0;
    }

    public void tickHunger(double deltaSec) {
        if (deltaSec <= 0D) {
            return;
        }
        hunger = clamp(hunger - (HUNGER_DECAY_PER_SEC * deltaSec), MIN_HUNGER, MAX_HUNGER);
    }

    public int recoverHungerFromFruit(@Nullable FruitState fruit) {
        if (fruit == null) {
            return 0;
        }
        long weight = Math.max(0L, fruit.weight());
        int gainByWeight = (int) Math.max(8L, Math.min(45L, (weight + 9L) / 10L));
        String lower = fruit.name() == null ? "" : fruit.name().toLowerCase();
        int bonus = lower.contains("perfect") ? 10 : (lower.contains("ripe") ? 5 : 0);
        return recoverHunger(gainByWeight + bonus);
    }

    public int recoverHunger(int amount) {
        int before = getHunger();
        hunger = clamp(hunger + Math.max(0, amount), MIN_HUNGER, MAX_HUNGER);
        return getHunger() - before;
    }

    public void useHeldItem() {
        if (!hasHeldItem()) {
            return;
        }
        String usedName = Objects.requireNonNull(heldFruit).name();
        clearHeldItem();
        ActionLogCsv.logEvent("use", usedName, "held item consumed by long SPACE press");
    }

    public void draw(@NotNull Graphics2D g2, int width, int height, Component observer) {
        int drawW = Math.max(40, (int) Math.round((width / 13.0) * PLAYER_SCALE));
        int drawH = Math.max(40, (int) Math.round((height / 8.0) * PLAYER_SCALE));
        this.lastDrawW = drawW;
        this.lastDrawH = drawH;
        int minX = 8;
        int maxX = Math.max(minX, width - drawW - 8);
        int minY = 8;
        int maxY = Math.max(minY, height - drawH - 8);

        if (x < 0) {
            x = width / 7.0;
        }
        if (y < 0) {
            y = Math.max(minY, height - drawH - 24.0);
        }

        long now = System.currentTimeMillis();
        if (lastUpdateAt == 0L) {
            lastUpdateAt = now;
        }
        double deltaSec = (now - lastUpdateAt) / 1000.0;
        lastUpdateAt = now;

        tickHunger(deltaSec);

        updatePosition(deltaSec, minX, maxX, minY, maxY, drawW, drawH);
        boolean movingHorizontally = moveLeft ^ moveRight;

        if (movingHorizontally) {
            if (lastFrameSwitchAt == 0L) {
                lastFrameSwitchAt = now;
            }
            if ((now - lastFrameSwitchAt) >= WALK_FRAME_INTERVAL_MS) {
                frameIndex = (frameIndex + 1) % walkRightFrames.length;
                lastFrameSwitchAt = now;
            }
        } else {
            frameIndex = 0;
            lastFrameSwitchAt = now;
        }

        Image frame = pickFrame(movingHorizontally);
        if (frame != null) {
            g2.drawImage(frame, (int) Math.round(x), (int) Math.round(y), drawW, drawH, observer);
        }

        drawHeldItem(g2, observer, drawW, drawH);
    }

    private void updatePosition(double deltaSec, int minX, int maxX, int minY, int maxY, int drawW, int drawH) {
        double speed = isStarving() ? SPEED_PX_PER_SEC * STARVING_SPEED_MULTIPLIER : SPEED_PX_PER_SEC;
        double distance = speed * deltaSec;

        double nextX = x;
        double nextY = y;

        if (moveLeft && !moveRight) {
            nextX -= distance;
            facing = Direction.LEFT;
            tickHunger(deltaSec);
        } else {
            if (moveRight && !moveLeft) {
                nextX += distance;
                facing = Direction.RIGHT;
                tickHunger(deltaSec);
            }
        }

        if (moveUp && !moveDown) {
            nextY -= distance;
            facing = Direction.UP;
            tickHunger(deltaSec);
        } else {
            if (moveDown && !moveUp) {
                nextY += distance;
                facing = Direction.DOWN;
                tickHunger(deltaSec);
            }
        }

        nextX = clamp(nextX, minX, maxX);
        nextY = clamp(nextY, minY, maxY);

        // X軸移動を先に適用し、障害物に当たるならキャンセル
        if (collides(nextX, y, drawW, drawH)) {
            x = nextX;
        }
        // Y軸移動を次に適用し、障害物に当たるならキャンセル
        if (collides(x, nextY, drawW, drawH)) {
            y = nextY;
        }
    }

    private Image pickFrame(boolean movingHorizontally) {
        return switch (facing) {
            case LEFT -> movingHorizontally ? walkLeftFrames[frameIndex] : idleLeftFrame;
            case RIGHT -> movingHorizontally ? walkRightFrames[frameIndex] : idleRightFrame;
            case UP -> backFrame;
            case DOWN -> frontFrame;
        };
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private boolean collides(double testX, double testY, int drawW, int drawH) {
        if (blockedAreas.isEmpty()) {
            return true;
        }
        Rectangle me = new Rectangle((int) Math.round(testX), (int) Math.round(testY), drawW, drawH);
        for (Rectangle blocked : blockedAreas) {
            if (blocked != null && me.intersects(blocked)) {
                return false;
            }
        }
        return true;
    }

    private void drawHeldItem(@NotNull Graphics2D g2, Component observer, int drawW, int drawH) {
        if (heldFruit == null || heldFruit.icon() == null) {
            return;
        }
        if (facing != Direction.DOWN) {
            return;
        }

        int holdX = (int) Math.round(x + drawW * 0.22 + 2);
        int holdY = (int) Math.round(y + drawH * 0.80 + 10);
        int itemW = Math.max(16, drawW / 4);
        int itemH = Math.max(16, drawH / 4);
        g2.drawImage(heldFruit.icon(), holdX, holdY, itemW, itemH, observer);
    }
}

