package com.matumaka.guimenus.gui;

import com.matumaka.guimenus.GUIMenusAPI;
import com.matumaka.guimenus.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.joml.Matrix4f;

import java.util.*;

/**
 * A head-tracked in-world menu displayed to a single player.
 *
 * <p>Customize its look with a {@link MenuStyle}:</p>
 *
 * <pre>{@code
 * Menu menu = new Menu("MAIN MENU", MenuStyle.builder().distance(3.0).build());
 * menu.registerButton("Close", menu::close);
 * menu.displayTo(player);
 * }</pre>
 */
public final class Menu {

    /**
     * The default menu distance in front of the player's face.
     *
     * @deprecated use {@link MenuStyle} instead
     */
    @Deprecated
    public static final double DISTANCE = 2.5;

    private static final float TEXT_CENTER = 0.11F;
    private static final float PANEL_GAP = 0.3F;

    private static final Map<UUID, Menu> menus = new HashMap<>();

    private final String title;
    private final MenuStyle style;
    private final List<Button> buttons = new ArrayList<>();
    private final List<Display> displays = new ArrayList<>();

    private Player player;
    private Location start;
    private Location eye;
    private Pig tripod;
    private TextDisplay cursor;
    private Button hovered;
    private long lastClick;

    /**
     * Creates a menu with the given title and default style.
     *
     * @param title MiniMessage title, shown above the buttons; may be null or blank
     *              to open the menu without a heading
     */
    public Menu(String title) {
        this(title, MenuStyle.defaults());
    }

    /**
     * Creates a title-less menu with a custom style.
     */
    public Menu(MenuStyle style) {
        this(null, style);
    }

    /**
     * Creates a menu with the given title and style.
     *
     * @param title MiniMessage title, may be null or blank to omit the heading
     * @param style appearance and behavior settings, not null
     */
    public Menu(String title, MenuStyle style) {
        this.title = title;
        this.style = style;
    }

    /**
     * The style this menu renders with.
     */
    public MenuStyle getStyle() {
        return this.style;
    }

    /**
     * Appends a plain button (no icon).
     */
    public Menu registerButton(String label, Runnable action) {
        return this.registerButton(Button.builder(label).action(action).build());
    }

    /**
     * Appends a button with an item icon.
     */
    public Menu registerButton(String label, Material icon, Runnable action) {
        return this.registerButton(Button.builder(label).icon(icon).action(action).build());
    }

    /**
     * Appends a fully customized {@link Button}.
     */
    public Menu registerButton(Button button) {
        this.buttons.add(button);

        return this;
    }

    /**
     * Opens this menu for the given player. If they already have a menu mounted,
     * this one seamlessly replaces it.
     */
    public void displayTo(Player player) {
        final Menu previous = getMenu(player);

        this.player = player;
        this.lastClick = System.currentTimeMillis();

        if (previous == null) {
            this.mount();
        } else {
            this.takeOver(previous);
        }

        this.draw();

        menus.put(player.getUniqueId(), this);
    }

