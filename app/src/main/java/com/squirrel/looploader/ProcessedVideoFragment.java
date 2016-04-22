package com.squirrel.looploader;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squirrel.looploader.helpers.DocsHelper;
import com.squirrel.looploader.model.VideoFile;

import java.io.File;
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ProcessedVideoFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private VideosRecyclerViewAdapter mAdapter;
    private OnListFragmentInteractionListener mListener;
    private TextView mEmptyView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProcessedVideoFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ProcessedVideoFragment newInstance(int columnCount) {
        ProcessedVideoFragment fragment = new ProcessedVideoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_processedvideo_list, container, false);

        // Set the adapter
        if (rootView != null) {

            Context context = rootView.getContext();
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
            mEmptyView = (TextView) rootView.findViewById(R.id.empty_view);

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            //get inititial files data
            Log.d(ProcessedVideoFragment.class.getSimpleName(), "Getting the folder data");
            Log.d(ProcessedVideoFragment.class.getSimpleName(), "Video path: " +
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera/"));
            Log.d(ProcessedVideoFragment.class.getSimpleName(), "App folder files list: ");
            for (String item : Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera/").list()){
                Log.d(ProcessedVideoFragment.class.getSimpleName(), "File: " + item);
            }

            mAdapter =
                    new VideosRecyclerViewAdapter(mListener, getLocalFolderFilesList());
            recyclerView.setAdapter(mAdapter);

//            mAdapter.updateFileList(getContext());

            if(mAdapter.getValues().isEmpty()){
                recyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            }
            else{
                recyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
        }
        return rootView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateFilesList(){
        mAdapter.updateFileList(getActivity().getApplicationContext());
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
    }


    public ArrayList<VideoFile> getLocalFolderFilesList(){
        ArrayList<VideoFile> videoFiles = new ArrayList<VideoFile>();
//        if(!DocsHelper.createDirIfNotExists(Environment.DIRECTORY_DCIM+"/Loops/")){
//            return videoFiles;
//        }
//        File[] listFiles = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Loops/").listFiles();
        File[] listFiles = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Loops/").listFiles();

        if(listFiles != null) {
            for(File file : listFiles){
                if(DocsHelper.checkIfVideo(file.getName())){
                    VideoFile item = new VideoFile(file.getPath(), file);
                    videoFiles.add(item);
                }
            }
        }
        return videoFiles;
    }

}
