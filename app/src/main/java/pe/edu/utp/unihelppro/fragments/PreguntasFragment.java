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
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pe.edu.utp.unihelppro.Connect;
import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.adapters.ComentarioAdapter;
import pe.edu.utp.unihelppro.adapters.IncidenteAdapter;
import pe.edu.utp.unihelppro.adapters.PreguntasAdapter;
import pe.edu.utp.unihelppro.adapters.SectionsPagerAdapter;
import pe.edu.utp.unihelppro.models.Comentarios;
import pe.edu.utp.unihelppro.models.Incidentes;
import pe.edu.utp.unihelppro.models.PreguntasFrecuentes;
import pe.edu.utp.unihelppro.models.UsuarioBackendless;


public class PreguntasFragment extends Fragment implements  PreguntasAdapter.OnPreguntasListener {

    private RecyclerView listRecycler;
    private Context mContext;

    public PreguntasFragment() {
        
    }

    public static PreguntasFragment newInstance() {
        PreguntasFragment fragment = new PreguntasFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preguntas_list, container, false);
        mContext = getContext();
        listRecycler = (RecyclerView) view.findViewById(R.id.list);
        listRecycler.setHasFixedSize(true);
        LinearLayoutManager incidentesLayoutManager = new LinearLayoutManager(mContext);
        //incidentesLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listRecycler.setLayoutManager(incidentesLayoutManager);

        List<PreguntasFrecuentes> preguntasFrecuentes = Connect.getInstance().getPreguntasFrecuentes();
        preguntasFrecuentes = PreguntasFrecuentes.listAll( PreguntasFrecuentes.class );
        if( preguntasFrecuentes == null ) {
            preguntasFrecuentes = new ArrayList<>();
        }

        PreguntasAdapter listAdapter = new PreguntasAdapter(preguntasFrecuentes, mContext, PreguntasFragment.this );
        listRecycler.setAdapter(listAdapter);

        obtenerPreguntas();

        return view;
    }

    void obtenerPreguntas(  ) {

        IDataStore<Map> incidentesStorage = Backendless.Data.of( "PreguntasFrecuentes" );
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        //queryBuilder.setSortBy( "-fecha");
        queryBuilder.setPageSize( 50 );
        incidentesStorage.find( queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse( List<Map> maps ) {
                List<PreguntasFrecuentes> preguntasFrecuentes = new ArrayList<>();
                for ( Map map: maps) {
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson( map );
                        PreguntasFrecuentes pregunta = gson.fromJson(json, PreguntasFrecuentes.class);
                        pregunta.save();
                        preguntasFrecuentes.add( pregunta );
                    } catch ( IllegalArgumentException exception ) {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                listRecycler.setHasFixedSize(true);
                LinearLayoutManager incidentesLayoutManager = new LinearLayoutManager(mContext);
                //incidentesLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                listRecycler.setLayoutManager(incidentesLayoutManager);
                PreguntasAdapter listAdapter = new PreguntasAdapter(preguntasFrecuentes, mContext, PreguntasFragment.this );
                listRecycler.setAdapter(listAdapter);
            }

            @Override
            public void handleFault( BackendlessFault fault ) {
                Toast.makeText(getContext(), fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } );
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }
    */

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onPreguntaInteraction(PreguntasFrecuentes item) {

    }
}
