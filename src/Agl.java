import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by David on 20/07/2017.
 */
public class Agl {
    public static void main(String[] args) throws Exception {
        Agl app = new Agl();
        app.run();
    }

    public void run() throws Exception {
        String url = "http://agl-developer-test.azurewebsites.net/people.json";
        String json = readJsonFromUrl(url);
        List<Owner> owners = extractOwnersFromJson(json).orElseThrow(()->new Exception("Parse error"));
        displayPetNamesBySexOfOwner("Male",owners);
        displayPetNamesBySexOfOwner("Female",owners);
        displayPetNamesBySexOfOwner("Trans",owners);
    }

    public String readJsonFromUrl(String url) {
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
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonBuilder.toString();
    }

    public Optional<List<Owner>> extractOwnersFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        List<Owner> owners = null;
        try {
            owners = mapper.readValue(json, new TypeReference<List<Owner>>() {});
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(owners);
    }

    public void displayPetNamesBySexOfOwner(final String sex, List<Owner> owners) {
        System.out.println(sex);
        owners.stream()
                .filter(t -> t.getGender().equals(sex))
                .map(Owner::getPets)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
                .stream()
                .map(Pet::getName)
                .sorted()
                .forEach(name -> System.out.format("\t%s\n", name));
    }
}
