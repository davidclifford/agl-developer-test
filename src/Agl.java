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
import java.util.Optional;

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
        String json = readJsonFromUrl(url).orElseThrow(()->new RuntimeException("Json read error"));;
        List<Owner> owners = extractOwnersFromJson(json).orElseThrow(()->new RuntimeException("Json parse error"));
        displayPetNamesBySexOfOwner("Male",owners);
        displayPetNamesBySexOfOwner("Female",owners);
    }

    private Optional<String> readJsonFromUrl(String url) {
        StringBuilder jsonBuilder = new StringBuilder();
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet(url);

            getRequest.addHeader("accept", "application/json");
            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String output;
            while ((output = br.readLine()) != null) {
                jsonBuilder.append(output);
            }

            httpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(jsonBuilder.toString());
    }

    private Optional<List<Owner>> extractOwnersFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        List<Owner> owners = null;
        try {
            owners = mapper.readValue(json, new TypeReference<List<Owner>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.ofNullable(owners);
    }

    private void displayPetNamesBySexOfOwner(final String sex, List<Owner> owners) {
        System.out.println(sex);
        owners.stream()
                .filter(owner -> owner.getGender().equals(sex))
                .map(Owner::getPets)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(Pet::getName)
                .sorted()
                .forEach(name -> System.out.format("\t%s\n", name));
    }
}
