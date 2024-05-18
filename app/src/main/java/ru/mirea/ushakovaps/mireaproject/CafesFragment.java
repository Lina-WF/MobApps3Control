package ru.mirea.ushakovaps.mireaproject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import ru.mirea.ushakovaps.mireaproject.databinding.FragmentCafesBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CafesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CafesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CafesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CafesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CafesFragment newInstance(String param1, String param2) {
        CafesFragment fragment = new CafesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private MapView mapView = null;
    private FragmentCafesBinding binding;
    private static final int REQUEST_CODE_PERMISSION = 200;
    boolean isWork;
    MyLocationNewOverlay locationNewOverlay;
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
        binding = FragmentCafesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        mapView = binding.mapView;
        Configuration.getInstance().load(getContext().getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()));

        mapView.setZoomRounding(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(55.794229, 37.700772);
        mapController.setCenter(startPoint);

        CompassOverlay compassOverlay = new CompassOverlay(getActivity().getApplicationContext(), new
                InternalCompassOrientationProvider(getActivity().getApplicationContext()), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        final Context context = getContext().getApplicationContext();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mapView.getOverlays().add(scaleBarOverlay);

        createMarker(55.730821, 37.593111, "Белый журавль", "Cafe1", "Это ресторан южно-корейской кухни, который с 2000 года радует гостей как традиционными, так и современными блюдами южно-корейской и китайской кухни.\n" +
                "«Белый журавль» – обладатель награды американского сайта путешествий Trip Advisor Travellers’ Choice 2020.");
        createMarker(55.766749, 37.624211, "CHICKO", "Cafe2", "Сеть ресторанов “Chicko Ricko”. Базируется на корейском стритфуде. Первая точка была основана и открыта в 2019 году. Всего на данный момент открыто около 15 точек в 7 городах РФ. В 2023 году планируется открытие ещё 3 точек в разных городах.");
        createMarker(55.755008, 37.560413, "Hite", "Cafe3", "Барбекю по-корейски – это популярный в корейской кухне способ приготовления мяса на гриле, обычно готовят говядину, свинину или курицу. Есть несколько разновидностей гриля, встраиваемые в обеденный стол на газу или на древесных углях, либо же портативные переносные газовые плиты.");
        return view;
    }

    private void createMarker(double latitude, double longitude, String desc, String title, String describtion) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(latitude, longitude));
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                binding.textViewCafe.setText(describtion);
                return true;
            }
        });
        mapView.getOverlays().add(marker);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        marker.setTitle(title);
    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(getContext().getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()));
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance().save(getContext().getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()));
        if (mapView != null) {
            mapView.onPause();
        }
    }
}