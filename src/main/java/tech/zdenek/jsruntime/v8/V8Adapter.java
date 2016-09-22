package tech.zdenek.jsruntime.v8;

import android.support.annotation.NonNull;
import android.webkit.JavascriptInterface;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8ResultUndefined;
import tech.zdenek.jsruntime.JsRuntime;

import java.lang.reflect.Method;

import tech.zdenek.jpromise.HandlerAny;
import tech.zdenek.jpromise.UnexpectedResultException;

public class V8Adapter implements JsRuntime
{
  @NonNull
  private V8 v8;

  public V8Adapter(V8 runtime)
  {
    this.v8 = runtime;
  }

  @Override
  public void addJavascriptInterface(@NonNull final Object object, @NonNull String name)
  {
    final V8Object jsObject = new V8Object(v8);
    v8.add(name, jsObject);
    for (Method method : object.getClass().getMethods()) {
      if( !method.isAnnotationPresent(JavascriptInterface.class) ) {
        continue;
      }
      String methodName = method.getName();
      jsObject.registerJavaMethod(object, methodName, methodName, method.getParameterTypes());
    }
    jsObject.release();
  }

  @Override
  public void removeJavascriptInterface(@NonNull String name)
  {
    v8.addUndefined(name);
  }

  @Override
  public <T> T evaluateJavascript(@NonNull Class<T> resultClass, @NonNull final String script) throws UnexpectedResultException
  {
    T retval = null;
    String name = resultClass.getSimpleName();
    if(name.equalsIgnoreCase("string")) {
      try {
        String s = v8.executeStringScript(script);
        return (T)s;
      } catch(V8ResultUndefined e) {
        throw new UnexpectedResultException("string",null);
      }
    } else if(name.equalsIgnoreCase("boolean")) {
      try {
        Boolean b = v8.executeBooleanScript(script);
        return (T)b;
      } catch(V8ResultUndefined e) {
        throw new UnexpectedResultException(new Boolean(true), null);
      }
    }
    throw new UnsupportedOperationException("Unsupported script result class");
  }

  @Override
  public <T> void evaluateJavascript(@NonNull final String script, final HandlerAny<T> resultHandler)
  {
//    try {
//      T result = evaluateJavascript(script);
//      resultHandler.handle(result, null);
//    } catch (UnexpectedResultException e) {
//      resultHandler.handle(null, e);
//    }
  }
}
