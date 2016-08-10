package org.edx.mobile.view;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.edx.mobile.R;
import org.edx.mobile.base.MainApplication;
import org.edx.mobile.event.EnrolledInCourseEvent;
import org.edx.mobile.exception.AuthException;
import org.edx.mobile.http.HttpResponseStatusException;
import org.edx.mobile.loader.AsyncTaskResult;
import org.edx.mobile.loader.CoursesAsyncLoader;
import org.edx.mobile.model.api.EnrolledCoursesResponse;
import org.edx.mobile.module.analytics.ISegment;
import org.edx.mobile.module.prefs.LoginPrefs;
import org.edx.mobile.module.prefs.PrefManager;
import org.edx.mobile.task.RestoreVideosCacheDataTask;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class MyCourseListTabFragment extends CourseListTabFragment {

    private final int MY_COURSE_LOADER_ID = 0x905000;

    protected View noCourseText;
    private boolean refreshOnResume;

    @Inject
    private LoginPrefs loginPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        environment.getSegment().trackScreenView(ISegment.Screens.MY_COURSES);
        EventBus.getDefault().register(this);

        // Restore cache of the courses for which the user has downloaded any videos
        PrefManager.UserPrefManager prefs = new PrefManager.UserPrefManager(MainApplication.application);
        if (!prefs.isVideosCacheRestored()) {
            new RestoreVideosCacheDataTask(MainApplication.application).execute();
        }
    }

    @Override
    public void handleCourseClick(EnrolledCoursesResponse model) {
        environment.getRouter().showCourseDashboardTabs(getActivity(), environment.getConfig(), model, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        noCourseText = view.findViewById(R.id.no_course_tv);
        return view;
    }

    protected void loadData(boolean showProgress) {
        //This Show progress is used to display the progress when a user enrolls in a Course
        if (showProgress && progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        getLoaderManager().restartLoader(MY_COURSE_LOADER_ID, null, this);
    }

    @Override
    protected int getViewResourceID() {
        return R.layout.fragment_my_course_list_tab;
    }

    @Override
    public Loader<AsyncTaskResult<List<EnrolledCoursesResponse>>> onCreateLoader(int i, Bundle bundle) {
        return new CoursesAsyncLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<AsyncTaskResult<List<EnrolledCoursesResponse>>> asyncTaskResultLoader, AsyncTaskResult<List<EnrolledCoursesResponse>> result) {
        if (result == null) {
            logger.warn("result is found null, was expecting non-null");
            return;
        }

        adapter.clear();

        if (result.getEx() != null) {
            logger.error(result.getEx());
            if (result.getEx() instanceof AuthException) {
                loginPrefs.clear();
                getActivity().finish();
            } else if (result.getEx() instanceof HttpResponseStatusException &&
                    ((HttpResponseStatusException) result.getEx()).getStatusCode() == 401) {
                environment.getRouter().forceLogout(
                        getContext(),
                        environment.getSegment(),
                        environment.getNotificationDelegate());
            }
        } else if (result.getResult() != null) {
            ArrayList<EnrolledCoursesResponse> newItems = new ArrayList<EnrolledCoursesResponse>(result.getResult());

            ((MyCoursesListActivity) getActivity()).updateDatabaseAfterDownload(newItems);

            if (result.getResult().size() > 0) {
                adapter.setItems(newItems);
                adapter.notifyDataSetChanged();
            }
        }

        invalidateSwipeFunctionality();
        progressBar.setVisibility(View.GONE);
        if (adapter.isEmpty()) {
            myCourseList.setVisibility(View.GONE);
            noCourseText.setVisibility(View.VISIBLE);
        } else {
            myCourseList.setVisibility(View.VISIBLE);
            noCourseText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<AsyncTaskResult<List<EnrolledCoursesResponse>>> asyncTaskResultLoader) {
        adapter.clear();
        adapter.notifyDataSetChanged();
        myCourseList.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(EnrolledInCourseEvent event) {
        refreshOnResume = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refreshOnResume) {
            loadData(true);
            refreshOnResume = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
