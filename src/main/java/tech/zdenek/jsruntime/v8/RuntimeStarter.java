package tech.zdenek.jsruntime.v8;

import android.support.annotation.NonNull;

import com.eclipsesource.v8.V8;

import tech.zdenek.jsruntime.AndroidEventLoop;
import tech.zdenek.jsruntime.JsRuntime;
import tech.zdenek.jsruntime.RuntimeCallable;

import java.util.concurrent.Executor;

import tech.zdenek.jpromise.ResultFuture;
import tech.zdenek.jpromise.WorkerStarter;

public final class RuntimeStarter extends WorkerStarter
{
  @NonNull  
  private JsRuntime runtime;

  public RuntimeStarter(@NonNull Executor callbackExecutor, @NonNull Runnable startedCallback)
  {
    super(new AndroidEventLoop(), callbackExecutor, startedCallback);
  }

  @Override
  protected void prepareThreadContext()
  {
    runtime = new V8Adapter(V8.createV8Runtime());
  }

  public <Retval> ResultFuture<Retval> postTask(final RuntimeCallable<Retval> task)
  {
    return postTask(runtime, task);
  }
}
