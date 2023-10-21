package edu.northeasternn.studycircle.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import edu.northeasternn.studycircle.AllUserFragment;
import edu.northeasternn.studycircle.PendingConnectionRequest;
import edu.northeasternn.studycircle.UserConnectionFragment;

public class ProfilePagerAdapter extends FragmentStateAdapter {


    private AllUserFragment allUserFragment;
    private PendingConnectionRequest pendingConnectionRequest;
    private UserConnectionFragment userConnectionFragment;


    public ProfilePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
        allUserFragment = new AllUserFragment();
        pendingConnectionRequest = new PendingConnectionRequest();
        userConnectionFragment = new UserConnectionFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return userConnectionFragment;
            case 1:
                return allUserFragment;
            case 2:
                return pendingConnectionRequest;
        }
        return allUserFragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}