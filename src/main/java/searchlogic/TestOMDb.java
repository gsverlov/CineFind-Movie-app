package searchlogic;

import apiservices.OMDbApiClient;

public class TestOMDb {
    public static void main(String[] args) {
        String apiKey = "51f8a124"; // replace with your key
        OMDbApiClient client = new OMDbApiClient(apiKey);

        try {
            String result = client.searchMovies("Batman");
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("placeholder");
        }
    }
}
