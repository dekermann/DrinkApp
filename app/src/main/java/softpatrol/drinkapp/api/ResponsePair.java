package softpatrol.drinkapp.api;

/**
 * Created by Hugo on 2014-07-08.
 */
public class ResponsePair
{
    private int status;
    private String result;

    public ResponsePair(int status, String result) {
        this.status = status;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}