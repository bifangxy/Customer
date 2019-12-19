package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.IndexBannerMall;
import com.xiaopu.customer.data.jsonresult.InformationState;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.ToastUtils;
import com.xiaopu.customer.utils.Util;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.SharePopupWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class InfoContentActivity extends ActivityBase implements WbShareCallback {
    private static final String LOG_TAG = MyInformationCollectActivity.class.getSimpleName();

    private Context mContext;

    private ImageView iv_share;

    private ImageView iv_collect;

    private ImageView iv_return;

    private ImageView iv_thumbs;

    private WebView mWebView;

    private ProgressBar mProgressBar;

    private ImageView ivCollect;

    private TextView tv_collect_count;

    private TextView tv_thumbs_count;

    private LinearLayout ll_bottom_bar;

    private MyClick myClick;

    private SharePopupWindow mSharePopupWindow;

    private int mId;

    private int mCollectType = 0;

    private int mThumbType = 0;

    private String mTitle;

    private String mContent;

    private String linkUrl;

    private String image;

    private boolean collectStatus = true;

    private int position;

    private InformationState mInformationState;

    private WbShareHandler shareHandler;

    private int showType;

    private IndexBannerMall indexBannerMall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_content);
        mContext = this;
        initActionBar("详情");
        initView();
        initDate();
        initListener();
    }

    private void initListener() {
        ivCollect.setOnClickListener(myClick);
        iv_thumbs.setOnClickListener(myClick);
        iv_share.setOnClickListener(myClick);
        iv_return.setOnClickListener(myClick);
    }

    private void initDate() {
        shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();
        myClick = new MyClick();
        showType = getIntent().getExtras().getInt("showType");
        if (showType == 0) {
            ll_bottom_bar.setVisibility(View.GONE);
            indexBannerMall = (IndexBannerMall) getIntent().getExtras().getSerializable("indexBannerMall");
        } else if (showType == 1) {
            ll_bottom_bar.setVisibility(View.VISIBLE);
            linkUrl = getIntent().getExtras().getString("link_url");
            mId = getIntent().getExtras().getInt("info_id");
            mTitle = getIntent().getExtras().getString("title");
            mContent = getIntent().getExtras().getString("content");
            image = getIntent().getExtras().getString("image");
            position = getIntent().getExtras().getInt("position", -1);
        }
        initInformationState();
        initWebViewSetting();

        if (showType == 0) {
            if (indexBannerMall.getType() == 0) {
                mWebView.postUrl(HttpConstant.News_Info, ("id=" + indexBannerMall.getInformationId()).getBytes());
            } else if (indexBannerMall.getType() == 1) {
                mWebView.loadUrl(indexBannerMall.getLinkUrl());
                openJD(indexBannerMall.getProductId());
            } else if (indexBannerMall.getType() == 2) {
                mWebView.loadUrl(indexBannerMall.getLinkUrl());
            }
        } else {
            mWebView.loadUrl(linkUrl);
        }


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    mProgressBar.setProgress(newProgress);//设置进度值
                }
            }
        });

    }

    private void initWebViewSetting() {
        WebSettings webSettings = mWebView.getSettings();
        //支持获取手势焦点，输入用户名、密码或其他
        mWebView.requestFocusFromTouch();
//        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webSettings.setJavaScriptEnabled(true);  //支持js
        webSettings.setUseWideViewPort(true);  //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.supportMultipleWindows();  //多窗口
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);  //关闭webview中缓存
        webSettings.setAllowFileAccess(true);  //设置可以访问文件
        webSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");
    }

    private void initInformationState() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("infoId", mId);
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_InformationCondition, new HttpCallBack<InformationState>() {
            @Override
            public void onSuccess(HttpResult result) {
                mInformationState = (InformationState) result.getData();
                initState();
            }

            @Override
            public void onFail(String msg) {
                mInformationState = new InformationState();
                mCollectType = 1;
                mThumbType = 1;
            }
        });


