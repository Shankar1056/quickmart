package com.bigappcompany.quikmart.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.service.RegistrationIntentService;
import com.bigappcompany.quikmart.util.Preference;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
	private static final int RC_AUTH = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow()
			.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		
		Animation leftRight = AnimationUtils.loadAnimation(this, R.anim.left_to_middle);
		View cart = findViewById(R.id.iv_cart);
		cart.startAnimation(leftRight);
		
		Animation cloud1 = AnimationUtils.loadAnimation(this, R.anim.cloud_anim1);
		findViewById(R.id.iv_cloud1).startAnimation(cloud1);
		
		Animation cloud2 = AnimationUtils.loadAnimation(this, R.anim.cloud_anim2);
		findViewById(R.id.iv_cloud2).startAnimation(cloud2);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				final ValueAnimator animator = ValueAnimator.ofFloat(0f, -1f);
				animator.setRepeatCount(0);
				animator.setDuration(2000);
				animator.addListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animation) {
						
					}
					
					@Override
					public void onAnimationEnd(Animator animation) {
						if (!isFinishing()) {
							if (new Preference(MainActivity.this).isLoggedIn()) {
								startActivity(new Intent(MainActivity.this, HomeActivity.class));
								finish();
							} else {
								Intent intent = new Intent(MainActivity.this, AuthActivity.class);
								startActivityForResult(intent, RC_AUTH);
							}
						}
					}
					
					@Override
					public void onAnimationCancel(Animator animation) {
						
					}
					
					@Override
					public void onAnimationRepeat(Animator animation) {
						
					}
				});
				animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						final float progress = (float) animation.getAnimatedValue();
						View building1 = findViewById(R.id.iv_building1);
						View building2 = findViewById(R.id.iv_building2);
						final float width = building1.getWidth();
						final float translationX = width * progress;
						building1.setTranslationX(translationX);
						building2.setTranslationX(translationX + width);
					}
				});
				animator.start();
			}
		}, 1000);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == RC_AUTH && resultCode == RESULT_OK) {
			// register FCM token in server database
			startService(new Intent(this, RegistrationIntentService.class));
			
			// authentication successful, goto HomeActivity
			startActivity(new Intent(this, HomeActivity.class));
		}
		
		finish();
	}
}
