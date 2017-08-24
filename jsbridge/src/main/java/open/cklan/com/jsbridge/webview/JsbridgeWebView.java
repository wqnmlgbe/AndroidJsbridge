package open.cklan.com.jsbridge.webview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import open.cklan.com.jsbridge.helper.JsCallJava;
import open.cklan.com.jsbridge.util.LogUtils;

/**
 * AUTHOR：lanchuanke on 17/8/23 18:17
 */
public class JsbridgeWebView extends WebView {
    private static final String TAG = JsbridgeWebView.class.getSimpleName();
    private HashMap<String, JsCallJava> mJsCallJavas;

    public JsbridgeWebView(Context context) {
        this(context,null);
    }
    public JsbridgeWebView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public JsbridgeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public JsbridgeWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    public JsbridgeWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init();
    }

    private void init(){
        WebSettingManager settingManager=new WebSettingManager(this);
        this.setWebChromeClient(new JsbridgeWebChromeClient());
    }

    @Override
    public void addJavascriptInterface(Object object, String name) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            super.addJavascriptInterface(object,name);
            return;
        }
        LogUtils.i("Info","addJavascriptInterface:"+object+"   interfaceName:"+name);
        if (mJsCallJavas == null) {
            mJsCallJavas = new HashMap<String, JsCallJava>();
        }
        mJsCallJavas.put(name, new JsCallJava(object, name));
        injectJavaScript();
        LogUtils.i(TAG, "injectJavaScript, addJavascriptInterface.interfaceObj = " + object + ", interfaceName = " + name);
    }

    private void injectJavaScript() {
        for (Map.Entry<String, JsCallJava> entry : mJsCallJavas.entrySet()) {
            this.loadUrl(buildNotRepeatInjectJS(entry.getKey(), entry.getValue().getPreloadInterfaceJS()));
        }
    }


    /**
     * 构建一个“不会重复注入”的js脚本；
     * @param key
     * @param js
     * @return
     */
    public String buildNotRepeatInjectJS(String key, String js) {
        String obj = String.format("__injectFlag_%1$s__", key);
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:try{(function(){if(window.");
        sb.append(obj);
        sb.append("){console.log('");
        sb.append(obj);
        sb.append(" has been injected');return;}window.");
        sb.append(obj);
        sb.append("=true;");
        sb.append(js);
        sb.append("}())}catch(e){console.warn(e)}");
        return sb.toString();
    }

    public class JsbridgeWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            if (mJsCallJavas != null && JsCallJava.isSafeWebViewCallMsg(message)) {
                JSONObject jsonObject = JsCallJava.getMsgJSONObject(message);
                String interfacedName = JsCallJava.getInterfacedName(jsonObject);
                if (interfacedName != null) {
                    JsCallJava jsCallJava = mJsCallJavas.get(interfacedName);
                    if (jsCallJava != null) {
                        result.confirm(jsCallJava.call(view, jsonObject));
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }
}
