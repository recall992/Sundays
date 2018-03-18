import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static final String getDate() {
        SimpleDateFormat format = new SimpleDateFormat();
        return format.format(new Date());
    }
}
