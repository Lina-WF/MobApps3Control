package ru.mirea.ushakovaps.mireaproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.mirea.ushakovaps.mireaproject.databinding.FragmentCameraBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private static final int REQUEST_CODE_PERMISSION = 100;
    private static final int CAMERA_REQUEST = 0;
    private boolean isWork = false;
    private Uri imageUri;
    private FragmentCameraBinding binding;

    @Override
    public  void onAttach(Context context)
    {
        super.onAttach(context);
        binding = FragmentCameraBinding.inflate(getLayoutInflater());

        int cameraPermissionStatus = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA);

        if (cameraPermissionStatus == PackageManager.PERMISSION_GRANTED) {
            isWork = true;
            Log.d("a", "isWork true");
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDirectory);
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
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        ActivityResultCallback<ActivityResult> callback = new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    ImageView imageView = view.findViewById(R.id.imageViewMoment);
                    imageView.setImageURI(imageUri);
                }
            }
        };
        ActivityResultLauncher<Intent> cameraActivityResultLauncher =  registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                callback);
        view.findViewById(R.id.imageViewMoment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("a", "click");
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (isWork) {
                    Log.d("a", "click true");
                    try {
                        File photoFile = createImageFile();
                        String authorities = requireContext().getApplicationContext().getPackageName() + ".fileprovider";
                        imageUri = FileProvider.getUriForFile(requireContext(), authorities, photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        cameraActivityResultLauncher.launch(cameraIntent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        view.findViewById(R.id.buttonMoment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = view.findViewById(R.id.editTextMoment);
                String text = String.valueOf(editText.getText());
                editText.setVisibility(View.INVISIBLE);
                view.findViewById(R.id.buttonMoment).setVisibility(View.INVISIBLE);
                TextView textView = view.findViewById(R.id.textViewMoment);
                textView.setText(text);
                textView.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }
}