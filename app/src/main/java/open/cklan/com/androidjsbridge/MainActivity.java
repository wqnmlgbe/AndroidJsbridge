package open.cklan.com.androidjsbridge;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.TextView;


import open.cklan.com.jsbridge.helper.javacalljs.JsEntraceAccess;
import open.cklan.com.jsbridge.helper.javacalljs.JsEntraceAccessImpl;
import open.cklan.com.jsbridge.webview.JsbridgeWebView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvShow;
    Button btnCallJs;
    JsbridgeWebView webView;
    JsEntraceAccess jsEntraceAccess;
    Handler handler=new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvShow= (TextView) findViewById(R.id.tv_show);
        btnCallJs= (Button) findViewById(R.id.btn_java_call_js);
        btnCallJs.setOnClickListener(this);
        webView= (JsbridgeWebView) findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/test.html");
        jsEntraceAccess=JsEntraceAccessImpl.getInstance(webView);
        webView.addJavascriptInterface(new MainJavascriptInterface(),"android");
    }

    @Override
    public void onClick(View v) {
        if(R.id.btn_java_call_js==v.getId()){
            jsEntraceAccess.quickCallJs("callByAndroidMoreParams", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    tvShow.setText("收到js发来的数据:"+value);
                }
            },"Android 发来的数据");
        }
    }


    class MainJavascriptInterface{
        @JavascriptInterface
        public void callAndroid(final String msg){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tvShow.setText("js调用了android，并且发来了消息:"+msg);
                    jsEntraceAccess.quickCallJs("callByAndroidMoreParams","你调用了android，我也返回给你");
                }
            });

        }
    }
}
