package ru.mirea.ushakovaps.mireaproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import ru.mirea.ushakovaps.mireaproject.databinding.ActivityFirebaseAuthBinding;
import ru.mirea.ushakovaps.mireaproject.databinding.FragmentProfileBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private EditText editTextNickname;
    private EditText editTextAge;
    private EditText editTextCountry;
    private SharedPreferences sharedPreferences;
    private String nickname;
    private int age;
    private String country;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private final String host = "time.nist.gov"; // или time-a.nist.gov
    private final int port = 13;

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        editTextNickname = view.findViewById(R.id.editTextNickname);
        editTextAge = view.findViewById(R.id.editTextAge);
        editTextCountry = view.findViewById(R.id.editTextCountry);

        sharedPreferences = getContext().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        nickname = sharedPreferences.getString("Nickname", "unknown");
        age = sharedPreferences.getInt("Age", 0);
        country = sharedPreferences.getString("Country", "unknown");

        if(!nickname.equals("unknown"))
        {
            editTextNickname.setText(nickname);
        }
        if(age != 0)
        {
            editTextAge.setText(String.valueOf(age));
        }
        if(!country.equals("unknown"))
        {
            editTextCountry.setText(country);
        }

        Button buttonSave = view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                nickname = editTextNickname.getText().toString();
                editor.putString("Nickname", nickname);
                age = Integer.parseInt(editTextAge.getText().toString());
                editor.putInt("Age", age);
                country = editTextCountry.getText().toString();
                editor.putString("Country", country);

                editor.apply();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        Button buttonSignOut = view.findViewById(R.id.buttonSignOut);
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), FirebaseAuthActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}