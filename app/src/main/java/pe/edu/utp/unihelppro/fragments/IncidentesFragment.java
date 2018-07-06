package pe.edu.utp.unihelppro.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.adapters.SectionsPagerAdapter;
import pe.edu.utp.unihelppro.Connect;
import pe.edu.utp.unihelppro.models.Incidentes;

public class IncidentesFragment extends Fragment {
    
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public IncidentesFragment() {
        
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incidentes, container, false);


        mSectionsPagerAdapter = new SectionsPagerAdapter( getChildFragmentManager());
        mViewPager = view.findViewById(R.id.viewPagerIncidentes);


        IncidenteFragment publicos;
        IncidenteFragment propias;

        publicos = IncidenteFragment.newInstance( true, false );
        propias = IncidenteFragment.newInstance( false, true );

        mSectionsPagerAdapter.addFragment( publicos, "Incidentes Públicos" );
        mSectionsPagerAdapter.addFragment( propias, "Mis Incidentes" );


        List<Incidentes> incidentesPublicasList = new ArrayList<>();
        List<Incidentes> incidentesPropiasList = new ArrayList<>();
        List<Incidentes> incidentesList = Select.from(Incidentes.class).orderBy("-FECHA").list();

        final String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
        for ( Incidentes inc: incidentesList) {
            if ( inc.getPublico() ){
                incidentesPublicasList.add( inc );
            }
            if ( inc.getUsuarioEmisor() != null ) {
                if (inc.getUsuarioEmisor().getObjectId().equals(currentUserObjectId) ) {
                    incidentesPropiasList.add( inc );
                }
            }
        }
        Connect.getInstance().setIncidentesPublicos( incidentesPublicasList );
        Connect.getInstance().setIncidentesPropios( incidentesPropiasList );

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(mViewPager);

        //mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.setOnTabSelectedListener( new TabLayout.ViewPagerOnTabSelectedListener( mViewPager ){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
            }
        });
        mSectionsPagerAdapter.notifyDataSetChanged();

        try {
            //if( Connect.getInstance().getIncidentesPublicos() == null ){
            IDataStore<Map> incidentesStorage = Backendless.Data.of( "Incidentes" );
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause( "( publico=true ) or ( ( usuarioEmisor!=null ) and ( usuarioEmisor='"+ currentUserObjectId +"' ) )" );
            //queryBuilder.setWhereClause( "(publico=true)or(usuarioEmisor='"+ currentUserObjectId +"')" );
            queryBuilder.setSortBy( "-fecha");
            queryBuilder.setPageSize( 20 );
            incidentesStorage.find( queryBuilder, new AsyncCallback<List<Map>>() {
                @Override
                public void handleResponse( List<Map> maps ) {
                    //List<Incidentes> incidentesList = new ArrayList<>();
                    List<Incidentes> incidentesPublicasList = new ArrayList<>();
                    List<Incidentes> incidentesPropiasList = new ArrayList<>();
                    mSectionsPagerAdapter = new SectionsPagerAdapter( getChildFragmentManager());
                    IncidenteFragment publicos;
                    IncidenteFragment propias;
                    publicos = IncidenteFragment.newInstance( true, false );
                    propias = IncidenteFragment.newInstance( false, true );

                    mSectionsPagerAdapter.addFragment( publicos, "Incidentes Públicos" );
                    mSectionsPagerAdapter.addFragment( propias, "Mis Incidentes" );

                    String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
                    for ( Map map: maps) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            Incidentes incidente = mapper.convertValue(map, Incidentes.class);
                            incidente.setupUsers( map );
                            incidente.save();
                            //incidentesList.add( incidente );
                            if ( incidente.getPublico() ){
                                incidentesPublicasList.add( incidente );
                            }
                            if (incidente.getUsuarioEmisor().getObjectId().equals(currentUserObjectId) ) {
                                incidentesPropiasList.add( incidente );
                            }
                        } catch ( IllegalArgumentException exception ) {
                            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    Connect.getInstance().setIncidentesPublicos( incidentesPublicasList );
                    Connect.getInstance().setIncidentesPropios( incidentesPropiasList );
                    //mSectionsPagerAdapter.notifyDataSetChanged();
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                }

                @Override
                public void handleFault( BackendlessFault fault ) {
                    Toast.makeText(getContext(), fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } );
            //}
        } catch ( BackendlessException exception ) {
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
        return view;
    }

}
