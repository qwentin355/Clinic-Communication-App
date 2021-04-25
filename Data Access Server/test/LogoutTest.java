import com.fasterxml.jackson.core.JsonProcessingException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;



import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class LogoutTest {
    private static final String SESSIONID = "1";
    private static final String USERNAME = "admin";


    @Test
  public void ValidLogout() throws JsonProcessingException {
               HttpResponse<String> response = Unirest.post("http://localhost:7001/logout")
                                .field("sessionID", SESSIONID)
                                .field("username", USERNAME)
                                .asString();
                assertThat(response.getStatus()).isEqualTo(404);


    }
    @Test
    public void InvalidLogout() throws JsonProcessingException {
        HttpResponse<String> response = Unirest.post("http://localhost:7001/logout")
                .field("sessionID", 25)
                .field("username", "test user")
                .asString();
        assertThat(response.getStatus()).isEqualTo(404);
    }

}


