package com.ping.okgo.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.ping.okgo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button)
    Button mButton;
    @BindView(R.id.button2)
    Button mButton2;
    @BindView(R.id.textView)
    TextView mTextView;
    @BindView(R.id.textView2)
    TextView mTextView2;
    @BindView(R.id.button3)
    Button mButton3;
    @BindView(R.id.button4)
    Button mButton4;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.button5)
    Button mButton5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    /**
     * get请求
     *
     * @param url
     */
    private void getByOkGo(String url) {
        OkGo.get(url)                            // 请求方式和请求url
                .tag(this)                       // 请求的 tag, 主要用于取消对应的请求
                .cacheKey("cacheGetKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        mTextView2.setText(s);
                    }
                });
    }

    /**
     * post请求
     *
     * @param url
     */
    private void postByOkGo(String url) {
        OkGo.post(url)
                .tag(this)
                .cacheKey("cachePostKey")
                .cacheMode(CacheMode.DEFAULT)
                .params("method", "album.item.get")
                .params("appKey", "myKey")
                .params("format", "json")
                .params("albumId", "Lqfme5hSolM")
                .params("pageNo", "1")
                .params("pageSize", "2")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        mTextView2.setText(s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mTextView2.setText(e.getMessage());
                    }
                });
    }

    /**
     * 下载文件
     *
     * @param url          下载地址
     * @param destFileDir  保存文件路径
     * @param destFileName 保存文件名
     */
    private void downLoad(String url, String destFileDir, String destFileName) {
        OkGo.get(url)//
                .tag(this)//
                .execute(new FileCallback(destFileDir, destFileName) {  //文件下载时，可以指定下载的文件目录和文件名
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        // file 即为文件数据，文件保存在指定目录
                        Toast.makeText(getApplicationContext(), file.getName() + "下载成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调下载进度(该回调在主线程,可以直接更新ui)
                        mProgressBar.setProgress((int) (100 * progress));
                        mTextView2.setText("已下载" + currentSize / 1024 / 1024 + "MB, 共" + totalSize / 1024 / 1024 + "MB;");
                    }
                });
    }

    /**
     * 多文件上传
     *
     * @param url
     * @param keyName
     * @param files   文件集合
     */
    private void uploadFiles(String url, String keyName, List<File> files) {
        OkGo.post(url)//
                .tag(this)//
                //.isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                //.params("param1", "paramValue1") 		// 这里可以上传参数
                //.params("file1", new File("filepath1"))   // 可以添加文件上传
                //.params("file2", new File("filepath2")) 	// 支持多文件同时添加上传
                .addFileParams(keyName, files)    // 这里支持一个key传多个文件
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //上传成功
                        Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                        mProgressBar.setProgress((int) (100 * progress));
                        mTextView2.setText("已上传" + currentSize / 1024 / 1024 + "MB, 共" + totalSize / 1024 / 1024 + "MB;");
                    }
                });
    }

    /**
     * 请求网络图片
     * @param url
     */
    private void getBitmap(String url) {
        OkGo.get(url)//
                .tag(this)//
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, Call call, Response response) {
                        // bitmap 即为返回的图片数据
                        mImageView.setImageBitmap(bitmap);
                    }
                });
    }

    @OnClick({R.id.button, R.id.button2, R.id.button3, R.id.button4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                getByOkGo("http://gank.io/api/data/Android/2/1");
                break;
            case R.id.button2:
                postByOkGo("http://api.tudou.com/v3/gw");
                break;
            case R.id.button3:
                downLoad("http://sw.bos.baidu.com/sw-search-sp/software/1e41f08ea1bea/QQ_8.9.2.20760_setup.exe", Environment.getExternalStorageDirectory().getAbsolutePath(), "QQ_setup.exe");
                break;
            case R.id.button4:
                List<File> files = new ArrayList<>();
                files.add(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "QQ_8.9.2.20760_setup.exe"));
                files.add(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "test.mp4"));
                uploadFiles("http://192.168.1.102/FileUpload/FileUploadServlet", "Mr.sorrow", files);
                break;
        }
    }

    @OnClick(R.id.button5)
    public void onViewClicked() {
        getBitmap("http://7xi8d6.com1.z0.glb.clouddn.com/2017-05-09-18443931_429618670743803_5734501112254300160_n.jpg");
    }
}
