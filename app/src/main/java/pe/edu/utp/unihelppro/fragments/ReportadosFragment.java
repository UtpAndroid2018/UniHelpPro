package pe.edu.utp.unihelppro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pe.edu.utp.unihelppro.Connect;
import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.adapters.PreguntasAdapter;
import pe.edu.utp.unihelppro.adapters.ReportadosAdapter;
import pe.edu.utp.unihelppro.models.PreguntasFrecuentes;
import pe.edu.utp.unihelppro.models.Reportados;

public class ReportadosFragment extends Fragment implements ReportadosAdapter.OnReportadosListener {


    private RecyclerView listRecycler;
    private Context mContext;

    public ReportadosFragment() {
    }


    public static ReportadosFragment newInstance() {
        ReportadosFragment fragment = new ReportadosFragment();
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
        View view = inflater.inflate(R.layout.fragment_reportados_list, container, false);
        mContext = getContext();
        listRecycler = (RecyclerView) view.findViewById(R.id.list);
        listRecycler.setHasFixedSize(true);
        LinearLayoutManager incidentesLayoutManager = new LinearLayoutManager(mContext);
        //incidentesLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listRecycler.setLayoutManager(incidentesLayoutManager);

        String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
        List<Reportados> reportados = Connect.getInstance().getReportados();
        List<Reportados> reportadosList = Reportados.listAll( Reportados.class );
        if( reportados == null ) {
            reportados = new ArrayList<>();
        }
        if( reportadosList == null ) {
            reportadosList = new ArrayList<>();
        }
        for( Reportados inc : reportadosList ) {
            if ( inc.getUsuarioEmisor() != null && inc.getUsuarioReceptor() != null ) {
                if (inc.getUsuarioEmisor().getProperties().getObjectId().equals(currentUserObjectId) ) {
                    reportados.add( inc );
                }
            }
        }
        ReportadosAdapter listAdapter = new ReportadosAdapter(reportados, mContext, ReportadosFragment.this );
        listRecycler.setAdapter(listAdapter);

        obtenerReportados();

        return view;
    }


    void obtenerReportados(  ) {
        String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
        IDataStore<Map> incidentesStorage = Backendless.Data.of( "UsuariosReportados" );
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( "( usuarioEmisor!=null ) and ( usuarioEmisor='"+ currentUserObjectId +"' )" );
        queryBuilder.setSortBy( "-fecha");
        queryBuilder.setPageSize( 50 );
        incidentesStorage.find( queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse( List<Map> maps ) {
                List<Reportados> reportadosList = new ArrayList<>();
                for ( Map map: maps) {
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson( map );
                        Reportados reportado = gson.fromJson(json, Reportados.class);
                        reportado.save();
                        if ( reportado.getUsuarioReceptor() != null ) {
                            reportadosList.add( reportado );
                        }
                    } catch ( IllegalArgumentException exception ) {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                listRecycler.setHasFixedSize(true);
                LinearLayoutManager incidentesLayoutManager = new LinearLayoutManager(mContext);
                //incidentesLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                listRecycler.setLayoutManager(incidentesLayoutManager);
                ReportadosAdapter listAdapter = new ReportadosAdapter(reportadosList, mContext, ReportadosFragment.this );
                listRecycler.setAdapter(listAdapter);
            }

            @Override
            public void handleFault( BackendlessFault fault ) {
                Toast.makeText(getContext(), fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onReportadoInteraction(Reportados item) {

    }
}
