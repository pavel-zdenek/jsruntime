package tech.zdenek.jsruntime;

import android.support.annotation.NonNull;

import tech.zdenek.annotation.Nullable;
import tech.zdenek.jpromise.HandlerAny;

public interface JsRuntime
{
  void addJavascriptInterface(@NonNull final Object object, @NonNull String name);
  void removeJavascriptInterface(@NonNull String name);
  <T> T evaluateJavascript(@NonNull Class<T> resultClass, @NonNull final String script) throws Exception;
  <T> void evaluateJavascript(@NonNull final String script, @Nullable final HandlerAny<T> resultHandler);
}
