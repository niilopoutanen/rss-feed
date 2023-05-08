package com.niilopoutanen.rss_feed.customization;

import static com.niilopoutanen.rss_feed.customization.Preferences.PREFS_FUNCTIONALITY;
import static com.niilopoutanen.rss_feed.customization.Preferences.SP_HAPTICS_TYPE;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.niilopoutanen.rss_feed.R;

import java.util.Arrays;
import java.util.List;

public class SettingsHapticsFragment extends Fragment {
    List<RelativeLayout> types;
    private Context appContext;
    private int selectedIndex;

    public SettingsHapticsFragment() {
    }

    public SettingsHapticsFragment(Context context) {
        this.appContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_haptics, container, false);

        rootView.findViewById(R.id.feedsettings_return).setOnClickListener(v -> {
            PreferencesManager.vibrate(v, PreferencesManager.loadPreferences(appContext), appContext);
            getParentFragmentManager().popBackStack();
        });

        Preferences.HapticTypes selected = PreferencesManager.getEnumPreference(Preferences.SP_HAPTICS_TYPE, Preferences.PREFS_FUNCTIONALITY, Preferences.HapticTypes.class, Preferences.SP_HAPTICS_TYPE_DEFAULT, appContext);

        types = Arrays.asList(
                rootView.findViewById(R.id.vibration_type1),
                rootView.findViewById(R.id.vibration_type2),
                rootView.findViewById(R.id.vibration_type3)
        );
        loadSavedData();
        for (int i = 0; i < types.size(); i++) {
            int finalI = i;
            types.get(i).setOnClickListener(v -> onTypeChange(types.get(finalI), types));
        }
        return rootView;
    }

    private void loadSavedData() {
        Preferences preferences = PreferencesManager.loadPreferences(appContext);
        switch (preferences.s_hapticstype) {
            case VIEW:
                checkButton(types.get(0));
                break;
            case VIBRATE:
                checkButton(types.get(1));
                break;
            case FALLBACK:
                checkButton(types.get(2));
                break;
        }
    }

    private void onTypeChange(RelativeLayout button, List<RelativeLayout> buttonCollection) {

        boolean isChecked = Boolean.parseBoolean(button.getTag().toString());
        if (isChecked) {
            return;
        }

        // Uncheck all other buttons
        for (RelativeLayout otherButton : buttonCollection) {
            if (otherButton == button) {
                continue;
            }
            otherButton.setTag(false);
            if (otherButton.getChildCount() >= 2) {
                otherButton.removeViewAt(otherButton.getChildCount() - 1);
            }
        }

        checkButton(button);

        // Save the selected style
        Preferences.HapticTypes selectedType;
        int selectedIndex = buttonCollection.indexOf(button);
        Vibrator vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
        switch (selectedIndex) {
            default:
                selectedType = Preferences.HapticTypes.VIEW;
                button.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                break;
            case 1:
                selectedType = Preferences.HapticTypes.VIBRATE;
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                break;
            case 2:
                selectedType = Preferences.HapticTypes.FALLBACK;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.EFFECT_CLICK));
                    }
                } catch (Exception ignored) {
                }
                break;
        }
        PreferencesManager.saveEnumPreference(SP_HAPTICS_TYPE, PREFS_FUNCTIONALITY, selectedType, appContext);
    }

    private void checkButton(RelativeLayout button) {
        Drawable checkmark = AppCompatResources.getDrawable(appContext, R.drawable.icon_checkmark);

        // Check the selected button
        button.setTag(true);
        View view = new View(appContext);
        view.setBackground(checkmark);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(PreferencesManager.dpToPx(15, appContext), PreferencesManager.dpToPx(15, appContext));
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        view.setLayoutParams(params);
        view.setBackgroundTintList(ColorStateList.valueOf(PreferencesManager.getAccentColor(appContext)));

        button.addView(view);
    }
}