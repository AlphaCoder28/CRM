package com.goldmedal.crm.common.transform;
import androidx.viewpager2.widget.ViewPager2;

import com.zhpan.bannerview.transform.ScaleInTransformer;

import static com.goldmedal.crm.common.transform.TransformerStyle.ACCORDION;
import static com.goldmedal.crm.common.transform.TransformerStyle.DEPTH;
import static com.goldmedal.crm.common.transform.TransformerStyle.ROTATE;
import static com.goldmedal.crm.common.transform.TransformerStyle.SCALE_IN;
import static com.goldmedal.crm.common.transform.TransformerStyle.STACK;

public class PageTransformerFactory {

    public static ViewPager2.PageTransformer createPageTransformer(int transformerStyle) {
        ViewPager2.PageTransformer transformer = null;
        switch (transformerStyle) {
            case DEPTH:
                transformer = new DepthPageTransformer();
                break;
            case ROTATE:
                transformer = new RotateUpTransformer();
                break;
            case STACK:
                transformer = new StackTransformer();
                break;
            case ACCORDION:
                transformer = new AccordionTransformer();
                break;
            case SCALE_IN:
                transformer = new ScaleInTransformer(ScaleInTransformer.DEFAULT_MIN_SCALE);
                break;
        }
        return transformer;
    }
}