/*
        HttpUtils.getInstantce().getInformationCondition(mId, new HttpConstant.SampleJsonResultListener<Feedback<InformationState>>() {
            @Override
            public void onSuccess(Feedback<InformationState> jsonData) {
                mInformationState = jsonData.getData();
                initState();
            }

            @Override
            public void onFailure(Feedback<InformationState> jsonData) {
                mInformationState = new InformationState();
                mCollectType = 1;
                mThumbType = 1;

            }
        });
*/
    }

    private void initState() {
        if (mInformationState.getIsCollected() == 1) {
            mCollectType = 0;
        } else {
            mCollectType = 1;
        }
        if (mInformationState.getIsThumb() == 1) {
            mThumbType = 0;
        } else {
            mThumbType = 1;
        }

        if (mCollectType == 0) {
            ivCollect.setSelected(true);
        } else {
            ivCollect.setSelected(false);
        }
        if (mThumbType == 0) {
            iv_thumbs.setSelected(true);
        } else {
            iv_thumbs.setSelected(false);
        }

        tv_collect_count.setText("" + mInformationState.getCollectedCount());
        tv_thumbs_count.setText("" + mInformationState.getThumbCount());
    }

    private void initView() {

        mWebView = (WebView) findViewById(R.id.webView);

        mProgressBar = (ProgressBar) findViewById(R.id.info_progressBar);

        ivCollect = (ImageView) findViewById(R.id.iv_collect);

        iv_share = (ImageView) findViewById(R.id.iv_share);

        iv_thumbs = (ImageView) findViewById(R.id.iv_thumbs);

        tv_collect_count = (TextView) findViewById(R.id.collect_count);

        tv_thumbs_count = (TextView) findViewById(R.id.thumbs_count);

        iv_return = (ImageView) findViewById(R.id.iv_return);

        ll_bottom_bar = (LinearLayout) findViewById(R.id.ll_bottom_bar);
    }

    @Override
    public void onWbShareSuccess() {
        T.showShort("分享成功");
    }

    @Override
    public void onWbShareCancel() {

    }

    @Override
    public void onWbShareFail() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        shareHandler.doResultIntent(intent, this);
    }

    public class MyClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_return:
                    setResult(RESULT_OK);
                    finish();
                    break;
                case R.id.iv_collect:
                    if (collectStatus) {
                        collectStatus = false;
                        collectInfo(1, mId, mCollectType);
                    }
                    break;
                case R.id.iv_thumbs:
                    if (collectStatus) {
                        collectStatus = false;
                        collectInfo(2, mId, mThumbType);
                    }
                    break;
                case R.id.iv_share:
                    mSharePopupWindow = new SharePopupWindow(mContext, myClick);
                    mSharePopupWindow.showAtLocation(findViewById(R.id.content_total), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    break;
                case R.id.tv_cancle:
                    mSharePopupWindow.dismiss();
                    break;
                case R.id.weChat_friend:
                    mSharePopupWindow.dismiss();
                    if (!Util.isWXAppInstalledAndSupported(ApplicationXpClient.MSGAPI)) {
                        ToastUtils.showErrorMsg("请安装微信");
                    } else {
                        WXWebpageObject webpage = new WXWebpageObject();
                        webpage.webpageUrl = linkUrl;
                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title = mTitle;
                        msg.description = mContent;
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_share_pooai);
                        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
                        bitmap.recycle();
                        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

                        //构造一个Req
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = String.valueOf(System.currentTimeMillis());
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        ApplicationXpClient.MSGAPI.sendReq(req);
                    }
                    break;
                case R.id.weChat_moments:
                    mSharePopupWindow.dismiss();
                    if (!Util.isWXAppInstalledAndSupported(ApplicationXpClient.MSGAPI)) {
                        ToastUtils.showErrorMsg("请安装微信，版本4.2以上");
                    } else {
                        WXWebpageObject webpage = new WXWebpageObject();
                        webpage.webpageUrl = linkUrl;
                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title = mTitle;
                        msg.description = mContent;
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_share_pooai);
                        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
                        bitmap.recycle();
                        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                        //构造一个Req
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = String.valueOf(System.currentTimeMillis());
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneTimeline;
                        ApplicationXpClient.MSGAPI.sendReq(req);
                    }
                    break;
                case R.id.weibo:
                    mSharePopupWindow.dismiss();
                    shareWB();
                    break;
                case R.id.iv_return:
                    Intent intent = new Intent();
                    intent.putExtra("mCollectType", mCollectType);
                    intent.putExtra("position", position);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }

    }

    private void shareWB() {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj();
        weiboMessage.imageObject = getImageObj();
        weiboMessage.mediaObject = getWebpageObj();
        shareHandler.shareMessage(weiboMessage, true);
    }

    private void collectInfo(final int choose, int infoId, int type) {
        String url = "";
        if (choose == 1) {
            url = HttpConstant.Url_collectionInformation;
        } else if (choose == 2) {
            url = HttpConstant.Url_thumbInformation;
        }
        Map<String, Object> maps = new HashMap<>();
        maps.put("infoId", infoId);
        maps.put("type", type);
        HttpUtils.getInstantce().postWithHead(maps, url, new HttpCallBack<Integer>() {
            @Override
            public void onSuccess(HttpResult result) {
                Integer count = (Integer) result.getData();
                collectStatus = true;
                if (choose == 1) {
                    if (mCollectType == 0) {
                        mCollectType = 1;
                        ivCollect.setSelected(false);
                    } else {
                        mCollectType = 0;
                        ivCollect.setSelected(true);
                    }
                    tv_collect_count.setText("" + count);
                } else {
                    if (mThumbType == 0) {
                        mThumbType = 1;
                        iv_thumbs.setSelected(false);
                    } else {
                        mThumbType = 0;
                        iv_thumbs.setSelected(true);
                    }
                    tv_thumbs_count.setText("" + count);
                }
            }

            @Override
            public void onFail(String msg) {
                collectStatus = true;
                if (msg != null)
                    T.showShort(msg);
            }
        });

/*
        HttpUtils.getInstantce().collectionOrthumbInformation(url, infoId, type, new HttpConstant.SampleJsonResultListener<Feedback<Integer>>() {
            @Override
            public void onSuccess(Feedback<Integer> jsonData) {
                collectStatus = true;
                if (choose == 1) {
                    if (mCollectType == 0) {
                        mCollectType = 1;
                        ivCollect.setSelected(false);
                    } else {
                        mCollectType = 0;
                        ivCollect.setSelected(true);
                    }
                    tv_collect_count.setText("" + jsonData.getData());
                } else {
                    if (mThumbType == 0) {
                        mThumbType = 1;
                        iv_thumbs.setSelected(false);
                    } else {
                        mThumbType = 0;
                        iv_thumbs.setSelected(true);
                    }
                    tv_thumbs_count.setText("" + jsonData.getData());
                }

            }

            @Override
            public void onFailure(Feedback<Integer> jsonData) {
                collectStatus = true;
                if (jsonData == null) {
                    T.showShort("网络不通，请检查网络。");
                } else {
                    T.showShort(jsonData.getMsg());
                }
            }
        });
*/
    }

    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = mContent;
        textObject.title = mTitle;
        textObject.actionUrl = linkUrl;
        return textObject;
    }

    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(image)) {
            bitmap = ImageLoader.getInstance().loadImageSync(image);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_share_pooai);
        }
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_share_pooai);
        }
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = mTitle;
        mediaObject.description = mContent;
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(image)) {
            bitmap = ImageLoader.getInstance().loadImageSync(image);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_share_pooai);
        }
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_share_pooai);
        }
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = linkUrl;
        mediaObject.defaultText = "小普资讯";
        return mediaObject;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("mCollectType", mCollectType);
            intent.putExtra("position", position);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void openJD(String id) {
        if (checkPackage("com.jingdong.app.mall")) {
            String url = "openapp.jdmobile://virtual?params=%7B%22sourceValue%22:%220_productDetail_97%22,%22des%22:%22productDetail%22,%22skuId%22:%22" + id + "%22,%22category%22:%22jump%22,%22sourceType%22:%22PCUBE_CHANNEL%22%7D";
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    /**
     * 检测该包名所对应的应用是否存在
     * * @param packageName
     *
     * @return
     */
    public boolean checkPackage(String packageName) {
        if (packageName == null || "".equals(packageName)) return false;
        try {
            //手机已安装，返回true
            getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            //手机未安装，跳转到应用商店下载，并返回false
//            Uri uri = Uri.parse("market://details?id=" + packageName);
//            Intent it = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(it);
            return false;
        }
    }
}
