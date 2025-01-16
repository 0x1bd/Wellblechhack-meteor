package meteordevelopment.meteorclient.systems.scripting;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Colors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;

public class Script implements ISerializable<Script> {

    public final Settings settings = new Settings();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<String> name = sgGeneral.add(new StringSetting.Builder()
        .name("name")
        .description("The name of the script (also the file's name).")
        .build()
    );

    public final Setting<Keybind> keybind = sgGeneral.add(new KeybindSetting.Builder()
        .name("keybind")
        .description("The bind to run the script.")
        .build()
    );

    public File file;

    public Script() {
    }

    public Script(File file) {
        load(file);
        name.set(file.getName());
    }

    public Script(NbtElement tag) {
        fromTag((NbtCompound) tag);
        file = new File(Scripts.get().DIRECTORY, name.get());
    }

    public boolean onAction(boolean isKey, int value, int modifiers) {
        if (!keybind.get().matches(isKey, value, modifiers)) return false;
        return onAction();
    }

    public boolean onAction() {
        run();
        return true;
    }

    public void load(File file) {
        this.file = file;
        MeteorClient.LOG.info("Loaded script: {}", file.getName());
    }

    public void load() {
        load(new File(Scripts.get().DIRECTORY, name.get()));
    }

    private final ScriptingStandardLibrary stdLib = new ScriptingStandardLibrary();

    public void run() {
        if (file == null) {
            MeteorClient.LOG.error("No script loaded to execute.");
            return;
        }

        new Thread(() -> {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("kotlin");

            if (engine == null) {
                stdLib.error("Kotlin script engine not found.");
                return;
            }

            engine.put("wbh", stdLib);

            try (FileReader reader = new FileReader(file)) {
                engine.eval(reader);
                MeteorClient.LOG.info("Executed script: {}", file.getName());
            } catch (Exception e) {
                stdLib.error("Failed to execute script " + file.getName() + ": " + e.getMessage());
                MeteorClient.LOG.error("Failed to execute script {}: {}", file.getName(), e.getMessage(), e);
            }
        }).start();
    }

    public void reload() {
        if (file == null) {
            MeteorClient.LOG.error("No script loaded to reload.");
            stdLib.error("No script loaded to reload.");
            return;
        }

        load(file);
        MeteorClient.LOG.info("Reloaded script: {}", file.getName());
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.put("settings", settings.toTag());
        return tag;
    }

    @Override
    public Script fromTag(NbtCompound tag) {
        if (tag.contains("settings")) {
            settings.fromTag(tag.getCompound("settings"));
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Script script = (Script) o;
        return Objects.equals(script.name.get(), this.name.get());
    }
}
