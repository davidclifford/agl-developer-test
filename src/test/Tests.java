package test;

import main.Agl;
import main.Owner;
import main.Pet;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class Tests {

    private Agl app = new Agl();

    @Test
    public void testReadJsonFromUrl() {
        String url = "http://agl-developer-test.azurewebsites.net/people.json";
        String json = app.readJsonFromUrl(url);
        assertNotNull(json);
        assertEquals(json.contains("Garfield"), true);
    }

    @Test(expected = RuntimeException.class)
    public void testReadJsonFromUrlWithInvalidURLThrowsException() {
        String url = "http://agl-developer-test.azurewebsites.net/people.jsonx";
        String json = app.readJsonFromUrl(url);
    }

    @Test
    public void testExtractOwnersFromJson() {
        List<Owner> owners = app.extractOwnersFromJson("[{\"name\":\"Bob\",\"gender\":\"Male\",\"age\":23,\"pets\":[{\"name\":\"Garfield\",\"type\":\"Cat\"},{\"name\":\"Fido\",\"type\":\"Dog\"}]}]");
        assertEquals(owners.size(),1);
        assertEquals(owners.get(0).getGender(),"Male");
        assertEquals(owners.get(0).getPets().size(),2);
        assertEquals(owners.get(0).getPets().get(0).getName(),"Garfield");
        assertEquals(owners.get(0).getPets().get(1).getName(),"Fido");
    }

    @Test
    public void testGetPetNamesByMaleOwner() {
        List<Owner> owners = buildOwnerList();
        List<String> names = app.getPetNamesBySexOfOwner("Male",owners);
        assertEquals(names.size(),2);
        assertEquals(names.get(0),"Fido");
        assertEquals(names.get(1),"Garfield");
    }

    @Test
    public void testGetPetNamesByFemaleOwner() {
        List<Owner> owners = buildOwnerList();
        List<String> names = app.getPetNamesBySexOfOwner("Female",owners);
        assertEquals(names.size(),1);
        assertEquals(names.get(0),"Garfield");
    }

    @Test
    public void testGetPetNamesByInvalidGender() {
        List<Owner> owners = buildOwnerList();
        List<String> names = app.getPetNamesBySexOfOwner("Invalid",owners);
        assertEquals(names.size(),0);
    }

    private List<Owner> buildOwnerList() {
        List<Owner> owners = new ArrayList<>();
        List<Pet> bobsPets = new ArrayList<>();
        List<Pet> jensPets = new ArrayList<>();

        Pet dog = new Pet();
        dog.setName("Fido");
        dog.setType("Dog");

        Pet cat = new Pet();
        cat.setName("Garfield");
        cat.setType("Cat");

        bobsPets.add(cat);
        bobsPets.add(dog);

        Owner bob = new Owner();
        bob.setName("Bob");
        bob.setGender("Male");
        bob.setAge(23);
        bob.setPets(bobsPets);
        owners.add(bob);

        jensPets.add(cat);

        Owner jennifer = new Owner();
        jennifer.setName("Jennifer");
        jennifer.setGender("Female");
        jennifer.setAge(18);
        jennifer.setPets(jensPets);
        owners.add(jennifer);

        Owner nopets = new Owner();
        nopets.setName("No Pets");
        nopets.setAge(39);
        nopets.setGender("Male");
        nopets.setPets(null);
        owners.add(nopets);

        return owners;
    }
}
