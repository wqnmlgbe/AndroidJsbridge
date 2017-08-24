package open.cklan.com.jsbridge.webview;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import open.cklan.com.jsbridge.util.LogUtils;
import open.cklan.com.jsbridge.util.NetworkUtils;

/**
 * AUTHOR：lanchuanke on 17/8/24 11:09
 */
public class WebSettingManager {
    WebView webView;
    WebSettings webSettings;

    public WebSettingManager(WebView webView) {
        this.webView = webView;
        init();
    }

    private void init() {
        webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSavePassword(false);
        if (NetworkUtils.checkNetwork(webView.getContext())) {
            //根据cache-control获取数据。
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //没网，则从本地获取，即离线加载
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            //适配5.0不允许http和https混合使用情况
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

//        webSettings.setRenderPriority(android.webkit.AgentWebSettings.RenderPriority.HIGH);
        webSettings.setTextZoom(100);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setBlockNetworkImage(false);//是否阻塞加载网络图片  协议http or https
        webSettings.setAllowFileAccess(true); //允许加载本地文件html  file协议, 这可能会造成不安全 , 建议重写关闭
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(false); //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            webSettings.setAllowUniversalAccessFromFileURLs(false);//允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
        }
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >=19)
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        else
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setNeedInitialFocus(true);
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setDefaultFontSize(16);
        webSettings.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
        webSettings.setGeolocationEnabled(true);

        //
        String dir = WebConfig.getCachePath(webView.getContext());

        LogUtils.i("Info", "dir:" + dir + "   appcache:" + WebConfig.getCachePath(webView.getContext()));
        //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
        webSettings.setGeolocationDatabasePath(dir);
        webSettings.setDatabasePath(dir);
        webSettings.setAppCachePath(dir);

        //缓存文件最大值
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
    }

}
