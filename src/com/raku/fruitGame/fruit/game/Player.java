package com.raku.fruitGame.fruit.game;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

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

    private final Image[] walkRightFrames;
    private final Image[] walkLeftFrames;
    private final Image frontFrame;
    private final Image backFrame;
    private final Image idleRightFrame;
    private final Image idleLeftFrame;
    private List<Rectangle> blockedAreas = Collections.emptyList();

    private double x = -1;
    private double y = -1;
    private int lastDrawW;
    private int lastDrawH;

    private FruitState heldFruit;

    private int frameIndex;
    private long lastFrameSwitchAt;
    private long lastUpdateAt;

    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveUp;
    private boolean moveDown;

    private Direction facing = Direction.RIGHT;

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

    public void setBlockedAreas(List<Rectangle> blockedAreas) {
        this.blockedAreas = blockedAreas == null ? Collections.emptyList() : blockedAreas;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) Math.round(x), (int) Math.round(y), Math.max(1, lastDrawW), Math.max(1, lastDrawH));
    }

    public boolean hasHeldItem() {
        return heldFruit != null;
    }

    public String getHeldItemName() {
        return heldFruit == null ? null : heldFruit.name();
    }

    public void setHeldItem(String name, Image icon) {
        this.heldFruit = new FruitState(name, "", 0L, 1, name, icon);
    }

    public void setHeldFruit(FruitState fruit) {
        this.heldFruit = fruit;
    }

    public void clearHeldItem() {
        this.heldFruit = null;
    }

    public boolean useHeldItem() {
        if (!hasHeldItem()) {
            return false;
        }
        clearHeldItem();
        return true;
    }

    public void draw(Graphics2D g2, int width, int height, Component observer) {
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
        double distance = SPEED_PX_PER_SEC * deltaSec;

        double nextX = x;
        double nextY = y;

        if (moveLeft && !moveRight) {
            nextX -= distance;
            facing = Direction.LEFT;
        } else {
            if (moveRight && !moveLeft) {
                nextX += distance;
                facing = Direction.RIGHT;
            }
        }

        if (moveUp && !moveDown) {
            nextY -= distance;
            facing = Direction.UP;
        } else {
            if (moveDown && !moveUp) {
                nextY += distance;
                facing = Direction.DOWN;
            }
        }

        nextX = clamp(nextX, minX, maxX);
        nextY = clamp(nextY, minY, maxY);

        // X軸移動を先に適用し、障害物に当たるならキャンセル
        if (!collides(nextX, y, drawW, drawH)) {
            x = nextX;
        }
        // Y軸移動を次に適用し、障害物に当たるならキャンセル
        if (!collides(x, nextY, drawW, drawH)) {
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
            return false;
        }
        Rectangle me = new Rectangle((int) Math.round(testX), (int) Math.round(testY), drawW, drawH);
        for (Rectangle blocked : blockedAreas) {
            if (blocked != null && me.intersects(blocked)) {
                return true;
            }
        }
        return false;
    }

    private void drawHeldItem(Graphics2D g2, Component observer, int drawW, int drawH) {
        if (heldFruit == null || heldFruit.icon() == null) {
            return;
        }
        if (facing != Direction.DOWN) {
            return;
        }

        // 指定座標 (21,153) を正規化した近似位置に表示。
        int holdX = (int) Math.round(x + drawW * 0.22);
        int holdY = (int) Math.round(y + drawH * 0.80);
        int itemW = Math.max(16, drawW / 4);
        int itemH = Math.max(16, drawH / 4);
        g2.drawImage(heldFruit.icon(), holdX, holdY, itemW, itemH, observer);
    }
}

