package tech.zdenek.jsruntime;

import android.os.Handler;

import java.util.concurrent.Executor;

public class AndroidExecutor implements Executor
{
  private final Handler handler;

  public AndroidExecutor(Handler h)
  {
    handler = h;
  }

  @Override
  public void execute(Runnable r)
  {
    handler.post(r);
  }
}
