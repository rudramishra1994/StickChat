package edu.northeasternn.studycircle.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import edu.northeasternn.studycircle.HomeFragment;
import edu.northeasternn.studycircle.ProfileFragment;
import edu.northeasternn.studycircle.SearchGroupFragment;

public class HomePagerAdapter extends FragmentStateAdapter {

    private HomeFragment homeTabFragment;

    private SearchGroupFragment searchGroupFragment;

    private ProfileFragment profileFragment;


    private final int numberOfTabs = 3;
    public HomePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        homeTabFragment = new HomeFragment();
        searchGroupFragment = new SearchGroupFragment();
        profileFragment = new ProfileFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return homeTabFragment;
            case 1:
                return searchGroupFragment;
            case 2:
                return profileFragment;
        }
        return homeTabFragment;
    }

    @Override
    public int getItemCount() {
        return  numberOfTabs;
    }
}