package com.matumaka.guimenus.gui;

import org.bukkit.Material;
import org.bukkit.Sound;

/**
 * Appearance and behavior settings for a {@link Menu}.
 *
 * <p>Every field has a sensible default (the classic GUIMenus look), so a plain
 * {@code Menu} is unchanged unless you customize it:</p>
 *
 * <pre>{@code
 * MenuStyle style = MenuStyle.builder()
 *         .distance(3.0)
 *         .titleFormat("<gold><bold>%s")
 *         .buttonMaterial(Material.PURPLE_CONCRETE)
 *         .build();
 *
 * Menu menu = new Menu("MAIN MENU", style);
 * }</pre>
 */
public final class MenuStyle {

    private double distance = 2.5;
    private double horizontalOffset = 0;
    private double verticalOffset = 0;
    private float rowSpacing = 0.5F;
    private float panelWidth = 3.2F;
    private float headingBarWidth = 1.4F;
    private float headingBarHeight = 0.035F;

    private Material panelMaterial = Material.BLACK_CONCRETE;
    private Material accentMaterial = Material.LIGHT_BLUE_CONCRETE;
    private Material buttonMaterial = Material.GRAY_CONCRETE;
    private Material buttonHoverMaterial = Material.LIGHT_BLUE_CONCRETE;

    private String titleFormat = "<gradient:#6ee7ff:#3fa9f5><bold>%s";
    private String buttonLabelFormat = "<white>%s";

    private String cursorGlyph = "♦";
    private String cursorColor = "<white>";
    private String cursorHoverColor = "<aqua>";

    private float titleScale = 1.4F;
    private float cursorScale = 0.6F;
    private float buttonLabelScale = 0.9F;
    private float iconScale = 0.28F;
    private float buttonWidth = 2.6F;
    private float buttonHeight = 0.4F;
    private float iconOffset = 0.18F;

    private Sound openSound = Sound.UI_TOAST_IN;
    private Sound closeSound = Sound.UI_TOAST_OUT;
    private Sound clickSound = Sound.UI_BUTTON_CLICK;
    private Sound hoverSound = Sound.BLOCK_NOTE_BLOCK_HAT;

    private float openSoundPitch = 1.4F;
    private float closeSoundPitch = 1.0F;
    private float clickSoundPitch = 1.2F;
    private float hoverSoundPitch = 1.6F;

    private long clickDelay = 250;

    private MenuStyle() {}

    /**
     * Creates a new builder with all defaults.
     */
    public static Builder builder() {
        final MenuStyle style = new MenuStyle();

        return style.new Builder();
    }

    /**
     * A style resolving every value to its default. Useful for deriving overrides.
     */
    public static MenuStyle defaults() {
        return builder().build();
    }

    /**
     * Builder for {@link MenuStyle}.
     *
     * @see MenuStyle#builder()
     */
    public final class Builder {

        private Builder() {}

        /** Distance of the menu in front of the player's face. */
        public Builder distance(double distance) {
            MenuStyle.this.distance = distance;
            return this;
        }

        /** Shifts the whole menu left/right relative to the view center. */
        public Builder horizontalOffset(double offset) {
            MenuStyle.this.horizontalOffset = offset;
            return this;
        }

        /** Shifts the whole menu up/down relative to the view center. */
        public Builder verticalOffset(double offset) {
            MenuStyle.this.verticalOffset = offset;
            return this;
        }

        /** Vertical gap between rows (and row height of buttons). */
        public Builder rowSpacing(float spacing) {
            MenuStyle.this.rowSpacing = spacing;
            return this;
        }

        /** Width of the dark backdrop panel. */
        public Builder panelWidth(float width) {
            MenuStyle.this.panelWidth = width;
            return this;
        }

        /** Width of the little accent bar under the title. */
        public Builder headingBarWidth(float width) {
            MenuStyle.this.headingBarWidth = width;
            return this;
        }

        /** Thickness of the accent bar under the title. */
        public Builder headingBarHeight(float height) {
            MenuStyle.this.headingBarHeight = height;
            return this;
        }

        /** Backdrop panel block. */
        public Builder panelMaterial(Material material) {
            MenuStyle.this.panelMaterial = material;
            return this;
        }

        /** Accent bar block, or null to hide it. */
        public Builder accentMaterial(Material material) {
            MenuStyle.this.accentMaterial = material;
            return this;
        }

        /** Button block when not hovered. */
        public Builder buttonMaterial(Material material) {
            MenuStyle.this.buttonMaterial = material;
            return this;
        }

        /** Button block when hovered. */
        public Builder buttonHoverMaterial(Material material) {
            MenuStyle.this.buttonHoverMaterial = material;
            return this;
        }

        /**
         * MiniMessage format for the title, {@code %s} is replaced by the title text.
         * Set to null together with a null/blank menu title to render no heading at all.
         */
        public Builder titleFormat(String format) {
            MenuStyle.this.titleFormat = format;
            return this;
        }

        /** MiniMessage format for button labels, {@code %s} is replaced by the label. */
        public Builder buttonLabelFormat(String format) {
            MenuStyle.this.buttonLabelFormat = format;
            return this;
        }

        /** The character used for the aiming cursor. */
        public Builder cursorGlyph(String glyph) {
            MenuStyle.this.cursorGlyph = glyph;
            return this;
        }

        /** MiniMessage color prefix for the cursor when not hovering a button. */
        public Builder cursorColor(String color) {
            MenuStyle.this.cursorColor = color;
            return this;
        }

        /** MiniMessage color prefix for the cursor while hovering a button. */
        public Builder cursorHoverColor(String color) {
            MenuStyle.this.cursorHoverColor = color;
            return this;
        }

        /** Text scale of the title. */
        public Builder titleScale(float scale) {
            MenuStyle.this.titleScale = scale;
            return this;
        }

