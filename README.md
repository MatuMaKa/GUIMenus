> [!NOTE]
> This library depends on NMS internals through Paper's dev bundle, so **it must be used on Paper 1.21.8+** (Paper servers run mojang-mapped runtime, no relocations needed). It is **not** compatible with Spigot.

# GUIMenus

Head-tracked, display-entity GUI menus for Paper servers. Instead of chest inventories or floating armor-stand keyboards, the menu is anchored in front of the player, follows their look direction, and is operated with the mouse cursor (attack = click, sneak = close). Great for immersive main menus on lobby servers.

<!-- TODO: add a GIF/demo of the menu in action -->

**Table of contents**

- [Installation](#installation)
- [Usage](#usage)
- [Customization](#customization)
- [Everything the API exposes](#everything-the-api-exposes)
- [Events](#events)
- [Notes & caveats](#notes--caveats)
- [Building this repo](#building-this-repo)

---

<a id="installation"></a>
## Installation

[![Release](https://jitpack.io/v/MatuMaKa/GUIMenus.svg)](https://jitpack.io/#MatuMaKa/GUIMenus)

Grab a version from the JitPack site: **[jitpack.io/#MatuMaKa/GUIMenus](https://jitpack.io/#MatuMaKa/GUIMenus)** — each page shows the build status and available tags; current release is **v1.2**.

Since JitPack coordinates just wrap *your* repository, the snippets below use the canonical repo URL.

As a dependency in your `build.gradle`:

```groovy
repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = "https://repo.papermc.io/repository/maven-public/"
    }
    maven {
        name = "jitpack"
        url = "https://jitpack.io"
    }
}

dependencies {
    compileOnly "com.github.MatuMaKa:GUIMenus:VERSION"
}
```

For `build.gradle.kts`:

```kotlin
dependencies {
    compileOnly("com.github.MatuMaKa:GUIMenus:VERSION")
}
```

Maven:

```xml
<repositories>
    <repository>
        <id>jitpack</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.MatuMaKa</groupId>
    <artifactId>GUIMenus</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

The API also works **inside your own plugin jar** (no relocation needed at runtime because it is `compileOnly`).

---

<a id="usage"></a>
## Usage

```java
import com.matumaka.guimenus.GUIMenusAPI;
import com.matumaka.guimenus.gui.Menu;
import com.matumaka.guimenus.Utils;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // One-time initialization: hooks the API into your plugin.
        GUIMenusAPI.init(this);
    }

    @Override
    public void onDisable() {
        // Close any open menus on shutdown (releases entities & camera).
        GUIMenusAPI.shutdown();
    }

    public void showMainMenu(Player player) {
        final Menu menu = new Menu("MAIN MENU");

        menu.registerButton("Close", () -> {
            menu.close();
            player.showTitle(
                Title.title(
                    Utils.toComponent("<aqua>TITLE"),
                    Utils.toComponent("<gray>SUBTITLE")
                )
            );
        });

        menu.registerButton("Give me a golden apple", Material.GOLDEN_APPLE, () -> {
            player.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.GOLDEN_APPLE));
        });

        menu.displayTo(player);
    }
}
```

Labels are parsed with [MiniMessage](https://docs.advntr.dev/minimessage.html), so gradients (`<gradient:#6ee7ff:#3fa9f5>`), `<bold>`, `<red>`, etc. all work — including in the menu title passed to `new Menu(...)`.

![Example](https://raw.githubusercontent.com/MatuMaKa/images/refs/heads/main/guimenus.png)

---

<a id="customization"></a>
## Customization

Every visual and behavioral detail of a menu can be changed through [`MenuStyle`](src/main/java/com/matumaka/guimenus/gui/MenuStyle.java), and individual buttons can override any of these via `Button.builder(...)`:

### Menu-level (`MenuStyle`)
- **Position** — `distance` (how far in front of the face), `horizontalOffset`, `verticalOffset`, `rowSpacing`, `panelWidth`
- **Blocks** — `panelMaterial`, `accentMaterial` (title underline; `null` hides it), `buttonMaterial`, `buttonHoverMaterial`
- **Text (MiniMessage)** — `titleFormat` (with `%s` placeholder), `buttonLabelFormat`, cursor glyph/colors (`cursorGlyph`, `cursorColor`, `cursorHoverColor`)
- **Scales** — `titleScale`, `cursorScale`, `buttonLabelScale`, `iconScale`, `headingBarWidth`, `headingBarHeight`
- **Behavior** — `clickDelay` (ms debounce), whether sneaking closes the menu (`sneakCloses`), sounds `openSound` / `closeSound` / `clickSound` / `hoverSound` (each `null`-able for silence) with matching `*Pitch` setters

```java
MenuStyle style = MenuStyle.builder()
        .distance(3.2)                     // push the menu out further
        .verticalOffset(-0.4)              // sit the menu lower in view
        .titleFormat("<gold><bold>%s")
        .buttonMaterial(Material.PURPLE_CONCRETE)
        .buttonHoverMaterial(Material.MAGENTA_CONCRETE)
        .clickSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
        .build();

Menu menu = new Menu("SERVER SELECT", style);
```

**Disabling sneak-to-close** — players close the menu by sneaking by default; if a menu should only be closable through its own buttons, disable it on the style:

```java
MenuStyle style = MenuStyle.builder().sneakCloses(false).build();
// or at runtime: style.setSneakCloses(false);
```

**Changing the look while a menu is open** — the panel and button backgrounds are mutable: update them on the style and call `Menu#redraw()` to re-render the open menu in place (no close/reopen, no flicker, no open sound replay):

```java
style.setPanelMaterial(Material.BLUE_CONCRETE);      // menu background
style.setButtonMaterial(Material.WHITE_CONCRETE);    // button background (resting)
style.setButtonHoverMaterial(Material.CYAN_CONCRETE); // button background (hovered)
menu.redraw();                                       // applies immediately
```

A menu can also be built **without a heading at all**:

```java
Menu menu = new Menu(MenuStyle.builder().rowSpacing(0.6F).build()); // no title
```

### Button-level (`Button.builder(...)`)
Per-button overrides: `width`, `height`, `material` / `hoverMaterial`, `labelFormat`, `labelScale`, `iconScale`, plus `icon`, `action`. Anything unset inherits from the menu's style:

```java
menu.registerButton(
    Button.builder("Back")
          .icon(Material.ARROW)
          .action(() -> previous.displayTo(player))
          .width(1.8F)
          .labelFormat("<yellow>%s")
          .build()
);
```

---

<a id="everything-the-api-exposes"></a>
## Everything the API exposes

### `GUIMenusAPI` (entry point)
- `init(Plugin)` — registers internal listeners and the per-tick targeting task on your plugin. **Required once** before any menu can be shown.
- `shutdown()` — closes every open menu; call from `onDisable()`.
- `getPlugin()` — the `Plugin` the API was initialized with.

### `Menu`
- `new Menu(String minimessageTitle)` — classic constructor; `new Menu(String, MenuStyle)` or `new Menu(MenuStyle)` for custom looks (a null/blank title renders no heading)
- `registerButton(String label, Runnable action)`
- `registerButton(String label, Material icon, Runnable action)` — shows an item-display icon left of the label
- `registerButton(Button)` — fully customized button (see [Customization](#customization))
- `displayTo(Player)` — opens the menu; automatically mounts the camera rig if the player does not already have one open, or transitions seamlessly if they do (multi-page flows just call `displayTo` again)
- `redraw()` — re-renders the open menu in place after mutating its `MenuStyle` (e.g. changed background blocks)
- `close()` — unmounts everything and restores the player
- `getStyle()` — this menu's `MenuStyle`
- Low-level hooks: overloaded `spawnText` / `spawnBlock` / `spawnItem` (WebGL-free, style offsets applied automatically)
- Static: `Menu.getMenu(Player)`, `Menu.closeAll()`, `Menu.tickAll()` (called for you once initialized)

### `MenuStyle` & `Button`
- `MenuStyle.builder()` — full customization of the menu look/behavior, see [Customization](#customization)
- `setPanelMaterial` / `setButtonMaterial` / `setButtonHoverMaterial` — mutable background blocks on an existing style (pair with `Menu#redraw()`)
- `Button.builder(label)` — per-button overrides; setters (`setLabel`, `setIcon`, `setAction`) also exist for convenience
### `Utils`
- `toComponent(String)` — MiniMessage deserialization
- `setCamera(Player, Entity)` — NMS camera hijack via `ClientboundSetCameraPacket`
- `playSound(Player, Sound, float pitch)`
- `toWorld(Location eye, x, y, distance)` — projects a screen-space (x, y) offset into world space in front of an eye location
- `project(float angle, double distance, double edge)` — the inverse: maps a view angle to screen-space x/y

---

<a id="events"></a>
## Events

Every stage of a menu's lifecycle fires a Bukkit event in `com.matumaka.guimenus.events`, all exposing the `Player` via `event.getPlayer()` and the `Menu` via `event.getMenu()`:

| Event | Cancelling it | Fired when |
|---|---|---|
| `MenuOpenEvent` | prevents the menu from being shown | just before a menu is displayed to the player |
| `MenuClickEvent` | stops the button's action + click sound | a hovered button is clicked |
| `MenuCloseEvent` | informational (not cancellable) | after a menu closed and the player was restored |

`MenuCloseEvent#getReason()` tells you *why* it closed: `BUTTON`, `SNEAK`, `TELEPORT`, `DEATH`, `QUIT` or `SHUTDOWN`.

```java
@EventHandler
public void onMenuOpen(MenuOpenEvent event) {
    if (isBanned(event.getPlayer())) {
        event.setCancelled(true);
    }
}

@EventHandler
public void onMenuClose(MenuCloseEvent event) {
    if (event.getReason() == MenuCloseEvent.Reason.DEATH) {
        // e.g. re-open the menu after the player respawns
    }
}
```

---
## Notes & caveats

- Close handling out of the box: sneak closes (disable per style with `sneakCloses(false)`), and death, quit, teleporting all close the menu. A self-interaction kick-event is cancelled while in a menu so the player isn't accidentally disconnected.
- The menu lifecycle fires Bukkit events under `com.matumaka.guimenus.events` — `MenuOpenEvent` (cancellable), `MenuClickEvent` (cancellable) and `MenuCloseEvent` (with a close `Reason`). See [Events](#events).
- While a menu is open the player is put briefly invisible and mounted on an invisible pig used as a camera tripod; movement is normal (it steers the cursor), sneak closes the menu.
- Menus are per-player; the target player and open menus are automatically tracked. Everything is cleaned up on quit/disable.
- Timing: `displayTo` re-uses an existing camera; opening a new menu over an existing one is seamless (no flicker).

---

## Building this repo

```bash
./gradlew build        # library jar + sources + javadoc + -demo/-all jars
./gradlew publishToMavenLocal
```

Requires Java 21. The demo plugin jar built from `src/demo` will be in `build/libs/GUIMenus-1.2-demo.jar`.

## License

MIT License — see [LICENSE](LICENSE).
