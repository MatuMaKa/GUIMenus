package com.matumaka.guimenus.gui;

import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;

/**
 * A single button row inside a {@link Menu}.
 *
 * <p>Create with the fluent builder for full control, or use the plain
 * constructor for default styling:</p>
 *
 * <pre>{@code
 * Button.builder("localhost")
 *       .icon(Material.DIAMOND)
 *       .action(() -> player.connect(...))
 *       .labelFormat("<gradient:#34eb8f:#f8e373><bold>%s")
 *       .width(2.2F)
 *       .build()
 * }</pre>
 *
 * <p>Any property left unset falls back to the menu's {@link MenuStyle}.</p>
 */
public final class Button {

    private String label;
    private Material icon;
    private Runnable action;

    private Float width;
    private Float height;
    private Material material;
    private Material hoverMaterial;
    private String labelFormat;
    private Float labelScale;
    private Float iconScale;

    private Menu menu;
    private double y;
    private BlockDisplay bar;

    /**
     * Creates a builder for a button with the given MiniMessage label.
     */
    public static Builder builder(String label) {
        final Button button = new Button(label, null, null);

        return button.new Builder();
    }

    /**
     * Creates a button with default styling from the menu's {@link MenuStyle}.
     *
     * @param label  MiniMessage label
     * @param icon   item icon, or null for none
     * @param action runs when clicked
     */
    public Button(String label, Material icon, Runnable action) {
        this.label = label;
        this.icon = icon;
        this.action = action;
    }

    /**
     * The MiniMessage label shown on the button.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Sets the MiniMessage label.
     */
    public Button setLabel(String label) {
        this.label = label;
        return this;
    }

    /**
     * The item icon, or null.
     */
    public Material getIcon() {
        return this.icon;
    }

    /**
     * Sets the item icon, or null for none.
     */
    public Button setIcon(Material icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Sets the action run on click.
     */
    public Button setAction(Runnable action) {
        this.action = action;
        return this;
    }

    /**
     * Builder for {@link Button}.
     */
    public final class Builder {

        private Builder() {}

        /** Item shown left of the label, or null for none. */
        public Builder icon(Material icon) {
            Button.this.icon = icon;
            return this;
        }

        /** The action run when the player clicks this button. */
        public Builder action(Runnable action) {
            Button.this.action = action;
            return this;
        }

        /** Button hitbox and bar width. */
        public Builder width(float width) {
            Button.this.width = width;
            return this;
        }

        /** Button hitbox and bar height. */
        public Builder height(float height) {
            Button.this.height = height;
            return this;
        }

        /** Block display while not hovered. */
        public Builder material(Material material) {
            Button.this.material = material;
            return this;
        }

        /** Block display while hovered. */
        public Builder hoverMaterial(Material material) {
            Button.this.hoverMaterial = material;
            return this;
        }

        /** MiniMessage label format, {@code %s} is replaced by the label. */
        public Builder labelFormat(String format) {
            Button.this.labelFormat = format;
            return this;
        }

        /** Text scale of the label. */
        public Builder labelScale(float scale) {
            Button.this.labelScale = scale;
            return this;
        }

        /** Item icon scale. */
        public Builder iconScale(float scale) {
            Button.this.iconScale = scale;
            return this;
        }

        /** Creates the button. */
        public Button build() {
            return Button.this;
        }
    }

    /**
     * Renders this button as part of the given menu at the given row.
     * Called by the menu.
     */
    public void draw(Menu menu, double y) {
        this.menu = menu;
        this.y = y;

        final MenuStyle style = menu.getStyle();
        final float width = this.resolveWidth(style);
        final float height = this.resolveHeight(style);
        final float labelScale = this.labelScale != null ? this.labelScale : style.getButtonLabelScale();

        this.bar = menu.spawnBlock(this.resolveMaterial(style, false), y, style.getDistance() + 0.05, width, height);

        menu.spawnText(this.resolveLabel(style), y, style.getDistance(), labelScale);

        if (this.icon != null) {
            final float iconScale = this.iconScale != null ? this.iconScale : style.getIconScale();

            menu.spawnItem(this.icon, -width / 2 + style.getIconOffset(), y, style.getDistance(), iconScale);
        }
    }

    /**
     * Whether the given cursor position (in the menu's screen space) is inside
     * this button's hitbox.
     */
    public boolean isWithin(double x, double y) {
        final MenuStyle style = this.menu != null ? this.menu.getStyle() : MenuStyle.defaults();
        final float width = this.resolveWidth(style);
        final float height = this.resolveHeight(style);

        return Math.abs(x) < width / 2 && Math.abs(y - this.y) < height / 2;
    }

    /**
     * Highlights or un-highlights the button. Called by the menu.
     */
    public void setHover(boolean hover) {
        final MenuStyle style = this.menu != null ? this.menu.getStyle() : MenuStyle.defaults();

        this.bar.setBlock(this.resolveMaterial(style, hover).createBlockData());
    }

    /**
     * Runs this button's action. Called by the menu on click.
     */
    public void click() {
        this.action.run();
    }

    private float resolveWidth(MenuStyle style) {
        return this.width != null ? this.width : style.getButtonWidth();
    }

    private float resolveHeight(MenuStyle style) {
        return this.height != null ? this.height : style.getButtonHeight();
    }

    private Material resolveMaterial(MenuStyle style, boolean hovered) {
        if (hovered) {
            return this.hoverMaterial != null ? this.hoverMaterial : style.getButtonHoverMaterial();
        }

        return this.material != null ? this.material : style.getButtonMaterial();
    }

    private String resolveLabel(MenuStyle style) {
        final String format = this.labelFormat != null ? this.labelFormat : style.getButtonLabelFormat();

        return format.replace("%s", this.label);
    }
}
