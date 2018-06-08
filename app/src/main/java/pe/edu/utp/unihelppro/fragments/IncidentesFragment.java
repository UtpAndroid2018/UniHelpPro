package pe.edu.utp.unihelppro.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pe.edu.utp.unihelppro.R;
import pe.edu.utp.unihelppro.adapters.SectionsPagerAdapter;
import pe.edu.utp.unihelppro.Connect;
import pe.edu.utp.unihelppro.models.Incidentes;
import pe.edu.utp.unihelppro.models.UsuarioBackendless;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incidentes, container, false);


        mSectionsPagerAdapter = new SectionsPagerAdapter( getChildFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.viewPagerIncidentes);


        IncidenteFragment publicos;
        IncidenteFragment propias;

        publicos = IncidenteFragment.newInstance( true, false );
        propias = IncidenteFragment.newInstance( false, true );

        mSectionsPagerAdapter.addFragment( publicos, "Incidentes Públicos" );
        mSectionsPagerAdapter.addFragment( propias, "Mis Incidentes" );


        List<Incidentes> incidentesPublicasList = new ArrayList<>();
        List<Incidentes> incidentesPropiasList = new ArrayList<>();
        List<Incidentes> incidentesList = Incidentes.listAll( Incidentes.class );

        String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
        for ( Incidentes inc: incidentesList) {
            if ( inc.getPublico() ){
                incidentesPublicasList.add( inc );
            }
            if (inc.getUsuarioEmisor().getObjectId().equals(currentUserObjectId) ) {
                incidentesPropiasList.add( inc );
            }
        }
        Connect.getInstance().setIncidentesPublicos( incidentesPublicasList );
        Connect.getInstance().setIncidentesPropios( incidentesPropiasList );

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
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
        if( Connect.getInstance().getIncidentesPublicos() == null ){

            IDataStore<Map> incidentesStorage = Backendless.Data.of( "Incidentes" );
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause( "(publico=true)or(usuarioEmisor='"+ currentUserObjectId +"')" );
            queryBuilder.setSortBy( "fecha" );
            queryBuilder.setPageSize( 20 );
            incidentesStorage.find( queryBuilder, new AsyncCallback<List<Map>>() {
                @Override
                public void handleResponse( List<Map> maps ) {
                    List<Incidentes> incidentesList = new ArrayList<>();
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
                            UsuarioBackendless ue = incidente.getUsuarioEmisor();
                            if ( ue != null ) {
                                ue.setObjectId( incidente.getUsuarioEmisor().getObjectId() );
                                ue.setEmail( incidente.getUsuarioEmisor().getEmail() );
                                for ( Object key : map.keySet() ) {
                                    if( key.equals("usuarioEmisor" ) ) {
                                        ue.setName( ( ( BackendlessUser ) map.get( key ) ).getProperty("name").toString() );
                                        ue.setSocialAccount( ( ( BackendlessUser ) map.get( key ) ).getProperty("socialAccount").toString() );
                                        ue.setUserStatus( ( ( BackendlessUser ) map.get( key ) ).getProperty("userStatus").toString() );
                                        ue.setTipo( ( ( BackendlessUser ) map.get( key ) ).getProperty("tipo").toString() );
                                        ue.setCodigo( ( ( BackendlessUser ) map.get( key ) ).getProperty("codigo").toString() );
                                    }
                                }
                                ue.setupUser();
                            }
                            UsuarioBackendless ur = incidente.getUsuarioReceptor();
                            if ( ur != null ) {
                                ur.setObjectId( incidente.getUsuarioReceptor().getObjectId() );
                                ur.setEmail( incidente.getUsuarioReceptor().getEmail() );
                                for ( Object key : map.keySet() ) {
                                    if( key.equals("usuarioReceptor" ) ) {
                                        ur.setName( ( ( BackendlessUser ) map.get( key ) ).getProperty("name").toString() );
                                        ur.setSocialAccount( ( ( BackendlessUser ) map.get( key ) ).getProperty("socialAccount").toString() );
                                        ur.setUserStatus( ( ( BackendlessUser ) map.get( key ) ).getProperty("userStatus").toString() );
                                        ur.setTipo( ( ( BackendlessUser ) map.get( key ) ).getProperty("tipo").toString() );
                                        ur.setCodigo( ( ( BackendlessUser ) map.get( key ) ).getProperty("codigo").toString() );
                                    }
                                }
                                ur.setupUser();
                            }
                            incidente.save();
                            incidentesList.add( incidente );
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
        }

        return view;
    }

}
