package com.zeeba.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by aipxperts on 8/4/16.
 */
public  class FontCustom {

    static public Typeface setFont(Context context)
    {
        Typeface font = Typeface.createFromAsset(context.getAssets(),"fonts/SinkinSans-500Medium.otf");
        return font;
    }
    static public Typeface setFontBold(Context context)
    {
        Typeface font = Typeface.createFromAsset(context.getAssets(),"fonts/SinkinSans-700Bold.otf");
        return font;
    }
    static public Typeface setFontcontent(Context context)
    {
        Typeface font = Typeface.createFromAsset(context.getAssets(),"fonts/SinkinSans-400Regular.otf");
        return font;
    }


}


