package tech.zdenek.jsruntime;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

import tech.zdenek.jpromise.WorkerLoop;

public class AndroidEventLoop implements WorkerLoop
{
  @Override
  @NonNull
  public Executor createExecutor()
  {
    Looper.prepare();
    return new AndroidExecutor(new Handler());
  }

  @Override
  public void loop()
  {
    Looper.loop();
  }
}

