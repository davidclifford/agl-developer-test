package main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by David on 20/07/2017.
 */
public class Agl {
    public static void main(String[] args) {
        Agl app = new Agl();
        app.run();
    }

    private void run() {
        String url = "http://agl-developer-test.azurewebsites.net/people.json";
        String json = readJsonFromUrl(url);
        List<Owner> owners = extractOwnersFromJson(json);
        displayPetNamesBySexOfOwner("Male",owners);
        displayPetNamesBySexOfOwner("Female",owners);
    }

    public String readJsonFromUrl(String url) {
        StringBuilder jsonBuilder = new StringBuilder();

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet(url);
            getRequest.addHeader("accept", "application/json");
            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Error reading from url, status code : " + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            jsonBuilder.append(br.lines().collect(Collectors.joining()));

            httpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return jsonBuilder.toString();
    }

    public List<Owner> extractOwnersFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        List<Owner> owners;
        try {
            owners = mapper.readValue(json, new TypeReference<List<Owner>>() {});
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return owners;
    }

    public void displayPetNamesBySexOfOwner(String gender, List<Owner> owners) {
        System.out.println(gender);
        getPetNamesBySexOfOwner(gender, owners)
            .forEach(name -> System.out.format("\t%s\n", name));
    }

    public List<String> getPetNamesBySexOfOwner(String gender, List<Owner> owners) {
        return owners.stream()
                .filter(owner -> owner.getGender().equals(gender))
                .map(Owner::getPets)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(Pet::getName)
                .sorted()
                .collect(Collectors.toList());
    }
}
