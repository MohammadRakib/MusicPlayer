package com.example.musicplayer;

import android.app.Activity;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;


import static com.example.musicplayer.App.myMediaPlayer;

public class EqualizerActivity extends Activity
        implements SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener,
        OnClickListener
{
    TextView bass_boost_label = null;
    SeekBar bass_boost = null;
    CheckBox enabled = null;
    Button flat = null;

    static Equalizer eq = null;
    BassBoost bb = null;

    int min_level = 0;
    int max_level = 100;
    static boolean equlizer_enable=false;
    static int level;

    static final int MAX_SLIDERS = 8;
    static SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
    TextView slider_labels[] = new TextView[MAX_SLIDERS];
    int num_sliders = 0;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);

        enabled = findViewById(R.id.enabled);
        enabled.setOnCheckedChangeListener (this);

        flat = findViewById(R.id.flat);
        flat.setOnClickListener(this);

        bass_boost = findViewById(R.id.bass_boost);
        bass_boost.setOnSeekBarChangeListener(this);
        bass_boost_label =  findViewById (R.id.bass_boost_label);

        sliders[0] = findViewById(R.id.slider_1);
        slider_labels[0] = findViewById(R.id.slider_label_1);
        sliders[1] = findViewById(R.id.slider_2);
        slider_labels[1] = findViewById(R.id.slider_label_2);
        sliders[2] = findViewById(R.id.slider_3);
        slider_labels[2] = findViewById(R.id.slider_label_3);
        sliders[3] = findViewById(R.id.slider_4);
        slider_labels[3] = findViewById(R.id.slider_label_4);
        sliders[4] = findViewById(R.id.slider_5);
        slider_labels[4] = findViewById(R.id.slider_label_5);
        sliders[5] = findViewById(R.id.slider_6);
        slider_labels[5] = findViewById(R.id.slider_label_6);
        sliders[6] = findViewById(R.id.slider_7);
        slider_labels[6] = findViewById(R.id.slider_label_7);
        sliders[7] = findViewById(R.id.slider_8);
        slider_labels[7] = findViewById(R.id.slider_label_8);

        eq = new Equalizer (0,myMediaPlayer.getAudioSessionId());
        if (eq != null)
        {
            eq.setEnabled (equlizer_enable);
            int num_bands = eq.getNumberOfBands();
            num_sliders = num_bands;
            short r[] = eq.getBandLevelRange();
            min_level = r[0];
            max_level = r[1];
            for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++)
            {
                int[] freq_range = eq.getBandFreqRange((short)i);
                sliders[i].setOnSeekBarChangeListener(this);
                slider_labels[i].setText (formatBandLabel (freq_range));
            }
        }
        for (int i = num_sliders ; i < MAX_SLIDERS; i++)
        {
            sliders[i].setVisibility(View.GONE);
            slider_labels[i].setVisibility(View.GONE);
        }

        bb = new BassBoost (0,myMediaPlayer.getAudioSessionId());
        if (bb == null)
        {
            bass_boost.setVisibility(View.GONE);
            bass_boost_label.setVisibility(View.GONE);
        }


        updateUI();
    }

    @Override
    public void onProgressChanged (SeekBar seekBar, int level,
                                   boolean fromTouch)
    {
        if (seekBar == bass_boost)
        {
            bb.setEnabled (level > 0);
            bb.setStrength ((short)level);
        }
        else if (eq != null)
        {
            int new_level = min_level + (max_level - min_level) * level / 100;

            for (int i = 0; i < num_sliders; i++)
            {
                if (sliders[i] == seekBar)
                {
                    eq.setBandLevel ((short)i, (short)new_level);
                    break;
                }
            }
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
    }

    public String formatBandLabel (int[] band)
    {
        return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
    }


    public String milliHzToString (int milliHz)
    {
        if (milliHz < 1000) return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz";
    }


    public void updateSliders ()
    {
        for (int i = 0; i < num_sliders; i++)
        {

            if (eq != null) {
                level = eq.getBandLevel((short) i);

            }
            else {
                level = 0;

            }
            int pos = 100 * level / (max_level - min_level) + 50;
            sliders[i].setProgress (pos);
        }
    }


    public void updateBassBoost ()
    {
        if (bb != null)
            bass_boost.setProgress (bb.getRoundedStrength());
        else
            bass_boost.setProgress (0);
    }


    @Override
    public void onCheckedChanged (CompoundButton view, boolean isChecked)
    {
        if (view ==  enabled)
        {
            if(!eq.getEnabled()){
            equlizer_enable=isChecked;
            eq.setEnabled (equlizer_enable);
            updateSliders();
            updateBassBoost();
            }
            else{
                equlizer_enable=isChecked;
                eq.setEnabled (equlizer_enable);
            }

        }
    }


    @Override
    public void onClick (View view)
    {
        if (view ==  flat)
        {
            setFlat();
        }
    }


    public void updateUI ()
    {
        updateSliders();
        updateBassBoost();
        enabled.setChecked (eq.getEnabled());
    }


    public void setFlat ()
    {
        if (eq != null)
        {
            for (int i = 0; i < num_sliders; i++)
            {
                eq.setBandLevel ((short)i, (short)0);
            }
        }

        if (bb != null)
        {
            bb.setEnabled (false);
            bb.setStrength ((short)0);
        }

        updateUI();
    }




}


