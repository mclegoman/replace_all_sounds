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
	private static final Map<Identifier, Identifier> registry = new HashMap<>();
	public static Identifier getSound(Identifier identifier) {
		if (!registry.isEmpty()) {
			if (registry.containsKey(identifier)) return registry.get(identifier);
			else if (registry.containsKey(Identifier.of(identifier.getNamespace(), "all"))) return registry.get(Identifier.of(identifier.getNamespace(), "all"));
			else if (registry.containsKey(Identifier.of("all", "all"))) return registry.get(Identifier.of("all", "all"));
		}
		return identifier;
	}
	public void reload() {
		registry.clear();
		MinecraftClient.getInstance().getResourceManager().findResources("replace_all_sounds", identifier -> identifier.getPath().endsWith(".json")).forEach((identifier, resource) -> {
			try (InputStream stream = resource.getInputStream()) {
				JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
				JsonObject reader = jsonElement.getAsJsonObject();
				List<Identifier> inputs = new ArrayList<>();
				if (JsonHelper.hasElement(reader, "input")) {
					JsonElement input = JsonHelper.getElement(reader, "input");
					if (input.isJsonArray()) input.getAsJsonArray().forEach((i) -> inputs.add(Identifier.of(i.getAsString())));
					else inputs.add(Identifier.of(input.getAsString()));
				}
				if (JsonHelper.getBoolean(reader, "enabled", true)) {
					for (Identifier input : inputs) {
						if (JsonHelper.hasString(reader, "output")) registry.put(input, Identifier.of(JsonHelper.getString(reader, "output")));
					}
				}
				else registry.remove(Identifier.of(JsonHelper.getString(reader, "input")));
			} catch (Exception error) {
				System.out.println(error.getLocalizedMessage());
			}
		});
	}
}
