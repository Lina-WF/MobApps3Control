package ru.mirea.ushakovaps.mireaproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewTextFileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewTextFileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewTextFileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewTextFileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewTextFileFragment newInstance(String param1, String param2) {
        NewTextFileFragment fragment = new NewTextFileFragment();
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
        View view = inflater.inflate(R.layout.fragment_new_text_file, container, false);
        Button buttonToFile = view.findViewById(R.id.buttonToFile);
        buttonToFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    EditText editTextFile = view.findViewById(R.id.editTextFile);
                    String fileName = editTextFile.getText().toString();
                    EditText editTextQuote = view.findViewById(R.id.editTextQuote);
                    String quote = editTextQuote.getText().toString();
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                    File file = new File(path, fileName);
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsoluteFile());
                        OutputStreamWriter output = new OutputStreamWriter(fileOutputStream);
                        output.write(quote);
                        output.close();
                        Log.d("writing", "success");
                    } catch (IOException e) {
                        Log.w("ExternalStorage", "Error writing " + file, e);
                    }
                }
            }
        });
        Button buttonFromFile = view.findViewById(R.id.buttonFromFile);
        buttonFromFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state) ||
                        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                    File path = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOCUMENTS);
                    EditText editTextFile = view.findViewById(R.id.editTextFile);
                    String fileName = editTextFile.getText().toString();
                    File file = new File(path, fileName);
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file.getAbsoluteFile());
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                        List<String> lines = new ArrayList<String>();
                        BufferedReader reader = new BufferedReader(inputStreamReader);
                        String line = reader.readLine();
                        while (line != null) {
                            lines.add(line);
                            line = reader.readLine();
                        }
                        String quote = lines.toString();
                        Log.w("ExternalStorage", String.format("Read from file %s successful", quote));
                        EditText editTextQuote = view.findViewById(R.id.editTextQuote);
                        editTextQuote.setText(quote);
                    } catch (Exception e) {
                        Log.w("ExternalStorage", String.format("Read from file %s failed", e.getMessage()));
                    }
                }
            }
        });

        return view;
    }
}