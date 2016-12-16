package pro.sample.my.last_pro;

/**
 * Created by admin on 12/14/2016.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by admin on 12/9/2016.
 */
public class Splash_screen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(Splash_screen.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}


