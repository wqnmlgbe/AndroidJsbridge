package open.cklan.com.jsbridge.util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * AUTHOR：lanchuanke on 17/8/24 10:06
 */
public class JsonUtils {
    public static boolean isJson(String target) {
        if (TextUtils.isEmpty(target))
            return false;

        boolean tag = false;
        try {
            if (target.startsWith("["))
                new JSONArray(target);
            else
                new JSONObject(target);

            tag = true;
        } catch (JSONException igonre) {
            tag = false;
        }

        return tag;

    }
}
