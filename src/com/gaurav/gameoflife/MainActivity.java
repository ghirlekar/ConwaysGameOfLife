package com.gaurav.gameoflife;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	public static final String EXTRAS_AA_FLAG = "com.gaurav.gameoflife.extras.aaflag";
	public static final String EXTRAS_RULE_SURVIVE = "com.gaurav.gameoflife.extras.rulesurvive";
	public static final String EXTRAS_RULE_BIRTH = "com.gaurav.gameoflife.extras.rulebirth";
	public static final String EXTRAS_SWIPE_TRAIL = "com.gaurav.gameoflife.extras.swipetrail";

	private static final int REQUEST_SETTINGS_ACTIVITY = 10;

	private static final String STATE_GOL = "com.gaurav.gameoflife.state.gol";
	private static final String STATE_PAUSED = "com.gaurav.gameoflife.state.paused";
	private static final String STATE_CANVAS_LOCKED = "com.gaurav.gameoflife.state.canvaslocked";
	private static final String STATE_X_WIDTH = "com.gaurav.gameoflife.state.xwidth";
	private static final String STATE_Y_WIDTH = "com.gaurav.gameoflife.state.ywidth";
	private static final String STATE_X_POS = "com.gaurav.gameoflife.state.xpos";
	private static final String STATE_Y_POS = "com.gaurav.gameoflife.state.ypos";

	public static final String SHARED_PREFS_NAME = "com.gaurav.gameoflife.sharedprefs";

	public static final String PREFS_SHOW_TIPS_ON_START = "com.gaurav.gameoflife.prefs.showtipsonstart";
	public static final String PREFS_BIRTH_RULE = "com.gaurav.gameoflife.prefs.birthrule";
	public static final String PREFS_SURVIVAL_RULE = "com.gaurav.gameoflife.prefs.survivalrule";
	public static final String PREFS_SWIPE_TRAIL = "com.gaurav.gameoflife.prefs.swipetrail";
	public static final String PREFS_ANTIALIAS = "com.gaurav.gameoflife.prefs.antialias";

	public Life life;
	Menu menu;
	Toast FPSToast;
	TipsDialog tipsDialog;
	private boolean paused, canvasLocked = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		life = (Life) findViewById(R.id.life);
		FPSToast = Toast.makeText(getApplicationContext(), life.getFps()
				+ " FPS", Toast.LENGTH_SHORT);

		SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		if (!sharedPrefs.contains(PREFS_SHOW_TIPS_ON_START))
			editor.putBoolean(PREFS_SHOW_TIPS_ON_START, true);
		if (!sharedPrefs.contains(PREFS_ANTIALIAS))
			editor.putBoolean(PREFS_ANTIALIAS, true);
		if (!sharedPrefs.contains(PREFS_SWIPE_TRAIL))
			editor.putInt(PREFS_SWIPE_TRAIL, 1);
		for (int i = 0; i < 9; i++) {
			if (!sharedPrefs.contains(PREFS_SURVIVAL_RULE + i)) {
				if (i == 2 || i == 3)
					editor.putBoolean(PREFS_SURVIVAL_RULE + i, true);
				else
					editor.putBoolean(PREFS_SURVIVAL_RULE + i, false);
			}
			if (!sharedPrefs.contains(PREFS_BIRTH_RULE + i)) {
				if (i == 3)
					editor.putBoolean(PREFS_BIRTH_RULE + i, true);
				else
					editor.putBoolean(PREFS_BIRTH_RULE + i, false);
			}
		}
		editor.commit();

		if (savedInstanceState == null) {
			tipsDialog = new TipsDialog();
			if (sharedPrefs.getBoolean(PREFS_SHOW_TIPS_ON_START, true))
				tipsDialog.show(getSupportFragmentManager(), "tipsDialog");
		} else {
			boolean[][] inArray = new boolean[Life.MAX_SIZE][Life.MAX_SIZE];
			for (int i = 0; i < Life.MAX_SIZE; i++)
				inArray[i] = savedInstanceState.getBooleanArray(STATE_GOL + i);
			life.setState(inArray);
			paused = savedInstanceState.getBoolean(STATE_PAUSED);
			canvasLocked = savedInstanceState.getBoolean(STATE_CANVAS_LOCKED);
			Point size = new Point();
			getWindowManager().getDefaultDisplay().getSize(size);
			if (size.x > size.y) {
				life.setxWidth(savedInstanceState.getInt(STATE_Y_WIDTH));
				life.setyWidth(savedInstanceState.getInt(STATE_X_WIDTH));
				life.setxPos(savedInstanceState.getInt(STATE_X_POS)
						- (size.x / savedInstanceState.getInt(STATE_X_WIDTH) - size.y
								/ savedInstanceState.getInt(STATE_Y_WIDTH)) / 2);
				life.setyPos(savedInstanceState.getInt(STATE_Y_POS)
						+ (size.x / savedInstanceState.getInt(STATE_X_WIDTH) - size.y
								/ savedInstanceState.getInt(STATE_Y_WIDTH)) / 2);
			} else {
				life.setxWidth(savedInstanceState.getInt(STATE_Y_WIDTH));
				life.setyWidth(savedInstanceState.getInt(STATE_X_WIDTH));
				life.setxPos(savedInstanceState.getInt(STATE_X_POS)
						+ (size.y / savedInstanceState.getInt(STATE_Y_WIDTH) - size.x
								/ savedInstanceState.getInt(STATE_X_WIDTH)) / 2);
				life.setyPos(savedInstanceState.getInt(STATE_Y_POS)
						- (size.y / savedInstanceState.getInt(STATE_Y_WIDTH) - size.x
								/ savedInstanceState.getInt(STATE_X_WIDTH)) / 2);
			}
		}

		life.setPaused(paused);
		life.setCanvasLock(canvasLocked);
		life.setAntiAliased(sharedPrefs.getBoolean(PREFS_ANTIALIAS, true));
		life.setSwipeTrail(sharedPrefs.getInt(PREFS_SWIPE_TRAIL, 1));
		boolean[] tempRuleBirth = new boolean[9], tempRuleSurvive = new boolean[9];
		for (int i = 0; i < 9; i++) {
			tempRuleSurvive[i] = getSharedPreferences(SHARED_PREFS_NAME,
					MODE_PRIVATE).getBoolean(PREFS_SURVIVAL_RULE + i, false);
			tempRuleBirth[i] = getSharedPreferences(SHARED_PREFS_NAME,
					MODE_PRIVATE).getBoolean(PREFS_BIRTH_RULE + i, false);
		}
		life.setRuleBirth(tempRuleBirth);
		life.setRuleSurvive(tempRuleSurvive);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		boolean[][] outArray = life.getState();
		for (int i = 0; i < Life.MAX_SIZE; i++)
			outState.putBooleanArray(STATE_GOL + i, outArray[i]);
		outState.putBoolean(STATE_PAUSED, paused);
		outState.putBoolean(STATE_CANVAS_LOCKED, canvasLocked);
		outState.putInt(STATE_X_WIDTH, life.getxWidth());
		outState.putInt(STATE_Y_WIDTH, life.getxWidth());
		outState.putInt(STATE_X_POS, life.getxPos());
		outState.putInt(STATE_Y_POS, life.getyPos());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		MenuItem lockItem = menu.findItem(R.id.action_lock);
		MenuItem runItem = menu.findItem(R.id.action_run);
		if (Build.VERSION.SDK_INT >= 14
				&& ViewConfiguration.get(this).hasPermanentMenuKey())
			menu.findItem(R.id.action_random).setShowAsAction(
					MenuItem.SHOW_AS_ACTION_IF_ROOM);
		if (canvasLocked)
			lockItem.setIcon(getResources().getDrawable(R.drawable.locked));
		else
			lockItem.setIcon(getResources().getDrawable(R.drawable.unlocked));
		if (paused) {
			runItem.setChecked(false);
			runItem.setIcon(getResources().getDrawable(R.drawable.play));
			runItem.setTitle(R.string.action_title_play);
		} else {
			runItem.setChecked(true);
			runItem.setIcon(getResources().getDrawable(R.drawable.pause));
			runItem.setTitle(R.string.action_title_pause);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.action_lock):
			if (canvasLocked) {
				life.setCanvasLock(false);
				canvasLocked = false;
				item.setIcon(getResources().getDrawable(R.drawable.unlocked));
			} else {
				life.setCanvasLock(true);
				canvasLocked = true;
				item.setIcon(getResources().getDrawable(R.drawable.locked));
			}
			break;
		case (R.id.action_run):
			if (!paused) {
				item.setChecked(false);
				life.setPaused(true);
				paused = true;
				item.setIcon(getResources().getDrawable(R.drawable.play));
				item.setTitle(R.string.action_title_play);
			} else {
				item.setChecked(true);
				life.setPaused(false);
				paused = false;
				item.setIcon(getResources().getDrawable(R.drawable.pause));
				item.setTitle(R.string.action_title_pause);
			}
			break;
		case (R.id.action_random):
			life.randomize(0.3);
			break;
		case (R.id.action_clear):
			life.clear();
			break;
		case (R.id.action_faster):
			int fpsFaster = life.getFps() + 1;
			if (fpsFaster >= 15)
				fpsFaster = 15;
			life.setFps(fpsFaster);
			FPSToast.setText(fpsFaster + " FPS");
			FPSToast.show();
			break;
		case (R.id.action_slower):
			int fpsSlower = life.getFps() - 1;
			if (fpsSlower <= 0)
				fpsSlower = 1;
			life.setFps(fpsSlower);
			FPSToast.setText(fpsSlower + " FPS");
			FPSToast.show();
			break;
		case (R.id.action_settings):
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivityForResult(settingsIntent, REQUEST_SETTINGS_ACTIVITY);
			break;
		case (R.id.action_help):
			if (isNetworkAvailable())
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://en.m.wikipedia.org/wiki/Conway%27s_game_of_life")));
			else
				startActivity(new Intent(this, HelpActivity.class));
		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		life.setPaused(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!paused)
			life.setPaused(false);
		life.refresh();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_SETTINGS_ACTIVITY)
			if (resultCode == RESULT_OK) {
				life.setAntiAliased(getSharedPreferences(SHARED_PREFS_NAME,
						MODE_PRIVATE).getBoolean(PREFS_ANTIALIAS, true));
				boolean[] tempRuleBirth = new boolean[9], tempRuleSurvive = new boolean[9];
				for (int i = 0; i < 9; i++) {
					tempRuleSurvive[i] = getSharedPreferences(
							SHARED_PREFS_NAME, MODE_PRIVATE).getBoolean(
							PREFS_SURVIVAL_RULE + i, false);
					tempRuleBirth[i] = getSharedPreferences(SHARED_PREFS_NAME,
							MODE_PRIVATE).getBoolean(PREFS_BIRTH_RULE + i,
							false);
				}
				life.setRuleBirth(tempRuleBirth);
				life.setRuleSurvive(tempRuleSurvive);
				life.setSwipeTrail(getSharedPreferences(SHARED_PREFS_NAME,
						MODE_PRIVATE).getInt(PREFS_SWIPE_TRAIL, 1));
			}
	}

	public static class TipsDialog extends DialogFragment implements
			View.OnClickListener {

		int tipNumber;
		String[] tips;
		TextView textMessage;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			tips = getResources().getStringArray(R.array.tips);
			tipNumber = (int) Math.round(Math.random() * (tips.length - 1));
			View rootView = inflater.inflate(R.layout.dilog_tips, container,
					false);
			textMessage = (TextView) rootView.findViewById(R.id.textMessage);
			getDialog().setTitle("Tips - " + tips[tipNumber].split(":")[0]);
			textMessage.setText(tips[tipNumber].split(":")[1]);
			rootView.findViewById(R.id.buttonRandom).setOnClickListener(this);
			return rootView;
		}

		public void onClick(View v) {
			int tempTipNumber = (int) Math.round(Math.random()
					* (tips.length - 1));
			while (tempTipNumber == tipNumber)
				tempTipNumber = (int) Math.round(Math.random()
						* (tips.length - 1));
			tipNumber = tempTipNumber;
			getDialog().setTitle("Tips - " + tips[tipNumber].split(":")[0]);
			textMessage.setText(tips[tipNumber].split(":")[1]);
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
