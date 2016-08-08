package by.dmitry.debts;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

public class SimplePagerAdapter extends PagerAdapter {

    List<View> pages = null;

    public SimplePagerAdapter(List<View> pages) {
        this.pages = pages;
    }

    @Override
    public Object instantiateItem(View view, int i) {
        View v = pages.get(i);
        ((ViewPager) view).addView(v, 0);
        return v;
    }

    @Override
    public void destroyItem(View view, int i, Object o) {
        ((ViewPager) view).removeView((View) o);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }

    @Override
    public void finishUpdate(View view) {

    }

    @Override
    public void restoreState(Parcelable parcelable, ClassLoader classLoader) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View view) {

    }
}
