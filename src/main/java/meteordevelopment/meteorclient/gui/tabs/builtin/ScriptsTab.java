/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.tabs.builtin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.systems.scripting.Script;
import meteordevelopment.meteorclient.systems.scripting.Scripts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Util;
import oshi.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ScriptsTab extends Tab {
    public ScriptsTab() {
        super("Scripts");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new ScriptsScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof ScriptsScreen;
    }

    private static class ScriptsScreen extends WindowTabScreen {
        public ScriptsScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {
            WTable table = add(theme.table()).expandX().minWidth(400).widget();
            initTable(table);

            add(theme.horizontalSeparator()).expandX();

            // Create
            WButton create = add(theme.button("Create")).expandX().widget();
            create.action = () -> mc.setScreen(new EditScriptScreen(theme, null, this::reload));
        }

        private void initTable(WTable table) {
            table.clear();
            if (Scripts.get().isEmpty()) return;

            for (Script script : Scripts.get()) {
                table.add(theme.label(script.name.get())).expandCellX();

                WButton run = table.add(theme.button("Run")).widget();
                run.action = script::run;

                WButton folder = table.add(theme.button(GuiRenderer.FOLDER)).widget();
                folder.action = () -> {
                    try {
                        Util.getOperatingSystem().open(script.file);
                    } catch (UnsupportedOperationException e) {
                        MeteorClient.LOG.error("Failed to open script file {}", String.valueOf(e));
                    }
                };

                WButton edit = table.add(theme.button(GuiRenderer.EDIT)).widget();
                edit.action = () -> mc.setScreen(new EditScriptScreen(theme, script, this::reload));

                WMinus remove = table.add(theme.minus()).widget();
                remove.action = () -> {
                    Scripts.get().remove(script);
                    reload();
                };

                table.row();
            }
        }
    }

    private static class EditScriptScreen extends WindowScreen {
        private WContainer settingsContainer;
        private final Script script;
        private final boolean isNew;
        private final Runnable action;

        public EditScriptScreen(GuiTheme theme, Script script, Runnable action) {
            super(theme, script == null ? "New Script" : "Edit Script");

            this.isNew = script == null;
            this.script = isNew ? new Script() : script;
            this.action = action;
        }

        @Override
        public void initWidgets() {
            settingsContainer = add(theme.verticalList()).expandX().minWidth(400).widget();
            settingsContainer.add(theme.settings(script.settings)).expandX();

            add(theme.horizontalSeparator()).expandX();

            WButton save = add(theme.button(isNew ? "Create" : "Save")).expandX().widget();
            save.action = () -> {
                if (script.name.get().isEmpty()) return;

                if (isNew) {
                    for (Script p : Scripts.get()) {
                        if (script.equals(p)) return;
                    }

                    script.load();
                    if (!script.file.exists()) {
                        try {
                            script.file.createNewFile();
                            Files.write(script.file.toPath(), "lib.info(\"Hello, World!\")".getBytes(StandardCharsets.UTF_8));
                        } catch (IOException e) {
                            MeteorClient.LOG.error("Failed to create script file {}", String.valueOf(e));
                        }
                    }
                    Scripts.get().add(script);
                }
                else Scripts.get().save();

                close();
            };

            enterAction = save.action;
        }

        @Override
        public void tick() {
            script.settings.tick(settingsContainer, theme);
        }

        @Override
        protected void onClosed() {
            if (action != null) action.run();
        }
    }
}
