package gator.lib.sec;

import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.TreeMap;

/** Server-owned destination allowlist shared by bearer APIs and mobile screens. */
public final class GatorOidcAssignments {
    private GatorOidcAssignments() { }

    public record Target(String index, String accessClientId) {
        public Target {
            if (index == null || !index.matches("[A-Za-z0-9][A-Za-z0-9._-]*")
                    || accessClientId == null || (!accessClientId.isEmpty()
                        && !accessClientId.matches("[A-Za-z0-9][A-Za-z0-9._-]*")))
                throw new IllegalArgumentException("Invalid destination assignment");
        }
        public boolean linked() { return !accessClientId.isEmpty(); }
    }

    public static Map<String, Map<String, Target>> parse(String json) {
        try (JsonReader reader = new JsonReader(new StringReader(json))) {
            reader.setStrictness(Strictness.STRICT);
            Map<String, Map<String, Target>> clients = new TreeMap<>();
            reader.beginObject();
            while (reader.hasNext()) {
                String client = reader.nextName();
                if (client.isBlank()) throw new IllegalArgumentException();
                Map<String, Target> targets = new TreeMap<>();
                reader.beginObject();
                while (reader.hasNext()) {
                    String context = reader.nextName();
                    if (!context.matches("gator[ews][A-Za-z0-9_-]+")) throw new IllegalArgumentException();
                    Target target;
                    if (reader.peek() == JsonToken.STRING) {
                        target = new Target(reader.nextString(), "");
                    } else {
                        String index = null, access = null;
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String field = reader.nextName();
                            if (reader.peek() != JsonToken.STRING) throw new IllegalArgumentException();
                            if (field.equals("index") && index == null) index = reader.nextString();
                            else if (field.equals("accessClientId") && access == null) access = reader.nextString();
                            else throw new IllegalArgumentException();
                        }
                        reader.endObject();
                        if (access == null || access.isBlank()) throw new IllegalArgumentException();
                        target = new Target(index, access);
                    }
                    if (targets.putIfAbsent(context, target) != null) throw new IllegalArgumentException();
                }
                reader.endObject();
                if (targets.isEmpty() || clients.putIfAbsent(client, Map.copyOf(targets)) != null)
                    throw new IllegalArgumentException();
            }
            reader.endObject();
            if (clients.isEmpty() || reader.peek() != JsonToken.END_DOCUMENT) throw new IllegalArgumentException();
            return Map.copyOf(clients);
        } catch (IOException | RuntimeException invalid) {
            throw new IllegalArgumentException("Invalid OIDC destination assignments");
        }
    }
}
