/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.scripting;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Scripts extends System<Scripts> implements Iterable<Script> {
    private List<Script> scripts = new ArrayList<>();

    public Scripts() {
        super("scripts");
    }

    public static Scripts get() {
        return Systems.get(Scripts.class);
    }

    @Override
    public void init() {
        File scriptsFolder = new File(MeteorClient.FOLDER, "scripts");
        scriptsFolder.mkdir();

        File[] files = scriptsFolder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.getName().endsWith(".kts")) {
                scripts.add(new Script(file));
            }
        }
    }

    public void add(Script script) {
        scripts.add(script);
        save();
    }

    public Script get(String name) {
        for (Script script : scripts) {
            if (script.name.get().equalsIgnoreCase(name)) return script;
        }

        return null;
    }

    public List<Script> getAll() {
        return scripts;
    }

    public void remove(Script script) {
        if (scripts.remove(script)) {
            script.file.delete();
            MeteorClient.EVENT_BUS.unsubscribe(script);
            save();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKey(KeyEvent event) {
        if (event.action == KeyAction.Release) return;

        for (Script script : scripts) {
            if (script.onAction(true, event.key, event.modifiers)) return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onButton(MouseButtonEvent event) {
        if (event.action == KeyAction.Release) return;

        for (Script script : scripts) {
            if (script.onAction(false, event.button, 0)) return;
        }
    }

    public boolean isEmpty() {
        return scripts.isEmpty();
    }

    @Override
    public @NotNull Iterator<Script> iterator() {
        return scripts.iterator();
    }
}