        /** Text scale of the aiming cursor. */
        public Builder cursorScale(float scale) {
            MenuStyle.this.cursorScale = scale;
            return this;
        }

        /** Text scale of button labels. */
        public Builder buttonLabelScale(float scale) {
            MenuStyle.this.buttonLabelScale = scale;
            return this;
        }

        /** Item icon scale of buttons. */
        public Builder iconScale(float scale) {
            MenuStyle.this.iconScale = scale;
            return this;
        }

        /** Default width of buttons. */
        public Builder buttonWidth(float width) {
            MenuStyle.this.buttonWidth = width;
            return this;
        }

        /** Default height of buttons. */
        public Builder buttonHeight(float height) {
            MenuStyle.this.buttonHeight = height;
            return this;
        }

        /** Horizontal inset of the icon from the left button edge. */
        public Builder iconOffset(float offset) {
            MenuStyle.this.iconOffset = offset;
            return this;
        }

        /** Sound on menu open, null for silence. */
        public Builder openSound(Sound sound) {
            MenuStyle.this.openSound = sound;
            return this;
        }

        /** Sound on menu close, null for silence. */
        public Builder closeSound(Sound sound) {
            MenuStyle.this.closeSound = sound;
            return this;
        }

        /** Sound on button click, null for silence. */
        public Builder clickSound(Sound sound) {
            MenuStyle.this.clickSound = sound;
            return this;
        }

        /** Sound when the hover target changes, null for silence. */
        public Builder hoverSound(Sound sound) {
            MenuStyle.this.hoverSound = sound;
            return this;
        }

        public Builder openSoundPitch(float pitch) {
            MenuStyle.this.openSoundPitch = pitch;
            return this;
        }

        public Builder closeSoundPitch(float pitch) {
            MenuStyle.this.closeSoundPitch = pitch;
            return this;
        }

        public Builder clickSoundPitch(float pitch) {
            MenuStyle.this.clickSoundPitch = pitch;
            return this;
        }

        public Builder hoverSoundPitch(float pitch) {
            MenuStyle.this.hoverSoundPitch = pitch;
            return this;
        }

        /** Minimum delay in ms between two button clicks (debounce). */
        public Builder clickDelay(long delayMs) {
            MenuStyle.this.clickDelay = delayMs;
            return this;
        }

        /** Creates the style. */
        public MenuStyle build() {
            return MenuStyle.this;
        }
    }

    public double getDistance() {
        return this.distance;
    }

    public double getHorizontalOffset() {
        return this.horizontalOffset;
    }

    public double getVerticalOffset() {
        return this.verticalOffset;
    }

    public float getRowSpacing() {
        return this.rowSpacing;
    }

    public float getPanelWidth() {
        return this.panelWidth;
    }

    public float getHeadingBarWidth() {
        return this.headingBarWidth;
    }

    public float getHeadingBarHeight() {
        return this.headingBarHeight;
    }

    public Material getPanelMaterial() {
        return this.panelMaterial;
    }

    /**
     * Sets the backdrop panel block. Menus that are already open re-render
     * live when redrawn via {@code Menu#redraw()}.
     */
    public MenuStyle setPanelMaterial(Material material) {
        this.panelMaterial = material;
        return this;
    }

    /** May be null (accent bar hidden). */
    public Material getAccentMaterial() {
        return this.accentMaterial;
    }

    public Material getButtonMaterial() {
        return this.buttonMaterial;
    }

    public Material getButtonHoverMaterial() {
        return this.buttonHoverMaterial;
    }

    /**
     * Sets the button block shown when not hovered. Menus that are already
     * open re-render live when redrawn via {@code Menu#redraw()}.
     */
    public MenuStyle setButtonMaterial(Material material) {
        this.buttonMaterial = material;
        return this;
    }

    /**
     * Sets the button block shown when hovered. Menus that are already open
     * re-render live when redrawn via {@code Menu#redraw()}.
     */
    public MenuStyle setButtonHoverMaterial(Material material) {
        this.buttonHoverMaterial = material;
        return this;
    }

    /** May be null (title font applied directly to title text). */
    public String getTitleFormat() {
        return this.titleFormat;
    }

    public String getButtonLabelFormat() {
        return this.buttonLabelFormat;
    }

    public String getCursorGlyph() {
        return this.cursorGlyph;
    }

    public String getCursorColor() {
        return this.cursorColor;
    }

    public String getCursorHoverColor() {
        return this.cursorHoverColor;
    }

    public float getTitleScale() {
        return this.titleScale;
    }

    public float getCursorScale() {
        return this.cursorScale;
    }

    public float getButtonLabelScale() {
        return this.buttonLabelScale;
    }

    public float getIconScale() {
        return this.iconScale;
    }

    public float getButtonWidth() {
        return this.buttonWidth;
    }

    public float getButtonHeight() {
        return this.buttonHeight;
    }

    public float getIconOffset() {
        return this.iconOffset;
    }

    /** May be null (no sound). */
    public Sound getOpenSound() {
        return this.openSound;
    }

    /** May be null (no sound). */
    public Sound getCloseSound() {
        return this.closeSound;
    }

    /** May be null (no sound). */
    public Sound getClickSound() {
        return this.clickSound;
    }

    /** May be null (no sound). */
    public Sound getHoverSound() {
        return this.hoverSound;
    }

    public float getOpenSoundPitch() {
        return this.openSoundPitch;
    }

    public float getCloseSoundPitch() {
        return this.closeSoundPitch;
    }

    public float getClickSoundPitch() {
        return this.clickSoundPitch;
    }

    public float getHoverSoundPitch() {
        return this.hoverSoundPitch;
    }

    public long getClickDelay() {
        return this.clickDelay;
    }
}
