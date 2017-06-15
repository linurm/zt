package zj.zfenlly.mobeta;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.mobeta.android.demodslv.DSLVFragment;
import com.mobeta.android.demodslv.DSLVFragmentClicks;
import com.mobeta.android.demodslv.DragInitModeDialog;
import com.mobeta.android.demodslv.EnablesDialog;
import com.mobeta.android.demodslv.RemoveModeDialog;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import zj.zfenlly.main.BaseFragment;
import zj.zfenlly.tools.R;

@SuppressLint("ValidFragment")
public class DragsortFragment extends BaseFragment implements
        RemoveModeDialog.RemoveOkListener,
        DragInitModeDialog.DragOkListener,
        EnablesDialog.EnabledOkListener {

    private final String TAG = this.getClass().getName();
    //private String mTag = Tag;
    public String mName;
    private int mNumHeaders = 0;
    private int mNumFooters = 0;
    private int mDragStartMode = DragSortController.ON_DRAG;
    private boolean mRemoveEnabled = true;
    private int mRemoveMode = DragSortController.FLING_REMOVE;
    private boolean mSortEnabled = true;
    private boolean mDragEnabled = true;
    private int mColorRes = -1;

    public DragsortFragment() {
        this(R.color.white, "dragsort");
    }

    public DragsortFragment(int colorRes, String name) {
        super(name, false);
        mColorRes = colorRes;
    }

    private void print(String msg) {
        Log.i(TAG, msg);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_dragsort);
        print("onCreate");
        //setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mColorRes = savedInstanceState.getInt("mColorRes");

        print("onCreateView");

        int color = getResources().getColor(mColorRes);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_dragsort, null);
        view.setBackgroundColor(color);
        ViewUtils.inject(this, view);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.content_frame, getNewDslvFragment(), TAG).commit();
        }

        return view;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getActivity().getMenuInflater();
//        inflater.inflate(R.menu.mode_menu, menu);
//        return true;
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mode_menu, menu);
        print("onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onRemoveOkClick(int removeMode) {
        if (removeMode != mRemoveMode) {
            mRemoveMode = removeMode;
            getFragmentManager().beginTransaction().replace(R.id.content_frame, getNewDslvFragment(), TAG).commit();
        }
    }

    @Override
    public void onDragOkClick(int dragStartMode) {
        mDragStartMode = dragStartMode;
        DSLVFragment f = (DSLVFragment) getFragmentManager().findFragmentByTag(TAG);
        f.getController().setDragInitMode(dragStartMode);
    }

    @Override
    public void onEnabledOkClick(boolean drag, boolean sort, boolean remove) {
        mSortEnabled = sort;
        mRemoveEnabled = remove;
        mDragEnabled = drag;
        DSLVFragment f = (DSLVFragment) getFragmentManager().findFragmentByTag(TAG);
        DragSortListView dslv = (DragSortListView) f.getListView();
        f.getController().setRemoveEnabled(remove);
        f.getController().setSortEnabled(sort);
        dslv.setDragEnabled(drag);
    }

    private Fragment getNewDslvFragment() {
        DSLVFragmentClicks f = DSLVFragmentClicks.newInstance(mNumHeaders, mNumFooters);
        f.removeMode = mRemoveMode;
        f.removeEnabled = mRemoveEnabled;
        f.dragStartMode = mDragStartMode;
        f.sortEnabled = mSortEnabled;
        f.dragEnabled = mDragEnabled;
        return f;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        FragmentTransaction transaction;
        DSLVFragment f = (DSLVFragment) getFragmentManager().findFragmentByTag(TAG);
        DragSortListView dslv = (DragSortListView) f.getListView();
        DragSortController control = f.getController();

        switch (item.getItemId()) {
            case R.id.select_remove_mode:
                RemoveModeDialog rdialog = new RemoveModeDialog(mRemoveMode);
                rdialog.setRemoveOkListener(this);
                rdialog.show(getFragmentManager(), "RemoveMode");
                return true;
            case R.id.select_drag_init_mode:
                DragInitModeDialog ddialog = new DragInitModeDialog(mDragStartMode);
                ddialog.setDragOkListener(this);
                ddialog.show(getFragmentManager(), "DragInitMode");
                return true;
            case R.id.select_enables:
                EnablesDialog edialog = new EnablesDialog(mDragEnabled, mSortEnabled, mRemoveEnabled);
                edialog.setEnabledOkListener(this);
                edialog.show(getFragmentManager(), "Enables");
                return true;
            case R.id.add_header:
                mNumHeaders++;

                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, getNewDslvFragment(), TAG);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
                return true;
            case R.id.add_footer:
                mNumFooters++;

                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, getNewDslvFragment(), TAG);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
