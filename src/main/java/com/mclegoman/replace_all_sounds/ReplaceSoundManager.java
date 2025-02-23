package com.mclegoman.replace_all_sounds;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ReplaceSoundManager {
	private static final Map<Identifier, Identifier> soundRegistry = new HashMap<>();
	private static final Map<Identifier, List<Identifier>> eventRegistry = new HashMap<>();
	public static Identifier getSound(Type type, Identifier identifier) {
		switch (type) {
			case sound -> {
				if (!soundRegistry.isEmpty()) {
					if (soundRegistry.containsKey(identifier)) return soundRegistry.get(identifier);
					else if (soundRegistry.containsKey(Identifier.of(identifier.getNamespace(), "all"))) return soundRegistry.get(Identifier.of(identifier.getNamespace(), "all"));
					else if (soundRegistry.containsKey(Identifier.of("all", "all"))) return soundRegistry.get(Identifier.of("all", "all"));
				}
			}
			case event -> {
				if (!eventRegistry.isEmpty()) {
					Random random = new Random();
					if (eventRegistry.containsKey(identifier)) return eventRegistry.get(identifier).get(random.nextInt(0, eventRegistry.size()));
					else if (eventRegistry.containsKey(Identifier.of(identifier.getNamespace(), "all"))) return eventRegistry.get(Identifier.of(identifier.getNamespace(), "all")).get(random.nextInt(0, eventRegistry.size()));
					else if (eventRegistry.containsKey(Identifier.of("all", "all"))) return eventRegistry.get(Identifier.of("all", "all")).get(random.nextInt(0, eventRegistry.size()));
				}
			}
		}
		return identifier;
	}
	public void reload() {
		soundRegistry.clear();
		eventRegistry.clear();
		MinecraftClient.getInstance().getResourceManager().findResources("replace_all_sounds", identifier -> identifier.getPath().endsWith(".json")).forEach((identifier, resource) -> {
			try (InputStream stream = resource.getInputStream()) {
				JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
				JsonObject reader = jsonElement.getAsJsonObject();
				List<Identifier> inputs = new ArrayList<>();
				Type type = getType(JsonHelper.getString(reader, "type", "sound"));
				if (JsonHelper.hasElement(reader, "input")) {
					JsonElement input = JsonHelper.getElement(reader, "input");
					if (input.isJsonArray()) input.getAsJsonArray().forEach((i) -> inputs.add(Identifier.of(i.getAsString())));
					else inputs.add(Identifier.of(input.getAsString()));
				} else throw new Exception("Missing 'input' string.");
				if (JsonHelper.getBoolean(reader, "enabled", true)) {
					for (Identifier input : inputs) {
						switch (type) {
							case sound -> {
								if (JsonHelper.hasString(reader, "output")) soundRegistry.put(input, Identifier.of(JsonHelper.getString(reader, "output")));
								else throw new Exception("Missing 'output' string.");
							}
							case event -> {
								if (JsonHelper.hasString(reader, "output")) eventRegistry.put(input, List.of(Identifier.of(JsonHelper.getString(reader, "output"))));
								else if (JsonHelper.hasArray(reader, "output")) {
									List<Identifier> outputs = new ArrayList<>();
									JsonHelper.getArray(reader, "output").forEach(o -> outputs.add(Identifier.of(o.getAsString())));
									eventRegistry.put(input, outputs);
								}
								else throw new Exception("Missing 'output' element.");
							}
						}
					}
				}
				else {
					for (Identifier input : inputs) {
						switch (type) {
							case sound -> soundRegistry.remove(input);
							case event -> eventRegistry.remove(input);
						}
					}
				}
			} catch (Exception error) {
				System.out.println("Error loading sound replacement '" + identifier + "': " + error.getLocalizedMessage());
			}
		});
	}
	private static final Map<String, Type> nameToType = new HashMap<>();
	public enum Type {
		sound("sound"),
		event("sound_event");
		private final String name;
		Type(String name) {
			this.name = name;
			ReplaceSoundManager.nameToType.put(name, this);
		}
		public String toString() {
			return name;
		}
	}
	public Type getType(String name) {
		return nameToType.get(name);
	}
}