    private void mount() {
        this.start = this.player.getLocation();
        this.eye = this.player.getEyeLocation();

        this.eye.setPitch(0F);

        this.player.leaveVehicle();
        this.player.setRotation(this.eye.getYaw(), 0F);
        this.player.setInvisible(true);

        this.tripod = this.eye.getWorld().spawn(this.eye, Pig.class, pig -> {
            pig.setInvisible(true);
            pig.setAI(false);
            pig.setGravity(false);
            pig.setSilent(true);
            pig.setInvulnerable(true);
            pig.setPersistent(false);
        });

        this.tripod.teleport(this.eye.clone().subtract(0, this.tripod.getEyeHeight(), 0));
        this.tripod.addPassenger(this.player);
        Utils.setCamera(this.player, this.tripod);

        final var plugin = GUIMenusAPI.getPlugin();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> this.player.sendActionBar(Component.empty()), 2);
    }

    private void takeOver(Menu previous) {
        this.start = previous.start;
        this.eye = previous.eye;
        this.tripod = previous.tripod;

        previous.removeDisplays();
    }

    private void draw() {
        this.draw(true);
    }

    /**
     * Re-renders the whole menu in place, applying any changes made to its
     * {@link MenuStyle} (e.g. a new panel or button background). The player's
     * camera and position are kept; the leading open sound is not replayed.
     */
    public void redraw() {
        this.draw(false);
    }

    private void draw(boolean playOpenSound) {
        final boolean hasTitle = this.hasTitle();
        final int rows = this.buttons.size() + (hasTitle ? 1 : 0);
        final float height = (float) (rows * this.style.getRowSpacing() + PANEL_GAP);
        final double distance = this.style.getDistance();

        this.spawnBlock(this.style.getPanelMaterial(), 0, distance + 0.12, this.style.getPanelWidth(), height);

        if (hasTitle) {
            final double top = this.getRowY(0);

            if (this.style.getAccentMaterial() != null) {
                this.spawnBlock(this.style.getAccentMaterial(), top - 0.2, distance + 0.05, this.style.getHeadingBarWidth(), this.style.getHeadingBarHeight());
            }

            if (this.style.getTitleFormat() != null) {
                this.spawnText(this.style.getTitleFormat().replace("%s", this.title), top, distance, this.style.getTitleScale());
            }
        }

        for (int index = 0; index < this.buttons.size(); index++) {
            this.buttons.get(index).draw(this, this.getRowY(index + (hasTitle ? 1 : 0)));
        }

        this.cursor = this.spawnText(this.cursorText(false), 0, distance - 0.05, this.style.getCursorScale());

        this.cursor.setTeleportDuration(1);

        this.play(playOpenSound ? this.style.getOpenSound() : null, this.style.getOpenSoundPitch());
        this.tick();
    }

    private boolean hasTitle() {
        return this.title != null && !this.title.isBlank();
    }

    private double getRowY(int row) {
        final int rows = this.buttons.size() + (this.hasTitle() ? 1 : 0);

        return this.style.getRowSpacing() * ((rows - 1) / 2D - row);
    }

    private void tick() {
        final double distance = this.style.getDistance();
        final double x = Utils.project(this.player.getYaw() - this.eye.getYaw(), distance, distance);
        final double y = Utils.project(-this.player.getPitch(), distance, distance);

        this.cursor.teleport(Utils.toWorld(this.eye, x, y - TEXT_CENTER * this.style.getCursorScale(), distance - 0.05));

        final Button button = this.findButton(x, y);

        if (button == this.hovered) {
            return;
        }

        if (this.hovered != null) {
            this.hovered.setHover(false);
        }

        if (button != null) {
            button.setHover(true);
            this.play(this.style.getHoverSound(), this.style.getHoverSoundPitch());
        }

        this.cursor.text(Utils.toComponent(this.cursorText(button != null)));
        this.hovered = button;
    }

    private Button findButton(double x, double y) {
        for (final Button button : this.buttons) {
            if (button.isWithin(x, y)) {
                return button;
            }
        }
        return null;
    }

    /**
     * Clicks whatever button is currently hovered. Called automatically while a
     * menu is open; you rarely need to call this yourself.
     */
    public void click() {
        final long now = System.currentTimeMillis();

        if (this.hovered == null || now - this.lastClick < Math.max(0, this.style.getClickDelay())) {
            return;
        }

        this.lastClick = now;

        this.play(this.style.getClickSound(), this.style.getClickSoundPitch());
        this.hovered.click();
    }

    /**
     * Unmounts the menu and restores the player to their original camera and position.
     */
    public void close() {
        menus.remove(this.player.getUniqueId());

        this.removeDisplays();
        this.player.leaveVehicle();
        Utils.setCamera(this.player, this.player);
        this.tripod.remove();
        this.player.setInvisible(false);
        this.play(this.style.getCloseSound(), this.style.getCloseSoundPitch());
        this.player.teleport(this.start);
    }

    /**
     * Spawns a billboard text display on the menu's vertical centerline.
     * Menu style offsets are applied automatically.
     */
    public TextDisplay spawnText(String text, double y, double distance, float scale) {
        return this.spawnText(0, text, y, distance, scale);
    }

    /**
     * Spawns a billboard text display at the given horizontal offset from the
     * menu's vertical centerline. Menu style offsets are applied automatically.
     */
    public TextDisplay spawnText(double x, String text, double y, double distance, float scale) {
        final TextDisplay display = this.eye.getWorld().spawn(this.toWorld(x, y - TEXT_CENTER * scale, distance), TextDisplay.class, spawned -> {
            spawned.text(Utils.toComponent(text));
            spawned.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            spawned.setShadowed(true);
        });

        this.setup(display, new Matrix4f().scale(scale));

        return display;
    }

    /**
     * Spawns a billboard block display stretched to the given size.
     * Menu style offsets are applied automatically.
     */
    public BlockDisplay spawnBlock(Material material, double y, double distance, float width, float height) {
        return this.spawnBlock(0, material, y, distance, width, height);
    }

    /**
     * Spawns a billboard block display at the given horizontal offset.
     * Menu style offsets are applied automatically.
     */
    public BlockDisplay spawnBlock(double x, Material material, double y, double distance, float width, float height) {
        final BlockDisplay display = this.eye.getWorld().spawn(this.toWorld(x, y, distance), BlockDisplay.class, spawned -> {
            spawned.setBlock(material.createBlockData());
        });

        this.setup(display, new Matrix4f().translate(-width / 2, -height / 2, -0.02F).scale(width, height, 0.04F));

        return display;
    }

    /**
     * Spawns an item display showing the given material as a GUI item.
     * Menu style offsets are applied automatically.
     */
    public void spawnItem(Material material, double x, double y, double distance, float scale) {
        final ItemDisplay display = this.eye.getWorld().spawn(this.toWorld(x, y, distance), ItemDisplay.class, spawned -> {
           spawned.setItemStack(new ItemStack(material));
           spawned.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);
        });

        this.setup(display, new Matrix4f().scale(scale));
    }

    private Location toWorld(double x, double y, double distance) {
        return Utils.toWorld(this.eye, x + this.style.getHorizontalOffset(), y + this.style.getVerticalOffset(), distance);
    }

    private void setup(Display display, Matrix4f matrix) {
        display.setBillboard(Display.Billboard.CENTER);
        display.setBrightness(new Display.Brightness(15, 15));
        display.setVisibleByDefault(false);
        display.setPersistent(false);
        display.setTransformationMatrix(matrix);

        this.player.showEntity(GUIMenusAPI.getPlugin(), display);
        this.displays.add(display);
    }

    private void removeDisplays() {
        for (final Display display : this.displays) {
            display.remove();
        }

        this.displays.clear();

        this.hovered = null;
    }

    /**
     * Returns the menu a player currently has open, or null.
     */
    public static Menu getMenu(Player player) {
        return menus.get(player.getUniqueId());
    }

    /**
     * Ticks every open menu. Called automatically once initialized via {@code GUIMenusAPI}.
     */
    public static void tickAll() {
        for (final Menu menu : menus.values()) {
            menu.tick();
        }
    }

    /**
     * Closes every open menu. Called automatically by {@code GUIMenusAPI#shutdown()}.
     */
    public static void closeAll() {
        for (final Menu menu : new ArrayList<>(menus.values())) {
            menu.close();
        }
    }

    private String cursorText(boolean hovering) {
        return (hovering ? this.style.getCursorHoverColor() : this.style.getCursorColor()) + this.style.getCursorGlyph();
    }

    private void play(Sound sound, float pitch) {
        if (sound != null) {
            Utils.playSound(this.player, sound, pitch);
        }
    }
}
