package tech.zdenek.jsruntime.webkit;

import android.os.Handler;
import android.os.Looper;

import android.webkit.ValueCallback;
import android.webkit.WebView;

import tech.zdenek.jsruntime.JsRuntime;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import tech.zdenek.annotation.NonNull;
import tech.zdenek.jpromise.HandlerAny;
import tech.zdenek.jpromise.UnexpectedResultException;

public class WebKitAdapter implements JsRuntime
{
  @NonNull
  private WebView webView;

  public WebKitAdapter(WebView webView)
  {
    this.webView = webView;
  }

  @Override
  public void addJavascriptInterface(@NonNull final Object object, @NonNull String name)
  {
    webView.addJavascriptInterface(object, name);
  }

  @Override
  public void removeJavascriptInterface(@NonNull String name)
  {
    webView.removeJavascriptInterface(name);
  }

  @Override
  public <T> T evaluateJavascript(@NonNull Class<T> resultClass, @NonNull final String script) throws Exception
  {
    if(Looper.myLooper() == Looper.getMainLooper())
    {
      throw new UnsupportedOperationException("Cannot evaluate synchronously on main thread (block it)");
    }
    class Finalizable<C> {
      C value;
    }
    final Finalizable<T> retval = new Finalizable<>();
    final Finalizable<Exception> exc = new Finalizable<Exception>();
    final Lock lock = new ReentrantLock();
    final Condition cond = lock.newCondition();
    lock.lock();
    new Handler(Looper.getMainLooper()).post(new Runnable()
    {
      @Override
      public void run()
      {
        evaluateJavascript(script, new HandlerAny<T>()
        {
          @Override
          public void handle(@Nullable T value, @Nullable Exception e)
          {
            lock.lock();
            retval.value = value;
            exc.value = e;
            cond.signal();
            lock.unlock();
          }
        });
      }
    });
    try {
      cond.wait();
    } finally {
      lock.unlock();
    }
    if(exc.value != null) {
      throw exc.value;
    }
    return retval.value;
  }

  @Override
  public <T> void evaluateJavascript(@NonNull final String script, final HandlerAny<T> resultHandler)
  {
    ValueCallback<String> evalResult = new ValueCallback<String>()
    {
      @Override
      public void onReceiveValue(String value)
      {
        T retval = null;
        Exception e = null;
        if(retval instanceof String)
        {
          retval = (T)value;
        } else if(retval instanceof Boolean) {
          if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            retval = (T)Boolean.valueOf(value);
          } else {
            e = new UnexpectedResultException(new Boolean(true), null);
          }
        } else {
          e = new UnsupportedOperationException("Unsupported script result class");
        }
        resultHandler.handle(retval, e);
      }
    };
    webView.evaluateJavascript(script, evalResult);
  }
}
