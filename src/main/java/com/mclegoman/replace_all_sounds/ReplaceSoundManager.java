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
	private static final Random random = new Random();
	private static final Map<Identifier, Identifier> soundRegistry = new HashMap<>();
	private static final Map<Identifier, List<Identifier>> eventRegistry = new HashMap<>();
	public static Identifier getSound(Type type, Identifier identifier) {
		Identifier[] identifiers = {
				identifier,
				Identifier.of(identifier.getNamespace(), "all"),
				Identifier.of("all", "all")
		};
		switch (type) {
			case sound -> {
				if (!soundRegistry.isEmpty()) {
					for (Identifier id : identifiers) {
						if (soundRegistry.containsKey(id)) return soundRegistry.get(id);
					}
				}
			}
			case event -> {
				if (!eventRegistry.isEmpty()) {
					for (Identifier id : identifiers) {
						if (eventRegistry.containsKey(id)) {
							List<Identifier> events = eventRegistry.get(id);
							Identifier soundEvent = events.size() > 1 ? events.get(random.nextInt(events.size())) : events.getFirst();
							if (soundEvent.equals(Identifier.of("random"))) {
								List<Identifier> sounds = new ArrayList<>(MinecraftClient.getInstance().getSoundManager().sounds.keySet().stream().toList());
								sounds.removeIf(sound -> sound.getPath().contains("music") || sound.getPath().contains("ambient"));
								soundEvent = sounds.size() > 1 ? sounds.get(random.nextInt(sounds.size())) : sounds.getFirst();
							}
							return soundEvent;
						}
					}
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
