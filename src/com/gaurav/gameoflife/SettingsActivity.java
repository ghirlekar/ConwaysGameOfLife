package com.gaurav.gameoflife;

import java.util.Arrays;
import java.util.List;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class SettingsActivity extends FragmentActivity {
	GameSettingsFragment gameSettingsFrag;
	GraphicsSettingsFragment graphicsSettingsFrag;
	GeneralSettingsFragment generalSettingsFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		ViewPager settingsPager = (ViewPager) findViewById(R.id.settings_pager);
		setResult(RESULT_OK);

		gameSettingsFrag = new GameSettingsFragment();
		generalSettingsFrag = new GeneralSettingsFragment();
		graphicsSettingsFrag = new GraphicsSettingsFragment();

		settingsPager.setAdapter(new SettingsFragmentPagerAdapter(
				getSupportFragmentManager()));
		settingsPager.setCurrentItem(1);
	}

	public class SettingsFragmentPagerAdapter extends FragmentPagerAdapter {

		public SettingsFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case (0):
				return gameSettingsFrag;
			case (1):
				return generalSettingsFrag;
			default:
				return graphicsSettingsFrag;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case (0):
				return "Game";
			case (1):
				return "General";
			default:
				return "Graphics";
			}
		}

		@Override
		public int getCount() {
			return 3;
		}

	}

	public static class GraphicsSettingsFragment extends Fragment {

		private CheckBox checkAntiAlias;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.frag_settings_graphics,
					container, false);
			checkAntiAlias = (CheckBox) rootView
					.findViewById(R.id.checkAntiAlias);
			checkAntiAlias.setChecked(getActivity().getSharedPreferences(
					MainActivity.SHARED_PREFS_NAME, MODE_PRIVATE).getBoolean(
					MainActivity.PREFS_ANTIALIAS, true) ? true : false);
			checkAntiAlias
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							getActivity()
									.getSharedPreferences(
											MainActivity.SHARED_PREFS_NAME,
											MODE_PRIVATE)
									.edit()
									.putBoolean(MainActivity.PREFS_ANTIALIAS,
											isChecked).commit();
						}
					});
			return rootView;
		}
	}

	public static class GameSettingsFragment extends Fragment {

		Dialog rulePicker;
		List<Integer> ruleSurvivalIds = Arrays.asList(new Integer[] {
				R.id.checkA0, R.id.checkA1, R.id.checkA2, R.id.checkA3,
				R.id.checkA4, R.id.checkA5, R.id.checkA6, R.id.checkA7,
				R.id.checkA8 });
		List<Integer> ruleBirthIds = Arrays.asList(new Integer[] {
				R.id.checkD0, R.id.checkD1, R.id.checkD2, R.id.checkD3,
				R.id.checkD4, R.id.checkD5, R.id.checkD6, R.id.checkD7,
				R.id.checkD8 });

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.frag_settings_game,
					container, false);
			if (Build.VERSION.SDK_INT >= 11) {
				rulePicker = new Dialog(getActivity());
				rulePicker.setContentView(R.layout.dialog_rule_picker);
				rulePicker.setTitle("Game Rules");
				for (int i = 0; i < 9; i++) {
					ToggleButton tempSurviveToggle, tempBirthToggle;
					tempSurviveToggle = (ToggleButton) rulePicker
							.findViewById(ruleSurvivalIds.get(i));
					tempSurviveToggle
							.setOnCheckedChangeListener(survivalRuleCheckListener);
					tempBirthToggle = (ToggleButton) rulePicker
							.findViewById(ruleBirthIds.get(i));
					tempBirthToggle
							.setOnCheckedChangeListener(birthRuleCheckListener);
					if (getActivity().getSharedPreferences(
							MainActivity.SHARED_PREFS_NAME, MODE_PRIVATE)
							.getBoolean(MainActivity.PREFS_SURVIVAL_RULE + i,
									false))
						tempSurviveToggle.setChecked(true);
					if (getActivity().getSharedPreferences(
							MainActivity.SHARED_PREFS_NAME, MODE_PRIVATE)
							.getBoolean(MainActivity.PREFS_BIRTH_RULE + i,
									false))
						tempBirthToggle.setChecked(true);
				}
				rootView.findViewById(R.id.button_rule_picker)
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								rulePicker.show();
							}
						});
			}
			RadioGroup swipeTrailGroup = (RadioGroup) rootView
					.findViewById(R.id.radio_group_swipe_trail);
			swipeTrailGroup
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							SharedPreferences.Editor editor = getActivity()
									.getSharedPreferences(
											MainActivity.SHARED_PREFS_NAME,
											MODE_PRIVATE).edit();
							switch (checkedId) {
							case (R.id.radio_swipe_circular):
								editor.putInt(MainActivity.PREFS_SWIPE_TRAIL, 0);
								break;
							case (R.id.radio_swipe_rectangular):
								editor.putInt(MainActivity.PREFS_SWIPE_TRAIL, 1);
								break;
							}
							editor.commit();

						}
					});
			switch (getActivity().getSharedPreferences(
					MainActivity.SHARED_PREFS_NAME, MODE_PRIVATE).getInt(
					MainActivity.PREFS_SWIPE_TRAIL, 1)) {
			case (0):
				swipeTrailGroup.check(R.id.radio_swipe_circular);
				break;
			case (1):
				swipeTrailGroup.check(R.id.radio_swipe_rectangular);
				break;
			}
			return rootView;
		}

		private CompoundButton.OnCheckedChangeListener survivalRuleCheckListener = new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				int i = ruleSurvivalIds.indexOf(buttonView.getId());
				getActivity()
						.getSharedPreferences(MainActivity.SHARED_PREFS_NAME,
								MODE_PRIVATE)
						.edit()
						.putBoolean(MainActivity.PREFS_SURVIVAL_RULE + i,
								isChecked).commit();
			}
		};

		private CompoundButton.OnCheckedChangeListener birthRuleCheckListener = new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				int i = ruleBirthIds.indexOf(buttonView.getId());
				getActivity()
						.getSharedPreferences(MainActivity.SHARED_PREFS_NAME,
								MODE_PRIVATE)
						.edit()
						.putBoolean(MainActivity.PREFS_BIRTH_RULE + i,
								isChecked).commit();
			}
		};
	}

	public static class GeneralSettingsFragment extends Fragment {

		MainActivity.TipsDialog tipsDialog;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.frag_settings_general,
					container, false);
			tipsDialog = new MainActivity.TipsDialog();
			((Button) rootView.findViewById(R.id.buttonShowTips))
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							tipsDialog.show(getActivity()
									.getSupportFragmentManager(), "tipsDialog");
						}
					});
			CheckBox checkShowTipsOnStart = (CheckBox) rootView
					.findViewById(R.id.checkShowTipsOnStart);
			checkShowTipsOnStart.setChecked(getActivity().getSharedPreferences(
					MainActivity.SHARED_PREFS_NAME, MODE_PRIVATE).getBoolean(
					MainActivity.PREFS_SHOW_TIPS_ON_START, true));
			checkShowTipsOnStart
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							getActivity()
									.getSharedPreferences(
											MainActivity.SHARED_PREFS_NAME,
											MODE_PRIVATE)
									.edit()
									.putBoolean(
											MainActivity.PREFS_SHOW_TIPS_ON_START,
											isChecked).commit();
						}
					});

			rootView.findViewById(R.id.buttonDefaults).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							SharedPreferences.Editor editor = getActivity()
									.getSharedPreferences(
											MainActivity.SHARED_PREFS_NAME,
											MODE_PRIVATE)
									.edit()
									.putBoolean(MainActivity.PREFS_ANTIALIAS,
											true)
									.putBoolean(
											MainActivity.PREFS_SHOW_TIPS_ON_START,
											true)
									.putInt(MainActivity.PREFS_SWIPE_TRAIL, 1);
							for (int i = 0; i < 9; i++) {
								editor.putBoolean(
										MainActivity.PREFS_SURVIVAL_RULE + i,
										(i == 2 || i == 3) ? true : false);
								editor.putBoolean(MainActivity.PREFS_BIRTH_RULE
										+ i, i == 3 ? true : false);
							}
							editor.commit();
							startActivity(getActivity().getIntent());
							getActivity().finish();
						}
					});
			return rootView;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
