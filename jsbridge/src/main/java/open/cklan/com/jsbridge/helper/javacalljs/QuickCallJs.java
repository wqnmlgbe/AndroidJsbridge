package open.cklan.com.jsbridge.helper.javacalljs;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.ValueCallback;

public interface QuickCallJs {
    void quickCallJs(String method, ValueCallback<String> callback, String... params);
    void quickCallJs(String method, String... params);
    void quickCallJs(String method);
}
