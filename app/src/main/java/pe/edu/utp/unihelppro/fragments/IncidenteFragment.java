package pe.edu.utp.unihelppro.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.adapters.IncidenteAdapter;
import pe.edu.utp.unihelppro.Connect;
import pe.edu.utp.unihelppro.models.Incidentes;


public class IncidenteFragment extends Fragment {
    private static final String ARG_PARAM1 = "publicos";
    private static final String ARG_PARAM2 = "propios";
    private List<Incidentes> incidentesList = new ArrayList<>();

    private Boolean publicos = false;
    private Boolean propios = false;
    private Context mContext;

    private OnFragmentInteractionListener mListener;

    public IncidenteFragment() {
        
    }

    public static IncidenteFragment newInstance(Boolean publicos, Boolean propios) {
        IncidenteFragment fragment = new IncidenteFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, publicos);
        args.putBoolean(ARG_PARAM2, propios);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            publicos = getArguments().getBoolean(ARG_PARAM1, false);
            propios = getArguments().getBoolean(ARG_PARAM2, false);
        }
    }

    public void updateData() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incidente, container, false);
        mContext = getContext();
        RecyclerView incidentesRecycler = (RecyclerView) view.findViewById(R.id.incidentesRecycler);
        incidentesRecycler.setHasFixedSize(true);
        LinearLayoutManager incidentesLayoutManager = new LinearLayoutManager(mContext);
        incidentesRecycler.setLayoutManager(incidentesLayoutManager);

        if( publicos ){
            incidentesList = Connect.getInstance().getIncidentesPublicos();
        }
        if( propios ){
            incidentesList = Connect.getInstance().getIncidentesPropios();
        }
        if( incidentesList == null ) {
            incidentesList = new ArrayList<>();
        }

        IncidenteAdapter incidentesAdapter = new IncidenteAdapter(incidentesList, mContext );
        incidentesRecycler.setAdapter(incidentesAdapter);

        return view;
    }

    
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
            //throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    
    public interface OnFragmentInteractionListener {
        
        void onFragmentInteraction(Uri uri);
    }
}
