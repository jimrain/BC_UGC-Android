package net.rainville.android.bcugc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.services.s3.AmazonS3Client;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "BGUGC-Download";
    private static final int READ_REQUEST_CODE = 42;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // BC CMS Variables
    // JR - I need to find a better place to stash these and make them configurable.
    private static final String AccountId = "4517911906001";
    private static final String ClientId = "68cee4a7-6f8f-4e07-9e7d-33480279f1ad";
    private static final String ClientSecret = "QAszeOF1o2W2p2iJvqdGo5uOhXFiBs22CFabc1yoOn6OwMNY-JF1fqkeHUXkO_j5WYPV8WRm5WEsYWHlqrZakw";

    // UI variables
    private Button mBtnSelectFile;
    private EditText mEditTitle;
    private EditText mEditDescription;
    private EditText mEditTags;
    private Button mBtnUpload;
    private ProgressBar mProgressBar;
    private TextView mStatusText;

    private OnFragmentInteractionListener mListener;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        mBtnSelectFile = (Button)view.findViewById(R.id.btnSelectFile);

        mBtnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getContext(), "Select a file", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("video/*");

                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });

        mEditTitle = (EditText)view.findViewById(R.id.editTitle);
        mEditDescription = (EditText)view.findViewById(R.id.editDescription);
        mEditTags = (EditText)view.findViewById(R.id.editTags);
        mProgressBar = view.findViewById(R.id.progressBar);
        mStatusText = view.findViewById(R.id.statusText);

        mBtnUpload = view.findViewById(R.id.btnUpload);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                String displayName = getDisplayName(uri);

                mEditTitle.setText(displayName);
                mEditTitle.setVisibility(View.VISIBLE);
                mEditDescription.setVisibility(View.VISIBLE);
                mEditTags.setVisibility(View.VISIBLE);
                mBtnUpload.setVisibility(View.VISIBLE);
                // uploadData(uri, displayName);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

            Toast.makeText(context, "Upload Fragment Attached", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public String getDisplayName(Uri uri) {

        String displayName = null;
        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor cursor = getActivity().getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null.  But since an
                // int can't be null in Java, the behavior is implementation-specific,
                // which is just a fancy term for "unpredictable".  So as
                // a rule, check if it's null before assigning to an int.  This will
                // happen often:  The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            cursor.close();
            // displayName = null;
        }

        return displayName;
    }

/*
    private String GetRealPathFromURI(Uri uri)
    {
        String thePath;
        try {

            ParcelFileDescriptor parcelFileDescriptor =
                    getActivity().getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            thePath = fileDescriptor.toString();
        } catch (FileNotFoundException e) {
            thePath = null;

        }
        return thePath;
    }
*/
    public File getFileFromInputStream(Uri uri, String displayName) {
        // File file = new File(getContext().getCacheDir(), "TempVideoFile.save");
        File file;
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            file = File.createTempFile(displayName, null, getContext().getCacheDir());
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(buffer);

        } catch (FileNotFoundException e ) {
            file = null;
        } catch (IOException e) {
            file = null;
        }

        return (file);
    }


    public void uploadData(Uri uri, String displayName) {

        // String thePath = GetRealPathFromURI(uri);
        // Initialize AWSMobileClient if not initialized upon the app startup.
        // AWSMobileClient.getInstance().initialize(this).execute();

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        // URI(URLEncoder.encode(uri.toString(), "UTF-8"));
        // URI juri = URI(URLEncoder.encode(uri.toString(), "UTF-8"));
        File theFile = getFileFromInputStream(uri, displayName);

        String bucketName = null;
        // bcugcwest-userfiles-mobilehub-231872367.s3.amazonaws.com
        // bucketName will contain my bucket. So I need to append s3.amazonaws.com/uploads/<file name>
        try {
            bucketName = AWSMobileClient.getInstance().getConfiguration().optJsonObject("S3TransferUtility").getString("Bucket");
        } catch (JSONException e) {
            Log.d(TAG, "Could not find bucket. ");
        }
        String cdnUrl = "https://" + bucketName + ".s3.amazonaws.com/public/" + displayName;
        Log.d(TAG, "CDN URL: " + cdnUrl);
        TransferObserver uploadObserver =
                transferUtility.upload(
                        "public/" + displayName,
                        theFile);

        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Log.d(TAG, "Upload Complete");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d(TAG, "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.d(TAG, "   Error:" + id + " " + ex.toString());
            }

        });

        // If your upload does not trigger the onStateChanged method inside your
        // TransferListener, you can directly check the transfer state as shown here.
        if (TransferState.COMPLETED == uploadObserver.getState()) {
            // Handle a completed upload.
        }
    }

}
