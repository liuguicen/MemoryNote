package com.lgc.memorynote.wordList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.lgc.wordnote.data.Word;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import a.baozouptu.common.util.BitmapTool;

/**
 * 缓存，异步，多线程
 * 为了加快速度，在内存中开启缓存（主要应用于重复图片较多时，或者同一个图片要多次被访问， 比如在ListView时来回滚动） 软引用不可用 public
 * Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String,
 * SoftReference<Bitmap>>();
 */
public class AsyncWordLoader {
    /**
     * 使用LRU算法，用key-value形式查找对象；
     */
    public static LruCache<String, Word> imageCache;
    private final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    LinkedBlockingQueue<Runnable> lque;
    /**
     * 线程池，固定相应个线程来执行任务，规定最大线程数量的线程池
     * 核心线程5秒不响应会自动销毁的
     */
    private ThreadPoolExecutor executorService;

    private final Handler handler = new Handler();
    private static AsyncWordLoader asyncWordLoader;

    public static AsyncWordLoader getInstance() {
        return SingleTonHolder.ASYNC_WORD_LOADER;
    }

    /**
     * 较好的线程安全的获取单例的形式
     */
    private static class SingleTonHolder{
        private static final AsyncWordLoader ASYNC_WORD_LOADER =new AsyncWordLoader();
    }
    private AsyncWordLoader() {
        lque = new LinkedBlockingQueue<Runnable>();
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        imageCache = new LruCache<String, Word>(maxMemory / 6) {};
        executorService = new ThreadPoolExecutor(CPU_COUNT * 2 + 1, CPU_COUNT * 2 + 1,
                5L, TimeUnit.SECONDS,
                lque);//核心线程=cup数量*2+1，5秒为运行后关闭之
        executorService.allowCoreThreadTimeOut(true);
    }

    /**
     * 如果缓存过就从缓存中取出数据
     *
     * @param imageUrl
     * @return
     */
    public Word getWord(String imageUrl) {
        return imageCache.get(imageUrl);
    }

    public Word getWord(int id) {
        String key = String.valueOf(id);
        return getWord(key);
    }

    /**
     * 使用线程池和handler将需要的图片加载到对应的View上面,如果图片存在LRUcache，则直接返回图片的Bitmap对象，
     * 如果不存在，直接异步获取Bitmap，并进行加载
     *
     * @param imageUrl 图像url地址
     * @param image    要加载图片的那个ImageView
     * @param callback 自己实现一个接口用于回调
     * @return 返回内存中缓存的图像，第一次加载返回null
     */
    public Bitmap loadBitmap(final String imageUrl, final ImageView image, final int position,
                             final ImageCallback callback, final int needWidth) {
        // 缓存中没有图像，则从SDcard取出数据，并将取出的数据缓存到内存中
        if (imageCache.get(imageUrl) != null) {
            return imageCache.get(imageUrl);
        }
        executorService.submit(new Runnable() {// 线程池执行取出图片的进程
            public void run() {
                try {
                    final Bitmap bitmap = BitmapTool.decodeInSize(imageUrl, needWidth, Bitmap.Config.ARGB_8888);// 获取图片URL对应的图片Bitmap
                    imageCache.put(imageUrl, bitmap);
                    handler.post(// handler的轻量级方法，利用handler的post方法，在attached的即handler依附的线程中执行下面的代码
                            new Runnable() {
                                public void run() {
                                    callback.imageLoaded(bitmap, image, position,
                                            imageUrl);
                                }
                            });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return null;
    }

    public Bitmap loadBitmap(final int id, final ImageView image, final int position,
                             final ImageCallback callback, final int needWidth) {
        // 缓存中没有图像，则从SDcard取出数据，并将取出的数据缓存到内存中
        final String key = String.valueOf(id);
        if (imageCache.get(key) != null) {
            return imageCache.get(key);
        }
        executorService.submit(new Runnable() {// 线程池执行取出图片的进程
            public void run() {
                try {
                    final Bitmap bitmap = BitmapFactory.decodeResource(GlobalData.appContext.getResources(), id);// 获取图片URL对应的图片Bitmap
                    imageCache.put(key, bitmap);
                    handler.post(// handler的轻量级方法，利用handler的post方法，在attached的即handler依附的线程中执行下面的代码
                            new Runnable() {
                                public void run() {
                                    callback.imageLoaded(bitmap, image, position,
                                            key);
                                }
                            });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return null;
    }

    public void cancelLoad() {
        while (!lque.isEmpty()) lque.clear();
    }

    // 对外界开放的回调接口
    public interface ImageCallback {
        /**
         * 将Bitmap的对象放入到image里面，这个接口已经导入，可以使用
         *
         * @param imageDrawable 将要放入的Bitmap对象，
         * @param image         显示Bitmap的View
         */
        void imageLoaded(Bitmap imageDrawable, ImageView image, int position,
                         String imageUrl);
    }

    /**
     * 清除调用的内存
     */
    public void evitAll() {
        imageCache.evictAll();
    }

    public void stop() {
        executorService.shutdown();
    }

    public void reStart() {
        if (executorService.isShutdown()) {
            executorService = new ThreadPoolExecutor(CPU_COUNT * 2 + 1, 0,
                    0L, TimeUnit.MILLISECONDS,
                    lque);
        }
    }
}
