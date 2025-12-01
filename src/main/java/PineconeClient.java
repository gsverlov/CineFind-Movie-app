import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PineconeClient {
    private final String apiKey;
    private final String indexHost;

    /**
     * Constructor using direct host URL
     * @param apiKey pinecone api key
     * @param indexHost host URL
     */
    public PineconeClient(String apiKey, String indexHost) {
        this.apiKey = apiKey;
        this.indexHost = indexHost.endsWith("/") ? indexHost.substring(0, indexHost.length() - 1) : indexHost;
    }

    /**
     * @param apiKey Pinecone API key
     * @param environment Pinecone environment
     * @param indexName index name
     */
    public PineconeClient(String apiKey, String environment, String indexName) {
        this.apiKey = apiKey;
        this.indexHost = String.format("https://%s.svc.%s.pinecone.io", indexName, environment);
    }

    /**
     * Upsert (insert or update) a vector into Pinecone
     */
    public void upsertVector(String id, float[] vector, JSONObject metadata) throws Exception {
        URL url = new URL(indexHost + "/vectors/upsert");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Api-Key", apiKey);
        conn.setDoOutput(true);

        //building vector object
        JSONObject vectorObj = new JSONObject();
        vectorObj.put("id", id);

        JSONArray vectorArray = new JSONArray();
        for (float v : vector) {
            vectorArray.put(v);
        }
        vectorObj.put("values", vectorArray);

        if (metadata != null) {
            vectorObj.put("metadata", metadata);
        }

        //building request body
        JSONObject requestBody = new JSONObject();
        JSONArray vectors = new JSONArray();
        vectors.put(vectorObj);
        requestBody.put("vectors", vectors);

        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();

        // Read response (for both success and error)
        BufferedReader br;
        if (responseCode == 200) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        conn.disconnect();

        if (responseCode != 200) {
            throw new Exception("Pinecone upsert error (" + responseCode + "): " + response.toString());
        }
    }
    /**
     * Query Pinecone for similar vectors
     */
    public List<PineconeMatch> query(float[] queryVector, int topK) throws Exception {
        URL url = new URL(indexHost + "/query");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Api-Key", apiKey);
        conn.setDoOutput(true);

        // Build query vector array
        JSONArray vectorArray = new JSONArray();
        for (float v : queryVector) {
            vectorArray.put(v);
        }

        // Build request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("vector", vectorArray);
        requestBody.put("topK", topK);
        requestBody.put("includeMetadata", true);

        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();

        // Read response
        BufferedReader br;
        if (responseCode == 200) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        br.close();
        conn.disconnect();

        if (responseCode != 200) {
            throw new Exception("Pinecone query error (" + responseCode + "): " + response.toString());
        }

        // Parse matches
        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray matches = jsonResponse.getJSONArray("matches");

        List<PineconeMatch> results = new ArrayList<>();
        for (int i = 0; i < matches.length(); i++) {
            JSONObject match = matches.getJSONObject(i);
            String id = match.getString("id");
            double score = match.getDouble("score");
            JSONObject metadata = match.optJSONObject("metadata");

            results.add(new PineconeMatch(id, score, metadata));
        }

        return results;
    }

    /**
     * Helper class to represent a Pinecone match result
     */
    public static class PineconeMatch {
        public String id;
        public double score;
        public JSONObject metadata;

        public PineconeMatch(String id, double score, JSONObject metadata) {
            this.id = id;
            this.score = score;
            this.metadata = metadata;
        }
    }
}