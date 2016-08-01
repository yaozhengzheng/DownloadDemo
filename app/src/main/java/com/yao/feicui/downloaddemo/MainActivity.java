package com.yao.feicui.downloaddemo;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.IllegalFormatCodePointException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ImageView mImageView;
    private Button mBtn_download;

    //    private final String url="http://developer.android.com/images/home/kk-hero.jpg";
    private ProgressDialog mProgressDialog;
    private URL mUrl = null;
    private HttpURLConnection mHttpURLConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化视图方法，包括控件关联id
     * 自定义进度dialog 使用系统ProgressDialog
     * 控件的监听事件
     */
    //初始化视图
    private void initView() {
        mImageView = (ImageView) findViewById(R.id.image);
        mBtn_download = (Button) findViewById(R.id.loadImage);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("提示信息");
        mProgressDialog.setMessage("正在下载中,请稍后....");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mBtn_download.setOnClickListener(this);
    }

    //点击事件，去执行异步任务。
    @Override
    public void onClick(View v) {
//自定义MyAsyncTask，继承AsyncTask
        new MyAsyncTask().execute(String.valueOf(mUrl));

    }

    /**
     * 自定义MyAsyncTask，继承AsyncTask,包括三个参数
     * Params: String类型，表示传递给异步任务的参数类型是String，通常指定的是URL路径
     * Progress: Integer类型，进度条的单位通常都是Integer类型
     * Result：byte[]类型表示返回已下载的数据
     * 重写AsyncTask的几个方法
     * onPreExecute：开始执行异步线程
     * doInBackground:执行网络请求，返回数据
     * onProgressUpdate:更新进度条
     * onPostExecute:接收doInBackground返回的数据更新到ui界面
     */
    private class MyAsyncTask extends AsyncTask<String, Integer, byte[]> {
        private ByteArrayOutputStream mBaos;

        //开始执行异步线程
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute:开始1 ");
            mProgressDialog.show();
        }

        // doInBackground通过调用publishProgress()方法
        //触发onProgressUpdate对UI进行操作
        @Override
        protected byte[] doInBackground(String... params) {
            Log.d(TAG, "doInBackground: 开始2");
            try {
                //获取网络路径，
                mUrl = new URL("http://img1.imgtn.bdimg.com/it/u=4165827300,1372791563&fm=21&gp=0.jpg");
                //连接网络
                mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
                //获取请求方式
                mHttpURLConnection.setRequestMethod("GET");
                Log.d(TAG, "doInBackground: 请求开始");
                //连接网络
//                mHttpURLConnection.setConnectTimeout(5000);
                mHttpURLConnection.connect();
                Log.d(TAG, "doInBackground: 打开链接");
                //获取网络流，读取数据
                InputStream inputStream = mHttpURLConnection.getInputStream();
                Log.d(TAG, "doInBackground: 获取网络流");
                //字节数组写入流
                mBaos = new ByteArrayOutputStream();
                //定义字节缓冲
                byte[] buf = new byte[1024];
                //拿到文件的长度
                long total_length = mHttpURLConnection.getContentLength();
                //定义一个变量来记录读取一次的文件长度
                long count = 0;
                //使用while循环读取数据
                int length = 0;
                while ((length = inputStream.read(buf)) != -1) {
                    mBaos.write(buf, 0, length);
                    count += length;
                    //计算进度条 调用publishProgress方法更新progress
                    publishProgress((int) (count * 100 / (float) total_length));
                    //使用Thread调用sleep方法让线程睡眠，观察进度条
                    Thread.sleep(50);
                }
                inputStream.close();
                mBaos.close();
            } catch (MalformedURLException e) {
                Log.d(TAG, "doInBackground: 异常1");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: 异常2");
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: 异常3");
            }
            return mBaos.toByteArray();
        }

        //更新进度
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(TAG, "onProgressUpdate: 开始更新进度");
            super.onProgressUpdate(values);
            int value = values[0];
            //进度条更新
            mProgressDialog.setProgress(value);
        }

        @Override
        protected void onPostExecute(byte[] result) {
            Log.d(TAG, "onPostExecute: 开始3");
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute:结果2" + result);
            //创建bitmap对象，把doInBackground的返回的byte数据转换成bitmap类型
            Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
            //更新图片
            mImageView.setImageBitmap(bitmap);
            //进度弹窗消失
            mProgressDialog.dismiss();
            Log.d(TAG, "onPostExecute: 结束");
        }
    }

}
